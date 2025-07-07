package com.nkwarealestate.expenditure.cli;

import java.util.Scanner;

/**
 * Menu-driven command-line interface for the expenditure management system
 */
public class MenuSystem {
    
    private Scanner scanner;
    private boolean running;
    
    public MenuSystem() {
        this.scanner = new Scanner(System.in);
        this.running = true;
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
                System.out.println("\n[Expenditure Management - Coming Soon...]");
                break;
            case 2:
                System.out.println("\n[Category Management - Coming Soon...]");
                break;
            case 3:
                System.out.println("\n[Bank Account Management - Coming Soon...]");
                break;
            case 4:
                System.out.println("\n[Search & Sort - Coming Soon...]");
                break;
            case 5:
                System.out.println("\n[Receipt/Invoice Management - Coming Soon...]");
                break;
            case 6:
                System.out.println("\n[Financial Analysis & Reports - Coming Soon...]");
                break;
            case 7:
                System.out.println("\n[System Settings - Coming Soon...]");
                break;
            case 0:
                running = false;
                break;
            default:
                System.out.println("\nInvalid option. Please select a number between 0-7.");
                break;
        }
    }
}
