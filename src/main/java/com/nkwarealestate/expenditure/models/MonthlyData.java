package com.nkwarealestate.expenditure.models;

import com.nkwarealestate.expenditure.datastructures.CustomHashMap;
import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;
import java.time.LocalDate;

/**
 * Represents monthly spending data for analysis
 */
public class MonthlyData {
    private LocalDate month;
    private double totalSpending;
    private CustomHashMap<String, Double> categorySpending;
    private int transactionCount;

    public MonthlyData(LocalDate month, double totalSpending,
            CustomHashMap<String, Double> categorySpending,
            int transactionCount) {
        this.month = month;
        this.totalSpending = totalSpending;
        this.categorySpending = categorySpending;
        this.transactionCount = transactionCount;
    }

    // Getters
    public LocalDate getMonth() {
        return month;
    }

    public double getTotalSpending() {
        return totalSpending;
    }

    public CustomHashMap<String, Double> getCategorySpending() {
        return categorySpending;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public double getAverageTransactionAmount() {
        return transactionCount > 0 ? totalSpending / transactionCount : 0.0;
    }

    public String getTopSpendingCategory() {
        if (categorySpending == null || categorySpending.isEmpty()) {
            return "None";
        }

        String topCategory = "";
        double maxAmount = 0.0;

        CustomLinkedList<String> categories = categorySpending.keySet();
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            Double amount = categorySpending.get(category);
            if (amount != null && amount > maxAmount) {
                maxAmount = amount;
                topCategory = category;
            }
        }

        return topCategory.isEmpty() ? "None" : topCategory;
    }
}
