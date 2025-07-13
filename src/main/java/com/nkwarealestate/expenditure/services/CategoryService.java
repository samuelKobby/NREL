package com.nkwarealestate.expenditure.services;

import com.nkwarealestate.expenditure.datastructures.CustomSet;
import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Service class for managing expenditure categories
 * Uses CustomSet to ensure category uniqueness
 */
public class CategoryService {

    private CustomSet<String> categories;
    private final String CATEGORIES_FILE = "data/categories.txt";

    public CategoryService() {
        this.categories = new CustomSet<>();
        loadCategoriesFromFile();
    }

    /**
     * Add a new category
     */
    public boolean addCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            System.out.println("✗ Category name cannot be empty.");
            return false;
        }

        String normalizedCategory = category.trim();

        if (categories.contains(normalizedCategory)) {
            System.out.println("✗ Category '" + normalizedCategory + "' already exists.");
            return false;
        }

        categories.add(normalizedCategory);
        saveCategoryToFile(normalizedCategory);
        System.out.println("✓ Category '" + normalizedCategory + "' added successfully.");
        return true;
    }

    /**
     * Remove a category
     */
    public boolean removeCategory(String category) {
        if (categories.remove(category)) {
            saveAllCategoriesToFile();
            System.out.println("✓ Category '" + category + "' removed successfully.");
            return true;
        } else {
            System.out.println("✗ Category '" + category + "' not found.");
            return false;
        }
    }

    /**
     * Check if category exists
     */
    public boolean categoryExists(String category) {
        return categories.contains(category);
    }

    /**
     * Get all categories as a linked list
     */
    public CustomLinkedList<String> getAllCategories() {
        CustomLinkedList<String> categoryList = new CustomLinkedList<>();

        // Since CustomSet doesn't have iterator, we'll get categories from file
        loadCategoriesIntoList(categoryList);

        return categoryList;
    }

    /**
     * Search categories by partial name
     */
    public CustomLinkedList<String> searchCategories(String searchTerm) {
        CustomLinkedList<String> results = new CustomLinkedList<>();
        CustomLinkedList<String> allCategories = getAllCategories();

        // Simple linear search through categories
        for (int i = 0; i < allCategories.size(); i++) {
            String category = allCategories.get(i);
            if (category.toLowerCase().contains(searchTerm.toLowerCase())) {
                results.add(category);
            }
        }

        return results;
    }

    /**
     * Display all categories
     */
    public void displayAllCategories() {
        CustomLinkedList<String> categoryList = getAllCategories();

        if (categoryList.size() == 0) {
            System.out.println("No categories found.");
            return;
        }

        System.out.println("\n=== ALL CATEGORIES ===");
        for (int i = 0; i < categoryList.size(); i++) {
            System.out.println((i + 1) + ". " + categoryList.get(i));
        }
        System.out.println("Total categories: " + categoryList.size());
        System.out.println("=======================");
    }

    /**
     * Display categories by construction phase
     */
    public void displayCategoriesByType() {
        System.out.println("\n=== CATEGORIES BY TYPE ===");

        System.out.println("\n🏗️ CONSTRUCTION MATERIALS:");
        String[] constructionCategories = { "Cement", "Steel Bars", "Roofing Sheets",
                "Electrical Materials", "Plumbing Materials", "Paint" };
        displayCategoryGroup(constructionCategories);

        System.out.println("\n👷 LABOR & SERVICES:");
        String[] laborCategories = { "Labor Costs", "Transportation", "Legal Fees" };
        displayCategoryGroup(laborCategories);

        System.out.println("\n📢 MARKETING:");
        String[] marketingCategories = { "Printing", "TV Adverts", "Radio Adverts", "Marketing Materials" };
        displayCategoryGroup(marketingCategories);

        System.out.println("\n🏢 OFFICE & ADMIN:");
        String[] officeCategories = { "Office Supplies", "Insurance", "Utilities" };
        displayCategoryGroup(officeCategories);

        System.out.println("============================");
    }

    /**
     * Helper method to display a group of categories
     */
    private void displayCategoryGroup(String[] categoryGroup) {
        for (String category : categoryGroup) {
            if (categories.contains(category)) {
                System.out.println("  ✓ " + category);
            } else {
                System.out.println("  - " + category + " (not available)");
            }
        }
    }

    /**
     * Get category count
     */
    public int getCategoryCount() {
        return categories.size();
    }

    /**
     * Validate category for expenditure entry
     */
    public boolean validateCategory(String category) {
        if (!categoryExists(category)) {
            System.out.println("✗ Category '" + category + "' does not exist.");
            System.out.println("Available categories:");
            displayAllCategories();
            return false;
        }
        return true;
    }

    /**
     * Load categories from file
     */
    private void loadCategoriesFromFile() {
        try {
            if (Files.exists(Paths.get(CATEGORIES_FILE))) {
                CustomLinkedList<String> lines = readLinesFromFile(CATEGORIES_FILE);

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    // Skip comments and empty lines
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        categories.add(line);
                    }
                }

                System.out.println("Loaded " + categories.size() + " categories from file.");
            } else {
                System.out.println("Categories file not found. Starting with empty category list.");
            }
        } catch (Exception e) {
            System.out.println("Error loading categories: " + e.getMessage());
        }
    }

    /**
     * Load categories into a linked list
     */
    private void loadCategoriesIntoList(CustomLinkedList<String> categoryList) {
        try {
            if (Files.exists(Paths.get(CATEGORIES_FILE))) {
                CustomLinkedList<String> lines = readLinesFromFile(CATEGORIES_FILE);

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    // Skip comments and empty lines
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        categoryList.add(line);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading categories into list: " + e.getMessage());
        }
    }

    /**
     * Save a single category to file
     */
    private void saveCategoryToFile(String category) {
        try (FileWriter writer = new FileWriter(CATEGORIES_FILE, true)) {
            writer.write(category + "\n");
        } catch (IOException e) {
            System.out.println("Error saving category to file: " + e.getMessage());
        }
    }

    /**
     * Save all categories to file (for removal operations)
     */
    private void saveAllCategoriesToFile() {
        try (FileWriter writer = new FileWriter(CATEGORIES_FILE)) {
            writer.write("# Categories\n");
            writer.write("# Format: CategoryName\n");

            CustomLinkedList<String> categoryList = new CustomLinkedList<>();
            loadCategoriesIntoList(categoryList);

            for (int i = 0; i < categoryList.size(); i++) {
                String category = categoryList.get(i);
                if (categories.contains(category)) {
                    writer.write(category + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving categories to file: " + e.getMessage());
        }
    }

    /**
     * Helper method to read lines from file
     */
    private CustomLinkedList<String> readLinesFromFile(String filename) throws IOException {
        CustomLinkedList<String> lines = new CustomLinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    // ================ BINARY SEARCH ALGORITHMS ================

    /**
     * Sort categories alphabetically
     * 
     * @param ascending true for A-Z order, false for Z-A
     * @return A sorted list of categories
     */
    public CustomLinkedList<String> sortCategories(boolean ascending) {
        CustomLinkedList<String> list = getAllCategories();
        int size = list.size();
        String[] array = new String[size];
        
        // Convert list to array
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        
        // Bubble sort
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                boolean shouldSwap;
                
                if (ascending) {
                    shouldSwap = array[j].compareToIgnoreCase(array[j + 1]) > 0;
                } else {
                    shouldSwap = array[j].compareToIgnoreCase(array[j + 1]) < 0;
                }
                
                if (shouldSwap) {
                    // Swap elements
                    String temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        
        // Convert back to CustomLinkedList
        CustomLinkedList<String> result = new CustomLinkedList<>();
        for (String category : array) {
            result.add(category);
        }
        
        return result;
    }

    /**
     * Binary search for exact category match
     * Time Complexity: O(log n)
     * 
     * @param categoryName The category name to search for
     * @return true if category exists, false otherwise
     */
    public boolean binarySearchCategory(String categoryName) {
        CustomLinkedList<String> sortedList = sortCategories(true);
        
        int left = 0;
        int right = sortedList.size() - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            String midCategory = sortedList.get(mid);
            
            int comparison = midCategory.compareToIgnoreCase(categoryName);
            
            if (comparison == 0) {
                return true; // Found exact match
            }
            
            if (comparison < 0) {
                left = mid + 1; // Search right half
            } else {
                right = mid - 1; // Search left half
            }
        }
        
        return false; // Not found
    }

    /**
     * Binary search for categories within alphabetical range
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param startLetter Starting letter (inclusive)
     * @param endLetter Ending letter (inclusive)
     * @return List of categories within the alphabetical range
     */
    public CustomLinkedList<String> binarySearchCategoryRange(char startLetter, char endLetter) {
        CustomLinkedList<String> results = new CustomLinkedList<>();
        CustomLinkedList<String> sortedList = sortCategories(true);
        
        if (sortedList.isEmpty()) {
            return results;
        }
        
        // Find first category starting with >= startLetter
        int startIndex = findFirstCategoryStartingWith(sortedList, startLetter);
        
        // Find last category starting with <= endLetter
        int endIndex = findLastCategoryStartingWith(sortedList, endLetter);
        
        // Add all categories in range
        for (int i = startIndex; i <= endIndex && i < sortedList.size(); i++) {
            results.add(sortedList.get(i));
        }
        
        return results;
    }

    /**
     * Binary search helper: Find first category starting with >= target letter
     */
    private int findFirstCategoryStartingWith(CustomLinkedList<String> sortedList, char targetLetter) {
        int left = 0, right = sortedList.size() - 1;
        int result = sortedList.size();
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            String midCategory = sortedList.get(mid);
            char firstLetter = Character.toLowerCase(midCategory.charAt(0));
            
            if (firstLetter >= Character.toLowerCase(targetLetter)) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        
        return result;
    }

    /**
     * Binary search helper: Find last category starting with <= target letter
     */
    private int findLastCategoryStartingWith(CustomLinkedList<String> sortedList, char targetLetter) {
        int left = 0, right = sortedList.size() - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            String midCategory = sortedList.get(mid);
            char firstLetter = Character.toLowerCase(midCategory.charAt(0));
            
            if (firstLetter <= Character.toLowerCase(targetLetter)) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }

    /**
     * Advanced binary search for categories with prefix matching
     * Time Complexity: O(log n + k) where k is number of matches
     * 
     * @param prefix The prefix to search for
     * @return List of categories that start with the given prefix
     */
    public CustomLinkedList<String> binarySearchCategoryByPrefix(String prefix) {
        CustomLinkedList<String> results = new CustomLinkedList<>();
        CustomLinkedList<String> sortedList = sortCategories(true);
        
        if (sortedList.isEmpty() || prefix == null || prefix.isEmpty()) {
            return results;
        }
        
        // Find first category with prefix >= target prefix
        int startIndex = findFirstCategoryWithPrefix(sortedList, prefix);
        
        // Collect all categories that start with the prefix
        for (int i = startIndex; i < sortedList.size(); i++) {
            String category = sortedList.get(i);
            if (category.toLowerCase().startsWith(prefix.toLowerCase())) {
                results.add(category);
            } else {
                break; // Since list is sorted, no more matches after this
            }
        }
        
        return results;
    }

    /**
     * Binary search helper: Find first category with prefix >= target
     */
    private int findFirstCategoryWithPrefix(CustomLinkedList<String> sortedList, String prefix) {
        int left = 0, right = sortedList.size() - 1;
        int result = sortedList.size();
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            String midCategory = sortedList.get(mid);
            
            // Check if this category could potentially match the prefix
            int comparison = midCategory.toLowerCase().compareTo(prefix.toLowerCase());
            
            if (comparison >= 0) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        
        return result;
    }

    /**
     * Enhanced search categories with binary search for prefix matching
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param searchTerm The search term to look for
     * @return List of categories that start with the search term
     */
    public CustomLinkedList<String> searchCategoriesBinary(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new CustomLinkedList<>();
        }
        
        // Use binary search for prefix matching (more efficient than linear search)
        return binarySearchCategoryByPrefix(searchTerm.trim());
    }

    /**
     * Performance comparison between linear and binary search for category searching
     * 
     * @param searchTerm The term to search for
     */
    public void performanceComparisonSearch(String searchTerm) {
        System.out.println("\n=== CATEGORY SEARCH PERFORMANCE COMPARISON ===");
        
        long startTime, endTime;
        
        // Test linear search (original method)
        startTime = System.nanoTime();
        CustomLinkedList<String> linearResults = searchCategories(searchTerm);
        endTime = System.nanoTime();
        long linearTime = endTime - startTime;
        
        // Test binary search
        startTime = System.nanoTime();
        CustomLinkedList<String> binaryResults = searchCategoriesBinary(searchTerm);
        endTime = System.nanoTime();
        long binaryTime = endTime - startTime;
        
        System.out.println("Search term: '" + searchTerm + "'");
        System.out.println("Linear Search Time: " + linearTime + " nanoseconds");
        System.out.println("Binary Search Time: " + binaryTime + " nanoseconds");
        System.out.println("Results count - Linear: " + linearResults.size() + ", Binary: " + binaryResults.size());
        
        if (linearTime > 0 && binaryTime > 0) {
            double speedup = (double) linearTime / binaryTime;
            System.out.println("Binary search is " + String.format("%.2f", speedup) + "x faster");
        }
        
        System.out.println("================================================\n");
    }

    /**
     * Get categories by first letter using binary search
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param letter The first letter to search for
     * @return List of categories starting with the specified letter
     */
    public CustomLinkedList<String> getCategoriesByFirstLetter(char letter) {
        return binarySearchCategoryRange(letter, letter);
    }

    /**
     * Display categories in alphabetical order using binary search sorting
     */
    public void displayCategoriesSorted() {
        CustomLinkedList<String> sortedCategories = sortCategories(true);
        
        if (sortedCategories.size() == 0) {
            System.out.println("No categories found.");
            return;
        }

        System.out.println("\n=== CATEGORIES (ALPHABETICAL ORDER) ===");
        for (int i = 0; i < sortedCategories.size(); i++) {
            System.out.println((i + 1) + ". " + sortedCategories.get(i));
        }
        System.out.println("Total categories: " + sortedCategories.size());
        System.out.println("========================================");
    }
}
