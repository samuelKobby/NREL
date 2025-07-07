package com.nkwarealestate.expenditure;

import com.nkwarealestate.expenditure.cli.MenuSystem;

/**
 * Main entry point for the NREL Expenditure Management System
 * 
 * This application provides offline-first expenditure tracking
 * for Nkwa Real Estate Ltd using custom data structures.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   NREL Expenditure Management System");
        System.out.println("   Nkwa Real Estate Ltd");
        System.out.println("===========================================");
        System.out.println();
        
        try {
            MenuSystem menuSystem = new MenuSystem();
            menuSystem.start();
        } catch (Exception e) {
            System.err.println("An error occurred while starting the application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
