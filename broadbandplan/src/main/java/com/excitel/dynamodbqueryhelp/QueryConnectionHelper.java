package com.excitel.dynamodbqueryhelp;

import com.excitel.model.BroadbandConnection;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import java.util.*;

/**
 * Helper class for DynamoDB queries related to broadband connections.
 */

@Component
public class QueryConnectionHelper {
    /**
     * Constructs a GetItemRequest to retrieve a broadband connection by mobile number and status.
     *
     * @param mobileNumber     The mobile number of the connection
     * @param connectionStatus The status of the connection
     * @return GetItemRequest for retrieving the connection
     */

    public GetItemRequest getItemByMobileNumber(String mobileNumber, String connectionStatus){
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("ConnectionStatus", AttributeValue.builder().s(connectionStatus).build());
        key.put("MobileNumber", AttributeValue.builder().s(mobileNumber).build());
        return GetItemRequest.builder()
                .tableName("connection-table")
                .key(key)
                .build();
    }

    /**
     * Constructs a PutItemRequest to add a new broadband connection to the database.
     *
     * @param broadbandConnection The broadband connection object to be added
     * @param mobileNumber        The mobile number associated with the connection
     * @return PutItemRequest for adding the connection
     */
    public PutItemRequest addConnectionQuery(BroadbandConnection broadbandConnection, String mobileNumber){
        Map<String, AttributeValue> item = new HashMap<>();
        UUID uuid=UUID.randomUUID();
        // Generate a unique ConnectionID for the new connection
        item.put("ConnectionID", AttributeValue.builder().s(uuid.toString()).build());
        // Populate the item attributes from the broadbandConnection object
        item.put("CustomerName",AttributeValue.builder().s(!Objects.isNull(broadbandConnection.getName())?broadbandConnection.getName():"null").build());
        item.put("Address",AttributeValue.builder().s(!Objects.isNull(broadbandConnection.getAddress())?broadbandConnection.getAddress():"null").build());
        item.put("PINCode",AttributeValue.builder().s(!Objects.isNull(broadbandConnection.getPinCode())?broadbandConnection.getPinCode():"null").build());
        item.put("City",AttributeValue.builder().s(!Objects.isNull(broadbandConnection.getCity())?broadbandConnection.getCity():"null").build());
        item.put("State",AttributeValue.builder().s(!Objects.isNull(broadbandConnection.getState())?broadbandConnection.getState():"null").build());
        item.put("Country",AttributeValue.builder().s(!Objects.isNull(broadbandConnection.getCountry())?broadbandConnection.getCountry():"null").build());
        item.put("ConnectionStatus",AttributeValue.builder().s(!Objects.isNull(broadbandConnection.getStatus())?broadbandConnection.getStatus():"Active").build());
        item.put("MobileNumber",AttributeValue.builder().s(mobileNumber).build());

        return PutItemRequest.builder()
                .tableName("connection-table")
                .conditionExpression("attribute_not_exists(ConnectionStatus) AND attribute_not_exists(MobileNumber)")
                .item(item)
                .build();
    }


}
