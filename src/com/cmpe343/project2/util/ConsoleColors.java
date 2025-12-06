package com.cmpe343.project2.util;

/**
 * Utility class for managing ANSI escape codes for colored console output.
 * This enhances the user experience by providing visual cues for errors,
 * successes, and menus.
 */
public class ConsoleColors {
    // Reset
    public static final String RESET = "\033[0m";

    // Regular Colors
    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";

    // Bold
    public static final String RED_BOLD = "\033[1;31m";
    public static final String GREEN_BOLD = "\033[1;32m";
    public static final String YELLOW_BOLD = "\033[1;33m";
    public static final String BLUE_BOLD = "\033[1;34m";
    public static final String PURPLE_BOLD = "\033[1;35m";
    public static final String CYAN_BOLD = "\033[1;36m";
    public static final String WHITE_BOLD = "\033[1;37m";

    /**
     * Prints an error message in Red.
     * 
     * @param msg The message to print.
     */
    public static void printError(String msg) {
        System.out.println(RED_BOLD + "[-] Error: " + msg + RESET);
    }

    /**
     * Prints a success message in Green.
     * 
     * @param msg The message to print.
     */
    public static void printSuccess(String msg) {
        System.out.println(GREEN_BOLD + "[+] Success: " + msg + RESET);
    }

    /**
     * Prints an info message in Cyan.
     * 
     * @param msg The message to print.
     */
    public static void printInfo(String msg) {
        System.out.println(CYAN + "[i] Info: " + msg + RESET);
    }

    /**
     * Prints a warning message in Yellow.
     * 
     * @param msg The message to print.
     */
    public static void printWarning(String msg) {
        System.out.println(YELLOW + "[!] Warning: " + msg + RESET);
    }
}