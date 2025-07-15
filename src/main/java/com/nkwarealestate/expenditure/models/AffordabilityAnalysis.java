package com.nkwarealestate.expenditure.models;

import com.nkwarealestate.expenditure.datastructures.CustomHashMap;
import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;

/**
 * Represents affordability analysis results
 */
public class AffordabilityAnalysis {
    private CustomHashMap<String, ProjectCostImpact> projectImpacts;
    private AffordabilityThresholds thresholds;
    private CustomLinkedList<String> recommendations;
    private double priceIncreasePercentage;
    private double totalMaterialCost;
    private double totalAdditionalCost;

    public AffordabilityAnalysis(CustomHashMap<String, ProjectCostImpact> projectImpacts,
            AffordabilityThresholds thresholds,
            CustomLinkedList<String> recommendations,
            double priceIncreasePercentage,
            double totalMaterialCost,
            double totalAdditionalCost) {
        this.projectImpacts = projectImpacts;
        this.thresholds = thresholds;
        this.recommendations = recommendations;
        this.priceIncreasePercentage = priceIncreasePercentage;
        this.totalMaterialCost = totalMaterialCost;
        this.totalAdditionalCost = totalAdditionalCost;
    }

    // Getters
    public CustomHashMap<String, ProjectCostImpact> getProjectImpacts() {
        return projectImpacts;
    }

    public AffordabilityThresholds getThresholds() {
        return thresholds;
    }

    public CustomLinkedList<String> getRecommendations() {
        return recommendations;
    }

    public double getPriceIncreasePercentage() {
        return priceIncreasePercentage;
    }

    public double getTotalMaterialCost() {
        return totalMaterialCost;
    }

    public double getTotalAdditionalCost() {
        return totalAdditionalCost;
    }

    public double getTotalNewCost() {
        return totalMaterialCost + totalAdditionalCost;
    }

    public double getActualImpactPercentage() {
        return totalMaterialCost > 0 ? (totalAdditionalCost / totalMaterialCost) * 100 : 0;
    }

    public int getAffectedProjectsCount() {
        return projectImpacts.size();
    }
}
