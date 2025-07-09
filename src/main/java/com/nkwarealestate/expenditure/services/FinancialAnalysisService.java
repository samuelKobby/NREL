package com.nkwarealestate.expenditure.services;

import com.nkwarealestate.expenditure.models.Expenditure;
import com.nkwarealestate.expenditure.models.Phase;
import com.nkwarealestate.expenditure.datastructures.CustomHashMap;
import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;
import com.nkwarealestate.expenditure.datastructures.CustomTree;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

/**
 * Service for financial analysis and forecasting
 * Uses various custom data structures for analysis
 */
public class FinancialAnalysisService {
    
    private ExpenditureService expenditureService;
    private DateTimeFormatter dateFormatter;
    
    public FinancialAnalysisService(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }
    
    /**
     * Calculate the monthly burn rate (average monthly expenditure)
     * 
     * @param months Number of months to consider for calculation
     * @return The average monthly expenditure amount
     */
    public double calculateMonthlyBurnRate(int months) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months);
        
        // Get expenditures within the date range
        CustomLinkedList<Expenditure> expenditures = 
                expenditureService.getExpendituresByDateRange(startDate, endDate);
        
        if (expenditures.isEmpty()) {
            return 0.0;
        }
        
        // Calculate total amount
        double totalAmount = 0.0;
        for (int i = 0; i < expenditures.size(); i++) {
            totalAmount += expenditures.get(i).getAmount();
        }
        
        // Return average monthly amount
        return totalAmount / months;
    }
    
    /**
     * Generate a forecast of future expenditures based on historical data
     * 
     * @param months Number of months to forecast
     * @return A string containing the forecast report
     */
    public String generateExpenditureForecast(int months) {
        // Calculate current burn rate
        double burnRate = calculateMonthlyBurnRate(3); // Using last 3 months as baseline
        
        // Get category breakdown from recent expenditures
        CustomHashMap<String, Double> categoryPercentages = calculateCategoryPercentages();
        
        StringBuilder forecast = new StringBuilder();
        forecast.append("EXPENDITURE FORECAST\n");
        forecast.append("===================\n\n");
        forecast.append(String.format("Based on a monthly burn rate of GHS %.2f:\n\n", burnRate));
        
        // Generate monthly forecasts
        LocalDate currentDate = LocalDate.now();
        double totalForecast = 0.0;
        
        for (int i = 1; i <= months; i++) {
            LocalDate forecastDate = currentDate.plusMonths(i);
            String monthName = forecastDate.getMonth().toString();
            int year = forecastDate.getYear();
            
            // Apply seasonal adjustment (example: increase by 10% for December)
            double adjustment = getSeasonalAdjustment(forecastDate.getMonth());
            double monthlyForecast = burnRate * adjustment;
            totalForecast += monthlyForecast;
            
            forecast.append(String.format("%s %d: GHS %.2f\n", monthName, year, monthlyForecast));
            
            // Add category breakdown for this month
            forecast.append("  Breakdown by category:\n");
            for (CustomHashMap.Entry<String, Double> entry : categoryPercentages.entries()) {
                double amount = monthlyForecast * entry.getValue();
                forecast.append(String.format("  - %s: GHS %.2f\n", entry.getKey(), amount));
            }
            forecast.append("\n");
        }
        
        forecast.append(String.format("\nTotal %d-month forecast: GHS %.2f\n", months, totalForecast));
        
        return forecast.toString();
    }
    
    /**
     * Calculate the percentage of total expenditure for each category
     */
    private CustomHashMap<String, Double> calculateCategoryPercentages() {
        // Get all recent expenditures (last 3 months)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);
        CustomLinkedList<Expenditure> recentExpenditures = 
                expenditureService.getExpendituresByDateRange(startDate, endDate);
        
        // Calculate total amount
        double totalAmount = 0.0;
        CustomHashMap<String, Double> categoryTotals = new CustomHashMap<>();
        
        for (int i = 0; i < recentExpenditures.size(); i++) {
            Expenditure exp = recentExpenditures.get(i);
            double amount = exp.getAmount();
            totalAmount += amount;
            
            String category = exp.getCategory();
            Double currentTotal = categoryTotals.get(category);
            if (currentTotal == null) {
                categoryTotals.put(category, amount);
            } else {
                categoryTotals.put(category, currentTotal + amount);
            }
        }
        
        // Convert totals to percentages
        CustomHashMap<String, Double> percentages = new CustomHashMap<>();
        if (totalAmount > 0) {
            for (CustomHashMap.Entry<String, Double> entry : categoryTotals.entries()) {
                percentages.put(entry.getKey(), entry.getValue() / totalAmount);
            }
        }
        
        return percentages;
    }
    
    /**
     * Get seasonal adjustment factor for a given month
     * This could be refined based on historical data
     */
    private double getSeasonalAdjustment(Month month) {
        switch (month) {
            case DECEMBER:
                return 1.10; // 10% increase
            case JANUARY:
                return 0.90; // 10% decrease
            case JULY:
            case AUGUST:
                return 1.05; // 5% increase
            default:
                return 1.0;  // No adjustment
        }
    }
    
    /**
     * Analyze the impact of material cost increases
     * 
     * @param materialCategory The material category to analyze
     * @param percentageIncrease The percentage increase to simulate
     * @return Analysis report
     */
    public String analyzeMaterialCostImpact(String materialCategory, double percentageIncrease) {
        // Get expenditures for this category
        CustomLinkedList<Expenditure> categoryExpenditures = 
                expenditureService.getExpendituresByCategory(materialCategory);
        
        if (categoryExpenditures.isEmpty()) {
            return "No expenditures found for category: " + materialCategory;
        }
        
        double currentTotal = 0.0;
        
        // Calculate current total
        for (int i = 0; i < categoryExpenditures.size(); i++) {
            currentTotal += categoryExpenditures.get(i).getAmount();
        }
        
        // Calculate new total with increase
        double newTotal = currentTotal * (1 + percentageIncrease / 100);
        double difference = newTotal - currentTotal;
        
        // Calculate impact on overall budget
        double totalExpenditure = expenditureService.getTotalExpenditureAmount();
        double currentPercentage = (currentTotal / totalExpenditure) * 100;
        double newPercentage = (newTotal / (totalExpenditure + difference)) * 100;
        
        StringBuilder report = new StringBuilder();
        report.append("MATERIAL COST IMPACT ANALYSIS\n");
        report.append("=============================\n\n");
        report.append(String.format("Material Category: %s\n", materialCategory));
        report.append(String.format("Price Increase: %.2f%%\n\n", percentageIncrease));
        
        report.append(String.format("Current Expenditure: GHS %.2f\n", currentTotal));
        report.append(String.format("Projected Expenditure: GHS %.2f\n", newTotal));
        report.append(String.format("Difference: GHS %.2f\n\n", difference));
        
        report.append(String.format("Current percentage of total budget: %.2f%%\n", currentPercentage));
        report.append(String.format("New percentage of total budget: %.2f%%\n\n", newPercentage));
        
        report.append("RECOMMENDATIONS:\n");
        if (percentageIncrease > 10) {
            report.append("- Consider alternative materials\n");
            report.append("- Negotiate bulk discounts with suppliers\n");
            report.append("- Review project timeline to optimize material purchases\n");
        } else {
            report.append("- Minor impact - continue monitoring prices\n");
            report.append("- Consider advance purchases if further increases expected\n");
        }
        
        return report.toString();
    }
    
    /**
     * Create a tree representation of expenditures by phase and category
     */
    public CustomTree<String> createExpenditureTree() {
        CustomTree<String> tree = new CustomTree<>("Expenditures");
        
        // Get all phases
        for (Phase phase : Phase.values()) {
            // Skip OTHER phase if there are no expenditures in it
            if (phase == Phase.OTHER && 
                    expenditureService.getExpendituresByPhase(Phase.OTHER).isEmpty()) {
                continue;
            }
            
            // Add phase node
            String phaseName = phase.toString();
            tree.addChild("Expenditures", phaseName);
            
            // Get expenditures for this phase
            CustomLinkedList<Expenditure> phaseExpenditures = 
                    expenditureService.getExpendituresByPhase(phase);
            
            // Extract unique categories for this phase
            CustomHashMap<String, Boolean> categories = new CustomHashMap<>();
            for (int i = 0; i < phaseExpenditures.size(); i++) {
                categories.put(phaseExpenditures.get(i).getCategory(), true);
            }
            
            // Add category nodes under phase
            for (String category : categories.keys()) {
                String categoryNode = phaseName + " - " + category;
                tree.addChild(phaseName, categoryNode);
                
                // Get expenditures for this category in this phase
                for (int i = 0; i < phaseExpenditures.size(); i++) {
                    Expenditure exp = phaseExpenditures.get(i);
                    if (exp.getCategory().equals(category)) {
                        String expNode = String.format("%s - %s - GHS %.2f", 
                                exp.getCode(), exp.getDate().format(dateFormatter), exp.getAmount());
                        tree.addChild(categoryNode, expNode);
                    }
                }
            }
        }
        
        return tree;
    }
    
    /**
     * Analyze profitability based on current expenditure trends
     * 
     * @param expectedRevenue Expected total revenue for the project
     * @return Profitability analysis report
     */
    public String analyzeProfitability(double expectedRevenue) {
        // Get total current expenditure
        double totalExpenditure = expenditureService.getTotalExpenditureAmount();
        
        // Project final expenditure based on current burn rate
        double burnRate = calculateMonthlyBurnRate(3);
        double estimatedRemainingMonths = 12; // Assuming project lasts 1 year from now
        double projectedAdditionalExpenditure = burnRate * estimatedRemainingMonths;
        double projectedTotalExpenditure = totalExpenditure + projectedAdditionalExpenditure;
        
        // Calculate profitability metrics
        double projectedProfit = expectedRevenue - projectedTotalExpenditure;
        double projectedMargin = (projectedProfit / expectedRevenue) * 100;
        double breakEvenRevenue = projectedTotalExpenditure;
        
        StringBuilder report = new StringBuilder();
        report.append("PROFITABILITY ANALYSIS\n");
        report.append("=====================\n\n");
        
        report.append(String.format("Expected Revenue: GHS %.2f\n", expectedRevenue));
        report.append(String.format("Current Expenditure: GHS %.2f\n", totalExpenditure));
        report.append(String.format("Projected Additional Expenditure: GHS %.2f\n", projectedAdditionalExpenditure));
        report.append(String.format("Projected Total Expenditure: GHS %.2f\n\n", projectedTotalExpenditure));
        
        report.append(String.format("Projected Profit: GHS %.2f\n", projectedProfit));
        report.append(String.format("Projected Profit Margin: %.2f%%\n", projectedMargin));
        report.append(String.format("Break-even Revenue: GHS %.2f\n\n", breakEvenRevenue));
        
        // Add status and recommendations
        report.append("STATUS: ");
        if (projectedMargin > 20) {
            report.append("EXCELLENT\n\n");
            report.append("RECOMMENDATIONS:\n");
            report.append("- Continue current expenditure controls\n");
            report.append("- Consider reinvestment opportunities for excess profit\n");
        } else if (projectedMargin > 10) {
            report.append("GOOD\n\n");
            report.append("RECOMMENDATIONS:\n");
            report.append("- Monitor high-cost categories\n");
            report.append("- Look for optimization opportunities in supply chain\n");
        } else if (projectedMargin > 0) {
            report.append("CAUTION\n\n");
            report.append("RECOMMENDATIONS:\n");
            report.append("- Implement cost-cutting measures\n");
            report.append("- Review and potentially renegotiate vendor contracts\n");
            report.append("- Consider ways to increase revenue\n");
        } else {
            report.append("AT RISK\n\n");
            report.append("RECOMMENDATIONS:\n");
            report.append("- Urgent budget review required\n");
            report.append("- Freeze non-essential expenditures\n");
            report.append("- Renegotiate payment terms with vendors\n");
            report.append("- Explore additional revenue streams\n");
        }
        
        return report.toString();
    }
}
