package com.excitel.dynamodbqueryhelp;

import com.excitel.model.BroadbandPlan;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

import static com.excitel.constants.AppConstants.*;

/**
 * Helper class for DynamoDB queries related to broadband plans.
 */
@Component
public class QueryHelper {

    /**
     * Constructs a GetItemRequest to retrieve a broadband plan by plan ID and plan type.
     *
     * @param planId   The ID of the plan
     * @param planType The type of the plan
     * @return GetItemRequest for retrieving the plan
     */
    //    @Cacheable("plan-table")
    public GetItemRequest getItemByPlanId(String planId, String planType){
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(PLAN_TYPE.getValue(), AttributeValue.builder().s(planType).build());
        key.put(PLAN_ID.getValue(), AttributeValue.builder().s(planId).build());

        return GetItemRequest.builder()
                .tableName(TABLE_NAME.getValue())
                .key(key)
                .build();
    }
    /**
     * Constructs a PutItemRequest to add a new broadband plan to the database.
     *
     * @param broadbandPlan The broadband plan object to be added
     * @return PutItemRequest for adding the plan
     */
    public PutItemRequest addQuery(BroadbandPlan broadbandPlan){
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(PLAN_ID.getValue(), AttributeValue.builder().s(broadbandPlan.getPlanId()).build());
        item.put(PLAN_TYPE.getValue(), AttributeValue.builder().s(!Objects.isNull(broadbandPlan.getPlanType())?broadbandPlan.getPlanType():"Broadband").build());
        item.put("Price",AttributeValue.builder().s(!Objects.isNull(broadbandPlan.getPrice())? broadbandPlan.getPrice():"null").build());
        item.put("Validity",AttributeValue.builder().s(!Objects.isNull(broadbandPlan.getValidity())?broadbandPlan.getValidity():"null").build());
        List<AttributeValue> ottAttributes = broadbandPlan.getOtt() != null ?
                broadbandPlan.getOtt().stream()
                        .map(s -> AttributeValue.builder().s(s).build())
                        .toList() :
                Collections.emptyList();
        item.put("OTT", AttributeValue.builder().l(ottAttributes).build());
        item.put("TotalData",AttributeValue.builder().s(!Objects.isNull(broadbandPlan.getData())? broadbandPlan.getData() : "null").build());
        item.put("Speed", AttributeValue.builder().s(!Objects.isNull(broadbandPlan.getSpeed()) ? broadbandPlan.getSpeed() : "null").build());
        item.put(ACTIVE.getValue(), AttributeValue.builder().s(!Objects.isNull(broadbandPlan.getActive()) ? broadbandPlan.getActive() : "True").build());


        return PutItemRequest.builder()
                .tableName(TABLE_NAME.getValue())
                .conditionExpression("attribute_not_exists(PlanType) AND attribute_not_exists(PlanID)")
                .item(item)
                .build();
    }

    /**
     * Constructs an UpdateItemRequest to update an existing broadband plan in the database.
     *
     * @param broadbandPlan The updated broadband plan object
     * @param planId        The ID of the plan to be updated
     * @return UpdateItemRequest for updating the plan
     */
    public UpdateItemRequest updateQuery(BroadbandPlan broadbandPlan, String planId){

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(PLAN_TYPE.getValue(), AttributeValue.builder().s(broadbandPlan.getPlanType()).build());
        key.put(PLAN_ID.getValue(), AttributeValue.builder().s(planId).build());

        Map<String, AttributeValueUpdate> item = new HashMap<>();
        // Update attributes based on broadbandPlan object
        item.put("Price", AttributeValueUpdate.builder().value(AttributeValue.builder().s(broadbandPlan.getPrice()).build()).build());
        item.put("Validity", AttributeValueUpdate.builder().value(AttributeValue.builder().s(broadbandPlan.getValidity()).build()).build());
        List<AttributeValue> ottAttributes = broadbandPlan.getOtt() != null ?
                broadbandPlan.getOtt().stream()
                        .map(s -> AttributeValue.builder().s(s).build())
                        .toList():
                Collections.emptyList();
        item.put("OTT", AttributeValueUpdate.builder().value(AttributeValue.builder().l(ottAttributes).build()).build());
        item.put("TotalData", AttributeValueUpdate.builder().value(AttributeValue.builder().s(broadbandPlan.getData()).build()).build());
        item.put("Speed", AttributeValueUpdate.builder().value(AttributeValue.builder().s(broadbandPlan.getSpeed()).build()).build());
        item.put(ACTIVE.getValue(), AttributeValueUpdate.builder().value(AttributeValue.builder().s(broadbandPlan.getActive()).build()).build());
        // Update attributes based on broadbandPlan object

        return UpdateItemRequest.builder()
                .tableName(TABLE_NAME.getValue())
                .key(key)
                .attributeUpdates(item)
                .build();
    }

    /**
     * Constructs an UpdateItemRequest to mark a broadband plan as inactive in the database.
     *
     * @param planId        The ID of the plan to be marked as inactive
     * @param broadbandPlan The broadband plan object for reference
     * @return UpdateItemRequest for marking the plan as inactive
     */
    public UpdateItemRequest deleteQuery(String planId, BroadbandPlan broadbandPlan){

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(PLAN_TYPE.getValue(), AttributeValue.builder().s(broadbandPlan.getPlanType()).build());
        key.put(PLAN_ID.getValue(), AttributeValue.builder().s(planId).build());

        Map<String, AttributeValueUpdate> item = new HashMap<>();

        item.put(ACTIVE.getValue(), AttributeValueUpdate.builder().value(AttributeValue.builder().s("False").build()).build());
        // Update to mark the plan as inactive
        return UpdateItemRequest.builder()
                .tableName(TABLE_NAME.getValue())
                .key(key)
                .attributeUpdates(item)
                .build();
    }
}