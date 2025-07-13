package com.nkwarealestate.expenditure.cli;

import com.nkwarealestate.expenditure.services.ExpenditureService;
import com.nkwarealestate.expenditure.services.CategoryService;
import com.nkwarealestate.expenditure.services.BankAccountService;
import com.nkwarealestate.expenditure.services.ReceiptService;
import com.nkwarealestate.expenditure.services.FinancialAnalysisService;
import com.nkwarealestate.expenditure.services.SystemMonitorService;
import com.nkwarealestate.expenditure.services.PerformanceTimer;
import com.nkwarealestate.expenditure.models.Expenditure;
import com.nkwarealestate.expenditure.models.Phase;
import com.nkwarealestate.expenditure.models.BankAccount;
import com.nkwarealestate.expenditure.models.Receipt;
import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Menu-driven command-line interface for the expenditure management system
 */
public class MenuSystem {

    private Scanner scanner;
    private boolean running;
    private ExpenditureService expenditureService;
    private CategoryService categoryService;
    private BankAccountService bankAccountService;
    private ReceiptService receiptService;
    private FinancialAnalysisService financialAnalysisService;
    private SystemMonitorService systemMonitor;
    private DateTimeFormatter dateFormatter;

    public MenuSystem() {
        this.scanner = new Scanner(System.in);
        this.running = true;
        this.systemMonitor = new SystemMonitorService();
        this.expenditureService = new ExpenditureService();
        this.categoryService = new CategoryService();
        this.bankAccountService = new BankAccountService();
        this.receiptService = new ReceiptService(expenditureService);
        this.financialAnalysisService = new FinancialAnalysisService(expenditureService);
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public void start() {
        while (running) {
            displayMainMenu();
            int choice = getMenuChoice();
            processMenuChoice(choice);
        }

        scanner.close();
        System.out.println("Thank you for using NREL Expenditure Management System!");
    }

    private void displayMainMenu() {
        System.out.println("\n=================== MAIN MENU ===================");
        System.out.println("1. Expenditure Management");
        System.out.println("2. Category Management");
        System.out.println("3. Bank Account Management");
        System.out.println("4. Search & Sort");
        System.out.println("5. Receipt/Invoice Management");
        System.out.println("6. Financial Analysis & Reports");
        System.out.println("7. System Settings");
        System.out.println("0. Exit");
        System.out.println("=================================================");
        System.out.print("Please select an option (0-7): ");
    }

    private int getMenuChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // Invalid input
        }
    }

    private void processMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleExpenditureManagement();
                break;
            case 2:
                handleCategoryManagement();
                break;
            case 3:
                handleBankAccountManagement();
                break;
            case 4:
                handleSearchAndSort();
                break;
            case 5:
                handleReceiptManagement();
                break;
            case 6:
                handleFinancialAnalysis();
                break;
            case 7:
                handleSystemSettings();
                break;
            case 0:
                running = false;
                break;
            default:
                System.out.println("\nInvalid option. Please select a number between 0-7.");
                break;
        }
    }

    // ================== EXPENDITURE MANAGEMENT ==================

    private void handleExpenditureManagement() {
        while (true) {
            System.out.println("\n=============== EXPENDITURE MANAGEMENT ===============");
            System.out.println("1. Add New Expenditure");
            System.out.println("2. View Expenditure by Code");
            System.out.println("3. View All Expenditures");
            System.out.println("4. Update Expenditure");
            System.out.println("5. Delete Expenditure");
            System.out.println("6. Expenditure Summary");
            System.out.println("0. Back to Main Menu");
            System.out.println("======================================================");
            System.out.print("Please select an option (0-6): ");

            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    addNewExpenditure();
                    break;
                case 2:
                    viewExpenditureByCode();
                    break;
                case 3:
                    viewAllExpenditures();
                    break;
                case 4:
                    updateExpenditure();
                    break;
                case 5:
                    deleteExpenditure();
                    break;
                case 6:
                    showExpenditureSummary();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid option. Please select a number between 0-6.");
                    break;
            }
        }
    }

    private void addNewExpenditure() {
        System.out.println("\n=== ADD NEW EXPENDITURE ===");

        try {
            // Get amount
            System.out.print("Enter amount (GHS): ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            if (amount <= 0) {
                System.out.println("✗ Amount must be greater than 0.");
                return;
            }

            // Get date
            System.out.print("Enter date (YYYY-MM-DD): ");
            String date = scanner.nextLine().trim();

            // Get phase
            System.out.println("Select phase:");
            Phase[] phases = Phase.values();
            for (int i = 0; i < phases.length; i++) {
                System.out.println((i + 1) + ". " + phases[i]);
            }
            System.out.print("Enter phase number: ");
            int phaseChoice = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (phaseChoice < 0 || phaseChoice >= phases.length) {
                System.out.println("✗ Invalid phase selection.");
                return;
            }
            Phase phase = phases[phaseChoice];

            // Show available categories
            System.out.println("\nAvailable categories:");
            categoryService.displayAllCategories();
            System.out.print("Enter category name: ");
            String category = scanner.nextLine().trim();

            // Validate category
            if (!categoryService.validateCategory(category)) {
                return;
            }

            // Show available accounts
            System.out.println("\nAvailable bank accounts:");
            bankAccountService.displayAllAccounts();
            System.out.print("Enter account ID: ");
            String accountId = scanner.nextLine().trim();

            // Validate account and balance
            if (!bankAccountService.validateAccountForExpenditure(accountId, amount)) {
                return;
            }

            // Get description
            System.out.print("Enter description: ");
            String description = scanner.nextLine().trim();

            // Add expenditure
            if (expenditureService.addExpenditure(amount, date, phase, category, accountId, description)) {
                // Debit the account
                bankAccountService.debitAccount(accountId, amount);
                System.out.println("✓ Expenditure recorded and account debited successfully!");
            }

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format. Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("✗ Error adding expenditure: " + e.getMessage());
        }
    }

    private void viewExpenditureByCode() {
        System.out.print("\nEnter expenditure code: ");
        String code = scanner.nextLine().trim().toUpperCase();

        Expenditure expenditure = expenditureService.getExpenditure(code);
        if (expenditure != null) {
            expenditureService.displayExpenditureSummary(expenditure);
        } else {
            System.out.println("✗ Expenditure with code '" + code + "' not found.");
        }
    }

    private void viewAllExpenditures() {
        PerformanceTimer timer = PerformanceTimer.startNew("View All Expenditures");
        
        System.out.println("\n=== ALL EXPENDITURES ===");
        
        CustomLinkedList<Expenditure> allExpenditures = expenditureService.getAllExpenditures();
        
        if (allExpenditures.isEmpty()) {
            System.out.println("No expenditures found in the system.");
            timer.stop();
            timer.recordInMonitor(systemMonitor);
            return;
        }
        
        // Display expenditures in table format
        expenditureService.displayExpendituresTable(allExpenditures);
        
        // Show summary
        System.out.println("\nSUMMARY:");
        System.out.println("Total expenditures: " + expenditureService.getExpenditureCount());
        System.out.println("Total amount: GHS " + String.format("%.2f", expenditureService.getTotalExpenditureAmount()));
        
        timer.stop();
        timer.recordInMonitor(systemMonitor);
        
        // Show performance info for this operation
        System.out.printf("\n[Performance] Loaded %d expenditures in %.2f ms\n", 
                allExpenditures.size(), timer.getElapsedTimeMs());
    }

    private void updateExpenditure() {
        System.out.print("\nEnter expenditure code to update: ");
        String code = scanner.nextLine().trim().toUpperCase();

        Expenditure expenditure = expenditureService.getExpenditure(code);
        if (expenditure == null) {
            System.out.println("✗ Expenditure with code '" + code + "' not found.");
            return;
        }

        // Show current details
        expenditureService.displayExpenditureSummary(expenditure);

        try {
            System.out.print("Enter new amount (current: " + expenditure.getAmount() + "): ");
            double newAmount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter new description (current: " + expenditure.getDescription() + "): ");
            String newDescription = scanner.nextLine().trim();

            expenditureService.updateExpenditure(code, newAmount, newDescription);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid amount format.");
        }
    }

    private void deleteExpenditure() {
        System.out.print("\nEnter expenditure code to delete: ");
        String code = scanner.nextLine().trim().toUpperCase();

        Expenditure expenditure = expenditureService.getExpenditure(code);
        if (expenditure == null) {
            System.out.println("✗ Expenditure with code '" + code + "' not found.");
            return;
        }

        // Show expenditure details
        expenditureService.displayExpenditureSummary(expenditure);

        System.out.print("Are you sure you want to delete this expenditure? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            expenditureService.deleteExpenditure(code);
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    private void showExpenditureSummary() {
        System.out.println("\n=== EXPENDITURE SUMMARY ===");
        System.out.println("Total Expenditures: " + expenditureService.getExpenditureCount());
        System.out
                .println("Total Amount: GHS " + String.format("%.2f", expenditureService.getTotalExpenditureAmount()));
        System.out.println("============================");
    }

    // ================== CATEGORY MANAGEMENT ==================

    private void handleCategoryManagement() {
        while (true) {
            System.out.println("\n================ CATEGORY MANAGEMENT ================");
            System.out.println("1. View All Categories");
            System.out.println("2. Add New Category");
            System.out.println("3. Remove Category");
            System.out.println("4. Search Categories");
            System.out.println("5. View Categories by Type");
            System.out.println("0. Back to Main Menu");
            System.out.println("======================================================");
            System.out.print("Please select an option (0-5): ");

            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    categoryService.displayAllCategories();
                    break;
                case 2:
                    addNewCategory();
                    break;
                case 3:
                    removeCategory();
                    break;
                case 4:
                    searchCategories();
                    break;
                case 5:
                    categoryService.displayCategoriesByType();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid option. Please select a number between 0-5.");
                    break;
            }
        }
    }

    private void addNewCategory() {
        System.out.print("\nEnter new category name: ");
        String categoryName = scanner.nextLine().trim();
        categoryService.addCategory(categoryName);
    }

    private void removeCategory() {
        categoryService.displayAllCategories();
        System.out.print("\nEnter category name to remove: ");
        String categoryName = scanner.nextLine().trim();
        categoryService.removeCategory(categoryName);
    }

    private void searchCategories() {
        System.out.print("\nEnter search term: ");
        String searchTerm = scanner.nextLine().trim();

        CustomLinkedList<String> results = categoryService.searchCategories(searchTerm);

        if (results.size() == 0) {
            System.out.println("No categories found matching '" + searchTerm + "'.");
        } else {
            System.out.println("\nSearch results for '" + searchTerm + "':");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        }
    }

    // ================== BANK ACCOUNT MANAGEMENT ==================

    private void handleBankAccountManagement() {
        while (true) {
            System.out.println("\n============== BANK ACCOUNT MANAGEMENT ==============");
            System.out.println("1. View All Accounts");
            System.out.println("2. Add New Account");
            System.out.println("3. View Account Details");
            System.out.println("4. Credit Account");
            System.out.println("5. Account Balance Summary");
            System.out.println("6. Low Balance Alert");
            System.out.println("0. Back to Main Menu");
            System.out.println("======================================================");
            System.out.print("Please select an option (0-6): ");

            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    bankAccountService.displayAllAccounts();
                    break;
                case 2:
                    addNewBankAccount();
                    break;
                case 3:
                    viewAccountDetails();
                    break;
                case 4:
                    creditBankAccount();
                    break;
                case 5:
                    showAccountBalanceSummary();
                    break;
                case 6:
                    bankAccountService.displayLowBalanceWarning();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid option. Please select a number between 0-6.");
                    break;
            }
        }
    }

    private void addNewBankAccount() {
        System.out.println("\n=== ADD NEW BANK ACCOUNT ===");

        try {
            System.out.print("Enter account ID: ");
            String accountId = scanner.nextLine().trim().toUpperCase();

            System.out.print("Enter bank name: ");
            String bankName = scanner.nextLine().trim();

            System.out.print("Enter initial balance: ");
            double initialBalance = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter account type (e.g., Construction, Marketing, Operations): ");
            String accountType = scanner.nextLine().trim();

            bankAccountService.addAccount(accountId, bankName, initialBalance, accountType);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid balance format.");
        }
    }

    private void viewAccountDetails() {
        System.out.print("\nEnter account ID: ");
        String accountId = scanner.nextLine().trim().toUpperCase();
        bankAccountService.displayAccountSummary(accountId);
    }

    private void creditBankAccount() {
        System.out.print("\nEnter account ID: ");
        String accountId = scanner.nextLine().trim().toUpperCase();

        try {
            System.out.print("Enter amount to credit: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            if (amount <= 0) {
                System.out.println("✗ Amount must be greater than 0.");
                return;
            }

            bankAccountService.creditAccount(accountId, amount);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid amount format.");
        }
    }

    private void showAccountBalanceSummary() {
        System.out.println("\n=== ACCOUNT BALANCE SUMMARY ===");
        bankAccountService.displayAllAccounts();
        System.out.println("Total Balance: GHS " + String.format("%,.2f", bankAccountService.getTotalBalance()));
        System.out.println("===============================");
    }

    // ================== PLACEHOLDER METHODS ==================

    private void handleSearchAndSort() {
        while (true) {
            System.out.println("\n================== SEARCH & SORT ==================");
            System.out.println("1. Search Expenditures by Date Range");
            System.out.println("2. Search Expenditures by Amount Range");
            System.out.println("3. Sort Expenditures by Amount (Ascending)");
            System.out.println("4. Sort Expenditures by Amount (Descending)");
            System.out.println("5. Sort Expenditures by Date (Oldest First)");
            System.out.println("6. Sort Expenditures by Date (Newest First)");
            System.out.println("7. Advanced Search");
            System.out.println("0. Back to Main Menu");
            System.out.println("===================================================");
            System.out.print("Please select an option (0-7): ");

            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    searchByDateRange();
                    break;
                case 2:
                    searchByAmountRange();
                    break;
                case 3:
                    sortByAmount(true);
                    break;
                case 4:
                    sortByAmount(false);
                    break;
                case 5:
                    sortByDate(true);
                    break;
                case 6:
                    sortByDate(false);
                    break;
                case 7:
                    advancedSearch();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid option. Please select a number between 0-7.");
                    break;
            }
        }
    }

    private void searchByDateRange() {
        System.out.println("\n=== SEARCH BY DATE RANGE ===");

        try {
            System.out.print("Enter start date (YYYY-MM-DD): ");
            String startDateStr = scanner.nextLine().trim();
            LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);

            System.out.print("Enter end date (YYYY-MM-DD): ");
            String endDateStr = scanner.nextLine().trim();
            LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);

            if (endDate.isBefore(startDate)) {
                System.out.println("✗ End date cannot be before start date.");
                return;
            }

            CustomLinkedList<Expenditure> results = expenditureService.getExpendituresByDateRange(startDate, endDate);
            expenditureService.displayExpendituresTable(results);

        } catch (DateTimeParseException e) {
            System.out.println("✗ Invalid date format. Please use YYYY-MM-DD format.");
        } catch (Exception e) {
            System.out.println("✗ Error during search: " + e.getMessage());
        }
    }

    private void searchByAmountRange() {
        System.out.println("\n=== SEARCH BY AMOUNT RANGE ===");

        try {
            System.out.print("Enter minimum amount (GHS): ");
            double minAmount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter maximum amount (GHS): ");
            double maxAmount = Double.parseDouble(scanner.nextLine().trim());

            if (maxAmount < minAmount) {
                System.out.println("✗ Maximum amount cannot be less than minimum amount.");
                return;
            }

            PerformanceTimer timer = PerformanceTimer.startNew("Amount Range Search");
            CustomLinkedList<Expenditure> results = expenditureService.getExpendituresByAmountRange(minAmount, maxAmount);
            timer.stop();
            
            expenditureService.displayExpendituresTable(results);
            
            // Show search performance
            System.out.printf("\n[Performance] Found %d results in %.2f ms using binary search\n", 
                    results.size(), timer.getElapsedTimeMs());
            
            timer.recordInMonitor(systemMonitor);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format.");
        } catch (Exception e) {
            System.out.println("✗ Error during search: " + e.getMessage());
        }
    }

    private void sortByAmount(boolean ascending) {
        System.out.println("\n=== SORT BY AMOUNT (" + (ascending ? "ASCENDING" : "DESCENDING") + ") ===");

        CustomLinkedList<Expenditure> results = expenditureService.sortExpendituresByAmount(ascending);
        expenditureService.displayExpendituresTable(results);
    }

    private void sortByDate(boolean ascending) {
        System.out.println("\n=== SORT BY DATE (" + (ascending ? "OLDEST FIRST" : "NEWEST FIRST") + ") ===");

        CustomLinkedList<Expenditure> results = expenditureService.sortExpendituresByDate(ascending);
        expenditureService.displayExpendituresTable(results);
    }

    private void advancedSearch() {
        System.out.println("\n=== ADVANCED SEARCH ===");
        System.out.println("(Leave fields blank to ignore that criterion)");

        try {
            // Category filter
            System.out.print("Category: ");
            String category = scanner.nextLine().trim();
            if (category.isEmpty()) {
                category = null;
            }

            // Phase filter
            System.out.println("Select phase (0 to skip):");
            Phase[] phases = Phase.values();
            for (int i = 0; i < phases.length; i++) {
                System.out.println((i + 1) + ". " + phases[i]);
            }
            System.out.print("Enter phase number (0 to skip): ");
            int phaseChoice = Integer.parseInt(scanner.nextLine().trim());

            Phase phase = null;
            if (phaseChoice > 0 && phaseChoice <= phases.length) {
                phase = phases[phaseChoice - 1];
            }

            // Amount range
            System.out.print("Minimum amount (GHS, blank to skip): ");
            String minAmountStr = scanner.nextLine().trim();
            Double minAmount = minAmountStr.isEmpty() ? null : Double.parseDouble(minAmountStr);

            System.out.print("Maximum amount (GHS, blank to skip): ");
            String maxAmountStr = scanner.nextLine().trim();
            Double maxAmount = maxAmountStr.isEmpty() ? null : Double.parseDouble(maxAmountStr);

            // Date range
            System.out.print("Start date (YYYY-MM-DD, blank to skip): ");
            String startDateStr = scanner.nextLine().trim();
            LocalDate startDate = startDateStr.isEmpty() ? null : LocalDate.parse(startDateStr, dateFormatter);

            System.out.print("End date (YYYY-MM-DD, blank to skip): ");
            String endDateStr = scanner.nextLine().trim();
            LocalDate endDate = endDateStr.isEmpty() ? null : LocalDate.parse(endDateStr, dateFormatter);

            // Keywords
            System.out.print("Keywords in description: ");
            String keywords = scanner.nextLine().trim();
            if (keywords.isEmpty()) {
                keywords = null;
            }

            CustomLinkedList<Expenditure> results = expenditureService.advancedSearch(
                    category, phase, minAmount, maxAmount, startDate, endDate, keywords);
            expenditureService.displayExpendituresTable(results);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format.");
        } catch (DateTimeParseException e) {
            System.out.println("✗ Invalid date format. Please use YYYY-MM-DD format.");
        } catch (Exception e) {
            System.out.println("✗ Error during search: " + e.getMessage());
        }
    }

    // ================== RECEIPT MANAGEMENT ==================

    private void handleReceiptManagement() {
        while (true) {
            System.out.println("\n=============== RECEIPT MANAGEMENT ================");
            System.out.println("1. Upload New Receipt/Invoice");
            System.out.println("2. View Receipt Details");
            System.out.println("3. View All Receipts");
            System.out.println("4. Validate Next Receipt in Queue");
            System.out.println("5. View Pending Validation Queue");
            System.out.println("6. View Recent Uploads");
            System.out.println("0. Back to Main Menu");
            System.out.println("===================================================");
            System.out.print("Please select an option (0-6): ");

            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    uploadReceipt();
                    break;
                case 2:
                    viewReceiptDetails();
                    break;
                case 3:
                    viewAllReceipts();
                    break;
                case 4:
                    validateNextReceipt();
                    break;
                case 5:
                    viewPendingValidationQueue();
                    break;
                case 6:
                    viewRecentUploads();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid option. Please select a number between 0-6.");
                    break;
            }
        }
    }

    private void uploadReceipt() {
        System.out.println("\n=== UPLOAD NEW RECEIPT/INVOICE ===");

        try {
            // Get expenditure code
            System.out.println("\nAvailable expenditures:");
            CustomLinkedList<Expenditure> expenditures = expenditureService.getAllExpenditures();
            expenditureService.displayExpendituresTable(expenditures);

            System.out.print("Enter expenditure code to link receipt to: ");
            String expenditureCode = scanner.nextLine().trim();

            Expenditure expenditure = expenditureService.getExpenditure(expenditureCode);
            if (expenditure == null) {
                System.out.println("✗ Expenditure not found with code: " + expenditureCode);
                return;
            }

            // Check if expenditure already has a receipt
            if (expenditure.getReceiptId() != null) {
                System.out.println("✗ This expenditure already has a receipt attached (ID: " +
                        expenditure.getReceiptId() + ")");
                System.out.print("Do you want to replace it? (y/n): ");
                String replace = scanner.nextLine().trim().toLowerCase();
                if (!replace.equals("y") && !replace.equals("yes")) {
                    return;
                }
            }

            // Get receipt details
            System.out.print("Enter the file path of the receipt: ");
            String filePath = scanner.nextLine().trim();

            System.out.print("Enter vendor name: ");
            String vendorName = scanner.nextLine().trim();

            System.out.print("Enter receipt date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();
            LocalDate receiptDate = LocalDate.parse(dateStr, dateFormatter);

            // Upload receipt
            String receiptId = receiptService.uploadReceipt(
                    expenditureCode, filePath, vendorName, receiptDate);

            if (receiptId != null) {
                System.out.println("✓ Receipt uploaded successfully with ID: " + receiptId);
            }

        } catch (DateTimeParseException e) {
            System.out.println("✗ Invalid date format. Please use YYYY-MM-DD format.");
        } catch (Exception e) {
            System.out.println("✗ Error uploading receipt: " + e.getMessage());
        }
    }

    private void viewReceiptDetails() {
        System.out.println("\n=== VIEW RECEIPT DETAILS ===");

        try {
            System.out.print("Enter receipt ID: ");
            String receiptId = scanner.nextLine().trim();

            Receipt receipt = receiptService.getReceipt(receiptId);
            if (receipt == null) {
                System.out.println("✗ Receipt not found with ID: " + receiptId);
                return;
            }

            receiptService.displayReceiptDetails(receipt);

        } catch (Exception e) {
            System.out.println("✗ Error viewing receipt: " + e.getMessage());
        }
    }

    private void viewAllReceipts() {
        System.out.println("\n=== ALL RECEIPTS ===");

        CustomLinkedList<Receipt> receipts = receiptService.getAllReceipts();
        if (receipts.isEmpty()) {
            System.out.println("No receipts found in the system.");
            return;
        }

        System.out.println("+----------+------------+------------+--------------------+------------+");
        System.out.println("| RECEIPT  | EXPENDITURE| DATE       | VENDOR             | VALIDATED  |");
        System.out.println("+----------+------------+------------+--------------------+------------+");

        for (int i = 0; i < receipts.size(); i++) {
            Receipt receipt = receipts.get(i);
            String vendor = receipt.getVendorName();
            if (vendor.length() > 18) {
                vendor = vendor.substring(0, 15) + "...";
            }

            System.out.printf("| %-8s | %-10s | %-10s | %-18s | %-10s |\n",
                    receipt.getId(),
                    receipt.getExpenditureCode(),
                    receipt.getDate().format(dateFormatter),
                    vendor,
                    receipt.isValidated() ? "Yes" : "No");
        }

        System.out.println("+----------+------------+------------+--------------------+------------+");
        System.out.println("Total: " + receipts.size() + " receipt(s)");
    }

    private void validateNextReceipt() {
        System.out.println("\n=== VALIDATE NEXT RECEIPT ===");

        Receipt nextReceipt = receiptService.peekNextPendingReceipt();
        if (nextReceipt == null) {
            System.out.println("No receipts pending validation.");
            return;
        }

        System.out.println("Next receipt for validation:");
        receiptService.displayReceiptDetails(nextReceipt);

        System.out.print("Enter your name (validator): ");
        String validatorName = scanner.nextLine().trim();

        if (validatorName.isEmpty()) {
            System.out.println("✗ Validator name cannot be empty.");
            return;
        }

        System.out.print("Confirm validation (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            if (receiptService.validateNextReceipt(validatorName)) {
                System.out.println("✓ Receipt validated successfully.");
            }
        } else {
            System.out.println("Receipt validation cancelled.");
        }
    }

    private void viewPendingValidationQueue() {
        System.out.println("\n=== PENDING VALIDATION QUEUE ===");

        int pendingCount = receiptService.getPendingValidationCount();
        System.out.println("Number of receipts pending validation: " + pendingCount);

        if (pendingCount > 0) {
            System.out.println("\nNext receipt in queue:");
            Receipt nextReceipt = receiptService.peekNextPendingReceipt();
            receiptService.displayReceiptDetails(nextReceipt);
        }
    }

    private void viewRecentUploads() {
        System.out.println("\n=== RECENT UPLOADS ===");

        System.out.print("Enter number of recent receipts to show: ");
        int count;
        try {
            count = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number. Using default of 5.");
            count = 5;
        }

        if (count <= 0) {
            System.out.println("✗ Count must be greater than 0. Using default of 5.");
            count = 5;
        }

        CustomLinkedList<Receipt> recentReceipts = receiptService.getRecentReceipts(count);

        if (recentReceipts.isEmpty()) {
            System.out.println("No recent receipt uploads found.");
            return;
        }

        System.out.println("Most recent " + recentReceipts.size() + " receipt(s):");

        System.out.println("+----------+------------+------------+--------------------+");
        System.out.println("| RECEIPT  | EXPENDITURE| DATE       | VENDOR             |");
        System.out.println("+----------+------------+------------+--------------------+");

        for (int i = 0; i < recentReceipts.size(); i++) {
            Receipt receipt = recentReceipts.get(i);
            String vendor = receipt.getVendorName();
            if (vendor.length() > 18) {
                vendor = vendor.substring(0, 15) + "...";
            }

            System.out.printf("| %-8s | %-10s | %-10s | %-18s |\n",
                    receipt.getId(),
                    receipt.getExpenditureCode(),
                    receipt.getDate().format(dateFormatter),
                    vendor);
        }

        System.out.println("+----------+------------+------------+--------------------+");
    }

    // ================== FINANCIAL ANALYSIS ==================

    private void handleFinancialAnalysis() {
        while (true) {
            System.out.println("\n=============== FINANCIAL ANALYSIS ================");
            System.out.println("1. Monthly Expenditure Statistics");
            System.out.println("2. Calculate Burn Rate");
            System.out.println("3. Generate Expenditure Forecast");
            System.out.println("4. Material Cost Impact Analysis");
            System.out.println("5. Profitability Analysis");
            System.out.println("6. Expenditure Tree View");
            System.out.println("0. Back to Main Menu");
            System.out.println("===================================================");
            System.out.print("Please select an option (0-6): ");

            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    viewMonthlyStatistics();
                    break;
                case 2:
                    calculateBurnRate();
                    break;
                case 3:
                    generateForecast();
                    break;
                case 4:
                    analyzeMaterialCost();
                    break;
                case 5:
                    analyzeProfitability();
                    break;
                case 6:
                    viewExpenditureTree();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid option. Please select a number between 0-6.");
                    break;
            }
        }
    }

    private void viewMonthlyStatistics() {
        System.out.println("\n=== MONTHLY EXPENDITURE STATISTICS ===");

        try {
            System.out.print("Enter year (YYYY): ");
            int year = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter month (1-12): ");
            int month = Integer.parseInt(scanner.nextLine().trim());

            if (month < 1 || month > 12) {
                System.out.println("✗ Month must be between 1 and 12.");
                return;
            }

            String statistics = expenditureService.getMonthlyStatistics(year, month);
            System.out.println("\n" + statistics);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format.");
        } catch (Exception e) {
            System.out.println("✗ Error generating statistics: " + e.getMessage());
        }
    }

    private void calculateBurnRate() {
        System.out.println("\n=== CALCULATE BURN RATE ===");

        try {
            System.out.print("Enter number of months to consider (1-12): ");
            int months = Integer.parseInt(scanner.nextLine().trim());

            if (months < 1 || months > 12) {
                System.out.println("✗ Number of months must be between 1 and 12.");
                return;
            }

            double burnRate = financialAnalysisService.calculateMonthlyBurnRate(months);

            System.out.println("\nBURN RATE ANALYSIS");
            System.out.println("==================");
            System.out.printf("Calculation period: Last %d month(s)\n", months);
            System.out.printf("Average monthly expenditure: GHS %.2f\n", burnRate);
            System.out.printf("Projected annual expenditure: GHS %.2f\n", burnRate * 12);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format.");
        } catch (Exception e) {
            System.out.println("✗ Error calculating burn rate: " + e.getMessage());
        }
    }

    private void generateForecast() {
        System.out.println("\n=== GENERATE EXPENDITURE FORECAST ===");

        try {
            System.out.print("Enter number of months to forecast (1-24): ");
            int months = Integer.parseInt(scanner.nextLine().trim());

            if (months < 1 || months > 24) {
                System.out.println("✗ Number of months must be between 1 and 24.");
                return;
            }

            String forecast = financialAnalysisService.generateExpenditureForecast(months);
            System.out.println("\n" + forecast);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format.");
        } catch (Exception e) {
            System.out.println("✗ Error generating forecast: " + e.getMessage());
        }
    }

    private void analyzeMaterialCost() {
        System.out.println("\n=== MATERIAL COST IMPACT ANALYSIS ===");

        try {
            // Show available categories
            System.out.println("Available categories:");
            categoryService.displayAllCategories();

            System.out.print("Enter material category to analyze: ");
            String materialCategory = scanner.nextLine().trim();

            System.out.print("Enter percentage increase to simulate (e.g., 10): ");
            double percentage = Double.parseDouble(scanner.nextLine().trim());

            if (percentage < 0) {
                System.out.println("✗ Percentage must be a positive number.");
                return;
            }

            String analysis = financialAnalysisService.analyzeMaterialCostImpact(materialCategory, percentage);
            System.out.println("\n" + analysis);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format.");
        } catch (Exception e) {
            System.out.println("✗ Error analyzing material cost: " + e.getMessage());
        }
    }

    private void analyzeProfitability() {
        System.out.println("\n=== PROFITABILITY ANALYSIS ===");

        try {
            System.out.print("Enter expected total revenue for the project (GHS): ");
            double revenue = Double.parseDouble(scanner.nextLine().trim());

            if (revenue <= 0) {
                System.out.println("✗ Revenue must be greater than 0.");
                return;
            }

            String analysis = financialAnalysisService.analyzeProfitability(revenue);
            System.out.println("\n" + analysis);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format.");
        } catch (Exception e) {
            System.out.println("✗ Error analyzing profitability: " + e.getMessage());
        }
    }

    private void viewExpenditureTree() {
        System.out.println("\n=== EXPENDITURE TREE VIEW ===");

        try {
            System.out.println("Generating expenditure hierarchy tree...");
            System.out.println();

            financialAnalysisService.createExpenditureTree().printTree();

        } catch (Exception e) {
            System.out.println("✗ Error generating tree view: " + e.getMessage());
        }
    }

    // ================== SYSTEM SETTINGS ==================

    private void handleSystemSettings() {
        while (true) {
            System.out.println("\n================ SYSTEM SETTINGS =================");
            System.out.println("1. System Information");
            System.out.println("2. Performance Monitor");
            System.out.println("3. Live Performance Meter");
            System.out.println("4. Performance Report");
            System.out.println("5. Export Data");
            System.out.println("6. Import Data");
            System.out.println("0. Back to Main Menu");
            System.out.println("===================================================");
            System.out.print("Please select an option (0-6): ");

            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    showSystemInfo();
                    break;
                case 2:
                    showPerformanceMonitor();
                    break;
                case 3:
                    showLivePerformanceMeter();
                    break;
                case 4:
                    showPerformanceReport();
                    break;
                case 5:
                    exportData();
                    break;
                case 6:
                    importData();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid option. Please select a number between 0-6.");
                    break;
            }
        }
    }

    private void showSystemInfo() {
        PerformanceTimer timer = PerformanceTimer.startNew("System Info Display");
        
        System.out.println("\n=== SYSTEM INFORMATION ===");

        int expenditureCount = expenditureService.getExpenditureCount();
        int categoryCount = categoryService.getCategoryCount();
        int accountCount = bankAccountService.getAccountCount();

        System.out.println("NREL Expenditure Management System");
        System.out.println("Version: 1.0.0");
        System.out.println("Built with custom data structures as per project requirements");
        System.out.println("\nData Summary:");
        System.out.println("- Expenditures: " + expenditureCount);
        System.out.println("- Categories: " + categoryCount);
        System.out.println("- Bank Accounts: " + accountCount);

        System.out.println("\nImplemented Data Structures:");
        System.out.println("- Custom HashMap: Used for key-value storage");
        System.out.println("- Custom LinkedList: Used for sequential collections");
        System.out.println("- Custom Stack: Used for receipt processing");
        System.out.println("- Custom Queue: Used for validation workflows");
        System.out.println("- Custom Tree: Used for hierarchical data representation");
        System.out.println("- Custom Set: Used for unique category management");
        System.out.println("- MinHeap: Used for bank account balance monitoring");
        System.out.println("- Graph: Used for account relationship mapping");

        // Enhanced memory and performance information
        SystemMonitorService.MemoryInfo memInfo = systemMonitor.getCurrentMemoryInfo();
        System.out.println("\nMemory Usage Information:");
        System.out.printf("- Maximum Memory: %d MB\n", memInfo.getMaxMemoryMB());
        System.out.printf("- Allocated Memory: %d MB\n", memInfo.getTotalMemoryMB());
        System.out.printf("- Used Memory: %d MB (%.1f%%)\n", 
                memInfo.getUsedMemoryMB(), memInfo.getMemoryUsagePercentage());
        System.out.printf("- Free Memory: %d MB\n", memInfo.getFreeMemoryMB());
        System.out.printf("- Memory Efficiency: %s\n", memInfo.getEfficiencyRating());

        // System uptime
        java.time.Duration uptime = systemMonitor.getUptime();
        System.out.printf("\nSystem Uptime: %d days, %d hours, %d minutes\n",
                uptime.toDays(), uptime.toHours() % 24, uptime.toMinutes() % 60);

        // Performance statistics
        SystemMonitorService.PerformanceStats stats = systemMonitor.getPerformanceStats();
        System.out.println("\nPerformance Statistics:");
        System.out.printf("- Total Operations Monitored: %d\n", stats.getTotalOperations());
        if (stats.getTotalOperations() > 0) {
            System.out.printf("- Average Operation Time: %.2f ms\n", stats.getAverageExecutionTimeMs());
            System.out.printf("- Fastest Operation: %.2f ms\n", stats.getMinExecutionTimeMs());
            System.out.printf("- Slowest Operation: %.2f ms\n", stats.getMaxExecutionTimeMs());
        }

        timer.stop();
        timer.recordInMonitor(systemMonitor);

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void exportData() {
        System.out.println("\n=== EXPORT DATA ===");
        System.out.println("This feature would export system data to files.");
        System.out.println("Implementation pending for future enhancement.");

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void importData() {
        System.out.println("\n=== IMPORT DATA ===");
        System.out.println("This feature would import data from external files.");
        System.out.println("Implementation pending for future enhancement.");

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void showPerformanceMonitor() {
        while (true) {
            System.out.println("\n============== PERFORMANCE MONITOR ===============");
            System.out.println("1. Test Expenditure Search Performance");
            System.out.println("2. Test Category Search Performance");
            System.out.println("3. Test Bank Account Search Performance");
            System.out.println("4. Test Receipt Search Performance");
            System.out.println("5. Memory Usage Analysis");
            System.out.println("6. Run All Performance Tests");
            System.out.println("0. Back to System Settings");
            System.out.println("===================================================");
            System.out.print("Please select an option (0-6): ");

            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    testExpenditureSearchPerformance();
                    break;
                case 2:
                    testCategorySearchPerformance();
                    break;
                case 3:
                    testBankAccountSearchPerformance();
                    break;
                case 4:
                    testReceiptSearchPerformance();
                    break;
                case 5:
                    analyzeMemoryUsage();
                    break;
                case 6:
                    runAllPerformanceTests();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid option. Please select a number between 0-6.");
                    break;
            }
        }
    }

    private void showLivePerformanceMeter() {
        System.out.println("\n=== LIVE PERFORMANCE METER ===");
        System.out.println("Displaying real-time system performance...\n");
        
        systemMonitor.displayLivePerformanceMeter();
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void showPerformanceReport() {
        PerformanceTimer timer = PerformanceTimer.startNew("Performance Report Generation");
        
        System.out.println("\n=== COMPREHENSIVE PERFORMANCE REPORT ===");
        String report = systemMonitor.generateSystemReport();
        System.out.println(report);
        
        timer.stop();
        timer.recordInMonitor(systemMonitor);
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void testExpenditureSearchPerformance() {
        System.out.println("\n=== EXPENDITURE SEARCH PERFORMANCE TEST ===");
        
        // Test binary search vs linear search for amount range
        PerformanceTimer timer = PerformanceTimer.startNew("Expenditure Performance Test");
        expenditureService.performanceComparison(1000.0, 5000.0);
        timer.stop();
        timer.recordInMonitor(systemMonitor);
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void testCategorySearchPerformance() {
        System.out.println("\n=== CATEGORY SEARCH PERFORMANCE TEST ===");
        
        PerformanceTimer timer = PerformanceTimer.startNew("Category Performance Test");
        categoryService.performanceComparisonSearch("C");
        timer.stop();
        timer.recordInMonitor(systemMonitor);
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void testBankAccountSearchPerformance() {
        System.out.println("\n=== BANK ACCOUNT SEARCH PERFORMANCE TEST ===");
        
        PerformanceTimer timer = PerformanceTimer.startNew("Bank Account Performance Test");
        bankAccountService.performanceComparisonBalance(10000.0);
        timer.stop();
        timer.recordInMonitor(systemMonitor);
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void testReceiptSearchPerformance() {
        System.out.println("\n=== RECEIPT SEARCH PERFORMANCE TEST ===");
        
        PerformanceTimer timer = PerformanceTimer.startNew("Receipt Performance Test");
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        receiptService.performanceComparisonDateRange(startDate, endDate);
        timer.stop();
        timer.recordInMonitor(systemMonitor);
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void analyzeMemoryUsage() {
        System.out.println("\n=== MEMORY USAGE ANALYSIS ===");
        
        PerformanceTimer timer = PerformanceTimer.startNew("Memory Analysis");
        
        SystemMonitorService.MemoryInfo memInfo = systemMonitor.getCurrentMemoryInfo();
        
        System.out.println("Current Memory Status:");
        System.out.printf("├─ Maximum Available: %d MB\n", memInfo.getMaxMemoryMB());
        System.out.printf("├─ Currently Allocated: %d MB\n", memInfo.getTotalMemoryMB());
        System.out.printf("├─ In Use: %d MB (%.1f%%)\n", 
                memInfo.getUsedMemoryMB(), memInfo.getMemoryUsagePercentage());
        System.out.printf("├─ Free: %d MB\n", memInfo.getFreeMemoryMB());
        System.out.printf("└─ Efficiency Rating: %s\n", memInfo.getEfficiencyRating());
        
        // Memory usage by data structure (estimated)
        System.out.println("\nEstimated Memory Usage by Component:");
        int expenditureCount = expenditureService.getExpenditureCount();
        int categoryCount = categoryService.getCategoryCount();
        int accountCount = bankAccountService.getAccountCount();
        
        // Rough estimates based on object sizes
        long expenditureMemory = expenditureCount * 200; // ~200 bytes per expenditure
        long categoryMemory = categoryCount * 50;        // ~50 bytes per category
        long accountMemory = accountCount * 150;         // ~150 bytes per account
        
        System.out.printf("├─ Expenditures (%d): ~%d KB\n", expenditureCount, expenditureMemory / 1024);
        System.out.printf("├─ Categories (%d): ~%d KB\n", categoryCount, categoryMemory / 1024);
        System.out.printf("├─ Bank Accounts (%d): ~%d KB\n", accountCount, accountMemory / 1024);
        System.out.printf("└─ System Overhead: ~%d KB\n", 
                (memInfo.getUsedMemoryMB() * 1024) - (expenditureMemory + categoryMemory + accountMemory) / 1024);
        
        // Memory recommendations
        System.out.println("\nMemory Optimization Recommendations:");
        if (memInfo.getMemoryUsagePercentage() > 80) {
            System.out.println("⚠️ HIGH MEMORY USAGE - Consider:");
            System.out.println("   • Archiving old expenditure records");
            System.out.println("   • Implementing data pagination");
            System.out.println("   • Running garbage collection");
        } else if (memInfo.getMemoryUsagePercentage() > 60) {
            System.out.println("📊 MODERATE MEMORY USAGE - Consider:");
            System.out.println("   • Monitoring growth trends");
            System.out.println("   • Periodic data cleanup");
        } else {
            System.out.println("✅ OPTIMAL MEMORY USAGE");
            System.out.println("   • Current usage is efficient");
            System.out.println("   • System has room for growth");
        }
        
        timer.stop();
        timer.recordInMonitor(systemMonitor);
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void runAllPerformanceTests() {
        System.out.println("\n=== COMPREHENSIVE PERFORMANCE TEST SUITE ===");
        System.out.println("Running all performance tests...\n");
        
        PerformanceTimer overallTimer = PerformanceTimer.startNew("Complete Performance Test Suite");
        
        // Test each component
        System.out.println("1/4 Testing Expenditure Service...");
        expenditureService.performanceComparison(1000.0, 5000.0);
        
        System.out.println("\n2/4 Testing Category Service...");
        categoryService.performanceComparisonSearch("C");
        
        System.out.println("\n3/4 Testing Bank Account Service...");
        bankAccountService.performanceComparisonBalance(10000.0);
        
        System.out.println("\n4/4 Testing Receipt Service...");
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        receiptService.performanceComparisonDateRange(startDate, endDate);
        
        overallTimer.stop();
        overallTimer.recordInMonitor(systemMonitor);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PERFORMANCE TEST SUITE COMPLETED");
        System.out.printf("Total execution time: %.2f ms\n", overallTimer.getElapsedTimeMs());
        System.out.println("=".repeat(60));
        
        // Display updated performance meter
        System.out.println("\nUpdated System Performance:");
        systemMonitor.displayLivePerformanceMeter();
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
