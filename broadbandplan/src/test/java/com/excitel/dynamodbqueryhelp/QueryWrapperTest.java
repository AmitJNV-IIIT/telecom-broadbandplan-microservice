package com.excitel.dynamodbqueryhelp;

import com.excitel.model.BroadbandConnection;
import com.excitel.model.BroadbandPlan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class QueryWrapperTest {

    @InjectMocks
    private QueryWrapper queryWrapper;

    @Test
    public void testMapToBroadbandPlan() {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("PlanType", AttributeValue.builder().s("testPlanType").build());
        item.put("PlanID", AttributeValue.builder().s("testPlanId").build());
        item.put("Active", AttributeValue.builder().s("True").build());
        item.put("Price", AttributeValue.builder().s("100").build());
        item.put("Category", AttributeValue.builder().s("testCategory").build());
        item.put("Validity", AttributeValue.builder().s("30 days").build());
        item.put("OTT", AttributeValue.builder().l(AttributeValue.builder().s("Netflix").build(), AttributeValue.builder().s("Prime Video").build()).build());
        item.put("VoiceLimit", AttributeValue.builder().s("Unlimited").build());
        item.put("SMS", AttributeValue.builder().s("1000").build());
        item.put("TotalData", AttributeValue.builder().s("1 TB").build());
        item.put("CouponIDs", AttributeValue.builder().l(AttributeValue.builder().s("COUPON123").build()).build());
        item.put("Limit", AttributeValue.builder().s("Unlimited").build());
        item.put("Speed", AttributeValue.builder().s("100 Mbps").build());

        BroadbandPlan broadbandPlan = queryWrapper.mapToBroadbandPlan(item);

        assertEquals("testPlanType", broadbandPlan.getPlanType());
        assertEquals("testPlanId", broadbandPlan.getPlanId());
        assertEquals("True", broadbandPlan.getActive());
        assertEquals("100", broadbandPlan.getPrice());
        assertEquals("testCategory", broadbandPlan.getCategory());
        assertEquals("30 days", broadbandPlan.getValidity());
        assertEquals(List.of("Netflix", "Prime Video"), broadbandPlan.getOtt());
        assertEquals("Unlimited", broadbandPlan.getVoiceLimit());
        assertEquals("1000", broadbandPlan.getSms());
        assertEquals("1 TB", broadbandPlan.getData());
        assertEquals(List.of("COUPON123"), broadbandPlan.getCouponIds());
        assertEquals("Unlimited", broadbandPlan.getLimit());
        assertEquals("100 Mbps", broadbandPlan.getSpeed());
    }

    @Test
    public void testMapToBroadbandPlanWithNulls() {
        Map<String, AttributeValue> item = new HashMap<>();

        BroadbandPlan broadbandPlan = queryWrapper.mapToBroadbandPlan(item);

        assertNull(broadbandPlan.getPlanType());
        assertNull(broadbandPlan.getPlanId());
        assertNull(broadbandPlan.getActive());
        assertNull(broadbandPlan.getPrice());
        assertNull(broadbandPlan.getCategory());
        assertNull(broadbandPlan.getValidity());
        assertNull(broadbandPlan.getOtt());
        assertNull(broadbandPlan.getVoiceLimit());
        assertNull(broadbandPlan.getSms());
        assertNull(broadbandPlan.getData());
        assertNull(broadbandPlan.getCouponIds());
        assertNull(broadbandPlan.getLimit());
        assertNull(broadbandPlan.getSpeed());
    }

    @Test
    public void testMapToBroadbandConnection() {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("MobileNumber", AttributeValue.builder().s("1234567890").build());
        item.put("ConnectionStatus", AttributeValue.builder().s("Active").build());
        item.put("ConnectionID", AttributeValue.builder().s("CON123").build());
        item.put("CustomerName", AttributeValue.builder().s("John Doe").build());
        item.put("Address", AttributeValue.builder().s("123 Street").build());
        item.put("City", AttributeValue.builder().s("City").build());
        item.put("State", AttributeValue.builder().s("State").build());
        item.put("Country", AttributeValue.builder().s("Country").build());
        item.put("PINCode", AttributeValue.builder().s("123456").build());

        BroadbandConnection broadbandConnection = queryWrapper.mapToBroadbandConnection(item);

        assertEquals("1234567890", broadbandConnection.getMobileNumber());
        assertEquals("Active", broadbandConnection.getStatus());
        assertEquals("CON123", broadbandConnection.getConnectionId());
        assertEquals("John Doe", broadbandConnection.getName());
        assertEquals("123 Street", broadbandConnection.getAddress());
        assertEquals("City", broadbandConnection.getCity());
        assertEquals("State", broadbandConnection.getState());
        assertEquals("Country", broadbandConnection.getCountry());
        assertEquals("123456", broadbandConnection.getPinCode());
    }

    @Test
    public void testMapToBroadbandConnectionWithNulls() {
        Map<String, AttributeValue> item = new HashMap<>();

        BroadbandConnection broadbandConnection = queryWrapper.mapToBroadbandConnection(item);

        assertNull(broadbandConnection.getMobileNumber());
        assertNull(broadbandConnection.getStatus());
        assertNull(broadbandConnection.getConnectionId());
        assertNull(broadbandConnection.getName());
        assertNull(broadbandConnection.getAddress());
        assertNull(broadbandConnection.getCity());
        assertNull(broadbandConnection.getState());
        assertNull(broadbandConnection.getCountry());
        assertNull(broadbandConnection.getPinCode());
    }
}
