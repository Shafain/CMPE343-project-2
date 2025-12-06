package com.cmpe343.project2.model;

/**
 * Represents a User record mapped to the {@code users} table. The class holds
 * credential metadata and role assignments that drive authorization within the
 * console UI. Data flow is straightforward: DAOs populate fields, menu logic
 * reads them through getters, and updates occur through setters.
 *
 * @author Raul Ibrahimov
 * @author Akhmed Nazarov
 * @author Omirbek Ubaidayev
 * @author Kuandyk Kyrykbayev
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private Role role;

    // Constructors

    /**
     * Creates an empty user placeholder typically used prior to hydration from
     * input or database records.
     */
    public User() {
    }

    /**
     * Builds a user instance without a database identifier, assigning each
     * property in order so DAO layers can persist it later.
     */
    public User(String username, String passwordHash, String firstName, String lastName, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    /**
     * Full constructor including primary key, used when reconstructing existing
     * users from persistence.
     */
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

    /**
     * Sets the primary key value after an insert or during an update.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the unique username used for authentication.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Updates the username field.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the hashed password stored for the account.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Persists the hashed password string.
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Exposes the first name portion of the profile.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Updates the stored first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Exposes the last name portion of the profile.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Updates the stored last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the role that drives menu visibility and permissions.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Changes the role assignment for the user.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Concatenates first and last names with a space for display purposes.
     *
     * @return full name string
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format("User [ID=%d, Name=%s, Role=%s]", userId, getFullName(), role);
    }
}