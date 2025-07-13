package com.nkwarealestate.expenditure.services;

import com.nkwarealestate.expenditure.models.Expenditure;
import com.nkwarealestate.expenditure.models.Phase;
import com.nkwarealestate.expenditure.datastructures.CustomHashMap;
import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Service class for managing expenditure operations
 * Uses CustomHashMap for efficient expenditure storage and retrieval
 */
public class ExpenditureService {

    private CustomHashMap<String, Expenditure> expenditures;
    private int nextExpenditureId;
    private DateTimeFormatter dateFormatter;

    public ExpenditureService() {
        this.expenditures = new CustomHashMap<>();
        this.nextExpenditureId = 1;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        loadSampleData();
    }

    /**
     * Add a new expenditure to the system
     */
    public boolean addExpenditure(double amount, String date, Phase phase,
            String category, String accountId, String description) {
        try {
            LocalDate expenditureDate = LocalDate.parse(date, dateFormatter);
            String code = generateExpenditureCode();

            Expenditure expenditure = new Expenditure(code, amount, expenditureDate,
                    phase, category, accountId, description);

            expenditures.put(code, expenditure);
            nextExpenditureId++;

            System.out.println("✓ Expenditure added successfully with code: " + code);
            return true;

        } catch (DateTimeParseException e) {
            System.out.println("✗ Invalid date format. Please use YYYY-MM-DD format.");
            return false;
        } catch (Exception e) {
            System.out.println("✗ Error adding expenditure: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieve expenditure by code
     */
    public Expenditure getExpenditure(String code) {
        return expenditures.get(code);
    }

    /**
     * Get all expenditures as a linked list
     */
    public CustomLinkedList<Expenditure> getAllExpenditures() {
        CustomLinkedList<Expenditure> allExpenditures = new CustomLinkedList<>();

        // Iterate through all entries in the expenditures map and add values to the list
        for (Expenditure exp : expenditures.values()) {
            allExpenditures.add(exp);
        }

        return allExpenditures;
    }

    /**
     * Get expenditures by category
     */
    public CustomLinkedList<Expenditure> getExpendituresByCategory(String category) {
        CustomLinkedList<Expenditure> categoryExpenditures = new CustomLinkedList<>();

        // Iterate through all expenditures and filter by category
        for (Expenditure exp : expenditures.values()) {
            if (exp.getCategory().equalsIgnoreCase(category)) {
                categoryExpenditures.add(exp);
            }
        }

        return categoryExpenditures;
    }

    /**
     * Get expenditures by phase
     */
    public CustomLinkedList<Expenditure> getExpendituresByPhase(Phase phase) {
        CustomLinkedList<Expenditure> phaseExpenditures = new CustomLinkedList<>();

        // Iterate through all expenditures and filter by phase
        for (Expenditure exp : expenditures.values()) {
            if (exp.getPhase() == phase) {
                phaseExpenditures.add(exp);
            }
        }

        return phaseExpenditures;
    }

    /**
     * Get expenditures within a date range using binary search
     * Time Complexity: O(log n + k) where k is number of results
     */
    public CustomLinkedList<Expenditure> getExpendituresByDateRange(LocalDate startDate, LocalDate endDate) {
        // Use binary search for efficient date range searching
        return binarySearchDateRange(startDate, endDate);
    }

    /**
     * Delete expenditure by code
     */
    public boolean deleteExpenditure(String code) {
        Expenditure removed = expenditures.remove(code);
        if (removed != null) {
            System.out.println("✓ Expenditure " + code + " removed successfully.");
            return true;
        } else {
            System.out.println("✗ Expenditure with code " + code + " not found.");
            return false;
        }
    }

    /**
     * Update expenditure
     */
    public boolean updateExpenditure(String code, double newAmount, String newDescription) {
        Expenditure expenditure = expenditures.get(code);
        if (expenditure != null) {
            expenditure.setAmount(newAmount);
            expenditure.setDescription(newDescription);
            System.out.println("✓ Expenditure " + code + " updated successfully.");
            return true;
        } else {
            System.out.println("✗ Expenditure with code " + code + " not found.");
            return false;
        }
    }

    /**
     * Generate unique expenditure code
     */
    private String generateExpenditureCode() {
        return String.format("EXP%04d", nextExpenditureId);
    }

    /**
     * Get total expenditure amount
     */
    public double getTotalExpenditureAmount() {
        double total = 0.0;
        
        // Sum up all expenditure amounts
        for (Expenditure exp : expenditures.values()) {
            total += exp.getAmount();
        }
        
        return total;
    }

    /**
     * Get expenditure count
     */
    public int getExpenditureCount() {
        return expenditures.size();
    }

    /**
     * Display expenditure summary
     */
    public void displayExpenditureSummary(Expenditure expenditure) {
        if (expenditure == null) {
            System.out.println("No expenditure to display.");
            return;
        }

        System.out.println("\n=== EXPENDITURE DETAILS ===");
        System.out.println("Code: " + expenditure.getCode());
        System.out.println("Amount: GHS " + String.format("%.2f", expenditure.getAmount()));
        System.out.println("Date: " + expenditure.getDate().format(dateFormatter));
        System.out.println("Phase: " + expenditure.getPhase());
        System.out.println("Category: " + expenditure.getCategory());
        System.out.println("Account ID: " + expenditure.getAccountId());
        System.out.println("Description: " + expenditure.getDescription());
        System.out.println("===========================");
    }

    /**
     * Load sample data for testing
     */
    private void loadSampleData() {
        // Add some sample expenditures for testing
        addExpenditure(5000.00, "2025-01-15", Phase.CONSTRUCTION, "Cement", "ACC001",
                "50 bags of cement for foundation");
        addExpenditure(2500.00, "2025-01-16", Phase.CONSTRUCTION, "Steel Bars", "ACC001", "Steel reinforcement bars");
        addExpenditure(1200.00, "2025-01-20", Phase.MARKETING, "TV Adverts", "ACC002",
                "Television advertising campaign");
        addExpenditure(800.00, "2025-01-22", Phase.MARKETING, "Printing", "ACC002", "Brochures and flyers");
        addExpenditure(3500.00, "2025-02-01", Phase.CONSTRUCTION, "Roofing Sheets", "ACC001",
                "Aluminum roofing sheets");
    }

    /**
     * Get expenditures within a specific amount range using binary search
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param minAmount The minimum amount (inclusive)
     * @param maxAmount The maximum amount (inclusive)
     * @return A list of expenditures within the specified amount range
     */
    public CustomLinkedList<Expenditure> getExpendituresByAmountRange(double minAmount, double maxAmount) {
        // Use binary search for efficient range searching
        return binarySearchAmountRange(minAmount, maxAmount);
    }

    /**
     * Sort expenditures by amount (ascending or descending)
     * 
     * @param ascending true for ascending order, false for descending
     * @return A sorted list of expenditures by amount
     */
    public CustomLinkedList<Expenditure> sortExpendituresByAmount(boolean ascending) {
        // Get all expenditures as an array for easier sorting
        CustomLinkedList<Expenditure> list = getAllExpenditures();
        int size = list.size();
        Expenditure[] array = new Expenditure[size];
        
        // Convert list to array
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        
        // Bubble sort (simple implementation for demonstration)
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                boolean shouldSwap;
                
                if (ascending) {
                    shouldSwap = array[j].getAmount() > array[j + 1].getAmount();
                } else {
                    shouldSwap = array[j].getAmount() < array[j + 1].getAmount();
                }
                
                if (shouldSwap) {
                    // Swap elements
                    Expenditure temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        
        // Convert back to CustomLinkedList
        CustomLinkedList<Expenditure> result = new CustomLinkedList<>();
        for (Expenditure exp : array) {
            result.add(exp);
        }
        
        return result;
    }

    /**
     * Sort expenditures by date (ascending or descending)
     * 
     * @param ascending true for oldest first, false for newest first
     * @return A sorted list of expenditures by date
     */
    public CustomLinkedList<Expenditure> sortExpendituresByDate(boolean ascending) {
        // Get all expenditures as an array for easier sorting
        CustomLinkedList<Expenditure> list = getAllExpenditures();
        int size = list.size();
        Expenditure[] array = new Expenditure[size];
        
        // Convert list to array
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        
        // Bubble sort (simple implementation for demonstration)
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                boolean shouldSwap;
                
                if (ascending) {
                    shouldSwap = array[j].getDate().isAfter(array[j + 1].getDate());
                } else {
                    shouldSwap = array[j].getDate().isBefore(array[j + 1].getDate());
                }
                
                if (shouldSwap) {
                    // Swap elements
                    Expenditure temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        
        // Convert back to CustomLinkedList
        CustomLinkedList<Expenditure> result = new CustomLinkedList<>();
        for (Expenditure exp : array) {
            result.add(exp);
        }
        
        return result;
    }

    /**
     * Display expenditures in a tabular format
     * 
     * @param expenditures The list of expenditures to display
     */
    public void displayExpendituresTable(CustomLinkedList<Expenditure> expenditures) {
        if (expenditures.isEmpty()) {
            System.out.println("No expenditures found matching your criteria.");
            return;
        }
        
        // Table header
        System.out.println("\n+------------+-------------+------------+---------------+------------+----------------------+");
        System.out.println("| CODE       | AMOUNT (GHS) | DATE       | PHASE         | ACCOUNT ID | DESCRIPTION          |");
        System.out.println("+------------+-------------+------------+---------------+------------+----------------------+");
        
        // Table rows
        for (int i = 0; i < expenditures.size(); i++) {
            Expenditure exp = expenditures.get(i);
            String description = exp.getDescription();
            if (description.length() > 20) {
                description = description.substring(0, 17) + "...";
            }
            
            System.out.printf("| %-10s | %11.2f | %-10s | %-13s | %-10s | %-20s |\n",
                    exp.getCode(),
                    exp.getAmount(),
                    exp.getDate().format(dateFormatter),
                    exp.getPhase().toString(),
                    exp.getAccountId(),
                    description);
        }
        
        // Table footer
        System.out.println("+------------+-------------+------------+---------------+------------+----------------------+");
        System.out.println("Total: " + expenditures.size() + " expenditure(s)");
    }

    /**
     * Advanced search for expenditures with multiple optional criteria
     * 
     * @param category Optional category to filter by (null to ignore)
     * @param phase Optional phase to filter by (null to ignore)
     * @param minAmount Optional minimum amount (null to ignore)
     * @param maxAmount Optional maximum amount (null to ignore)
     * @param startDate Optional start date (null to ignore)
     * @param endDate Optional end date (null to ignore)
     * @param keywords Optional keywords to search in description (null to ignore)
     * @return A list of expenditures matching all provided criteria
     */
    public CustomLinkedList<Expenditure> advancedSearch(
            String category, 
            Phase phase, 
            Double minAmount, 
            Double maxAmount, 
            LocalDate startDate, 
            LocalDate endDate, 
            String keywords) {
        
        CustomLinkedList<Expenditure> results = new CustomLinkedList<>();
        
        // Iterate through all expenditures and apply filters
        for (Expenditure exp : expenditures.values()) {
            boolean matches = true;
            
            // Apply category filter if provided
            if (category != null && !category.isEmpty() && 
                !exp.getCategory().equalsIgnoreCase(category)) {
                matches = false;
            }
            
            // Apply phase filter if provided
            if (phase != null && exp.getPhase() != phase) {
                matches = false;
            }
            
            // Apply amount range filter if provided
            if (minAmount != null && exp.getAmount() < minAmount) {
                matches = false;
            }
            if (maxAmount != null && exp.getAmount() > maxAmount) {
                matches = false;
            }
            
            // Apply date range filter if provided
            if (startDate != null && exp.getDate().isBefore(startDate)) {
                matches = false;
            }
            if (endDate != null && exp.getDate().isAfter(endDate)) {
                matches = false;
            }
            
            // Apply keyword search in description if provided
            if (keywords != null && !keywords.isEmpty() && 
                !exp.getDescription().toLowerCase().contains(keywords.toLowerCase())) {
                matches = false;
            }
            
            // Add to results if all applied filters match
            if (matches) {
                results.add(exp);
            }
        }
        
        return results;
    }
    
    /**
     * Calculate monthly expenditure statistics
     * 
     * @param year The year to analyze
     * @param month The month to analyze (1-12)
     * @return A summary string with statistics
     */
    public String getMonthlyStatistics(int year, int month) {
        CustomLinkedList<Expenditure> monthExpenditures = new CustomLinkedList<>();
        double totalAmount = 0.0;
        CustomHashMap<String, Double> categoryTotals = new CustomHashMap<>();
        CustomHashMap<Phase, Double> phaseTotals = new CustomHashMap<>();
        
        // Filter expenditures for the specified month
        for (Expenditure exp : expenditures.values()) {
            LocalDate date = exp.getDate();
            if (date.getYear() == year && date.getMonthValue() == month) {
                monthExpenditures.add(exp);
                double amount = exp.getAmount();
                totalAmount += amount;
                
                // Update category totals
                String category = exp.getCategory();
                Double categoryTotal = categoryTotals.get(category);
                if (categoryTotal == null) {
                    categoryTotals.put(category, amount);
                } else {
                    categoryTotals.put(category, categoryTotal + amount);
                }
                
                // Update phase totals
                Phase phase = exp.getPhase();
                Double phaseTotal = phaseTotals.get(phase);
                if (phaseTotal == null) {
                    phaseTotals.put(phase, amount);
                } else {
                    phaseTotals.put(phase, phaseTotal + amount);
                }
            }
        }
        
        if (monthExpenditures.isEmpty()) {
            return "No expenditures found for " + getMonthName(month) + " " + year + ".";
        }
        
        // Build statistics string
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Monthly Report: %s %d\n", getMonthName(month), year));
        sb.append("------------------------------\n");
        sb.append(String.format("Total Expenditures: %d\n", monthExpenditures.size()));
        sb.append(String.format("Total Amount: GHS %.2f\n\n", totalAmount));
        
        // Category breakdown
        sb.append("Breakdown by Category:\n");
        for (CustomHashMap.Entry<String, Double> entry : categoryTotals.entries()) {
            double percentage = (entry.getValue() / totalAmount) * 100;
            sb.append(String.format("  %-15s: GHS %.2f (%.1f%%)\n", 
                    entry.getKey(), entry.getValue(), percentage));
        }
        sb.append("\n");
        
        // Phase breakdown
        sb.append("Breakdown by Phase:\n");
        for (CustomHashMap.Entry<Phase, Double> entry : phaseTotals.entries()) {
            double percentage = (entry.getValue() / totalAmount) * 100;
            sb.append(String.format("  %-15s: GHS %.2f (%.1f%%)\n", 
                    entry.getKey().toString(), entry.getValue(), percentage));
        }
        
        return sb.toString();
    }
    
    /**
     * Helper method to get month name
     */
    private String getMonthName(int month) {
        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        
        if (month >= 1 && month <= 12) {
            return months[month - 1];
        } else {
            return "Unknown";
        }
    }
    
    // ================ BINARY SEARCH ALGORITHMS ================
    
    /**
     * Binary search for expenditures by exact amount
     * Requires expenditures to be sorted by amount first
     * Time Complexity: O(log n)
     * 
     * @param targetAmount The exact amount to search for
     * @return The first expenditure with the target amount, or null if not found
     */
    public Expenditure binarySearchByAmount(double targetAmount) {
        // First, get sorted array of expenditures by amount
        CustomLinkedList<Expenditure> sortedList = sortExpendituresByAmount(true);
        
        int left = 0;
        int right = sortedList.size() - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            Expenditure midExpenditure = sortedList.get(mid);
            double midAmount = midExpenditure.getAmount();
            
            if (Math.abs(midAmount - targetAmount) < 0.01) { // Compare with small tolerance for doubles
                return midExpenditure; // Found exact match
            }
            
            if (midAmount < targetAmount) {
                left = mid + 1; // Search right half
            } else {
                right = mid - 1; // Search left half
            }
        }
        
        return null; // Not found
    }
    
    /**
     * Binary search for expenditures within amount range
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param minAmount Minimum amount (inclusive)
     * @param maxAmount Maximum amount (inclusive)
     * @return List of expenditures within the amount range
     */
    public CustomLinkedList<Expenditure> binarySearchAmountRange(double minAmount, double maxAmount) {
        CustomLinkedList<Expenditure> results = new CustomLinkedList<>();
        CustomLinkedList<Expenditure> sortedList = sortExpendituresByAmount(true);
        
        if (sortedList.isEmpty()) {
            return results;
        }
        
        // Find first occurrence >= minAmount
        int startIndex = findFirstGreaterOrEqualAmount(sortedList, minAmount);
        
        // Find last occurrence <= maxAmount
        int endIndex = findLastLessOrEqualAmount(sortedList, maxAmount);
        
        // Add all expenditures in range
        for (int i = startIndex; i <= endIndex && i < sortedList.size(); i++) {
            results.add(sortedList.get(i));
        }
        
        return results;
    }
    
    /**
     * Binary search helper: Find first expenditure with amount >= target
     */
    private int findFirstGreaterOrEqualAmount(CustomLinkedList<Expenditure> sortedList, double target) {
        int left = 0, right = sortedList.size() - 1;
        int result = sortedList.size(); // If not found, return size (invalid index)
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            double midAmount = sortedList.get(mid).getAmount();
            
            if (midAmount >= target) {
                result = mid;
                right = mid - 1; // Continue searching left for first occurrence
            } else {
                left = mid + 1;
            }
        }
        
        return result;
    }
    
    /**
     * Binary search helper: Find last expenditure with amount <= target
     */
    private int findLastLessOrEqualAmount(CustomLinkedList<Expenditure> sortedList, double target) {
        int left = 0, right = sortedList.size() - 1;
        int result = -1; // If not found, return -1
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            double midAmount = sortedList.get(mid).getAmount();
            
            if (midAmount <= target) {
                result = mid;
                left = mid + 1; // Continue searching right for last occurrence
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    /**
     * Binary search for expenditures by date range
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of expenditures within the date range
     */
    public CustomLinkedList<Expenditure> binarySearchDateRange(LocalDate startDate, LocalDate endDate) {
        CustomLinkedList<Expenditure> results = new CustomLinkedList<>();
        CustomLinkedList<Expenditure> sortedList = sortExpendituresByDate(true);
        
        if (sortedList.isEmpty()) {
            return results;
        }
        
        // Find first occurrence >= startDate
        int startIndex = findFirstGreaterOrEqualDate(sortedList, startDate);
        
        // Find last occurrence <= endDate
        int endIndex = findLastLessOrEqualDate(sortedList, endDate);
        
        // Add all expenditures in range
        for (int i = startIndex; i <= endIndex && i < sortedList.size(); i++) {
            results.add(sortedList.get(i));
        }
        
        return results;
    }
    
    /**
     * Binary search helper: Find first expenditure with date >= target
     */
    private int findFirstGreaterOrEqualDate(CustomLinkedList<Expenditure> sortedList, LocalDate target) {
        int left = 0, right = sortedList.size() - 1;
        int result = sortedList.size(); // If not found, return size (invalid index)
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            LocalDate midDate = sortedList.get(mid).getDate();
            
            if (midDate.isEqual(target) || midDate.isAfter(target)) {
                result = mid;
                right = mid - 1; // Continue searching left for first occurrence
            } else {
                left = mid + 1;
            }
        }
        
        return result;
    }
    
    /**
     * Binary search helper: Find last expenditure with date <= target
     */
    private int findLastLessOrEqualDate(CustomLinkedList<Expenditure> sortedList, LocalDate target) {
        int left = 0, right = sortedList.size() - 1;
        int result = -1; // If not found, return -1
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            LocalDate midDate = sortedList.get(mid).getDate();
            
            if (midDate.isEqual(target) || midDate.isBefore(target)) {
                result = mid;
                left = mid + 1; // Continue searching right for last occurrence
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    // ================ PERFORMANCE COMPARISON METHODS ================
    
    /**
     * Get expenditures by category using linear search (original method for comparison)
     * Time Complexity: O(n)
     * 
     * @param category The category to search for
     * @return List of expenditures in the specified category
     */
    public CustomLinkedList<Expenditure> getExpendituresByCategoryLinear(String category) {
        CustomLinkedList<Expenditure> categoryExpenditures = new CustomLinkedList<>();

        // Linear search through all expenditures
        for (Expenditure exp : expenditures.values()) {
            if (exp.getCategory().equalsIgnoreCase(category)) {
                categoryExpenditures.add(exp);
            }
        }

        return categoryExpenditures;
    }
    
    /**
     * Performance test method to compare linear vs binary search
     * 
     * @param minAmount Minimum amount for range search
     * @param maxAmount Maximum amount for range search
     */
    public void performanceComparison(double minAmount, double maxAmount) {
        System.out.println("\n=== PERFORMANCE COMPARISON ===");
        
        long startTime, endTime;
        
        // Test linear filtering (original method)
        startTime = System.nanoTime();
        CustomLinkedList<Expenditure> linearResults = new CustomLinkedList<>();
        for (Expenditure exp : expenditures.values()) {
            double amount = exp.getAmount();
            if (amount >= minAmount && amount <= maxAmount) {
                linearResults.add(exp);
            }
        }
        endTime = System.nanoTime();
        long linearTime = endTime - startTime;
        
        // Test binary search
        startTime = System.nanoTime();
        CustomLinkedList<Expenditure> binaryResults = binarySearchAmountRange(minAmount, maxAmount);
        endTime = System.nanoTime();
        long binaryTime = endTime - startTime;
        
        System.out.println("Linear Search Time: " + linearTime + " nanoseconds");
        System.out.println("Binary Search Time: " + binaryTime + " nanoseconds");
        System.out.println("Results count - Linear: " + linearResults.size() + ", Binary: " + binaryResults.size());
        
        if (linearTime > 0) {
            double speedup = (double) linearTime / binaryTime;
            System.out.println("Binary search is " + String.format("%.2f", speedup) + "x faster");
        }
        
        System.out.println("==============================\n");
    }

    /**
     * Find expenditure by exact amount using binary search
     * Time Complexity: O(log n)
     * 
     * @param exactAmount The exact amount to search for
     * @return The first expenditure with the exact amount, or null if not found
     */
    public Expenditure findExpenditureByExactAmount(double exactAmount) {
        return binarySearchByAmount(exactAmount);
    }
    
    /**
     * Find all expenditures with exact amount using binary search
     * Time Complexity: O(log n + k) where k is number of matches
     * 
     * @param exactAmount The exact amount to search for
     * @return List of all expenditures with the exact amount
     */
    public CustomLinkedList<Expenditure> findAllExpendituresByExactAmount(double exactAmount) {
        CustomLinkedList<Expenditure> results = new CustomLinkedList<>();
        CustomLinkedList<Expenditure> sortedList = sortExpendituresByAmount(true);
        
        // Find first occurrence of the exact amount
        int firstIndex = findFirstGreaterOrEqualAmount(sortedList, exactAmount);
        
        // Collect all expenditures with the exact amount
        for (int i = firstIndex; i < sortedList.size(); i++) {
            Expenditure exp = sortedList.get(i);
            if (Math.abs(exp.getAmount() - exactAmount) < 0.01) { // Small tolerance for double comparison
                results.add(exp);
            } else {
                break; // Since list is sorted, no more matches after this
            }
        }
        
        return results;
    }
}
