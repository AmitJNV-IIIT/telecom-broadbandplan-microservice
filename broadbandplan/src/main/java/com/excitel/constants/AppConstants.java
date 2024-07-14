package com.excitel.constants;

public enum AppConstants {
     ADMIN("ADMIN"),
    USER_DENIED_MESSAGE("Reserved route for Admin"),
    REQUEST_ATTRIBUTE("role"),
    PLAN_TYPE("PlanType"),
    PLAN_ID("PlanID"),
    TABLE_NAME("plan-table"),
    ACTIVE("Active"),
    API_V1_PREFIX("/api/v2/"),
    NO_PLAN("No plan found with id: ");
    private final String value;

    AppConstants(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
