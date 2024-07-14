package com.excitel.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@DynamoDBTable(tableName = "plan-table")
public class BroadbandPlan implements Serializable {

    @DynamoDBRangeKey
    @DynamoDBAttribute(attributeName = "PlanID")
    private String planId;

    @NotEmpty
    @NotNull
    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "PlanType")
    private String planType;

    //    @NotBlank
    @NotNull
    @DynamoDBAttribute(attributeName = "Price")
    private String price;

    //    @NotBlank
//    @NotNull
    @DynamoDBAttribute(attributeName = "Category")
    private String category;

    //    @NotBlank
    @NotNull
    @DynamoDBAttribute(attributeName = "Validity")
    private String validity;

    @DynamoDBAttribute(attributeName = "OTT")
    private List<String> ott;

    @DynamoDBAttribute(attributeName = "VoiceLimit")
    private String voiceLimit;

    @DynamoDBAttribute(attributeName = "SMS")
    private String sms;

    @DynamoDBAttribute(attributeName = "TotalData")
    private String data;

    @DynamoDBAttribute(attributeName = "CouponIDs")
    private List<String> couponIds;

    @DynamoDBAttribute(attributeName = "PlanLimit")
    private String limit;

    @DynamoDBAttribute(attributeName = "Speed")
    private String speed;

    @DynamoDBAttribute(attributeName = "Active")
    private String active;
}