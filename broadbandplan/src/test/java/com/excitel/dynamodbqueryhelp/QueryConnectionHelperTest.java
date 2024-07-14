package com.excitel.dynamodbqueryhelp;

import com.excitel.model.BroadbandConnection;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryConnectionHelperTest {

    @Test
    void testGetItemByMobileNumber() {
        // Arrange
        QueryConnectionHelper queryConnectionHelper = new QueryConnectionHelper();
        String mobileNumber = "1234567890";
        String connectionStatus = "Active";

        // Act
        GetItemRequest getItemRequest = queryConnectionHelper.getItemByMobileNumber(mobileNumber, connectionStatus);

        // Assert
        assertEquals("connection-table", getItemRequest.tableName());
        assertEquals(2, getItemRequest.key().size());
        assertEquals(connectionStatus, getItemRequest.key().get("ConnectionStatus").s());
        assertEquals(mobileNumber, getItemRequest.key().get("MobileNumber").s());
    }

    @Test
    void testAddConnectionQuery() {
        // Arrange
        QueryConnectionHelper queryConnectionHelper = new QueryConnectionHelper();
        BroadbandConnection broadbandConnection = new BroadbandConnection();
        broadbandConnection.setName("John Doe");
        broadbandConnection.setAddress("123 Main St");
        broadbandConnection.setPinCode("12345");
        broadbandConnection.setCity("City");
        broadbandConnection.setState("State");
        broadbandConnection.setCountry("Country");
        broadbandConnection.setStatus("Active");
        String mobileNumber = "1234567890";

        // Act
        PutItemRequest putItemRequest = queryConnectionHelper.addConnectionQuery(broadbandConnection, mobileNumber);

        // Assert
        assertEquals("connection-table", putItemRequest.tableName());
        assertEquals("attribute_not_exists(ConnectionStatus) AND attribute_not_exists(MobileNumber)", putItemRequest.conditionExpression());
        assertEquals(9, putItemRequest.item().size());
        assertEquals("John Doe", putItemRequest.item().get("CustomerName").s());
        assertEquals("123 Main St", putItemRequest.item().get("Address").s());
        assertEquals("12345", putItemRequest.item().get("PINCode").s());
        assertEquals("City", putItemRequest.item().get("City").s());
        assertEquals("State", putItemRequest.item().get("State").s());
        assertEquals("Country", putItemRequest.item().get("Country").s());
        assertEquals("Active", putItemRequest.item().get("ConnectionStatus").s());
        assertEquals(mobileNumber, putItemRequest.item().get("MobileNumber").s());
    }
}
