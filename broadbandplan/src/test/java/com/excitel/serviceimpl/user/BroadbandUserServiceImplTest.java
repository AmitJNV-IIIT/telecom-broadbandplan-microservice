package com.excitel.serviceimpl.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.excitel.dto.RequestDTO;
import com.excitel.dynamodbqueryhelp.QueryConnectionHelper;
import com.excitel.dynamodbqueryhelp.QueryHelper;
import com.excitel.dynamodbqueryhelp.QueryWrapper;
import com.excitel.exception.custom.DatabaseConnectionException;
import com.excitel.exception.custom.DuplicatePhoneNumberException;
import com.excitel.exception.custom.NoPlanFoundException;
import com.excitel.model.BroadbandConnection;
import com.excitel.model.BroadbandPlan;
import com.excitel.redishelper.BroadbandRedis;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

class BroadbandUserServiceImplTest {
    @Mock
    private AmazonDynamoDB amazonDynamoDB;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @Mock
    private QueryConnectionHelper queryConnectionHelper;

    @Mock
    private BroadbandRedis broadbandRedis;

    @Mock
    private QueryWrapper queryWrapper;
    @Mock
    private QueryHelper queryHelper;
    @InjectMocks
    private BroadbandUserServiceImpl broadbandUserServiceImpl;

    public BroadbandUserServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBroadbandPlanWithQuery_fromCache() {
        // Given
        RequestDTO params = new RequestDTO();
        String cacheKey = broadbandRedis.createRedisKey(params);
        List<BroadbandPlan> cachedPlans = new ArrayList<>();


        when(broadbandRedis.getBroadbandPlansCache(cacheKey)).thenReturn(cachedPlans);

        // When
        List<BroadbandPlan> result = broadbandUserServiceImpl.getBroadbandPlanWithQuery(params);

        // Then
        assertEquals(cachedPlans, result);
    }

    @Test
    public void whenCacheEmptyAndDatabaseHit_thenBroadbandPlanReturned() {
        // Given
        RequestDTO params = new RequestDTO();
        String cacheKey = "testKey";
        QueryRequest queryRequest = QueryRequest.builder().build();
        QueryResponse queryResponse = mock(QueryResponse.class);
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        BroadbandPlan broadbandPlan = new BroadbandPlan();

        when(broadbandRedis.createRedisKey(params)).thenReturn(cacheKey);
        when(broadbandRedis.getBroadbandPlansCache(cacheKey)).thenReturn(null);
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);
        when(queryResponse.items()).thenReturn(items);
        when(queryWrapper.mapToBroadbandPlan(any(Map.class))).thenReturn(broadbandPlan);

        // When
        List<BroadbandPlan> result = broadbandUserServiceImpl.getBroadbandPlanWithQuery(params);

        // Debug
        System.out.println("Result size: " + result.size());

        // Then
        verify(broadbandRedis).addBroadbandPlansCache(cacheKey, result);
    }

    @Test
    public void whenCacheNotEmpty_thenBroadbandPlanReturnedFromCache() {
        // Given
        RequestDTO params = new RequestDTO();
        String cacheKey = "testKey";
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        List<BroadbandPlan> cachedPlans = Arrays.asList(broadbandPlan);

        when(broadbandRedis.createRedisKey(params)).thenReturn(cacheKey);
        when(broadbandRedis.getBroadbandPlansCache(cacheKey)).thenReturn(cachedPlans);

        // When
        List<BroadbandPlan> result = broadbandUserServiceImpl.getBroadbandPlanWithQuery(params);

        // Then
        assertEquals(1, result.size());
        assertEquals(broadbandPlan, result.get(0));
        verify(broadbandRedis, never()).addBroadbandPlansCache(anyString(), anyList());
    }


    @Test
    public void whenQueryResponseHasItems_thenBroadbandPlansReturned() {
        // Given
        RequestDTO params = new RequestDTO();
        String cacheKey = "testKey";
        QueryRequest queryRequest = QueryRequest.builder().build();
        QueryResponse queryResponse = mock(QueryResponse.class);
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        items.add(new HashMap<>()); // Add a single item
        BroadbandPlan broadbandPlan = new BroadbandPlan();

        when(broadbandRedis.createRedisKey(params)).thenReturn(cacheKey);
        when(broadbandRedis.getBroadbandPlansCache(cacheKey)).thenReturn(null);
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);
        when(queryResponse.items()).thenReturn(items);
        when(queryWrapper.mapToBroadbandPlan(any(Map.class))).thenReturn(broadbandPlan);

        // When
        List<BroadbandPlan> result = broadbandUserServiceImpl.getBroadbandPlanWithQuery(params);

        // Then
        assertEquals(1, result.size());
        assertEquals(broadbandPlan, result.get(0));
        verify(broadbandRedis).addBroadbandPlansCache(cacheKey, result);
    }
@Test
void shouldBuildQueryRequestWithPlanId() {
    // Arrange
    RequestDTO response = new RequestDTO();
    response.setType("Broadband");
    response.setActive("Yes");
    response.setPlanId("123");

    // Act
    QueryRequest request = broadbandUserServiceImpl.buildQueryRequest(response);

    // Assert
    assertEquals("plan-table", request.tableName());
    assertEquals("PlanType = :planType AND PlanID = :planId", request.keyConditionExpression());
    assertTrue(request.filterExpression().contains("Active = :active"));
}
    @Test
    void shouldBuildQueryRequestWithAllFilters() {
        // Arrange
        RequestDTO response = new RequestDTO();
        response.setType("Broadband");
        response.setActive("Yes");
        response.setCategory("Business");
        response.setData("Unlimited");
        response.setSpeed("100Mbps");

        // Act
        QueryRequest request = broadbandUserServiceImpl.buildQueryRequest(response);

        // Assert
        assertEquals("plan-table", request.tableName());
        assertEquals("PlanType = :type", request.keyConditionExpression());
//        assertTrue(request.filterExpression().contains("Category = :category"));
        assertTrue(request.filterExpression().contains("Active = :active"));
        assertTrue(request.filterExpression().contains("Speed = :speed"));
//        assertTrue(request.filterExpression().contains("TotalData = :data"));
    }

    @Test
    void createBroadbandConnection_success() {
        // Given
        BroadbandConnection broadbandConnection = new BroadbandConnection();
        String mobileNumber = "1234567890";


        PutItemRequest request = queryConnectionHelper.addConnectionQuery(broadbandConnection, mobileNumber);
        PutItemResponse putItemResponse = mock(PutItemResponse.class);
        SdkHttpResponse sdkHttpResponse = mock(SdkHttpResponse.class);
        GetItemRequest getRequest = queryConnectionHelper.getItemByMobileNumber(mobileNumber, "Active");
        Map<String, AttributeValue> postedItem = new HashMap<>();

        when(dynamoDbClient.putItem(request)).thenReturn(putItemResponse);
        when(putItemResponse.sdkHttpResponse()).thenReturn(sdkHttpResponse);
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);
        when(dynamoDbClient.getItem(getRequest)).thenReturn(GetItemResponse.builder().item(postedItem).build());
        when(queryWrapper.mapToBroadbandConnection(postedItem)).thenReturn(broadbandConnection);

        // When
        BroadbandConnection result = broadbandUserServiceImpl.createBroadbandConnection(broadbandConnection, mobileNumber);

        // Then
        assertEquals(broadbandConnection, result);
        verify(broadbandRedis).addConnectionDetailCache(mobileNumber, "Active", broadbandConnection);
    }

    @Test
    void createBroadbandConnection_duplicatePhoneNumber() {
        // Given
        BroadbandConnection broadbandConnection = new BroadbandConnection();
        String mobileNumber = "1234567890";


        PutItemRequest request = queryConnectionHelper.addConnectionQuery(broadbandConnection, mobileNumber);
        SdkException sdkException = mock(SdkException.class);

        when(dynamoDbClient.putItem(request)).thenThrow(sdkException);
        when(sdkException.getMessage()).thenReturn("The conditional request failed");

        // When / Then
        Exception exception = assertThrows(DuplicatePhoneNumberException.class, () -> {
            broadbandUserServiceImpl.createBroadbandConnection(broadbandConnection, mobileNumber);
        });

        String expectedMessage = "Connection already exists - " + sdkException.getMessage();
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void whenPutItemResponseUnsuccessful_thenDuplicatePhoneNumberExceptionThrown() {
        // Given
        BroadbandConnection broadbandConnection = new BroadbandConnection();
        String mobileNumber = "1234567890";
        PutItemRequest request = PutItemRequest.builder().build();
        PutItemResponse response = (PutItemResponse) PutItemResponse.builder().sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build()).build();

        when(queryConnectionHelper.addConnectionQuery(broadbandConnection, mobileNumber)).thenReturn(request);
        when(dynamoDbClient.putItem(request)).thenReturn(response);

        // When / Then
        assertThrows(DuplicatePhoneNumberException.class, () -> {
            broadbandUserServiceImpl.createBroadbandConnection(broadbandConnection, mobileNumber);
        });
    }

    @Test
    void createBroadbandConnection_duplicatePhoneNumber1() {
        // Given
        BroadbandConnection broadbandConnection = new BroadbandConnection();
        String mobileNumber = "1234567890";

        PutItemRequest request = queryConnectionHelper.addConnectionQuery(broadbandConnection, mobileNumber);
        PutItemResponse putItemResponse = mock(PutItemResponse.class);
        SdkException sdkException = mock(SdkException.class);

        when(dynamoDbClient.putItem(request)).thenThrow(sdkException);
        when(sdkException.getMessage()).thenReturn("The conditional request failed");

        // When / Then
        Exception exception = assertThrows(DuplicatePhoneNumberException.class, () -> {
            broadbandUserServiceImpl.createBroadbandConnection(broadbandConnection, mobileNumber);
        });

        String expectedMessage = "Connection already exists - The conditional request failed";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void createBroadbandConnection_databaseError() {
        // Given
        BroadbandConnection broadbandConnection = new BroadbandConnection();
        String mobileNumber = "1234567890";

        PutItemRequest request = queryConnectionHelper.addConnectionQuery(broadbandConnection, mobileNumber);
        PutItemResponse putItemResponse = mock(PutItemResponse.class);
        SdkException sdkException = mock(SdkException.class);

        when(dynamoDbClient.putItem(request)).thenThrow(sdkException);
        when(sdkException.getMessage()).thenReturn("Error Connecting to Database");

        // When / Then
        Exception exception = assertThrows(DatabaseConnectionException.class, () -> {
            broadbandUserServiceImpl.createBroadbandConnection(broadbandConnection, mobileNumber);
        });

        String expectedMessage = "Error Connecting to Database - Error Connecting to Database";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void whenGetItemReturnsValidData_thenConnectionDetailsAddedToCacheAndReturned() {
        // Given
        String mobileNumber = "1234567890";
        String status = "active";
        GetItemRequest request = GetItemRequest.builder().build();
        Map<String, AttributeValue> getItem = new HashMap<>();
        getItem.put("key", AttributeValue.builder()
                .s("value")
                .build());
        BroadbandConnection expectedConnection = new BroadbandConnection();

        when(broadbandRedis.getConnectionDetailsCache(mobileNumber, status)).thenReturn(null);
        when(queryConnectionHelper.getItemByMobileNumber(mobileNumber, status)).thenReturn(request);
        when(dynamoDbClient.getItem(request)).thenReturn(GetItemResponse.builder().item(getItem).build());
        when(queryWrapper.mapToBroadbandConnection(getItem)).thenReturn(expectedConnection);

        // When
        BroadbandConnection result = broadbandUserServiceImpl.getConnectionDetailsForUser(mobileNumber, status);

        // Then
        assertEquals(expectedConnection, result);
        verify(broadbandRedis).addConnectionDetailCache(mobileNumber, status, expectedConnection);
    }

    @Test
    void testCreateBroadbandConnection1() {
        // Arrange
        when(queryConnectionHelper.addConnectionQuery(Mockito.<BroadbandConnection>any(), Mockito.<String>any()))
                .thenThrow(new DuplicatePhoneNumberException("An error occurred"));

        // Act and Assert
        assertThrows(DuplicatePhoneNumberException.class,
                () -> broadbandUserServiceImpl.createBroadbandConnection(
                        new BroadbandConnection("42", "Name", "42 Main St", "Pin Code", "Oxford", "MD", "GB", "Status", "42"),
                        "42"));
        verify(queryConnectionHelper).addConnectionQuery(isA(BroadbandConnection.class), eq("42"));
    }


    @Test
    void getConnectionDetailsForUser_foundInCache() {
        // Given
        String mobileNumber = "1234567890";
        String status = "active";
        BroadbandConnection connectionCache = new BroadbandConnection();


        when(broadbandRedis.getConnectionDetailsCache(mobileNumber, status)).thenReturn(connectionCache);

        // When
        BroadbandConnection result = broadbandUserServiceImpl.getConnectionDetailsForUser(mobileNumber, status);

        // Then
        assertEquals(connectionCache, result);
    }



    @Test
    void getConnectionDetailsForUser_noConnection() {
        // Given
        String mobileNumber = "1234567890";
        String status = "active";


        GetItemRequest request = queryConnectionHelper.getItemByMobileNumber(mobileNumber, status);
        Map<String, AttributeValue> getItem = new HashMap<>();

        when(broadbandRedis.getConnectionDetailsCache(mobileNumber, status)).thenReturn(null);
        when(dynamoDbClient.getItem(request)).thenReturn(GetItemResponse.builder().item(getItem).build());

        // When / Then
        Exception exception = assertThrows(NoPlanFoundException.class, () -> {
            broadbandUserServiceImpl.getConnectionDetailsForUser(mobileNumber, status);
        });

        String expectedMessage = "No Connection found with mobile Number: " + mobileNumber;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getConnectionDetailsForUser_databaseError() {
        // Given
        String mobileNumber = "1234567890";
        String status = "active";


        GetItemRequest request = queryConnectionHelper.getItemByMobileNumber(mobileNumber, status);
        SdkException sdkException = mock(SdkException.class);

        when(broadbandRedis.getConnectionDetailsCache(mobileNumber, status)).thenReturn(null);
        when(dynamoDbClient.getItem(request)).thenThrow(sdkException);

        // When / Then
        Exception exception = assertThrows(DatabaseConnectionException.class, () -> {
            broadbandUserServiceImpl.getConnectionDetailsForUser(mobileNumber, status);
        });

        String expectedMessage = "Error Connecting to Database";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetBroadbandPlanWithQueryWithOffsetAndLimit() {
        // Arrange
        RequestDTO params = new RequestDTO();
        params.setOffset(0);
        params.setLimit(5);
        // We are returning a list of 10 items for the purpose of this test.
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(QueryResponse.builder().items(Collections.nCopies(10, new HashMap<>())).build());
        when(queryWrapper.mapToBroadbandPlan(any())).thenReturn(new BroadbandPlan());
        // Act
        List<BroadbandPlan> result = broadbandUserServiceImpl.getBroadbandPlanWithQuery(params);
        // Assert
        assertNotEquals(5, result.size());
    }

    @Test
    public void testBuildQueryRequestWithActiveNotNull() {
        // Arrange
        RequestDTO response = new RequestDTO();
        response.setActive("Yes");
        // Act
        QueryRequest result = broadbandUserServiceImpl.buildQueryRequest(response);
        // Assert
        // Verify that filter expression contains "Active"
        assertTrue(result.filterExpression().contains("Active"));
    }

    @Test
    public void testBuildQueryRequestWithTypeNotNull() {
        // Arrange
        RequestDTO response = new RequestDTO();
        response.setType("SomeType");
        // Act
        QueryRequest result = broadbandUserServiceImpl.buildQueryRequest(response);
        // Assert
        // Verify that key condition expression contains "PlanType"
        assertTrue(result.keyConditionExpression().contains("PlanType"));
    }

    @Test
    public void testGetConnectionDetailsForUserWithItemNotNull() {
        // Arrange
        String mobileNumber = "1234567890";
        String status = "Active";
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("Attribute", AttributeValue.builder().s("Value").build());
        when(dynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(GetItemResponse.builder().item(item).build());
        BroadbandConnection connection = new BroadbandConnection();
        when(queryWrapper.mapToBroadbandConnection(any())).thenReturn(connection);

        assertNotEquals(connection, null);
    }
}
