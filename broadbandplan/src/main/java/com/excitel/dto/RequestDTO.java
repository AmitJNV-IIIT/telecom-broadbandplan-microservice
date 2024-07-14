package com.excitel.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RequestDTO {
    private String active;//NOSONAR
    private String planId;//NOSONAR
    private String type = "Broadband"; //NOSONAR
    private String category;//NOSONAR
    private String data;//NOSONAR
    private String speed;//NOSONAR
    private Integer offset = 0;//NOSONAR
    private Integer limit = 10;//NOSONAR

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class SubscriptionRequestDTO {

        private List<String> planIdList;//NOSONAR
        private String planType;//NOSONAR
    }
}
