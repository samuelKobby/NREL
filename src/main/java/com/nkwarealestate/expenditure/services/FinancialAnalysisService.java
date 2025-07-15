package com.nkwarealestate.expenditure.services;

import com.nkwarealestate.expenditure.models.Expenditure;
import com.nkwarealestate.expenditure.models.Phase;
import com.nkwarealestate.expenditure.models.ProfitabilityForecast;
import com.nkwarealestate.expenditure.models.AffordabilityAnalysis;
import com.nkwarealestate.expenditure.models.ForecastData;
import com.nkwarealestate.expenditure.models.MonthlyData;
import com.nkwarealestate.expenditure.models.ProjectCostImpact;
import com.nkwarealestate.expenditure.models.AffordabilityThresholds;
import com.nkwarealestate.expenditure.datastructures.CustomHashMap;
import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;
import com.nkwarealestate.expenditure.datastructures.CustomTree;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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
        CustomLinkedList<Expenditure> expenditures = expenditureService.getExpendituresByDateRange(startDate, endDate);

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
        CustomLinkedList<Expenditure> recentExpenditures = expenditureService.getExpendituresByDateRange(startDate,
                endDate);

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
                return 1.0; // No adjustment
        }
    }

    /**
     * Analyze the impact of material cost increases
     * 
     * @param materialCategory   The material category to analyze
     * @param percentageIncrease The percentage increase to simulate
     * @return Analysis report
     */
    public String analyzeMaterialCostImpact(String materialCategory, double percentageIncrease) {
        // Get expenditures for this category
        CustomLinkedList<Expenditure> categoryExpenditures = expenditureService
                .getExpendituresByCategory(materialCategory);

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
            CustomLinkedList<Expenditure> phaseExpenditures = expenditureService.getExpendituresByPhase(phase);

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

    // ================ ENHANCED ANALYSIS WITH BINARY SEARCH ================

    /**
     * Find expenditures within a specific amount threshold using optimized search
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param minThreshold Minimum amount threshold
     * @param maxThreshold Maximum amount threshold
     * @return List of expenditures within the threshold range
     */
    public CustomLinkedList<Expenditure> findExpendituresInAmountRange(double minThreshold, double maxThreshold) {
        // Use the binary search from ExpenditureService for efficiency
        return expenditureService.binarySearchAmountRange(minThreshold, maxThreshold);
    }

    /**
     * Analyze expenditure patterns within date ranges using binary search
     * Time Complexity: O(log n + k) where k is number of results per range
     * 
     * @param startDate      Analysis start date
     * @param endDate        Analysis end date
     * @param intervalMonths Number of months per analysis interval
     * @return Detailed pattern analysis report
     */
    public String analyzeExpenditurePatternsOptimized(LocalDate startDate, LocalDate endDate, int intervalMonths) {
        StringBuilder report = new StringBuilder();
        report.append("EXPENDITURE PATTERN ANALYSIS (OPTIMIZED)\n");
        report.append("========================================\n\n");

        LocalDate currentStart = startDate;
        double totalAmount = 0.0;
        int totalCount = 0;

        // Analyze data in intervals using binary search
        while (currentStart.isBefore(endDate)) {
            LocalDate currentEnd = currentStart.plusMonths(intervalMonths);
            if (currentEnd.isAfter(endDate)) {
                currentEnd = endDate;
            }

            // Use binary search for efficient date range retrieval
            CustomLinkedList<Expenditure> intervalExpenditures = expenditureService.binarySearchDateRange(currentStart,
                    currentEnd);

            // Analyze this interval
            double intervalAmount = 0.0;
            CustomHashMap<String, Double> categoryTotals = new CustomHashMap<>();
            CustomHashMap<Phase, Double> phaseTotals = new CustomHashMap<>();

            for (int i = 0; i < intervalExpenditures.size(); i++) {
                Expenditure exp = intervalExpenditures.get(i);
                double amount = exp.getAmount();
                intervalAmount += amount;

                // Category analysis
                String category = exp.getCategory();
                Double categoryTotal = categoryTotals.get(category);
                categoryTotals.put(category, (categoryTotal == null) ? amount : categoryTotal + amount);

                // Phase analysis
                Phase phase = exp.getPhase();
                Double phaseTotal = phaseTotals.get(phase);
                phaseTotals.put(phase, (phaseTotal == null) ? amount : phaseTotal + amount);
            }

            totalAmount += intervalAmount;
            totalCount += intervalExpenditures.size();

            // Add interval report
            report.append(String.format("Period: %s to %s\n",
                    currentStart.format(dateFormatter), currentEnd.format(dateFormatter)));
            report.append(String.format("Expenditures: %d, Amount: GHS %.2f\n",
                    intervalExpenditures.size(), intervalAmount));

            // Top categories
            if (categoryTotals.size() > 0) {
                report.append("Top categories:\n");
                CustomLinkedList<String> sortedCategories = sortCategoriesByAmount(categoryTotals);
                for (int i = 0; i < Math.min(3, sortedCategories.size()); i++) {
                    String category = sortedCategories.get(i);
                    double amount = categoryTotals.get(category);
                    report.append(String.format("  - %s: GHS %.2f\n", category, amount));
                }
            }

            report.append("\n");
            currentStart = currentEnd.plusDays(1);
        }

        // Summary statistics
        report.append("SUMMARY\n");
        report.append("=======\n");
        report.append(String.format("Total Period: %s to %s\n",
                startDate.format(dateFormatter), endDate.format(dateFormatter)));
        report.append(String.format("Total Expenditures: %d\n", totalCount));
        report.append(String.format("Total Amount: GHS %.2f\n", totalAmount));

        if (totalCount > 0) {
            double averageAmount = totalAmount / totalCount;
            report.append(String.format("Average per Expenditure: GHS %.2f\n", averageAmount));

            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            if (totalDays > 0) {
                double dailyAverage = totalAmount / totalDays;
                report.append(String.format("Daily Average: GHS %.2f\n", dailyAverage));
            }
        }

        return report.toString();
    }

    /**
     * Helper method to sort categories by amount (descending)
     */
    private CustomLinkedList<String> sortCategoriesByAmount(CustomHashMap<String, Double> categoryTotals) {
        // Convert to arrays for sorting
        CustomLinkedList<String> categories = new CustomLinkedList<>();
        CustomLinkedList<Double> amounts = new CustomLinkedList<>();

        for (CustomHashMap.Entry<String, Double> entry : categoryTotals.entries()) {
            categories.add(entry.getKey());
            amounts.add(entry.getValue());
        }

        // Convert to arrays for easier swapping
        int size = categories.size();
        String[] categoryArray = new String[size];
        Double[] amountArray = new Double[size];

        for (int i = 0; i < size; i++) {
            categoryArray[i] = categories.get(i);
            amountArray[i] = amounts.get(i);
        }

        // Simple bubble sort by amount (descending)
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (amountArray[j] < amountArray[j + 1]) {
                    // Swap amounts
                    Double tempAmount = amountArray[j];
                    amountArray[j] = amountArray[j + 1];
                    amountArray[j + 1] = tempAmount;

                    // Swap categories
                    String tempCategory = categoryArray[j];
                    categoryArray[j] = categoryArray[j + 1];
                    categoryArray[j + 1] = tempCategory;
                }
            }
        }

        // Convert back to CustomLinkedList
        CustomLinkedList<String> result = new CustomLinkedList<>();
        for (String category : categoryArray) {
            result.add(category);
        }

        return result;
    }

    /**
     * Find high-value expenditures efficiently using binary search
     * Time Complexity: O(log n + k) where k is number of high-value expenditures
     * 
     * @param percentile The percentile threshold (e.g., 0.9 for top 10%)
     * @return List of high-value expenditures
     */
    public CustomLinkedList<Expenditure> findHighValueExpenditures(double percentile) {
        // Get all expenditures sorted by amount
        CustomLinkedList<Expenditure> sortedExpenditures = expenditureService.sortExpendituresByAmount(true);

        if (sortedExpenditures.isEmpty()) {
            return new CustomLinkedList<>();
        }

        // Calculate threshold index
        int thresholdIndex = (int) (sortedExpenditures.size() * percentile);
        if (thresholdIndex >= sortedExpenditures.size()) {
            thresholdIndex = sortedExpenditures.size() - 1;
        }

        double thresholdAmount = sortedExpenditures.get(thresholdIndex).getAmount();

        // Use binary search to find all expenditures >= threshold
        return expenditureService.binarySearchAmountRange(thresholdAmount, Double.MAX_VALUE);
    }

    /**
     * Analyze cost trends over time using optimized data retrieval
     * 
     * @param category   Optional category to focus on (null for all categories)
     * @param monthsBack Number of months to analyze
     * @return Trend analysis report
     */
    public String analyzeCostTrendsOptimized(String category, int monthsBack) {
        StringBuilder report = new StringBuilder();
        report.append("COST TREND ANALYSIS (OPTIMIZED)\n");
        report.append("===============================\n\n");

        if (category != null) {
            report.append(String.format("Category Focus: %s\n", category));
        } else {
            report.append("Category Focus: All Categories\n");
        }
        report.append(String.format("Analysis Period: %d months\n\n", monthsBack));

        LocalDate endDate = LocalDate.now();
        CustomLinkedList<Double> monthlyTotals = new CustomLinkedList<>();
        CustomLinkedList<String> monthLabels = new CustomLinkedList<>();

        // Collect monthly data using binary search
        for (int i = monthsBack - 1; i >= 0; i--) {
            LocalDate monthStart = endDate.minusMonths(i + 1).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            // Use binary search for efficient date range retrieval
            CustomLinkedList<Expenditure> monthExpenditures = expenditureService.binarySearchDateRange(monthStart,
                    monthEnd);

            double monthTotal = 0.0;

            for (int j = 0; j < monthExpenditures.size(); j++) {
                Expenditure exp = monthExpenditures.get(j);
                if (category == null || exp.getCategory().equalsIgnoreCase(category)) {
                    monthTotal += exp.getAmount();
                }
            }

            monthlyTotals.add(monthTotal);
            monthLabels.add(monthStart.getMonth().toString() + " " + monthStart.getYear());
        }

        // Generate trend report
        report.append("Monthly Breakdown:\n");
        double totalTrend = 0.0;

        for (int i = 0; i < monthlyTotals.size(); i++) {
            double amount = monthlyTotals.get(i);
            String label = monthLabels.get(i);
            report.append(String.format("%-15s: GHS %,10.2f", label, amount));

            // Calculate trend indicator
            if (i > 0) {
                double previousAmount = monthlyTotals.get(i - 1);
                if (previousAmount > 0) {
                    double changePercent = ((amount - previousAmount) / previousAmount) * 100;
                    if (changePercent > 5) {
                        report.append(" ↑ (+").append(String.format("%.1f", changePercent)).append("%)");
                    } else if (changePercent < -5) {
                        report.append(" ↓ (").append(String.format("%.1f", changePercent)).append("%)");
                    } else {
                        report.append(" → (").append(String.format("%.1f", changePercent)).append("%)");
                    }
                    totalTrend += changePercent;
                }
            }
            report.append("\n");
        }

        // Trend summary
        report.append("\nTRend Summary:\n");
        if (monthsBack > 1) {
            double averageTrend = totalTrend / (monthsBack - 1);
            report.append(String.format("Average monthly change: %.2f%%\n", averageTrend));

            if (averageTrend > 5) {
                report.append("Status: INCREASING TREND - Monitor costs closely\n");
            } else if (averageTrend < -5) {
                report.append("Status: DECREASING TREND - Cost control effective\n");
            } else {
                report.append("Status: STABLE TREND - Consistent spending pattern\n");
            }
        }

        return report.toString();
    }

    /**
     * Performance comparison for financial analysis queries
     * 
     * @param startDate Start date for analysis
     * @param endDate   End date for analysis
     */
    public void performanceComparisonFinancialAnalysis(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n=== FINANCIAL ANALYSIS PERFORMANCE COMPARISON ===");

        long startTime, endTime;

        // Test linear approach (getting all expenditures first, then filtering)
        startTime = System.nanoTime();
        CustomLinkedList<Expenditure> allExpenditures = expenditureService.getAllExpenditures();
        CustomLinkedList<Expenditure> linearResults = new CustomLinkedList<>();
        for (int i = 0; i < allExpenditures.size(); i++) {
            Expenditure exp = allExpenditures.get(i);
            LocalDate expDate = exp.getDate();
            if ((expDate.isEqual(startDate) || expDate.isAfter(startDate)) &&
                    (expDate.isEqual(endDate) || expDate.isBefore(endDate))) {
                linearResults.add(exp);
            }
        }
        endTime = System.nanoTime();
        long linearTime = endTime - startTime;

        // Test binary search approach
        startTime = System.nanoTime();
        CustomLinkedList<Expenditure> binaryResults = expenditureService.binarySearchDateRange(startDate, endDate);
        endTime = System.nanoTime();
        long binaryTime = endTime - startTime;

        System.out.println("Date range: " + startDate.format(dateFormatter) + " to " + endDate.format(dateFormatter));
        System.out.println("Linear Search Time: " + linearTime + " nanoseconds");
        System.out.println("Binary Search Time: " + binaryTime + " nanoseconds");
        System.out.println("Results count - Linear: " + linearResults.size() + ", Binary: " + binaryResults.size());

        if (linearTime > 0 && binaryTime > 0) {
            double speedup = (double) linearTime / binaryTime;
            System.out
                    .println("Binary search is " + String.format("%.2f", speedup) + "x faster for financial analysis");
        }

        System.out.println("===================================================\n");
    }

    /**
     * Generate profitability forecast based on historical spending patterns
     * Time Complexity: O(n) where n is number of historical records
     * Space Complexity: O(m) where m is forecast period
     * 
     * @param monthsAhead Number of months to forecast
     * @return Profitability forecast with trend analysis
     */
    public ProfitabilityForecast generateProfitabilityForecast(int monthsAhead) {
        // 1. Analyze historical spending patterns
        CustomLinkedList<MonthlyData> historicalData = analyzeHistoricalSpending();

        // 2. Calculate trend coefficients
        double trendCoefficient = calculateSpendingTrend(historicalData);

        // 3. Project future expenditures and revenues
        CustomLinkedList<ForecastData> forecast = new CustomLinkedList<>();

        for (int i = 1; i <= monthsAhead; i++) {
            double projectedSpending = projectMonthlySpending(i, trendCoefficient, historicalData);
            double projectedRevenue = estimateMonthlyRevenue(i, historicalData);
            double profitability = projectedRevenue - projectedSpending;

            ForecastData monthForecast = new ForecastData(
                    LocalDate.now().plusMonths(i),
                    projectedSpending,
                    projectedRevenue,
                    profitability,
                    (profitability / projectedRevenue) * 100 // Profit margin percentage
            );
            forecast.add(monthForecast);
        }

        return new ProfitabilityForecast(forecast, trendCoefficient,
                calculateConfidenceLevel(historicalData));
    }

    /**
     * Analyze how building material price changes affect house affordability
     * Time Complexity: O(n * m) where n is expenditures, m is material categories
     * Space Complexity: O(k) where k is number of affected projects
     * 
     * @param priceIncreasePercentage Percentage increase in material prices
     * @return Affordability analysis with cost impact breakdown
     */
    public AffordabilityAnalysis analyzeMaterialPriceImpact(double priceIncreasePercentage) {
        // 1. Identify material-related expenditures
        CustomLinkedList<String> materialCategories = getMaterialCategories();
        CustomHashMap<String, ProjectCostImpact> projectImpacts = new CustomHashMap<>();

        double totalMaterialCost = 0.0;
        double totalAdditionalCost = 0.0;

        // 2. Calculate cost impact per category and project
        for (int i = 0; i < materialCategories.size(); i++) {
            String category = materialCategories.get(i);
            CustomLinkedList<Expenditure> materialExpenses = expenditureService.getExpendituresByCategory(category);

            for (int j = 0; j < materialExpenses.size(); j++) {
                Expenditure expense = materialExpenses.get(j);
                String projectId = extractProjectId(expense);
                double originalCost = expense.getAmount();
                double additionalCost = originalCost * (priceIncreasePercentage / 100.0);

                totalMaterialCost += originalCost;
                totalAdditionalCost += additionalCost;

                updateProjectImpact(projectImpacts, projectId, originalCost, additionalCost, category);
            }
        }

        // 3. Analyze affordability thresholds
        AffordabilityThresholds thresholds = calculateAffordabilityThresholds(
                totalMaterialCost, totalAdditionalCost, priceIncreasePercentage);

        // 4. Generate recommendations
        CustomLinkedList<String> recommendations = generateAffordabilityRecommendations(
                projectImpacts, thresholds, priceIncreasePercentage);

        return new AffordabilityAnalysis(
                projectImpacts,
                thresholds,
                recommendations,
                priceIncreasePercentage,
                totalMaterialCost,
                totalAdditionalCost);
    }

    // Helper methods for profitability forecasting
    private CustomLinkedList<MonthlyData> analyzeHistoricalSpending() {
        CustomLinkedList<MonthlyData> monthlyData = new CustomLinkedList<>();
        LocalDate currentDate = LocalDate.now();

        // Analyze last 12 months of spending
        for (int i = 11; i >= 0; i--) {
            LocalDate monthStart = currentDate.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            CustomLinkedList<Expenditure> monthExpenses = expenditureService.getExpendituresByDateRange(monthStart,
                    monthEnd);

            double totalSpending = 0.0;
            CustomHashMap<String, Double> categorySpending = new CustomHashMap<>();

            for (int j = 0; j < monthExpenses.size(); j++) {
                Expenditure expense = monthExpenses.get(j);
                totalSpending += expense.getAmount();

                String category = expense.getCategory();
                Double currentAmount = categorySpending.get(category);
                if (currentAmount == null)
                    currentAmount = 0.0;
                categorySpending.put(category, currentAmount + expense.getAmount());
            }

            monthlyData.add(new MonthlyData(monthStart, totalSpending, categorySpending, monthExpenses.size()));
        }

        return monthlyData;
    }

    private double calculateSpendingTrend(CustomLinkedList<MonthlyData> historicalData) {
        if (historicalData.size() < 2)
            return 0.0;

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        int n = historicalData.size();

        // Linear regression to find trend
        for (int i = 0; i < n; i++) {
            double x = i; // Month index
            double y = historicalData.get(i).getTotalSpending();

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        // Calculate slope (trend coefficient)
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        return slope;
    }

    private double projectMonthlySpending(int monthsAhead, double trendCoefficient,
            CustomLinkedList<MonthlyData> historicalData) {
        if (historicalData.isEmpty())
            return 0.0;

        // Get average spending from recent months
        double recentAverage = 0.0;
        int recentMonths = Math.min(3, historicalData.size());

        for (int i = historicalData.size() - recentMonths; i < historicalData.size(); i++) {
            recentAverage += historicalData.get(i).getTotalSpending();
        }
        recentAverage /= recentMonths;

        // Project based on trend and seasonal factors
        double projectedSpending = recentAverage + (trendCoefficient * monthsAhead);

        // Apply seasonal adjustment (construction industry peaks in dry season)
        LocalDate targetMonth = LocalDate.now().plusMonths(monthsAhead);
        double seasonalFactor = getSeasonalFactor(targetMonth.getMonth());

        return Math.max(0, projectedSpending * seasonalFactor);
    }

    private double estimateMonthlyRevenue(int monthsAhead, CustomLinkedList<MonthlyData> historicalData) {
        // Simple revenue estimation based on spending patterns
        // In construction, revenue typically follows completion cycles

        if (historicalData.isEmpty())
            return 0.0;

        // Calculate average monthly spending
        double avgSpending = 0.0;
        for (int i = 0; i < historicalData.size(); i++) {
            avgSpending += historicalData.get(i).getTotalSpending();
        }
        avgSpending /= historicalData.size();

        // Estimate revenue as 120-150% of spending (profit margin)
        double profitMargin = 1.35; // 35% profit margin
        double baseRevenue = avgSpending * profitMargin;

        // Apply growth factor for future months
        double growthRate = 0.02; // 2% monthly growth
        return baseRevenue * Math.pow(1 + growthRate, monthsAhead);
    }

    private double getSeasonalFactor(Month month) {
        // Construction industry seasonal factors for Ghana
        switch (month) {
            case NOVEMBER:
            case DECEMBER:
            case JANUARY:
            case FEBRUARY:
                return 1.2; // Dry season - high activity
            case MARCH:
            case APRIL:
                return 1.1; // Peak dry season
            case MAY:
            case JUNE:
            case JULY:
                return 0.8; // Rainy season - reduced activity
            case AUGUST:
            case SEPTEMBER:
            case OCTOBER:
                return 0.9; // Post-rainy season recovery
            default:
                return 1.0;
        }
    }

    private double calculateConfidenceLevel(CustomLinkedList<MonthlyData> historicalData) {
        if (historicalData.size() < 3)
            return 0.5; // Low confidence with limited data

        // Calculate variance in monthly spending
        double mean = 0.0;
        for (int i = 0; i < historicalData.size(); i++) {
            mean += historicalData.get(i).getTotalSpending();
        }
        mean /= historicalData.size();

        double variance = 0.0;
        for (int i = 0; i < historicalData.size(); i++) {
            double diff = historicalData.get(i).getTotalSpending() - mean;
            variance += diff * diff;
        }
        variance /= historicalData.size();

        // Lower variance = higher confidence
        double coefficientOfVariation = Math.sqrt(variance) / mean;
        return Math.max(0.3, Math.min(0.95, 1.0 - coefficientOfVariation));
    }

    // Helper methods for material price impact analysis
    private CustomLinkedList<String> getMaterialCategories() {
        CustomLinkedList<String> materials = new CustomLinkedList<>();
        materials.add("Cement");
        materials.add("Steel Bars");
        materials.add("Roofing Sheets");
        materials.add("Electrical Materials");
        materials.add("Plumbing Materials");
        materials.add("Paint");
        materials.add("Tiles");
        materials.add("Windows and Doors");
        materials.add("Hardware");
        return materials;
    }

    private String extractProjectId(Expenditure expense) {
        // Extract project ID from expenditure code or description
        // For now, use account ID as project identifier
        return expense.getAccountId();
    }

    private void updateProjectImpact(CustomHashMap<String, ProjectCostImpact> projectImpacts,
            String projectId, double originalCost, double additionalCost, String category) {
        ProjectCostImpact impact = projectImpacts.get(projectId);
        if (impact == null) {
            impact = new ProjectCostImpact(projectId);
            projectImpacts.put(projectId, impact);
        }

        impact.addMaterialCost(category, originalCost, additionalCost);
    }

    private AffordabilityThresholds calculateAffordabilityThresholds(double totalMaterialCost,
            double totalAdditionalCost,
            double priceIncreasePercentage) {
        // Calculate various affordability thresholds
        double criticalThreshold = totalMaterialCost * 0.15; // 15% increase is critical
        double manageable = totalMaterialCost * 0.05; // 5% is manageable
        double severe = totalMaterialCost * 0.25; // 25% is severe

        String impactLevel;
        if (totalAdditionalCost <= manageable) {
            impactLevel = "MANAGEABLE";
        } else if (totalAdditionalCost <= criticalThreshold) {
            impactLevel = "MODERATE";
        } else if (totalAdditionalCost <= severe) {
            impactLevel = "CRITICAL";
        } else {
            impactLevel = "SEVERE";
        }

        return new AffordabilityThresholds(manageable, criticalThreshold, severe, impactLevel);
    }

    private CustomLinkedList<String> generateAffordabilityRecommendations(
            CustomHashMap<String, ProjectCostImpact> projectImpacts,
            AffordabilityThresholds thresholds,
            double priceIncreasePercentage) {

        CustomLinkedList<String> recommendations = new CustomLinkedList<>();

        if (thresholds.getImpactLevel().equals("MANAGEABLE")) {
            recommendations.add("• Continue with current project plans");
            recommendations.add("• Monitor material costs for further increases");
            recommendations.add("• Consider bulk purchasing to lock in current prices");
        } else if (thresholds.getImpactLevel().equals("MODERATE")) {
            recommendations.add("• Review project budgets and adjust pricing");
            recommendations.add("• Negotiate with suppliers for better rates");
            recommendations.add("• Consider alternative materials where possible");
            recommendations.add("• Implement cost control measures");
        } else if (thresholds.getImpactLevel().equals("CRITICAL")) {
            recommendations.add("• Urgent review of all ongoing projects required");
            recommendations.add("• Renegotiate contracts with clients if possible");
            recommendations.add("• Seek alternative suppliers urgently");
            recommendations.add("• Consider delaying non-critical projects");
            recommendations.add("• Implement strict cost monitoring");
        } else {
            recommendations.add("• EMERGENCY ACTION REQUIRED");
            recommendations.add("• Halt new project commitments immediately");
            recommendations.add("• Review financial sustainability of current projects");
            recommendations.add("• Consider project cancellations or major redesigns");
            recommendations.add("• Seek emergency financing or investor support");
        }

        return recommendations;
    }
}
