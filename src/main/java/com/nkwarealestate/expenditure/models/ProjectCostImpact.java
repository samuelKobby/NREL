package com.nkwarealestate.expenditure.models;

import com.nkwarealestate.expenditure.datastructures.CustomHashMap;

/**
 * Represents cost impact for a specific project
 */
public class ProjectCostImpact {
    private String projectId;
    private CustomHashMap<String, Double> originalCosts;
    private CustomHashMap<String, Double> additionalCosts;
    private double totalOriginalCost;
    private double totalAdditionalCost;

    public ProjectCostImpact(String projectId) {
        this.projectId = projectId;
        this.originalCosts = new CustomHashMap<>();
        this.additionalCosts = new CustomHashMap<>();
        this.totalOriginalCost = 0.0;
        this.totalAdditionalCost = 0.0;
    }

    public void addMaterialCost(String category, double originalCost, double additionalCost) {
        // Update category-specific costs
        Double currentOriginal = originalCosts.get(category);
        Double currentAdditional = additionalCosts.get(category);

        originalCosts.put(category, (currentOriginal != null ? currentOriginal : 0.0) + originalCost);
        additionalCosts.put(category, (currentAdditional != null ? currentAdditional : 0.0) + additionalCost);

        // Update totals
        totalOriginalCost += originalCost;
        totalAdditionalCost += additionalCost;
    }

    // Getters
    public String getProjectId() {
        return projectId;
    }

    public CustomHashMap<String, Double> getOriginalCosts() {
        return originalCosts;
    }

    public CustomHashMap<String, Double> getAdditionalCosts() {
        return additionalCosts;
    }

    public double getTotalOriginalCost() {
        return totalOriginalCost;
    }

    public double getTotalAdditionalCost() {
        return totalAdditionalCost;
    }

    public double getTotalNewCost() {
        return totalOriginalCost + totalAdditionalCost;
    }

    public double getImpactPercentage() {
        return totalOriginalCost > 0 ? (totalAdditionalCost / totalOriginalCost) * 100 : 0;
    }

    public String getMostAffectedCategory() {
        String mostAffected = "";
        double highestImpact = 0.0;

        // Iterate through additional costs to find highest impact
        // Use the keys() method which returns an Iterable<K>
        for (String category : originalCosts.keys()) {
            Double impact = additionalCosts.get(category);
            if (impact != null && impact > highestImpact) {
                highestImpact = impact;
                mostAffected = category;
            }
        }

        return mostAffected.isEmpty() ? "None" : mostAffected;
    }
}
