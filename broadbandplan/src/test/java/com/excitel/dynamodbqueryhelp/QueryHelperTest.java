package com.excitel.dynamodbqueryhelp;
import com.excitel.model.BroadbandPlan;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.*;
import static com.excitel.constants.AppConstants.ACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;



@ExtendWith(MockitoExtension.class)
public class QueryHelperTest {
    @InjectMocks
    private QueryHelper queryHelper;

    @Mock
    private BroadbandPlan mockBroadbandPlan;

    @Test
    void testGetItemByPlanId() {
        // Arrange
        QueryHelper queryHelper = new QueryHelper();
        String planId = "1234567890";
        String planType = "Broadband";

        // Act
        GetItemRequest getItemRequest = queryHelper.getItemByPlanId(planId, planType);

        // Assert
        assertEquals("plan-table", getItemRequest.tableName());
        assertEquals(2, getItemRequest.key().size());
        assertEquals(planType, getItemRequest.key().get("PlanType").s());
        assertEquals(planId, getItemRequest.key().get("PlanID").s());
    }


    @Test
    void testUpdateQuery() {
        // Arrange
        QueryHelper queryHelper = new QueryHelper();
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        broadbandPlan.setPlanId("1234567890");
        broadbandPlan.setPlanType("Broadband");
        broadbandPlan.setPrice("$60");
        broadbandPlan.setValidity("60 days");
        broadbandPlan.setOtt(Arrays.asList("Netflix", "Amazon Prime"));
        broadbandPlan.setData("Unlimited");
        broadbandPlan.setSpeed("200 Mbps");
        broadbandPlan.setActive("True");

        // Act
        UpdateItemRequest updateItemRequest = queryHelper.updateQuery(broadbandPlan, "1234567890");

        // Assert
        assertEquals("plan-table", updateItemRequest.tableName());
        assertEquals(2, updateItemRequest.key().size());
        assertEquals("Broadband", updateItemRequest.key().get("PlanType").s());
        assertEquals("1234567890", updateItemRequest.key().get("PlanID").s());
        assertEquals(6, updateItemRequest.attributeUpdates().size());
        assertEquals("$60", updateItemRequest.attributeUpdates().get("Price").value().s());
        assertEquals("60 days", updateItemRequest.attributeUpdates().get("Validity").value().s());
        assertEquals(Arrays.asList("Netflix", "Amazon Prime"), updateItemRequest.attributeUpdates().get("OTT").value().l().stream().map(AttributeValue::s).toList());
        assertEquals("Unlimited", updateItemRequest.attributeUpdates().get("TotalData").value().s());
        assertEquals("200 Mbps", updateItemRequest.attributeUpdates().get("Speed").value().s());
        assertEquals("True", updateItemRequest.attributeUpdates().get("Active").value().s());
    }

    @Test
    void testDeleteQuery() {
        // Arrange
        QueryHelper queryHelper = new QueryHelper();
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        broadbandPlan.setPlanId("1234567890");
        broadbandPlan.setPlanType("Broadband");

        // Act
        UpdateItemRequest updateItemRequest = queryHelper.deleteQuery("1234567890", broadbandPlan);

        // Assert
        assertEquals("plan-table", updateItemRequest.tableName());
        assertEquals(2, updateItemRequest.key().size());
        assertEquals("Broadband", updateItemRequest.key().get("PlanType").s());
        assertEquals("1234567890", updateItemRequest.key().get("PlanID").s());
        assertEquals(1, updateItemRequest.attributeUpdates().size());
        assertEquals("False", updateItemRequest.attributeUpdates().get("Active").value().s());
    }

    @Test
    public void testAddQuery() {
        // given
        Mockito.when(mockBroadbandPlan.getPlanId()).thenReturn("123");
        Mockito.when(mockBroadbandPlan.getPlanType()).thenReturn("Broadband");
        Mockito.when(mockBroadbandPlan.getPrice()).thenReturn(null);
        Mockito.when(mockBroadbandPlan.getValidity()).thenReturn(null);
        Mockito.when(mockBroadbandPlan.getOtt()).thenReturn(Arrays.asList("OTT1", "OTT2"));
        Mockito.when(mockBroadbandPlan.getData()).thenReturn(null);
        Mockito.when(mockBroadbandPlan.getSpeed()).thenReturn(null);
        Mockito.when(mockBroadbandPlan.getActive()).thenReturn(null);

        // when
        PutItemRequest result = queryHelper.addQuery(mockBroadbandPlan);

        // then
        Assertions.assertEquals("null", result.item().get("Price").s());
        Assertions.assertEquals("null", result.item().get("Validity").s());
        Assertions.assertEquals("null", result.item().get("TotalData").s());
        Assertions.assertEquals("null", result.item().get("Speed").s());
        Assertions.assertEquals("True", result.item().get(ACTIVE.getValue()).s());
        Assertions.assertEquals(2, result.item().get("OTT").l().size());
    }
}