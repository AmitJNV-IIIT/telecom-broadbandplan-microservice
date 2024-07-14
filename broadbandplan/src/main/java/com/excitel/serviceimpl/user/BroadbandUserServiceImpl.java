package com.excitel.serviceimpl.user;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.excitel.dto.RequestDTO;
import com.excitel.dynamodbqueryhelp.QueryConnectionHelper;
import com.excitel.dynamodbqueryhelp.QueryWrapper;
import com.excitel.exception.custom.DatabaseConnectionException;
import com.excitel.exception.custom.DuplicatePhoneNumberException;
import com.excitel.exception.custom.NoPlanFoundException;
import com.excitel.model.BroadbandConnection;
import com.excitel.model.BroadbandPlan;
import com.excitel.redishelper.BroadbandRedis;
import com.excitel.service.user.BroadbandUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.web.bind.annotation.RequestBody;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Service
public class BroadbandUserServiceImpl implements BroadbandUserService {

    private static final Logger log = LoggerFactory.getLogger(BroadbandUserServiceImpl.class);
    @Autowired //NOSONAR
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired //NOSONAR
    private QueryConnectionHelper queryConnectionHelper;

    @Autowired //NOSONAR
    private QueryWrapper queryWrapper;

    @Autowired //NOSONAR
    DynamoDbClient dynamoDbClient;

    @Autowired //NOSONAR
    private BroadbandRedis broadbandRedis;

    /**
     * Retrieves broadband plans based on the provided parameters.
     *
     * @param params The request parameters.
     * @return A list of broadband plans matching the query.
     */
    @Override
    public List<BroadbandPlan> getBroadbandPlanWithQuery(RequestDTO params) {
        String active = params.getActive();
        String planId = params.getPlanId();
        String type = params.getType();
        String category = params.getCategory();
        String data = params.getData();
        String speed = params.getSpeed();
        Integer offset = params.getOffset();
        Integer limit = params.getLimit();

        List<BroadbandPlan> broadbandPlans1;
        String cacheKey = broadbandRedis.createRedisKey(params);
        broadbandPlans1 = broadbandRedis.getBroadbandPlansCache(cacheKey);
        if (broadbandPlans1 != null) {
            return broadbandPlans1;
        }

        List<BroadbandPlan> broadbandPlans = new ArrayList<>();
        RequestDTO requestDTO = new RequestDTO( active, planId,type, category, data, speed, offset, limit);
        QueryRequest queryRequest = buildQueryRequest(requestDTO);
        QueryResponse queryResponse1 = dynamoDbClient.query(queryRequest);
        for (Map<String, AttributeValue> item : queryResponse1.items()) {
            BroadbandPlan broadbandPlan = queryWrapper.mapToBroadbandPlan(item);
            broadbandPlans.add(broadbandPlan);
        }
        if (offset != null && limit != null) {
            broadbandPlans = broadbandPlans.subList(offset, Math.min(offset + limit, broadbandPlans.size()));
        }
        broadbandRedis.addBroadbandPlansCache(cacheKey, broadbandPlans);
        return broadbandPlans;
    }
    /**
     * Builds a query request based on the provided request parameters.
     *
     * @param response The request parameters.
     * @return A QueryRequest object representing the query.
     */
    public QueryRequest buildQueryRequest(RequestDTO response) {
        String type = response.getType();
        String active = response.getActive();
        String planId = response.getPlanId();
        String speed = response.getSpeed();
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        List<String> filterConditions = new ArrayList<>();
        QueryRequest.Builder queryRequestBuilder = QueryRequest.builder()
                .tableName("plan-table");

        // Handle filter conditions based on provided parameters
        if (planId != null) {
            expressionAttributeValues.put(":planId", AttributeValue.builder().s(planId).build());
            expressionAttributeValues.put(":planType", AttributeValue.builder().s(type).build());

            queryRequestBuilder.keyConditionExpression("PlanType = :planType AND PlanID = :planId");
            if (active != null) {
                expressionAttributeValues.put(":active", AttributeValue.builder().s(active).build());
                queryRequestBuilder.filterExpression("Active = :active")
                        .expressionAttributeValues(expressionAttributeValues);
            }
            queryRequestBuilder.expressionAttributeValues(expressionAttributeValues);
        } else { // Use filter expression for other attributes
            if (type != null) {
                expressionAttributeValues.put(":type", AttributeValue.builder().s(type).build());
                queryRequestBuilder.keyConditionExpression("PlanType = :type");
            }
            if (active != null) {
                expressionAttributeValues.put(":active", AttributeValue.builder().s(active).build());
                filterConditions.add("Active = :active");
            }
            if (speed != null) {
                expressionAttributeValues.put(":speed", AttributeValue.builder().s(speed).build());
                filterConditions.add("Speed = :speed");
            }

            if (!filterConditions.isEmpty()) {
                String combinedFilterExpression = String.join(" AND ", filterConditions);

                queryRequestBuilder = queryRequestBuilder.filterExpression(combinedFilterExpression)
                        .expressionAttributeValues(expressionAttributeValues);
            }else{
                queryRequestBuilder=queryRequestBuilder.expressionAttributeValues(expressionAttributeValues);
            }
        }
        return queryRequestBuilder.build();
    }
    /**
     * Creates a new broadband connection.
     *
     * @param broadbandConnection The broadband connection object to be created.
     * @param mobileNumber        The mobile number associated with the connection.
     * @return The created BroadbandConnection object.
     */

    @Override
    public BroadbandConnection createBroadbandConnection(@Valid @RequestBody BroadbandConnection broadbandConnection, String mobileNumber) {
        try {
            PutItemRequest request = queryConnectionHelper.addConnectionQuery(broadbandConnection, mobileNumber);
            PutItemResponse response = dynamoDbClient.putItem(request);
            if (response.sdkHttpResponse().isSuccessful()) {
                GetItemRequest getRequest = queryConnectionHelper.getItemByMobileNumber(mobileNumber, "Active");
                Map<String, AttributeValue> postedItem = dynamoDbClient.getItem(getRequest).item();
                broadbandRedis.addConnectionDetailCache(mobileNumber,"Active",queryWrapper.mapToBroadbandConnection(postedItem));
                return queryWrapper.mapToBroadbandConnection(postedItem);
            } else {
                log.error("Connection already exists - ");
                throw new DuplicatePhoneNumberException("Connection already present");
            }
        } catch (DuplicatePhoneNumberException e) {
            throw e;
        } catch (SdkException e) {
            if (e.getMessage().contains("The conditional request failed")) {
                log.error("Connection already exists - {}",e.getMessage());
                throw new DuplicatePhoneNumberException("Connection already exists - "+ e.getMessage());
            } else {
                log.error("Error Connecting to Database - {}",e.getMessage());
                throw new DatabaseConnectionException("Error Connecting to Database - " + e.getMessage());
            }
        }
    }
    /**
     * Retrieves connection details for a user based on the mobile number and status.
     *
     * @param mobileNumber The mobile number of the user.
     * @param status       The status of the connection.
     * @return The BroadbandConnection object representing the user's connection details.
     */

    public BroadbandConnection getConnectionDetailsForUser(String mobileNumber, String status) {
        try {
            BroadbandConnection connectionCache = broadbandRedis.getConnectionDetailsCache(mobileNumber,status);
            if(connectionCache!=null) return connectionCache;

            GetItemRequest request = queryConnectionHelper.getItemByMobileNumber(mobileNumber, status);
            Map<String, AttributeValue> getItem = dynamoDbClient.getItem(request).item();

            if (getItem != null && !getItem.isEmpty()) {
                broadbandRedis.addConnectionDetailCache(mobileNumber,status,queryWrapper.mapToBroadbandConnection(getItem));
                return queryWrapper.mapToBroadbandConnection(getItem);
            } else {
                log.error("No Connection found with mobile Number: {}", mobileNumber);
                throw new NoPlanFoundException("No Connection found with mobile Number: " + mobileNumber);
            }
        }catch (SdkException e) {
            log.error("Error Connecting to Database");
            throw new DatabaseConnectionException("Error Connecting to Database");
        }

    }
}