package com.cmpe343.project2.model;

/**
 * Represents a User in the system.
 * Maps to the 'users' table in the database.
 * Encapsulates user credentials and role information.
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private Role role;

    // Constructors
    public User() {
    }

    public User(String username, String passwordHash, String firstName, String lastName, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public User(int userId, String username, String passwordHash, String firstName, String lastName, Role role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    // Getters and Setters (Encapsulation)

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Returns the full name of the user.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format("User [ID=%d, Name=%s, Role=%s]", userId, getFullName(), role);
    }
}