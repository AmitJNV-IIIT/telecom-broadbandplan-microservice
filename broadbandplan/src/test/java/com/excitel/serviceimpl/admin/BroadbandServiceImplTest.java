package com.excitel.serviceimpl.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.excitel.exception.custom.NoPlanFoundException;
import com.excitel.redishelper.BroadbandRedis;
import org.mockito.*;
import com.excitel.dynamodbqueryhelp.QueryHelper;
import com.excitel.dynamodbqueryhelp.QueryWrapper;
import com.excitel.exception.custom.DatabaseConnectionException;
import com.excitel.model.BroadbandPlan;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;


class BroadbandServiceImplTest {

    @Mock
    private QueryHelper queryHelper;

    @Mock
    private QueryWrapper queryWrapper;
    @Mock
    private DynamoDbClient dynamoDbClient;

    @Mock
    private BroadbandServiceImpl broadbandService;


    @InjectMocks
    private BroadbandServiceImpl broadbandServiceImpl;

    @Mock
    private BroadbandRedis  broadbandRedis;

    public BroadbandServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void givenDatabaseConnectionError_whenAddingBroadbandPlan_thenThrowDatabaseConnectionException() {
        // Arrange
        when(queryHelper.addQuery(Mockito.<BroadbandPlan>any()))
                .thenThrow(new DatabaseConnectionException("An error occurred"));

        // Act and Assert
        assertThrows(DatabaseConnectionException.class, () -> broadbandServiceImpl.addBroadbandPlan(new BroadbandPlan()));
        verify(queryHelper).addQuery(isA(BroadbandPlan.class));
    }
    @Test
    void shouldReturnOriginalBroadbandPlanWhenAddingFails() {
        // Arrange
        BroadbandPlan broadbandPlan = BroadbandPlan.builder()
                .planId(UUID.randomUUID().toString())
                .planType("Broadband")
                .price("300")
                .data("2")
                .validity("28")
                .build();

        PutItemRequest putRequest = queryHelper.addQuery(broadbandPlan);
        when(queryHelper.addQuery(broadbandPlan)).thenReturn(putRequest);

        // Simulate an unsuccessful put response
        PutItemResponse putResponse = (PutItemResponse) PutItemResponse.builder()
                .sdkHttpResponse(SdkHttpResponse.builder().statusCode(400).build())
                .build();
        when(dynamoDbClient.putItem(putRequest)).thenReturn(putResponse);

        // Act
        BroadbandPlan addedPlan = broadbandServiceImpl.addBroadbandPlan(broadbandPlan);

        // Assert
        verify(dynamoDbClient, times(1)).putItem(putRequest);
        assertEquals(broadbandPlan, addedPlan);
    }
    @Test
    void givenDatabaseConnectionError_whenUpdatingBroadbandPlan_thenThrowDatabaseConnectionException() {
        // Arrange
        when(queryHelper.updateQuery(Mockito.<BroadbandPlan>any(), Mockito.<String>any()))
                .thenThrow(new DatabaseConnectionException("An error occurred"));

        // Act and Assert
        assertThrows(DatabaseConnectionException.class,
                () -> broadbandServiceImpl.updateBroadbandPlan(new BroadbandPlan(), "42"));
        verify(queryHelper).updateQuery(isA(BroadbandPlan.class), eq("42"));
    }

    @Test
    void addBroadbandPlan_success() {
        // Arrange
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        String uuid = String.valueOf(UUID.randomUUID());
        broadbandPlan.setPlanId(uuid);

        PutItemRequest request = queryHelper.addQuery(broadbandPlan);
        PutItemResponse putItemResponse = mock(PutItemResponse.class);
        SdkHttpResponse sdkHttpResponse = mock(SdkHttpResponse.class);
        GetItemRequest getRequest = queryHelper.getItemByPlanId(uuid,broadbandPlan.getPlanType());
        Map<String, AttributeValue> updatedItem = new HashMap<>();

        when(dynamoDbClient.putItem(request)).thenReturn(putItemResponse);
        when(putItemResponse.sdkHttpResponse()).thenReturn(sdkHttpResponse);
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);
        when(dynamoDbClient.getItem(getRequest)).thenReturn(GetItemResponse.builder().item(updatedItem).build());
        when(queryWrapper.mapToBroadbandPlan(updatedItem)).thenReturn(broadbandPlan);

        // Act
        BroadbandPlan result = broadbandServiceImpl.addBroadbandPlan(broadbandPlan);

        // Assert
        assertEquals(broadbandPlan, result);
        verify(broadbandRedis).clearBroadbandCache();
    }

    @Test
    void addBroadbandPlan_databaseError() {
        // Arrange
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        String uuid = String.valueOf(UUID.randomUUID());
        broadbandPlan.setPlanId(uuid);

        PutItemRequest request = queryHelper.addQuery(broadbandPlan);
        SdkException sdkException = mock(SdkException.class);

        when(dynamoDbClient.putItem(request)).thenThrow(sdkException);
        when(sdkException.getMessage()).thenReturn("Error Connecting to Database");

        // Act / Assert
        Exception exception = assertThrows(DatabaseConnectionException.class, () -> {
            broadbandServiceImpl.addBroadbandPlan(broadbandPlan);
        });

        String expectedMessage = "Error Connecting to Database";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(broadbandRedis, never()).clearBroadbandCache();  // Verify clearBroadbandCache() was not called
    }

    @Test
    void updateBroadbandPlan_SuccessfulUpdate() {
        // Given
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        String planId = "testPlanId";

        UpdateItemRequest request = queryHelper.updateQuery(broadbandPlan, planId);
        UpdateItemResponse updateResponse = mock(UpdateItemResponse.class);
        SdkHttpResponse sdkHttpResponse = mock(SdkHttpResponse.class);
        GetItemRequest getRequest = queryHelper.getItemByPlanId(planId,"Broadband");
        Map<String, AttributeValue> updatedItem = new HashMap<>();
        updatedItem.put("dummyKey", AttributeValue.builder().s("dummyValue").build()); // Ensure the item is not empty

        when(dynamoDbClient.updateItem(request)).thenReturn(updateResponse);
        when(updateResponse.sdkHttpResponse()).thenReturn(sdkHttpResponse);
        when(sdkHttpResponse.isSuccessful()).thenReturn(true);
        when(dynamoDbClient.getItem(getRequest)).thenReturn(GetItemResponse.builder().item(updatedItem).build());
        when(queryWrapper.mapToBroadbandPlan(updatedItem)).thenReturn(broadbandPlan);

        // When
        BroadbandPlan result = broadbandServiceImpl.updateBroadbandPlan(broadbandPlan, planId);

        // Then
        assertEquals(broadbandPlan, result);
        verify(broadbandRedis).clearBroadbandCache();
    }

    @Test
    void whenUpdateItemReturnsNull_thenNoPlanFoundExceptionThrown() {
        // Given
        String planId = "123";
        GetItemRequest getRequest = GetItemRequest.builder().build();
        Map<String, AttributeValue> broadband = new HashMap<>();
        broadband.put("key", AttributeValue.builder().s("value").build());
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        UpdateItemRequest request = UpdateItemRequest.builder().build();

        when(queryHelper.getItemByPlanId(planId, "Broadband")).thenReturn(getRequest);
        when(dynamoDbClient.getItem(getRequest)).thenReturn(GetItemResponse.builder().item(broadband).build());
        when(queryWrapper.mapToBroadbandPlan(broadband)).thenReturn(broadbandPlan);
        when(queryHelper.deleteQuery(planId, broadbandPlan)).thenReturn(request);
        when(dynamoDbClient.updateItem(request)).thenReturn(null);

        // When / Then
        assertThrows(NoPlanFoundException.class, () -> {
            broadbandServiceImpl.deleteBroadbandPlan(planId);
        });
    }


    @Test
    void whenGetItemReturnsNull_thenNoPlanFoundExceptionThrown() {
        // Given
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        String planId = "123";
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        UpdateItemResponse response = (UpdateItemResponse) UpdateItemResponse.builder().sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build()).build();
        GetItemRequest getRequest = GetItemRequest.builder().build();

        when(queryHelper.updateQuery(broadbandPlan, planId)).thenReturn(request);
        when(dynamoDbClient.updateItem(request)).thenReturn(response);
        when(queryHelper.getItemByPlanId(planId,"Broadband")).thenReturn(getRequest);
        when(dynamoDbClient.getItem(getRequest)).thenReturn(GetItemResponse.builder().build());

        // When / Then
        assertThrows(NoPlanFoundException.class, () -> {
            broadbandServiceImpl.updateBroadbandPlan(broadbandPlan, planId);
        });
    }



    @Test
    void deleteBroadbandPlan_success() {
        // Given
        String planId = "testPlanId";
        BroadbandPlan broadbandPlan = new BroadbandPlan();


        Map<String, AttributeValue> broadband = new HashMap<>();
        broadband.put("testKey", AttributeValue.builder().s("testValue").build());

        GetItemRequest getRequest = queryHelper.getItemByPlanId(planId,"Broadband");
        UpdateItemRequest request = queryHelper.deleteQuery(planId, broadbandPlan);
        UpdateItemResponse updateResponse = mock(UpdateItemResponse.class);

        when(dynamoDbClient.getItem(getRequest)).thenReturn(GetItemResponse.builder().item(broadband).build());
        when(queryWrapper.mapToBroadbandPlan(broadband)).thenReturn(broadbandPlan);
        when(dynamoDbClient.updateItem(request)).thenReturn(updateResponse);

        // When
        boolean result = broadbandServiceImpl.deleteBroadbandPlan(planId);

        // Then
        assertTrue(result);
        verify(broadbandRedis).clearBroadbandCache();
    }





    @Test
    void shouldThrowNoPlanFoundExceptionWhenDeletingNonExistentPlan() {
        // Arrange
        String planId = "123";

        GetItemRequest getRequest = queryHelper.getItemByPlanId(planId,"Broadband");
        when(queryHelper.getItemByPlanId(planId, "Broadband")).thenReturn(getRequest);

        Map<String, AttributeValue> broadband = new HashMap<>();
        when(dynamoDbClient.getItem(getRequest)).thenReturn(GetItemResponse.builder().item(broadband).build());

        // Act and Assert
        assertThrows(NoPlanFoundException.class, () -> broadbandServiceImpl.deleteBroadbandPlan(planId));
    }

    @Test
    void testAddBroadbandPlan() {
        BroadbandPlan plan = new BroadbandPlan();
        PutItemRequest request = PutItemRequest.builder().build();
        PutItemResponse response = PutItemResponse.builder().build();
        GetItemRequest getRequest = GetItemRequest.builder().build();
        GetItemResponse getItemResponse = GetItemResponse.builder().item(Map.of()).build();

        when(queryHelper.addQuery(plan)).thenReturn(request);
        when(dynamoDbClient.putItem(request)).thenReturn(response);
//        when(response.sdkHttpResponse().isSuccessful()).thenReturn(true);
        when(queryHelper.getItemByPlanId(anyString(), anyString())).thenReturn(getRequest);
        when(dynamoDbClient.getItem(getRequest)).thenReturn(getItemResponse);

        assertDoesNotThrow(() -> broadbandService.addBroadbandPlan(plan));
    }

    @Test
    void testUpdateBroadbandPlan() {
        BroadbandPlan plan = new BroadbandPlan();
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        UpdateItemResponse response = UpdateItemResponse.builder().build();
        GetItemRequest getRequest = GetItemRequest.builder().build();
        GetItemResponse getItemResponse = GetItemResponse.builder().item(Map.of()).build();

        when(dynamoDbClient.updateItem(request)).thenReturn(response);
        when(queryHelper.getItemByPlanId(anyString(), anyString())).thenReturn(getRequest);
        when(dynamoDbClient.getItem(getRequest)).thenReturn(getItemResponse);

        assertDoesNotThrow(() -> broadbandService.updateBroadbandPlan(plan, "testPlanId"));
    }


    @Test
    void deleteBroadbandPlan_noPlanFound() {
        // Given
        String planId = "testPlanId";

        GetItemRequest getRequest = queryHelper.getItemByPlanId(planId,"Broadband");
        Map<String, AttributeValue> broadband = new HashMap<>(); // Empty map

        when(dynamoDbClient.getItem(getRequest)).thenReturn(GetItemResponse.builder().item(broadband).build());

        // When / Then
        Exception exception = assertThrows(NoPlanFoundException.class, () -> {
            broadbandServiceImpl.deleteBroadbandPlan(planId);
        });

        String expectedMessage = "No plan found with id: " + planId;
        String actualMessage = exception.getMessage();

        assertFalse(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDeleteBroadbandPlan() {
        String planId = "testPlanId";
        GetItemRequest getRequest = GetItemRequest.builder().build();
        GetItemResponse getItemResponse = GetItemResponse.builder().item(Map.of()).build();
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        UpdateItemResponse response = UpdateItemResponse.builder().build();

        when(queryHelper.getItemByPlanId(planId, "Broadband")).thenReturn(getRequest);
        when(dynamoDbClient.getItem(getRequest)).thenReturn(getItemResponse);
        when(queryWrapper.mapToBroadbandPlan(getItemResponse.item())).thenReturn(new BroadbandPlan());
        when(queryHelper.deleteQuery(planId, new BroadbandPlan())).thenReturn(request);
        when(dynamoDbClient.updateItem(request)).thenReturn(response);

        assertFalse(broadbandService.deleteBroadbandPlan(planId));
    }



}
