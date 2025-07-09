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
}
