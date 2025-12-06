package com.cmpe343.project2.util;

import java.util.Arrays;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Helper class to handle user input from the console safely.
 * Prevents crashes due to InputMismatchException and handles Data Validation.
 */
public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String BACK_KEYWORD = "back";

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
     * Reads a string with validation and optional ability to go back.
     *
     * @param prompt      Text to display.
     * @param allowEmpty  Whether an empty response is accepted.
     * @param pattern     Regular expression that the input must satisfy.
     * @param errorText   Message to display when the validation fails.
     * @param allowBack   When true, users can type "back" to return (returns null).
     * @return The validated string, or null if the user chose to go back.
     */
    public static String readValidatedString(String prompt, boolean allowEmpty, String pattern, String errorText,
            boolean allowBack) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();

            if (allowBack && BACK_KEYWORD.equalsIgnoreCase(input)) {
                return null;
            }

            if (input.isEmpty() && allowEmpty) {
                return "";
            }

            if (input.matches(pattern)) {
                return input;
            }

            ConsoleColors.printError(errorText);
        }
    }

    /**
     * Reads a non-empty alpha-only string (letters, spaces and hyphens allowed).
     */
    public static String readName(String prompt, boolean allowBack) {
        String value = readValidatedString(prompt, false, "[A-Za-z\\-\\s]+",
                "Please use letters, spaces or hyphens only.", allowBack);
        return value == null ? null : value.trim();
    }

    /**
     * Reads an optional alpha-only string (letters, spaces and hyphens allowed).
     */
    public static String readOptionalName(String prompt, boolean allowBack) {
        String value = readValidatedString(prompt + " (Optional)", true, "[A-Za-z\\-\\s]*",
                "Please use letters, spaces or hyphens only.", allowBack);
        return value == null ? null : value.trim();
    }

    /**
     * Reads a string and validates it is a proper email format.
     * Requirement: "Validating records... correctness of user-provided data."
     * 
     * @param prompt The text to display.
     * @return A valid email string.
     */
    public static String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();

            // Basic Regex for standard email format (user@domain.com)
            if (input.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return input;
            }
            ConsoleColors.printError("Invalid email format. Example: user@example.com");
        }
    }

    /**
     * Reads an email or returns empty when optional. Supports "back".
     */
    public static String readOptionalEmail(String prompt, boolean allowBack) {
        while (true) {
            System.out.print(prompt + " (Optional): ");
            String input = scanner.nextLine().trim();

            if (allowBack && BACK_KEYWORD.equalsIgnoreCase(input)) {
                return null;
            }

            if (input.isEmpty()) {
                return "";
            }

            if (input.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return input;
            }
            ConsoleColors.printError("Invalid email format. Example: user@example.com");
        }
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
     * Reads an integer that must be one of the provided options.
     */
    public static int readChoice(String prompt, Integer... allowed) {
        while (true) {
            int value = readInt(prompt);
            if (Arrays.asList(allowed).contains(value)) {
                return value;
            }
            ConsoleColors.printError("Invalid option. Allowed: " + Arrays.toString(allowed));
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
     * Reads an optional phone number; empty allowed.
     */
    public static String readOptionalPhone(String prompt, boolean allowBack) {
        while (true) {
            System.out.print(prompt + " (Optional): ");
            String phone = scanner.nextLine().trim();

            if (allowBack && BACK_KEYWORD.equalsIgnoreCase(phone)) {
                return null;
            }

            if (phone.isEmpty()) {
                return "";
            }
            if (phone.matches("^\\+?[0-9]{7,15}$")) {
                return phone;
            }
            ConsoleColors.printError("Invalid phone format. Digits only, 7-15 chars.");
        }
    }

    /**
     * Clears the terminal using ANSI escape codes.
     */
    public static void clearScreen() {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder builder = os.contains("win")
                ? new ProcessBuilder("cmd", "/c", "cls")
                : new ProcessBuilder("clear");
        try {
            builder.inheritIO().start().waitFor();
            return;
        } catch (Exception e) {
            // Fallback to ANSI escape codes when external command fails
        }

        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Closes the scanner resources.
     */
    public static void close() {
        scanner.close();
    }
}