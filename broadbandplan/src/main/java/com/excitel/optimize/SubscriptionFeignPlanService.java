package com.excitel.optimize;

import com.excitel.dto.ErrorResponseDTO;
import com.excitel.model.BroadbandPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SubscriptionFeignPlanService {

    @Autowired //NOSONAR
    private DynamoDbClient dynamoDbClient;

    /**
     * Retrieves details of plans by their IDs.
     *
     * @param planIds  List of plan IDs
     * @param planType Plan type
     * @return SubscriptionResponseDTO containing mobile plans
     */
    public ErrorResponseDTO.SubscriptionResponseDTO getAllPlanByPlanIdList(List<String> planIds, String planType) {
        // Construct keys to get from DynamoDB
        List<Map<String, AttributeValue>> keysToGet = new ArrayList<>();
        for (String planId : planIds) {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("PlanType", AttributeValue.builder().s(planType).build());
            key.put("PlanID", AttributeValue.builder().s(planId).build());
            keysToGet.add(key);
        }
        // Create a batch request to fetch items
        KeysAndAttributes keysAndAttributes = KeysAndAttributes.builder()
                .projectionExpression("PlanID, Validity, Price, OTT, VoiceLimit, SMS, Speed,TotalData")
                .keys(keysToGet)
                .build();

        BatchGetItemRequest batchGetItemRequest = BatchGetItemRequest.builder()
                .requestItems(Map.of("plan-table", keysAndAttributes))
                .build();
        // Execute the batch request and map the response to BroadbandPlan objects
        BatchGetItemResponse batchGetItemResponse = dynamoDbClient.batchGetItem(batchGetItemRequest);
        Map<String, BroadbandPlan> plansMap = batchGetItemResponse.responses().get("plan-table").stream()
                .map(this::mapToPlanDetail)
                .collect(Collectors.toMap(BroadbandPlan::getPlanId, Function.identity()));

        return ErrorResponseDTO.SubscriptionResponseDTO.builder().status(HttpStatus.OK).mobilePlans(plansMap).build();
    }
    /**
     * Maps a DynamoDB item to a BroadbandPlan object.
     *
     * @param item DynamoDB item
     * @return BroadbandPlan object
     */
    public BroadbandPlan mapToPlanDetail(Map<String, AttributeValue> item) {
        String planID = getStringOrNull(item, "PlanID");
        String planType = getStringOrNull(item, "PlanType");
        Double price = getDoubleOrNull(item, "Price");
        String category = getStringOrNull(item, "Category");
        Integer validity = getIntegerOrNull(item, "Validity");
        List<String> ott = getListOrNull(item, "OTT");
        String voiceLimit = getStringOrNull(item, "VoiceLimit");
        String sms = getStringOrNull(item, "SMS");
        String data = getStringOrNull(item, "TotalData");
        List<String> couponIds = getListOrNull(item, "CouponIDs");
        String limit = getStringOrNull(item, "Limit");
        Float speed = getFloatOrNull(item, "Speed");
        Boolean active = getBooleanOrNull(item, "Active");

        return BroadbandPlan.builder()
                .planId(planID)
                .planType(planType)
                .price(String.valueOf(price))
                .category(category)
                .validity(String.valueOf(validity))
                .ott(ott)
                .voiceLimit(voiceLimit)
                .sms(sms)
                .data(data)
                .couponIds(couponIds)
                .limit(limit)
                .speed(String.valueOf(speed))
                .active(String.valueOf(active))
                .build();
    }
    // Helper methods for handling attribute values

    /**
     * Retrieves a string attribute from a map, or returns null if not found.
     */
    public String getStringOrNull(Map<String, AttributeValue> item, String key) {
        if (item != null && item.containsKey(key)) {
            AttributeValue attributeValue = item.get(key);
            if (attributeValue != null && attributeValue.s() != null) {
                return attributeValue.s().trim();
            }
        }
        return null;
    }

    /**
     * Retrieves a double attribute from a map, or returns null if not found.
     */
    public Double getDoubleOrNull(Map<String, AttributeValue> item, String key) {
        if (item != null && item.containsKey(key)) {
            AttributeValue attributeValue = item.get(key);
            if (attributeValue != null && attributeValue.s() != null) {
                return Double.parseDouble(attributeValue.s().trim());
            }
        }
        return null;
    }

    /**
     * Retrieves an integer attribute from a map, or returns null if not found.
     */
    public Integer getIntegerOrNull(Map<String, AttributeValue> item, String key) {
        if (item != null && item.containsKey(key)) {
            AttributeValue attributeValue = item.get(key);
            if (attributeValue != null && attributeValue.s() != null) {
                return Integer.parseInt(attributeValue.s().trim());
            }
        }
        return null;
    }
    /**
     * Retrieves a list attribute from a map, or returns an empty list if not found.
     */
    public List<String> getListOrNull(Map<String, AttributeValue> item, String key) {
        if (item != null && item.containsKey(key)) {
            AttributeValue attributeValue = item.get(key);
            if (attributeValue != null && attributeValue.l() != null) {
                return attributeValue.l().stream().map(AttributeValue::s).toList();
            }
        }
        return  null;//NOSONAR
    }
    /**
     * Retrieves a list attribute from a map, or returns null if not found.
     */
    public Float getFloatOrNull(Map<String, AttributeValue> item, String key) {
        if (item != null && item.containsKey(key)) {
            AttributeValue attributeValue = item.get(key);
            if (attributeValue != null && attributeValue.s() != null) {
                return Float.parseFloat(attributeValue.s().trim());
            }
        }
        return null;
    }
    /**
     * Retrieves a list attribute from a map, or returns an empty list if not found.
     */
    public Boolean getBooleanOrNull(Map<String, AttributeValue> item, String key) {
        if (item != null && item.containsKey(key)) {
            AttributeValue attributeValue = item.get(key);
            if (attributeValue != null && attributeValue.s() != null) {
                return Boolean.valueOf(attributeValue.s().trim());
            }
        }
        return null;//NOSONAR
    }

}
