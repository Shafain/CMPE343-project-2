package com.cmpe343.project2.util;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Helper class to handle user input from the console safely.
 * Prevents crashes due to InputMismatchException.
 */
public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Reads a string from the console.
     * 
     * @param prompt The text to display to the user.
     * @return The user input string.
     */
    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    /**
     * Reads an integer from the console with validation.
     * 
     * @param prompt The text to display.
     * @return A valid integer.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                ConsoleColors.printError("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a Date in YYYY-MM-DD format.
     * Validates logical dates (e.g., rejects Feb 30).
     * 
     * @param prompt The text to display.
     * @return LocalDate object or null if optional/skipped.
     */
    public static LocalDate readDate(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty())
                return null; // Allow empty for optional fields

            try {
                LocalDate date = LocalDate.parse(input, formatter);
                // Additional logic: Check if date is in the future (for birthdate)
                if (date.isAfter(LocalDate.now())) {
                    ConsoleColors.printError("Birth date cannot be in the future.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                ConsoleColors.printError("Invalid date format or logically invalid date (e.g., Feb 30). Try again.");
            }
        }
    }

    /**
     * Reads a phone number and validates basic format.
     * 
     * @param prompt The prompt text.
     * @return Valid phone string.
     */
    public static String readPhone(String prompt) {
        while (true) {
            String phone = readString(prompt);
            // Simple Regex for digits, allowing + at start, length 7-15
            if (phone.matches("^\\+?[0-9]{7,15}$")) {
                return phone;
            }
            ConsoleColors.printError("Invalid phone format. Digits only, 7-15 chars.");
        }
    }

    /**
     * Closes the scanner resources.
     */
    public static void close() {
        scanner.close();
    }
}