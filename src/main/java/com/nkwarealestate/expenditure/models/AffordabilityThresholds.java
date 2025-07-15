package com.nkwarealestate.expenditure.models;

/**
 * Represents affordability thresholds for cost impact analysis
 */
public class AffordabilityThresholds {
    private double manageableThreshold;
    private double criticalThreshold;
    private double severeThreshold;
    private String impactLevel;

    public AffordabilityThresholds(double manageableThreshold, double criticalThreshold,
            double severeThreshold, String impactLevel) {
        this.manageableThreshold = manageableThreshold;
        this.criticalThreshold = criticalThreshold;
        this.severeThreshold = severeThreshold;
        this.impactLevel = impactLevel;
    }

    // Getters
    public double getManageableThreshold() {
        return manageableThreshold;
    }

    public double getCriticalThreshold() {
        return criticalThreshold;
    }

    public double getSevereThreshold() {
        return severeThreshold;
    }

    public String getImpactLevel() {
        return impactLevel;
    }

    public String getThresholdDescription() {
        switch (impactLevel) {
            case "MANAGEABLE":
                return "Impact is within acceptable limits";
            case "MODERATE":
                return "Impact requires attention but is manageable";
            case "CRITICAL":
                return "Impact poses significant challenges to profitability";
            case "SEVERE":
                return "Impact threatens project viability";
            default:
                return "Unknown impact level";
        }
    }

    public String getColorCode() {
        switch (impactLevel) {
            case "MANAGEABLE":
                return "GREEN";
            case "MODERATE":
                return "YELLOW";
            case "CRITICAL":
                return "ORANGE";
            case "SEVERE":
                return "RED";
            default:
                return "GRAY";
        }
    }
}
