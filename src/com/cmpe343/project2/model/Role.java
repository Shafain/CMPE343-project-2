package com.cmpe343.project2.model;

/**
 * Enumeration representing the different roles a User can hold.
 * Defines the hierarchy of permissions.
 */
public enum Role {
    TESTER,
    JUNIOR, // Junior Developer
    SENIOR, // Senior Developer
    MANAGER;

    /**
     * Helper to parse a string from DB to Enum safely.
     */
    public static Role fromString(String roleStr) {
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return TESTER; // Default fall back
        }
    }
}