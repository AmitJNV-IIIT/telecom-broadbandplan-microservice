package com.excitel.dynamodbqueryhelp;

import com.excitel.model.BroadbandConnection;
import com.excitel.model.BroadbandPlan;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

/**
 * Helper class to map DynamoDB query results to Java objects.
 */
@Component
public class QueryWrapper {

    /**
     * Maps a DynamoDB item to a BroadbandPlan object.
     *
     * @param item The DynamoDB item to map
     * @return A BroadbandPlan object mapped from the DynamoDB item
     */

    public BroadbandPlan mapToBroadbandPlan(Map<String, AttributeValue> item) {
        // Mapping attributes from DynamoDB item to BroadbandPlan object
        BroadbandPlan broadbandPlan = new BroadbandPlan();
        broadbandPlan.setPlanType(item.containsKey("PlanType") ? item.get("PlanType").s() : null);
        broadbandPlan.setPlanId(item.containsKey("PlanID") ? item.get("PlanID").s() : null);
        broadbandPlan.setActive(item.containsKey("Active") ? item.get("Active").s() : null);
        broadbandPlan.setPrice(item.containsKey("Price") ? item.get("Price").s() : null);
        broadbandPlan.setCategory(item.containsKey("Category") ? item.get("Category").s() : null);
        broadbandPlan.setValidity(item.containsKey("Validity") ? item.get("Validity").s() : null);
        // Mapping list attributes from DynamoDB item to BroadbandPlan object
        if (item.containsKey("OTT")) {
            List<String> ottList = item.get("OTT").l().stream()
                    .map(AttributeValue::s)
                            .toList();
            broadbandPlan.setOtt(ottList);
        } else {
            broadbandPlan.setOtt(null);
        }
        broadbandPlan.setVoiceLimit(item.containsKey("VoiceLimit") ? item.get("VoiceLimit").s() : null);
        broadbandPlan.setSms(item.containsKey("SMS") ? item.get("SMS").s() : null);
        broadbandPlan.setData(item.containsKey("TotalData") ? item.get("TotalData").s() : null);
        // Mapping list attributes from DynamoDB item to BroadbandPlan object
        if (item.containsKey("CouponIDs")) {
            List<String> couponList = item.get("CouponIDs").l().stream()
                    .map(AttributeValue::s)
                            .toList();
            broadbandPlan.setCouponIds(couponList);
        } else {
            broadbandPlan.setCouponIds(null);
        }
        broadbandPlan.setLimit(item.containsKey("Limit") ? item.get("Limit").s() : null);
        broadbandPlan.setSpeed(item.containsKey("Speed") ? item.get("Speed").s() : null);
        return broadbandPlan;
    }
    /**
     * Maps a DynamoDB item to a BroadbandConnection object.
     *
     * @param item The DynamoDB item to map
     * @return A BroadbandConnection object mapped from the DynamoDB item
     */
    public BroadbandConnection mapToBroadbandConnection(Map<String, AttributeValue> item){
        BroadbandConnection broadbandConnection = new BroadbandConnection();
        // Mapping attributes from DynamoDB item to BroadbandConnection object
        broadbandConnection.setMobileNumber(item.containsKey("MobileNumber") ? item.get("MobileNumber").s() : null);
        broadbandConnection.setStatus(item.containsKey("ConnectionStatus")?item.get("ConnectionStatus").s(): null);
        broadbandConnection.setConnectionId(item.containsKey("ConnectionID")?item.get("ConnectionID").s(): null);
        broadbandConnection.setName(item.containsKey("CustomerName")?item.get("CustomerName").s():null);
        broadbandConnection.setAddress(item.containsKey("Address")?item.get("Address").s():null);
        broadbandConnection.setCity(item.containsKey("City")?item.get("City").s():null);
        broadbandConnection.setState(item.containsKey("State")?item.get("State").s():null);
        broadbandConnection.setCountry(item.containsKey("Country")?item.get("Country").s():null);
        broadbandConnection.setPinCode(item.containsKey("PINCode")?item.get("PINCode").s():null);

        return broadbandConnection;
    }
}
