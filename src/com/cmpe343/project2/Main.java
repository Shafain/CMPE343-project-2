package com.cmpe343.project2;

import com.cmpe343.project2.db.DataSeeder;
import com.cmpe343.project2.ui.MenuSystem;
import com.cmpe343.project2.util.ConsoleColors;

/**
 * CMPE 343 - Project 2
 * Advanced Java Programming with Object-Oriented Paradigms and Database
 * Integration.
 * 
 * Main Entry Point.
 */
public class Main {

    public static void main(String[] args) {
        try {
            // 1. Initialize Database and Seed Data if necessary
            // This ensures the project requirements (50 records, 4 specific users) are met
            // immediately.
            DataSeeder.seedData();

            // 2. Launch the Menu System
            MenuSystem menuSystem = new MenuSystem();
            menuSystem.start();

        } catch (Exception e) {
            ConsoleColors.printError("Critical System Failure: " + e.getMessage());
            e.printStackTrace();
        }
    }
}