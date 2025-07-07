package com.nkwarealestate.expenditure.models;

/**
 * Represents a bank account in the system
 */
public class BankAccount {
    
    private String accountId;
    private String bankName;
    private double balance;
    private String accountNumber;
    private boolean isActive;
    
    public BankAccount() {
        // Default constructor
    }
    
    public BankAccount(String accountId, String bankName, double balance, String accountNumber) {
        this.accountId = accountId;
        this.bankName = bankName;
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.isActive = true;
    }
    
    // Getters and Setters
    public String getAccountId() {
        return accountId;
    }
    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    /**
     * Deduct amount from account balance
     * @param amount Amount to deduct
     * @return true if successful, false if insufficient funds
     */
    public boolean deductAmount(double amount) {
        if (amount <= 0) {
            return false;
        }
        
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        
        return false; // Insufficient funds
    }
    
    /**
     * Add amount to account balance
     * @param amount Amount to add
     */
    public void addAmount(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    /**
     * Check if account has low funds (less than specified threshold)
     */
    public boolean hasLowFunds(double threshold) {
        return balance < threshold;
    }
    
    @Override
    public String toString() {
        return String.format("BankAccount{id='%s', bank='%s', balance=%.2f, account='%s', active=%s}",
                accountId, bankName, balance, accountNumber, isActive);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BankAccount that = (BankAccount) obj;
        return accountId != null ? accountId.equals(that.accountId) : that.accountId == null;
    }
    
    @Override
    public int hashCode() {
        return accountId != null ? accountId.hashCode() : 0;
    }
}
