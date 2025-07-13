package com.nkwarealestate.expenditure.services;

import com.nkwarealestate.expenditure.models.Receipt;
import com.nkwarealestate.expenditure.models.Expenditure;
import com.nkwarealestate.expenditure.datastructures.CustomHashMap;
import com.nkwarealestate.expenditure.datastructures.CustomLinkedList;
import com.nkwarealestate.expenditure.datastructures.CustomQueue;
import com.nkwarealestate.expenditure.datastructures.CustomStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Service for managing receipts and invoices
 * Uses CustomHashMap for storage and CustomQueue for validation processing
 */
public class ReceiptService {
    
    private CustomHashMap<String, Receipt> receipts;
    private CustomQueue<Receipt> pendingValidationQueue;
    private CustomStack<Receipt> recentUploads;
    private int nextReceiptId;
    private ExpenditureService expenditureService;
    private DateTimeFormatter dateFormatter;
    private String receiptStoragePath;
    
    public ReceiptService(ExpenditureService expenditureService) {
        this.receipts = new CustomHashMap<>();
        this.pendingValidationQueue = new CustomQueue<>();
        this.recentUploads = new CustomStack<>();
        this.nextReceiptId = 1;
        this.expenditureService = expenditureService;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Set up receipts storage directory
        this.receiptStoragePath = "data/receipts/";
        createReceiptDirectory();
    }
    
    /**
     * Create the receipts directory if it doesn't exist
     */
    private void createReceiptDirectory() {
        try {
            File directory = new File(receiptStoragePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } catch (Exception e) {
            System.out.println("Error creating receipt directory: " + e.getMessage());
        }
    }
    
    /**
     * Upload a receipt file and link it to an expenditure
     * 
     * @param expenditureCode The code of the expenditure to link
     * @param originalFilePath The original path of the receipt file
     * @param vendorName The name of the vendor who issued the receipt
     * @param date The date on the receipt
     * @return The receipt ID if successful, null otherwise
     */
    public String uploadReceipt(String expenditureCode, String originalFilePath, 
                              String vendorName, LocalDate date) {
        // Verify the expenditure exists
        Expenditure expenditure = expenditureService.getExpenditure(expenditureCode);
        if (expenditure == null) {
            System.out.println("✗ Expenditure not found with code: " + expenditureCode);
            return null;
        }
        
        // Verify the file exists
        File originalFile = new File(originalFilePath);
        if (!originalFile.exists() || !originalFile.isFile()) {
            System.out.println("✗ File not found: " + originalFilePath);
            return null;
        }
        
        try {
            // Generate receipt ID
            String receiptId = generateReceiptId();
            
            // Determine file extension
            String extension = "";
            int lastDotIndex = originalFilePath.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = originalFilePath.substring(lastDotIndex);
            }
            
            // Create the destination path
            String destFileName = receiptId + extension;
            String destPath = receiptStoragePath + destFileName;
            
            // Copy file to receipts directory
            Path source = Paths.get(originalFilePath);
            Path destination = Paths.get(destPath);
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            
            // Create receipt object
            Receipt receipt = new Receipt(receiptId, expenditureCode, date, vendorName, destPath);
            
            // Store receipt
            receipts.put(receiptId, receipt);
            
            // Add to validation queue
            pendingValidationQueue.enqueue(receipt);
            
            // Add to recent uploads stack
            recentUploads.push(receipt);
            
            // Link receipt to expenditure
            expenditure.setReceiptId(receiptId);
            
            // Increment ID counter
            nextReceiptId++;
            
            System.out.println("✓ Receipt uploaded successfully with ID: " + receiptId);
            return receiptId;
            
        } catch (IOException e) {
            System.out.println("✗ Error uploading receipt: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate a receipt in the validation queue
     * 
     * @param validatorName The name of the person validating the receipt
     * @return true if validation was successful, false if queue is empty
     */
    public boolean validateNextReceipt(String validatorName) {
        if (pendingValidationQueue.isEmpty()) {
            System.out.println("No receipts pending validation.");
            return false;
        }
        
        Receipt receipt = pendingValidationQueue.dequeue();
        receipt.setValidated(true);
        receipt.setValidatedBy(validatorName);
        
        System.out.println("✓ Receipt " + receipt.getId() + " validated successfully.");
        return true;
    }
    
    /**
     * Get the next receipt pending validation without removing from queue
     */
    public Receipt peekNextPendingReceipt() {
        if (pendingValidationQueue.isEmpty()) {
            return null;
        }
        return pendingValidationQueue.peek();
    }
    
    /**
     * Get a receipt by ID
     */
    public Receipt getReceipt(String receiptId) {
        return receipts.get(receiptId);
    }
    
    /**
     * Get all receipts
     */
    public CustomLinkedList<Receipt> getAllReceipts() {
        CustomLinkedList<Receipt> allReceipts = new CustomLinkedList<>();
        for (Receipt receipt : receipts.values()) {
            allReceipts.add(receipt);
        }
        return allReceipts;
    }
    
    /**
     * Get receipts for a specific expenditure
     */
    public Receipt getReceiptForExpenditure(String expenditureCode) {
        for (Receipt receipt : receipts.values()) {
            if (receipt.getExpenditureCode().equals(expenditureCode)) {
                return receipt;
            }
        }
        return null;
    }
    
    /**
     * Get the most recently uploaded receipts
     * 
     * @param count The number of recent receipts to return
     */
    public CustomLinkedList<Receipt> getRecentReceipts(int count) {
        CustomLinkedList<Receipt> recentList = new CustomLinkedList<>();
        CustomStack<Receipt> tempStack = new CustomStack<>();
        
        // Get up to 'count' receipts from the stack
        int counter = 0;
        while (!recentUploads.isEmpty() && counter < count) {
            Receipt receipt = recentUploads.pop();
            recentList.add(receipt);
            tempStack.push(receipt);
            counter++;
        }
        
        // Restore the stack
        while (!tempStack.isEmpty()) {
            recentUploads.push(tempStack.pop());
        }
        
        return recentList;
    }
    
    /**
     * Delete a receipt
     */
    public boolean deleteReceipt(String receiptId) {
        Receipt receipt = receipts.get(receiptId);
        if (receipt == null) {
            return false;
        }
        
        // Remove from receipts map
        receipts.remove(receiptId);
        
        // Try to delete the file
        try {
            File file = new File(receipt.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            
            // Unlink from expenditure
            Expenditure expenditure = expenditureService.getExpenditure(receipt.getExpenditureCode());
            if (expenditure != null && receiptId.equals(expenditure.getReceiptId())) {
                expenditure.setReceiptId(null);
            }
            
            return true;
        } catch (Exception e) {
            System.out.println("Error deleting receipt file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get count of receipts pending validation
     */
    public int getPendingValidationCount() {
        return pendingValidationQueue.size();
    }
    
    /**
     * Generate unique receipt ID
     */
    private String generateReceiptId() {
        return String.format("RCP%04d", nextReceiptId);
    }
    
    /**
     * Display receipt details
     */
    public void displayReceiptDetails(Receipt receipt) {
        if (receipt == null) {
            System.out.println("No receipt to display.");
            return;
        }
        
        System.out.println("\n=== RECEIPT DETAILS ===");
        System.out.println("ID: " + receipt.getId());
        System.out.println("Expenditure Code: " + receipt.getExpenditureCode());
        System.out.println("Date: " + receipt.getDate().format(dateFormatter));
        System.out.println("Vendor: " + receipt.getVendorName());
        System.out.println("File Path: " + receipt.getFilePath());
        System.out.println("Validation Status: " + (receipt.isValidated() ? "Validated" : "Pending"));
        
        if (receipt.isValidated()) {
            System.out.println("Validated By: " + receipt.getValidatedBy());
        }
        
        System.out.println("========================");
    }

    // ================ BINARY SEARCH ALGORITHMS ================

    /**
     * Sort receipts by date (ascending or descending)
     * 
     * @param ascending true for oldest first, false for newest first
     * @return A sorted list of receipts by date
     */
    public CustomLinkedList<Receipt> sortReceiptsByDate(boolean ascending) {
        CustomLinkedList<Receipt> list = getAllReceipts();
        int size = list.size();
        Receipt[] array = new Receipt[size];
        
        // Convert list to array
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        
        // Bubble sort
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
                    Receipt temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        
        // Convert back to CustomLinkedList
        CustomLinkedList<Receipt> result = new CustomLinkedList<>();
        for (Receipt receipt : array) {
            result.add(receipt);
        }
        
        return result;
    }

    /**
     * Sort receipts by vendor name alphabetically
     * 
     * @param ascending true for A-Z order, false for Z-A
     * @return A sorted list of receipts by vendor name
     */
    public CustomLinkedList<Receipt> sortReceiptsByVendor(boolean ascending) {
        CustomLinkedList<Receipt> list = getAllReceipts();
        int size = list.size();
        Receipt[] array = new Receipt[size];
        
        // Convert list to array
        for (int i = 0; i < size; i++) {
            array[i] = list.get(i);
        }
        
        // Bubble sort
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                boolean shouldSwap;
                
                if (ascending) {
                    shouldSwap = array[j].getVendorName().compareToIgnoreCase(array[j + 1].getVendorName()) > 0;
                } else {
                    shouldSwap = array[j].getVendorName().compareToIgnoreCase(array[j + 1].getVendorName()) < 0;
                }
                
                if (shouldSwap) {
                    // Swap elements
                    Receipt temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        
        // Convert back to CustomLinkedList
        CustomLinkedList<Receipt> result = new CustomLinkedList<>();
        for (Receipt receipt : array) {
            result.add(receipt);
        }
        
        return result;
    }

    /**
     * Binary search for receipts within date range
     * Time Complexity: O(log n + k) where k is number of results
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of receipts within the date range
     */
    public CustomLinkedList<Receipt> binarySearchReceiptsByDateRange(LocalDate startDate, LocalDate endDate) {
        CustomLinkedList<Receipt> results = new CustomLinkedList<>();
        CustomLinkedList<Receipt> sortedList = sortReceiptsByDate(true);
        
        if (sortedList.isEmpty()) {
            return results;
        }
        
        // Find first receipt with date >= startDate
        int startIndex = findFirstReceiptGreaterOrEqualDate(sortedList, startDate);
        
        // Find last receipt with date <= endDate
        int endIndex = findLastReceiptLessOrEqualDate(sortedList, endDate);
        
        // Add all receipts in range
        for (int i = startIndex; i <= endIndex && i < sortedList.size(); i++) {
            results.add(sortedList.get(i));
        }
        
        return results;
    }

    /**
     * Binary search helper: Find first receipt with date >= target
     */
    private int findFirstReceiptGreaterOrEqualDate(CustomLinkedList<Receipt> sortedList, LocalDate target) {
        int left = 0, right = sortedList.size() - 1;
        int result = sortedList.size();
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            LocalDate midDate = sortedList.get(mid).getDate();
            
            if (midDate.isEqual(target) || midDate.isAfter(target)) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        
        return result;
    }

    /**
     * Binary search helper: Find last receipt with date <= target
     */
    private int findLastReceiptLessOrEqualDate(CustomLinkedList<Receipt> sortedList, LocalDate target) {
        int left = 0, right = sortedList.size() - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            LocalDate midDate = sortedList.get(mid).getDate();
            
            if (midDate.isEqual(target) || midDate.isBefore(target)) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }

    /**
     * Binary search for receipts by vendor name
     * Time Complexity: O(log n + k) where k is number of matches
     * 
     * @param vendorName The vendor name to search for
     * @return List of receipts from the specified vendor
     */
    public CustomLinkedList<Receipt> binarySearchReceiptsByVendor(String vendorName) {
        CustomLinkedList<Receipt> results = new CustomLinkedList<>();
        CustomLinkedList<Receipt> sortedList = sortReceiptsByVendor(true);
        
        if (sortedList.isEmpty() || vendorName == null) {
            return results;
        }
        
        // Find first receipt with vendor >= target vendor
        int startIndex = findFirstReceiptWithVendor(sortedList, vendorName);
        
        // Collect all receipts with the exact vendor name
        for (int i = startIndex; i < sortedList.size(); i++) {
            Receipt receipt = sortedList.get(i);
            if (receipt.getVendorName().equalsIgnoreCase(vendorName)) {
                results.add(receipt);
            } else {
                break; // Since list is sorted, no more matches after this
            }
        }
        
        return results;
    }

    /**
     * Binary search helper: Find first receipt with vendor >= target
     */
    private int findFirstReceiptWithVendor(CustomLinkedList<Receipt> sortedList, String targetVendor) {
        int left = 0, right = sortedList.size() - 1;
        int result = sortedList.size();
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            String midVendor = sortedList.get(mid).getVendorName();
            
            if (midVendor.compareToIgnoreCase(targetVendor) >= 0) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        
        return result;
    }

    /**
     * Get receipts by validation status using efficient filtering
     * 
     * @param validated true for validated receipts, false for pending
     * @return List of receipts with the specified validation status
     */
    public CustomLinkedList<Receipt> getReceiptsByValidationStatus(boolean validated) {
        CustomLinkedList<Receipt> results = new CustomLinkedList<>();
        CustomLinkedList<Receipt> allReceipts = getAllReceipts();
        
        for (int i = 0; i < allReceipts.size(); i++) {
            Receipt receipt = allReceipts.get(i);
            if (receipt.isValidated() == validated) {
                results.add(receipt);
            }
        }
        
        return results;
    }

    /**
     * Search receipts by vendor name prefix using binary search
     * Time Complexity: O(log n + k) where k is number of matches
     * 
     * @param vendorPrefix The vendor name prefix to search for
     * @return List of receipts from vendors whose names start with the prefix
     */
    public CustomLinkedList<Receipt> searchReceiptsByVendorPrefix(String vendorPrefix) {
        CustomLinkedList<Receipt> results = new CustomLinkedList<>();
        CustomLinkedList<Receipt> sortedList = sortReceiptsByVendor(true);
        
        if (sortedList.isEmpty() || vendorPrefix == null || vendorPrefix.trim().isEmpty()) {
            return results;
        }
        
        // Find first receipt with vendor >= prefix
        int startIndex = findFirstReceiptWithVendor(sortedList, vendorPrefix.trim());
        
        // Collect all receipts that start with the prefix
        for (int i = startIndex; i < sortedList.size(); i++) {
            Receipt receipt = sortedList.get(i);
            if (receipt.getVendorName().toLowerCase().startsWith(vendorPrefix.toLowerCase())) {
                results.add(receipt);
            } else {
                break; // Since list is sorted, no more matches after this
            }
        }
        
        return results;
    }

    /**
     * Performance comparison between linear and binary search for date range
     * 
     * @param startDate Start date for the range
     * @param endDate End date for the range
     */
    public void performanceComparisonDateRange(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n=== RECEIPT DATE SEARCH PERFORMANCE COMPARISON ===");
        
        long startTime, endTime;
        
        // Test linear search
        startTime = System.nanoTime();
        CustomLinkedList<Receipt> linearResults = new CustomLinkedList<>();
        CustomLinkedList<Receipt> allReceipts = getAllReceipts();
        for (int i = 0; i < allReceipts.size(); i++) {
            Receipt receipt = allReceipts.get(i);
            LocalDate receiptDate = receipt.getDate();
            if ((receiptDate.isEqual(startDate) || receiptDate.isAfter(startDate)) &&
                (receiptDate.isEqual(endDate) || receiptDate.isBefore(endDate))) {
                linearResults.add(receipt);
            }
        }
        endTime = System.nanoTime();
        long linearTime = endTime - startTime;
        
        // Test binary search
        startTime = System.nanoTime();
        CustomLinkedList<Receipt> binaryResults = binarySearchReceiptsByDateRange(startDate, endDate);
        endTime = System.nanoTime();
        long binaryTime = endTime - startTime;
        
        System.out.println("Date range: " + startDate.format(dateFormatter) + " to " + endDate.format(dateFormatter));
        System.out.println("Linear Search Time: " + linearTime + " nanoseconds");
        System.out.println("Binary Search Time: " + binaryTime + " nanoseconds");
        System.out.println("Results count - Linear: " + linearResults.size() + ", Binary: " + binaryResults.size());
        
        if (linearTime > 0 && binaryTime > 0) {
            double speedup = (double) linearTime / binaryTime;
            System.out.println("Binary search is " + String.format("%.2f", speedup) + "x faster");
        }
        
        System.out.println("====================================================\n");
    }

    /**
     * Display receipts in tabular format
     * 
     * @param receipts The list of receipts to display
     */
    public void displayReceiptsTable(CustomLinkedList<Receipt> receipts) {
        if (receipts.isEmpty()) {
            System.out.println("No receipts found matching your criteria.");
            return;
        }
        
        // Table header
        System.out.println("\n+----------+-------------+------------+----------------------+------------+");
        System.out.println("| ID       | EXP CODE    | DATE       | VENDOR               | STATUS     |");
        System.out.println("+----------+-------------+------------+----------------------+------------+");
        
        // Table rows
        for (int i = 0; i < receipts.size(); i++) {
            Receipt receipt = receipts.get(i);
            String vendor = receipt.getVendorName();
            if (vendor.length() > 20) {
                vendor = vendor.substring(0, 17) + "...";
            }
            
            String status = receipt.isValidated() ? "Validated" : "Pending";
            
            System.out.printf("| %-8s | %-11s | %-10s | %-20s | %-10s |\n",
                    receipt.getId(),
                    receipt.getExpenditureCode(),
                    receipt.getDate().format(dateFormatter),
                    vendor,
                    status);
        }
        
        // Table footer
        System.out.println("+----------+-------------+------------+----------------------+------------+");
        System.out.println("Total: " + receipts.size() + " receipt(s)");
    }
}
