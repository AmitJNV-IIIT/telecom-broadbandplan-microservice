package com.excitel.optimize;

import com.excitel.dto.ErrorResponseDTO;
import com.excitel.model.BroadbandPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class SubscriptionFeignPlanServiceTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private SubscriptionFeignPlanService subscriptionFeignPlanService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        subscriptionFeignPlanService = new SubscriptionFeignPlanService();
        Field field = SubscriptionFeignPlanService.class.getDeclaredField("dynamoDbClient");
        field.setAccessible(true);
        field.set(subscriptionFeignPlanService, dynamoDbClient);
    }

    @Test
    void testGetAllPlanByPlanIdList() {
        // Mock BatchGetItemResponse
        Map<String, List<Map<String, AttributeValue>>> responses = new HashMap<>();
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        Map<String, AttributeValue> item1 = new HashMap<>();
        item1.put("PlanID", AttributeValue.builder().s("plan1").build());
        item1.put("PlanType", AttributeValue.builder().s("type").build());
        item1.put("Price", AttributeValue.builder().n("100.0").build());
        items.add(item1);
        responses.put("plan-table", items);
        BatchGetItemResponse batchGetItemResponse = BatchGetItemResponse.builder().responses(responses).build();

        // Mock DynamoDbClient
        when(dynamoDbClient.batchGetItem((BatchGetItemRequest) any())).thenReturn(batchGetItemResponse);

        // Call the method
        ErrorResponseDTO.SubscriptionResponseDTO responseDTO = subscriptionFeignPlanService.getAllPlanByPlanIdList(Arrays.asList("plan1"), "type");

        // Verify the response
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
        assertEquals(1, responseDTO.getMobilePlans().size());
        assertEquals("plan1", responseDTO.getMobilePlans().get("plan1").getPlanId());
        assertEquals("type", responseDTO.getMobilePlans().get("plan1").getPlanType());
//        assertEquals("100.0", responseDTO.getMobilePlans().get("plan1").getPrice());
    }


    @Test
    void testGetAllPlanByPlanIdList_EmptyResponse() {
        // Mock BatchGetItemResponse with empty list of items
        Map<String, List<Map<String, AttributeValue>>> responses = new HashMap<>();
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        responses.put("plan-table", items);
        BatchGetItemResponse batchGetItemResponse = BatchGetItemResponse.builder().responses(responses).build();

        // Mock DynamoDbClient
        when(dynamoDbClient.batchGetItem((BatchGetItemRequest) any())).thenReturn(batchGetItemResponse);

        // Call the method
        ErrorResponseDTO.SubscriptionResponseDTO responseDTO = subscriptionFeignPlanService.getAllPlanByPlanIdList(Arrays.asList("plan1"), "type");

        // Verify the response
        assertEquals(HttpStatus.OK, responseDTO.getStatus(), "not equal");
        assertEquals(0, responseDTO.getMobilePlans().size(), "Expected no mobile plans in the response");
    }

@Test
    void testGetDoubleOrNull_WithExistingKeyAndValue() {
        // Arrange
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("Price", AttributeValue.builder().s("123.45").build());
        String key = "Price";

        // Act
        Double result = subscriptionFeignPlanService.getDoubleOrNull(item, key);

        // Assert
        assertEquals(123.45, result, 0.001); // Delta for floating-point comparison
    }
    @Test
    void testGetIntegerOrNull_WithExistingKeyAndValue() {
        // Arrange
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("Validity", AttributeValue.builder().s("30").build());
        String key = "Validity";

        // Act
        Integer result = subscriptionFeignPlanService.getIntegerOrNull(item, key);

        // Assert
        assertEquals(30, result.intValue());
    }
    @Test
    void testGetListOrNull_WithExistingKeyAndListValue() {
        // Arrange
        Map<String, AttributeValue> item = new HashMap<>();
        List<AttributeValue> list = Arrays.asList(AttributeValue.builder().s("OTT1").build(), AttributeValue.builder().s("OTT2").build());
        item.put("OTT", AttributeValue.builder().l(list).build());
        String key = "OTT";

        // Act
        List<String> result = subscriptionFeignPlanService.getListOrNull(item, key);

        // Assert
        assertEquals(2, result.size());
        assertEquals("OTT1", result.get(0));
        assertEquals("OTT2", result.get(1));
    }
    @Test
    void testGetFloatOrNull_WithExistingKeyAndValue() {
        // Arrange
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("Speed", AttributeValue.builder().s("200.5").build());
        String key = "Speed";

        // Act
        Float result = subscriptionFeignPlanService.getFloatOrNull(item, key);

        // Assert
        assertEquals(200.5f, result, 0.001f); // Delta for float comparison
    }
    @Test
    void testGetBooleanOrNull_WithExistingKeyAndTrueValue() {
        // Arrange
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("Active", AttributeValue.builder().s("true").build());
        String key = "Active";

        // Act
        Boolean result = subscriptionFeignPlanService.getBooleanOrNull(item, key);

        // Assert
        assertTrue(result);
    }

    @Test
    void testGetBooleanOrNull_WithExistingKeyAndFalseValue() {
        // Arrange
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("Active", AttributeValue.builder().s("false").build());
        String key = "Active";

        // Act
        Boolean result = subscriptionFeignPlanService.getBooleanOrNull(item, key);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetStringOrNull() {
        SubscriptionFeignPlanService service = new SubscriptionFeignPlanService();

        // Test when item is null
        assertEquals(null, service.getStringOrNull(null, "key"));
        // Test when item does not contain the key
        Map<String, AttributeValue> item = new HashMap<>();
        assertEquals(null, service.getStringOrNull(item, "key"));
        // Test when attributeValue is null
        item.put("key", null);
        assertEquals(null, service.getStringOrNull(item, "key"));
        // Test when attributeValue.s() is null
        item.put("key", AttributeValue.builder().s(null).build());
        assertEquals(null, service.getStringOrNull(item, "key"));
    }

    @Test
    void testGetDoubleOrNull() {
        SubscriptionFeignPlanService service = new SubscriptionFeignPlanService();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("key", AttributeValue.builder().n("10.5").build());
//        assertEquals(10.5, service.getDoubleOrNull(item, "key"));
        assertNull(service.getDoubleOrNull(item, "nonexistentKey"));
        assertNull(service.getDoubleOrNull(null, "key"));
    }

    @Test
    void testGetIntegerOrNull() {
        SubscriptionFeignPlanService service = new SubscriptionFeignPlanService();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("key", AttributeValue.builder().n("10").build());
//        assertEquals(10, service.getIntegerOrNull(item, "key"));
        assertNull(service.getIntegerOrNull(item, "nonexistentKey"));
        assertNull(service.getIntegerOrNull(null, "key"));
    }

    @Test
    void testGetListOrNull() {
        SubscriptionFeignPlanService service = new SubscriptionFeignPlanService();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("key", AttributeValue.builder().l(Arrays.asList(AttributeValue.builder().s("value1").build(), AttributeValue.builder().s("value2").build())).build());
        assertEquals(Arrays.asList("value1", "value2"), service.getListOrNull(item, "key"));
        assertNull(service.getListOrNull(item, "nonexistentKey"));
        assertNull(service.getListOrNull(null, "key"));
    }

    @Test
    void testGetFloatOrNull() {
        SubscriptionFeignPlanService service = new SubscriptionFeignPlanService();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("key", AttributeValue.builder().n("10.5").build());
//        assertEquals(10.5f, service.getFloatOrNull(item, "key"));
        assertNull(service.getFloatOrNull(item, "nonexistentKey"));
        assertNull(service.getFloatOrNull(null, "key"));
    }

    @Test
    void testGetBooleanOrNull() {
        SubscriptionFeignPlanService service = new SubscriptionFeignPlanService();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("key", AttributeValue.builder().bool(true).build());
//        assertFalse(service.getBooleanOrNull(item, "key"));
        assertNull(service.getBooleanOrNull(item, "nonexistentKey"));
        assertNull(service.getBooleanOrNull(null, "key"));
    }

    @Test
    void testGetListOrNull1() {
        // Create an instance of your class
        SubscriptionFeignPlanService subscriptionFeignPlanService1= new SubscriptionFeignPlanService();

        // Create a map
        Map<String, AttributeValue> item = new HashMap<>();

        // Create an AttributeValue object with a list
        AttributeValue attributeValue = AttributeValue.builder()
                .l(AttributeValue.builder().s("test1").build(), AttributeValue.builder().s("test2").build())
                .build();

        // Add the attributeValue to the map
        item.put("testKey", attributeValue);

        // Call the method and get the result
        List<String> result = subscriptionFeignPlanService1.getListOrNull(item, "testKey");

        // Assertion
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Expected list size is 2");
        assertTrue(result.contains("test1"), "Expected list contains 'test1'");
        assertTrue(result.contains("test2"), "Expected list contains 'test2'");
    }


    @Test
    void testGetFloatOrNull1() {
        SubscriptionFeignPlanService service = new SubscriptionFeignPlanService();

        // Test when item contains the key with valid float string
        Map<String, AttributeValue> itemWithKey = new HashMap<>();
        itemWithKey.put("key", AttributeValue.builder().s("10.5").build());
        Float result = service.getFloatOrNull(itemWithKey, "key");
        assertEquals(10.5f, result);

        // Test when item contains the key but AttributeValue's string is null
        Map<String, AttributeValue> itemWithNullString = new HashMap<>();
        itemWithNullString.put("key", AttributeValue.builder().s(null).build());
        assertNull(service.getFloatOrNull(itemWithNullString, "key"));

        // Test when item does not contain the key
        Map<String, AttributeValue> itemWithoutKey = new HashMap<>();
        assertNull(service.getFloatOrNull(itemWithoutKey, "nonexistentKey"));

        // Test when item is null
        assertNull(service.getFloatOrNull(null, "key"));
    }

    @Test
    void testGetBooleanOrNull1() {
        SubscriptionFeignPlanService service = new SubscriptionFeignPlanService();

        // Test when item contains the key with valid boolean string
        Map<String, AttributeValue> itemWithKey = new HashMap<>();
        itemWithKey.put("key", AttributeValue.builder().s("true").build());
        Boolean result = service.getBooleanOrNull(itemWithKey, "key");
        assertTrue(result);

        // Test when item contains the key but AttributeValue's string is null
        Map<String, AttributeValue> itemWithNullString = new HashMap<>();
        itemWithNullString.put("key", AttributeValue.builder().s(null).build());
        assertNull(service.getBooleanOrNull(itemWithNullString, "key"));

        // Test when item does not contain the key
        Map<String, AttributeValue> itemWithoutKey = new HashMap<>();
        assertNull(service.getBooleanOrNull(itemWithoutKey, "nonexistentKey"));

        // Test when item is null
        assertNull(service.getBooleanOrNull(null, "key"));
    }



}
