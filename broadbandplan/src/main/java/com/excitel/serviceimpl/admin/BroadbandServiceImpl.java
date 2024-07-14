package com.excitel.serviceimpl.admin;

import com.excitel.dynamodbqueryhelp.QueryHelper;
import com.excitel.dynamodbqueryhelp.QueryWrapper;
import com.excitel.redishelper.BroadbandRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import com.excitel.exception.custom.DatabaseConnectionException;
import com.excitel.exception.custom.NoPlanFoundException;
import com.excitel.model.BroadbandPlan;
import com.excitel.service.admin.BroadbandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;

import static com.excitel.constants.AppConstants.NO_PLAN;

@Service
public class BroadbandServiceImpl implements BroadbandService {

    private static final Logger log = LoggerFactory.getLogger(BroadbandServiceImpl.class);
    @Autowired //NOSONAR
    private QueryHelper queryHelper;
    @Autowired //NOSONAR
    private QueryWrapper queryWrapper;
    @Autowired //NOSONAR
    private DynamoDbClient dynamoDbClient;
    @Autowired
    private BroadbandRedis  broadbandRedis;

    /**
     * Adds a new broadband plan to the database.
     *
     * @param broadbandPlan The broadband plan to be added
     * @return The added broadband plan
     * @throws DatabaseConnectionException if there's an error connecting to the database
     */

    @Override
    public BroadbandPlan addBroadbandPlan(BroadbandPlan broadbandPlan) {
        try {
            String uuid= String.valueOf(UUID.randomUUID());
            broadbandPlan.setPlanId(uuid);
            PutItemRequest request = queryHelper.addQuery(broadbandPlan);
            PutItemResponse response = dynamoDbClient.putItem(request);

            if (response != null && response.sdkHttpResponse().isSuccessful()) {
                GetItemRequest getRequest = queryHelper.getItemByPlanId(uuid,broadbandPlan.getPlanType());
                Map<String, AttributeValue> updatedItem = dynamoDbClient.getItem(getRequest).item();
                broadbandRedis.clearBroadbandCache();
                return queryWrapper.mapToBroadbandPlan(updatedItem);
            }
        } catch (SdkException exception) { // throw error when db connection failed
            log.error("Error Connecting to Database : {}",exception.getMessage());
            throw new DatabaseConnectionException("Error Connecting to Database");
        }
        return broadbandPlan;
    }
    /**
     * Updates an existing broadband plan in the database.
     *
     * @param broadbandPlan The updated broadband plan
     * @param planId        The ID of the plan to be updated
     * @return The updated broadband plan
     * @throws NoPlanFoundException       if no plan is found with the given ID
     * @throws DatabaseConnectionException if there's an error connecting to the database
     */
    @Override
    public BroadbandPlan updateBroadbandPlan(BroadbandPlan broadbandPlan, String planId) {

        UpdateItemRequest request = queryHelper.updateQuery(broadbandPlan, planId);
        UpdateItemResponse response = dynamoDbClient.updateItem(request);

        Map<String, AttributeValue> updatedItem = null;
        if (response.sdkHttpResponse().isSuccessful()) {
            GetItemRequest getRequest = queryHelper.getItemByPlanId(planId,"Broadband");
            updatedItem = dynamoDbClient.getItem(getRequest).item();
        }
        if (updatedItem != null && !updatedItem.isEmpty()) {
            broadbandRedis.clearBroadbandCache();
            return queryWrapper.mapToBroadbandPlan(updatedItem);
        } else { // throw error when the response is null
            log.error("Found no Plans in the updated Item");
            throw new NoPlanFoundException(NO_PLAN.getValue() + planId);
        }
    }

    /**
     * Deletes a broadband plan from the database.
     *
     * @param planId The ID of the plan to be deleted
     * @return true if the plan was successfully deleted, false otherwise
     * @throws NoPlanFoundException if no plan is found with the given ID
     */
    @Override
    public boolean deleteBroadbandPlan(String planId) {
        GetItemRequest getRequest = queryHelper.getItemByPlanId(planId,"Broadband");
        Map<String, AttributeValue> broadband = dynamoDbClient.getItem(getRequest).item();
        // check if the entity broadband is empty or not
        if(broadband.isEmpty()){
            log.error(NO_PLAN + "{}", planId);
            throw new NoPlanFoundException(NO_PLAN + planId);
        }
        BroadbandPlan broadbandPlan = queryWrapper.mapToBroadbandPlan(broadband);
        UpdateItemRequest request = queryHelper.deleteQuery(planId, broadbandPlan);
        UpdateItemResponse response = dynamoDbClient.updateItem(request);
        // throw error when the response is null
        if(response == null){
            log.error(NO_PLAN + "{}", planId);
            throw new NoPlanFoundException(NO_PLAN + planId);
        }
        broadbandRedis.clearBroadbandCache();
        return true;
    }
}
