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
}
