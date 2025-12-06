package com.cmpe343.project2;

import com.cmpe343.project2.db.DataSeeder;
import com.cmpe343.project2.ui.MenuSystem;
import com.cmpe343.project2.util.ConsoleColors;

/**
 * CMPE 343 - Project 2
 * Advanced Java Programming with Object-Oriented Paradigms and Database
 * Integration.
 *
 * Main entry point responsible for orchestrating application startup. The
 * method performs the following steps in order:
 * <ol>
 * <li>Seed baseline data via {@link com.cmpe343.project2.db.DataSeeder} so the
 *     UI can be explored immediately.</li>
 * <li>Create the {@link com.cmpe343.project2.ui.MenuSystem} controller.</li>
 * <li>Delegate user interaction by invoking
 *     {@link com.cmpe343.project2.ui.MenuSystem#start()}.</li>
 * </ol>
 *
 * @author Raul Ibrahimov
 * @author Akhmed Nazarov
 * @author Omirbek Ubaidayev
 * @author Kuandyk Kyrykbayev
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