package com.nkwarealestate.expenditure.models;

import java.time.LocalDate;

/**
 * Represents an expenditure record in the system
 */
public class Expenditure {
    
    private String code;
    private double amount;
    private LocalDate date;
    private Phase phase;
    private String category;
    private String accountId;
    private String description;
    private String receiptId;
    
    public Expenditure() {
        // Default constructor
    }
    
    public Expenditure(String code, double amount, LocalDate date, Phase phase, 
                      String category, String accountId, String description) {
        this.code = code;
        this.amount = amount;
        this.date = date;
        this.phase = phase;
        this.category = category;
        this.accountId = accountId;
        this.description = description;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Phase getPhase() {
        return phase;
    }
    
    public void setPhase(Phase phase) {
        this.phase = phase;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getAccountId() {
        return accountId;
    }
    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getReceiptId() {
        return receiptId;
    }
    
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }
    
    @Override
    public String toString() {
        return String.format("Expenditure{code='%s', amount=%.2f, date=%s, phase=%s, category='%s', account='%s'}",
                code, amount, date, phase, category, accountId);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Expenditure that = (Expenditure) obj;
        return code != null ? code.equals(that.code) : that.code == null;
    }
    
    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}
