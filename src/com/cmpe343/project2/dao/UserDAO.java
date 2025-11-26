package com.cmpe343.project2.dao;

import com.cmpe343.project2.db.DatabaseConnection;
import com.cmpe343.project2.model.Role;
import com.cmpe343.project2.model.User;
import com.cmpe343.project2.util.ConsoleColors;
import com.cmpe343.project2.util.SecurityUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entities.
 * Handles authentication, CRUD operations, and Password Management for Users.
 */
public class UserDAO {

    /**
     * Authenticates a user by username and password.
     * 
     * @param username    The input username.
     * @param rawPassword The plain text password.
     * @return User object if successful, null otherwise.
     */
    public User authenticate(String username, String rawPassword) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (SecurityUtil.verifyPassword(rawPassword, storedHash)) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            ConsoleColors.printError("Authentication failed due to DB error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Adds a new user to the database.
     * Only accessible by Managers.
     */
    public boolean addUser(User user, String rawPassword) {
        String sql = "INSERT INTO users (username, password_hash, first_name, last_name, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            // Encrypt password before storing
            stmt.setString(2, SecurityUtil.hashPassword(rawPassword));
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getRole().toString());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            ConsoleColors.printError("Error adding user (Username might be duplicate): " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all users.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            ConsoleColors.printError("Error listing users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Deletes a user by ID.
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ConsoleColors.printError("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing user's details (Role, Name, etc).
     * Passwords are updated via a separate method.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getRole().toString());
            stmt.setInt(4, user.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ConsoleColors.printError("Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds a user by ID.
     * Helper method for update operations.
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            ConsoleColors.printError("Error finding user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates the password for a specific user.
     * 
     * @param userId         The ID of the user changing their password.
     * @param newRawPassword The new plain text password.
     * @return true if successful.
     */
    public boolean updatePassword(int userId, String newRawPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Hash the new password before storing it
            stmt.setString(1, SecurityUtil.hashPassword(newRawPassword));
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ConsoleColors.printError("Error updating password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper to map a ResultSet row to a User object.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setRole(Role.fromString(rs.getString("role")));
        return user;
    }
}