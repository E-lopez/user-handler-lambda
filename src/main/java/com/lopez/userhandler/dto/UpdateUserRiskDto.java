package com.lopez.userhandler.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UpdateUserRiskDto {

    private String userId;
    private String riskLevel;

    public UpdateUserRiskDto() {
        // No initialization needed
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}