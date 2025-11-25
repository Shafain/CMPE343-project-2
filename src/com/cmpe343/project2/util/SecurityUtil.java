package com.cmpe343.project2.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

/**
 * Utility class for security-related operations, specifically password hashing.
 */
public class SecurityUtil {

    /**
     * Hashes a plain text password using SHA-256 algorithm.
     * 
     * @param password The plain text password.
     * @return The hexadecimal string representation of the hashed password.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            // Convert byte array into signum representation
            BigInteger number = new BigInteger(1, hash);
            // Convert message digest into hex value
            StringBuilder hexString = new StringBuilder(number.toString(16));
            // Pad with leading zeros
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            ConsoleColors.printError("Hashing algorithm not found.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies a password against a stored hash.
     * 
     * @param inputPassword The plain text password entered by the user.
     * @param storedHash    The hash stored in the database.
     * @return true if matches, false otherwise.
     */
    public static boolean verifyPassword(String inputPassword, String storedHash) {
        String inputHash = hashPassword(inputPassword);
        return inputHash.equals(storedHash);
    }
}