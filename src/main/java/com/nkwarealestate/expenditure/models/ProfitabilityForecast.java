package com.nkwarealestate.expenditure.models;

import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;
import java.time.LocalDate;

/**
 * Represents profitability forecast data
 */
public class ProfitabilityForecast {
    private CustomLinkedList<ForecastData> forecastData;
    private double trendCoefficient;
    private double confidenceLevel;
    private LocalDate generatedDate;

    public ProfitabilityForecast(CustomLinkedList<ForecastData> forecastData,
            double trendCoefficient, double confidenceLevel) {
        this.forecastData = forecastData;
        this.trendCoefficient = trendCoefficient;
        this.confidenceLevel = confidenceLevel;
        this.generatedDate = LocalDate.now();
    }

    // Getters
    public CustomLinkedList<ForecastData> getForecastData() {
        return forecastData;
    }

    public double getTrendCoefficient() {
        return trendCoefficient;
    }

    public double getConfidenceLevel() {
        return confidenceLevel;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public String getTrendDescription() {
        if (trendCoefficient > 100) {
            return "RAPIDLY INCREASING";
        } else if (trendCoefficient > 50) {
            return "INCREASING";
        } else if (trendCoefficient > -50) {
            return "STABLE";
        } else if (trendCoefficient > -100) {
            return "DECREASING";
        } else {
            return "RAPIDLY DECREASING";
        }
    }

    public String getConfidenceDescription() {
        if (confidenceLevel > 0.8) {
            return "HIGH";
        } else if (confidenceLevel > 0.6) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
}
