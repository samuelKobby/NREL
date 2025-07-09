package com.nkwarealestate.expenditure.models;

import java.time.LocalDate;

/**
 * Represents a receipt or invoice in the system
 * Receipts are used to verify expenditures
 */
public class Receipt {
    
    private String id;
    private String expenditureCode;
    private LocalDate date;
    private String vendorName;
    private String filePath;
    private boolean validated;
    private String validatedBy;
    
    public Receipt() {
        // Default constructor
    }
    
    public Receipt(String id, String expenditureCode, LocalDate date, String vendorName, String filePath) {
        this.id = id;
        this.expenditureCode = expenditureCode;
        this.date = date;
        this.vendorName = vendorName;
        this.filePath = filePath;
        this.validated = false;
        this.validatedBy = null;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getExpenditureCode() {
        return expenditureCode;
    }
    
    public void setExpenditureCode(String expenditureCode) {
        this.expenditureCode = expenditureCode;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getVendorName() {
        return vendorName;
    }
    
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public boolean isValidated() {
        return validated;
    }
    
    public void setValidated(boolean validated) {
        this.validated = validated;
    }
    
    public String getValidatedBy() {
        return validatedBy;
    }
    
    public void setValidatedBy(String validatedBy) {
        this.validatedBy = validatedBy;
    }
    
    @Override
    public String toString() {
        return String.format("Receipt{id='%s', expenditure='%s', date=%s, vendor='%s', validated=%s}",
                id, expenditureCode, date, vendorName, validated);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Receipt that = (Receipt) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
