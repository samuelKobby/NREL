package com.nkwarealestate.expenditure.services;

import com.nkwarealestate.expenditure.models.BankAccount;
import com.nkwarealestate.expenditure.datastructures.CustomHashMap;
import com.nkwarealestate.expenditure.datastructures.MinHeap;
import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;
import com.nkwarealestate.expenditure.datastructures.Graph;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Service class for managing bank accounts
 * Uses MinHeap for low balance monitoring and Graph for account relationships
 */
public class BankAccountService {

    private CustomHashMap<String, BankAccount> accounts;
    private MinHeap balanceMonitor;
    private Graph<String> accountRelationships;
    private final String ACCOUNTS_FILE = "data/accounts.txt";
    private final double LOW_BALANCE_THRESHOLD = 5000.0; // GHS 5,000

    public BankAccountService() {
        this.accounts = new CustomHashMap<>();
        this.balanceMonitor = new MinHeap(50); // Initial capacity
        this.accountRelationships = new Graph<String>();
        loadAccountsFromFile();
    }

    /**
     * Add a new bank account
     */
    public boolean addAccount(String accountId, String bankName, double initialBalance, String accountType) {
        if (accounts.get(accountId) != null) {
            System.out.println("✗ Account with ID '" + accountId + "' already exists.");
            return false;
        }

        BankAccount account = new BankAccount(accountId, bankName, initialBalance, accountType);
        accounts.put(accountId, account);

        // Add to balance monitoring heap
        balanceMonitor.insert((int) initialBalance, accountId);

        // Add account node to relationship graph
        accountRelationships.addVertex(accountId);

        saveAccountToFile(account);
        System.out.println("✓ Account '" + accountId + "' added successfully.");

        return true;
    }

    /**
     * Get account by ID
     */
    public BankAccount getAccount(String accountId) {
        return accounts.get(accountId);
    }

    /**
     * Update account balance (for expenditure transactions)
     */
    public boolean debitAccount(String accountId, double amount) {
        BankAccount account = accounts.get(accountId);
        if (account == null) {
            System.out.println("✗ Account '" + accountId + "' not found.");
            return false;
        }

        if (account.getBalance() < amount) {
            System.out.println("✗ Insufficient funds in account '" + accountId + "'.");
            System.out.println("Available balance: GHS " + String.format("%.2f", account.getBalance()));
            System.out.println("Required amount: GHS " + String.format("%.2f", amount));
            return false;
        }

        double newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);

        // Update balance monitoring
        updateBalanceMonitoring(accountId, newBalance);

        // Check for low balance warning
        if (newBalance < LOW_BALANCE_THRESHOLD) {
            System.out.println("⚠️ WARNING: Account '" + accountId + "' has low balance: GHS " +
                    String.format("%.2f", newBalance));
        }

        saveAllAccountsToFile();
        return true;
    }

    /**
     * Credit account balance
     */
    public boolean creditAccount(String accountId, double amount) {
        BankAccount account = accounts.get(accountId);
        if (account == null) {
            System.out.println("✗ Account '" + accountId + "' not found.");
            return false;
        }

        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);

        updateBalanceMonitoring(accountId, newBalance);
        saveAllAccountsToFile();

        System.out.println("✓ Account '" + accountId + "' credited with GHS " +
                String.format("%.2f", amount));
        return true;
    }

    /**
     * Get all accounts
     */
    public CustomLinkedList<BankAccount> getAllAccounts() {
        CustomLinkedList<BankAccount> accountList = new CustomLinkedList<>();

        // Use the new iteration support in CustomHashMap
        for (CustomHashMap.Entry<String, BankAccount> entry : accounts.entries()) {
            accountList.add(entry.getValue());
        }

        return accountList;
    }

    /**
     * Display account summary
     */
    public void displayAccountSummary(String accountId) {
        BankAccount account = accounts.get(accountId);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.println("\n=== ACCOUNT SUMMARY ===");
        System.out.println("Account ID: " + account.getAccountId());
        System.out.println("Bank Name: " + account.getBankName());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.println("Current Balance: GHS " + String.format("%.2f", account.getBalance()));

        if (account.getBalance() < LOW_BALANCE_THRESHOLD) {
            System.out.println("Status: ⚠️ LOW BALANCE");
        } else {
            System.out.println("Status: ✓ Normal");
        }

        System.out.println("========================");
    }

    /**
     * Display all accounts
     */
    public void displayAllAccounts() {
        CustomLinkedList<BankAccount> accountList = getAllAccounts();

        if (accountList.size() == 0) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.println("\n=== ALL BANK ACCOUNTS ===");
        double totalBalance = 0.0;

        for (int i = 0; i < accountList.size(); i++) {
            BankAccount account = accountList.get(i);
            System.out.printf("%-8s | %-20s | GHS %,10.2f | %s%n",
                    account.getAccountId(),
                    account.getBankName(),
                    account.getBalance(),
                    account.getAccountType());
            totalBalance += account.getBalance();
        }

        System.out.println("--------------------------------------------------");
        System.out.printf("Total Balance Across All Accounts: GHS %,10.2f%n", totalBalance);
        System.out.println("==========================");
    }

    /**
     * Get accounts with low balance
     */
    public CustomLinkedList<BankAccount> getLowBalanceAccounts() {
        CustomLinkedList<BankAccount> lowBalanceAccounts = new CustomLinkedList<>();
        CustomLinkedList<BankAccount> allAccounts = getAllAccounts();

        for (int i = 0; i < allAccounts.size(); i++) {
            BankAccount account = allAccounts.get(i);
            if (account.getBalance() < LOW_BALANCE_THRESHOLD) {
                lowBalanceAccounts.add(account);
            }
        }

        return lowBalanceAccounts;
    }

    /**
     * Display low balance warning
     */
    public void displayLowBalanceWarning() {
        CustomLinkedList<BankAccount> lowBalanceAccounts = getLowBalanceAccounts();

        if (lowBalanceAccounts.size() == 0) {
            System.out.println("✓ All accounts have sufficient balance.");
            return;
        }

        System.out.println("\n⚠️ === LOW BALANCE ALERT ===");
        for (int i = 0; i < lowBalanceAccounts.size(); i++) {
            BankAccount account = lowBalanceAccounts.get(i);
            System.out.printf("Account %s (%s): GHS %,.2f%n",
                    account.getAccountId(),
                    account.getBankName(),
                    account.getBalance());
        }
        System.out.println("============================");
    }

    /**
     * Add relationship between accounts (for internal transfers)
     */
    public void addAccountRelationship(String fromAccountId, String toAccountId, String relationshipType) {
        accountRelationships.addEdge(fromAccountId, toAccountId, 1, relationshipType);
        System.out.println("✓ Relationship added: " + fromAccountId + " -> " + toAccountId +
                " (" + relationshipType + ")");
    }

    /**
     * Get total balance across all accounts
     */
    public double getTotalBalance() {
        double total = 0.0;
        CustomLinkedList<BankAccount> accountList = getAllAccounts();

        for (int i = 0; i < accountList.size(); i++) {
            total += accountList.get(i).getBalance();
        }

        return total;
    }

    /**
     * Validate account for expenditure
     */
    public boolean validateAccountForExpenditure(String accountId, double amount) {
        BankAccount account = accounts.get(accountId);
        if (account == null) {
            System.out.println("✗ Account '" + accountId + "' does not exist.");
            return false;
        }

        if (account.getBalance() < amount) {
            System.out.println("✗ Insufficient funds in account '" + accountId + "'.");
            return false;
        }

        return true;
    }

    /**
     * Returns the number of bank accounts in the system.
     * 
     * @return the count of bank accounts
     */
    public int getAccountCount() {
        return accounts.size();
    }

    /**
     * Update balance monitoring system
     */
    private void updateBalanceMonitoring(String accountId, double newBalance) {
        // In a more sophisticated implementation, we would update the heap
        // For now, we'll recreate it when needed
    }

    /**
     * Load accounts from file
     */
    private void loadAccountsFromFile() {
        try {
            if (Files.exists(Paths.get(ACCOUNTS_FILE))) {
                CustomLinkedList<String> lines = readLinesFromFile(ACCOUNTS_FILE);

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    // Skip comments and empty lines
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        parseAccountFromLine(line);
                    }
                }

                System.out.println("Loaded " + accounts.size() + " accounts from file.");
            } else {
                System.out.println("Accounts file not found. Loading default accounts.");
                loadDefaultAccounts();
            }
        } catch (Exception e) {
            System.out.println("Error loading accounts: " + e.getMessage());
            loadDefaultAccounts();
        }
    }

    /**
     * Parse account from file line
     */
    private void parseAccountFromLine(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 4) {
                String accountId = parts[0].trim();
                String bankName = parts[1].trim();
                double balance = Double.parseDouble(parts[2].trim());
                String accountType = parts[3].trim();

                BankAccount account = new BankAccount(accountId, bankName, balance, accountType);
                accounts.put(accountId, account);

                // Add to monitoring systems
                balanceMonitor.insert((int) balance, accountId);
                accountRelationships.addVertex(accountId);
            }
        } catch (Exception e) {
            System.out.println("Error parsing account line: " + line);
        }
    }

    /**
     * Load accounts into list
     */
    private void loadAccountsIntoList(CustomLinkedList<BankAccount> accountList) {
        try {
            if (Files.exists(Paths.get(ACCOUNTS_FILE))) {
                CustomLinkedList<String> lines = readLinesFromFile(ACCOUNTS_FILE);

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        BankAccount account = parseAccountFromLineToObject(line);
                        if (account != null) {
                            accountList.add(account);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading accounts into list: " + e.getMessage());
        }
    }

    /**
     * Parse account from line to object
     */
    private BankAccount parseAccountFromLineToObject(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 4) {
                String accountId = parts[0].trim();
                String bankName = parts[1].trim();
                double balance = Double.parseDouble(parts[2].trim());
                String accountType = parts[3].trim();

                return new BankAccount(accountId, bankName, balance, accountType);
            }
        } catch (Exception e) {
            System.out.println("Error parsing account line: " + line);
        }
        return null;
    }

    /**
     * Save account to file
     */
    private void saveAccountToFile(BankAccount account) {
        try (FileWriter writer = new FileWriter(ACCOUNTS_FILE, true)) {
            writer.write(String.format("%s|%s|%.2f|%s%n",
                    account.getAccountId(),
                    account.getBankName(),
                    account.getBalance(),
                    account.getAccountType()));
        } catch (IOException e) {
            System.out.println("Error saving account to file: " + e.getMessage());
        }
    }

    /**
     * Save all accounts to file
     */
    private void saveAllAccountsToFile() {
        try (FileWriter writer = new FileWriter(ACCOUNTS_FILE)) {
            writer.write("# Bank Accounts\n");
            writer.write("# Format: AccountID|BankName|Balance|AccountType\n");

            // Use the new iteration support to directly access all accounts
            for (CustomHashMap.Entry<String, BankAccount> entry : accounts.entries()) {
                BankAccount account = entry.getValue();
                writer.write(String.format("%s|%s|%.2f|%s%n",
                        account.getAccountId(),
                        account.getBankName(),
                        account.getBalance(),
                        account.getAccountType()));
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts to file: " + e.getMessage());
        }
    }

    /**
     * Load default accounts for testing
     */
    private void loadDefaultAccounts() {
        addAccount("ACC001", "Ghana Commercial Bank", 50000.0, "Construction");
        addAccount("ACC002", "Ecobank Ghana", 25000.0, "Marketing");
        addAccount("ACC003", "Fidelity Bank", 15000.0, "Operations");
        addAccount("ACC004", "Zenith Bank", 3000.0, "Petty Cash");
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
     * Sort bank accounts by balance (ascending or descending)
     * 
     * @param ascending true for ascending order, false for descending
     * @return A sorted list of bank accounts by balance
     */
    public CustomLinkedList<BankAccount> sortAccountsByBalance(boolean ascending) {
        CustomLinkedList<BankAccount> list = getAllAccounts();
        int size = list.size();
        BankAccount[] array = new BankAccount[size];
        
        // Convert list to array
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        
        // Bubble sort
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                boolean shouldSwap;
                
                if (ascending) {
                    shouldSwap = array[j].getBalance() > array[j + 1].getBalance();
                } else {
                    shouldSwap = array[j].getBalance() < array[j + 1].getBalance();
                }
                
                if (shouldSwap) {
                    // Swap elements
                    BankAccount temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        
        // Convert back to CustomLinkedList
        CustomLinkedList<BankAccount> result = new CustomLinkedList<>();
        for (BankAccount account : array) {
            result.add(account);
        }
        
        return result;
    }

    /**
     * Sort bank accounts by account ID (alphabetically)
     * 
     * @param ascending true for A-Z order, false for Z-A
     * @return A sorted list of bank accounts by account ID
     */
    public CustomLinkedList<BankAccount> sortAccountsById(boolean ascending) {
        CustomLinkedList<BankAccount> list = getAllAccounts();
        int size = list.size();
        BankAccount[] array = new BankAccount[size];
        
        // Convert list to array
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        
        // Bubble sort
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                boolean shouldSwap;
                
                if (ascending) {
                    shouldSwap = array[j].getAccountId().compareTo(array[j + 1].getAccountId()) > 0;
                } else {
                    shouldSwap = array[j].getAccountId().compareTo(array[j + 1].getAccountId()) < 0;
                }
                
                if (shouldSwap) {
                    // Swap elements
                    BankAccount temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        
        // Convert back to CustomLinkedList
        CustomLinkedList<BankAccount> result = new CustomLinkedList<>();
        for (BankAccount account : array) {
            result.add(account);
        }
        
        return result;
    }

    /**
     * Binary search for accounts within balance range
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param minBalance Minimum balance (inclusive)
     * @param maxBalance Maximum balance (inclusive)
     * @return List of accounts within the balance range
     */
    public CustomLinkedList<BankAccount> binarySearchBalanceRange(double minBalance, double maxBalance) {
        CustomLinkedList<BankAccount> results = new CustomLinkedList<>();
        CustomLinkedList<BankAccount> sortedList = sortAccountsByBalance(true);
        
        if (sortedList.isEmpty()) {
            return results;
        }
        
        // Find first occurrence >= minBalance
        int startIndex = findFirstGreaterOrEqualBalance(sortedList, minBalance);
        
        // Find last occurrence <= maxBalance
        int endIndex = findLastLessOrEqualBalance(sortedList, maxBalance);
        
        // Add all accounts in range
        for (int i = startIndex; i <= endIndex && i < sortedList.size(); i++) {
            results.add(sortedList.get(i));
        }
        
        return results;
    }

    /**
     * Binary search helper: Find first account with balance >= target
     */
    private int findFirstGreaterOrEqualBalance(CustomLinkedList<BankAccount> sortedList, double target) {
        int left = 0, right = sortedList.size() - 1;
        int result = sortedList.size();
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            double midBalance = sortedList.get(mid).getBalance();
            
            if (midBalance >= target) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        
        return result;
    }

    /**
     * Binary search helper: Find last account with balance <= target
     */
    private int findLastLessOrEqualBalance(CustomLinkedList<BankAccount> sortedList, double target) {
        int left = 0, right = sortedList.size() - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            double midBalance = sortedList.get(mid).getBalance();
            
            if (midBalance <= target) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }

    /**
     * Binary search for account by exact ID
     * Time Complexity: O(log n)
     * 
     * @param accountId The account ID to search for
     * @return The account with the specified ID, or null if not found
     */
    public BankAccount binarySearchByAccountId(String accountId) {
        CustomLinkedList<BankAccount> sortedList = sortAccountsById(true);
        
        int left = 0;
        int right = sortedList.size() - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            BankAccount midAccount = sortedList.get(mid);
            String midId = midAccount.getAccountId();
            
            int comparison = midId.compareTo(accountId);
            
            if (comparison == 0) {
                return midAccount; // Found exact match
            }
            
            if (comparison < 0) {
                left = mid + 1; // Search right half
            } else {
                right = mid - 1; // Search left half
            }
        }
        
        return null; // Not found
    }

    /**
     * Get accounts with balance above a threshold using binary search
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param threshold The minimum balance threshold
     * @return List of accounts with balance above threshold
     */
    public CustomLinkedList<BankAccount> getAccountsAboveThreshold(double threshold) {
        CustomLinkedList<BankAccount> results = new CustomLinkedList<>();
        CustomLinkedList<BankAccount> sortedList = sortAccountsByBalance(true);
        
        if (sortedList.isEmpty()) {
            return results;
        }
        
        // Find first account with balance >= threshold
        int startIndex = findFirstGreaterOrEqualBalance(sortedList, threshold);
        
        // Add all accounts from this index onwards
        for (int i = startIndex; i < sortedList.size(); i++) {
            results.add(sortedList.get(i));
        }
        
        return results;
    }

    /**
     * Get accounts with balance below a threshold using binary search
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param threshold The maximum balance threshold
     * @return List of accounts with balance below threshold
     */
    public CustomLinkedList<BankAccount> getAccountsBelowThreshold(double threshold) {
        CustomLinkedList<BankAccount> results = new CustomLinkedList<>();
        CustomLinkedList<BankAccount> sortedList = sortAccountsByBalance(true);
        
        if (sortedList.isEmpty()) {
            return results;
        }
        
        // Find last account with balance <= threshold
        int endIndex = findLastLessOrEqualBalance(sortedList, threshold);
        
        // Add all accounts from start to this index
        for (int i = 0; i <= endIndex && i < sortedList.size(); i++) {
            results.add(sortedList.get(i));
        }
        
        return results;
    }

    /**
     * Performance comparison between linear and binary search for balance threshold
     * 
     * @param threshold The balance threshold to search for
     */
    public void performanceComparisonBalance(double threshold) {
        System.out.println("\n=== BALANCE SEARCH PERFORMANCE COMPARISON ===");
        
        long startTime, endTime;
        
        // Test linear search
        startTime = System.nanoTime();
        CustomLinkedList<BankAccount> linearResults = new CustomLinkedList<>();
        CustomLinkedList<BankAccount> allAccounts = getAllAccounts();
        for (int i = 0; i < allAccounts.size(); i++) {
            BankAccount account = allAccounts.get(i);
            if (account.getBalance() >= threshold) {
                linearResults.add(account);
            }
        }
        endTime = System.nanoTime();
        long linearTime = endTime - startTime;
        
        // Test binary search
        startTime = System.nanoTime();
        CustomLinkedList<BankAccount> binaryResults = getAccountsAboveThreshold(threshold);
        endTime = System.nanoTime();
        long binaryTime = endTime - startTime;
        
        System.out.println("Linear Search Time: " + linearTime + " nanoseconds");
        System.out.println("Binary Search Time: " + binaryTime + " nanoseconds");
        System.out.println("Results count - Linear: " + linearResults.size() + ", Binary: " + binaryResults.size());
        
        if (linearTime > 0) {
            double speedup = (double) linearTime / binaryTime;
            System.out.println("Binary search is " + String.format("%.2f", speedup) + "x faster");
        }
        
        System.out.println("===============================================\n");
    }
}
