package com.nkwarealestate.expenditure.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents forecast data for a specific month
 */
public class ForecastData {
    private LocalDate month;
    private double projectedSpending;
    private double projectedRevenue;
    private double profitability;
    private double profitMarginPercentage;

    public ForecastData(LocalDate month, double projectedSpending,
            double projectedRevenue, double profitability,
            double profitMarginPercentage) {
        this.month = month;
        this.projectedSpending = projectedSpending;
        this.projectedRevenue = projectedRevenue;
        this.profitability = profitability;
        this.profitMarginPercentage = profitMarginPercentage;
    }

    // Getters
    public LocalDate getMonth() {
        return month;
    }

    public double getProjectedSpending() {
        return projectedSpending;
    }

    public double getProjectedRevenue() {
        return projectedRevenue;
    }

    public double getProfitability() {
        return profitability;
    }

    public double getProfitMarginPercentage() {
        return profitMarginPercentage;
    }

    public String getMonthYearString() {
        return month.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    public boolean isProfitable() {
        return profitability > 0;
    }

    public String getProfitabilityStatus() {
        if (profitability > 0) {
            if (profitMarginPercentage > 30) {
                return "EXCELLENT";
            } else if (profitMarginPercentage > 20) {
                return "GOOD";
            } else if (profitMarginPercentage > 10) {
                return "MODERATE";
            } else {
                return "MARGINAL";
            }
        } else {
            return "LOSS";
        }
    }
}
