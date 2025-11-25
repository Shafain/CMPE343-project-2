package com.cmpe343.project2.dao;

import com.cmpe343.project2.db.DatabaseConnection;
import com.cmpe343.project2.model.Contact;
import com.cmpe343.project2.util.ConsoleColors;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Contact entities.
 * Handles creating, reading, updating, searching, and deleting contacts.
 */
public class ContactDAO {

    /**
     * Adds a new contact.
     */
    public boolean addContact(Contact c) {
        String sql = "INSERT INTO contacts (first_name, middle_name, last_name, nickname, phone_primary, phone_secondary, email, linkedin_url, birth_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getFirstName());
            stmt.setString(2, c.getMiddleName());
            stmt.setString(3, c.getLastName());
            stmt.setString(4, c.getNickname());
            stmt.setString(5, c.getPhonePrimary());
            stmt.setString(6, c.getPhoneSecondary());
            stmt.setString(7, c.getEmail());
            stmt.setString(8, c.getLinkedinUrl());
            stmt.setDate(9, java.sql.Date.valueOf(c.getBirthDate()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ConsoleColors.printError("Error adding contact: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing contact.
     */
    public boolean updateContact(Contact c) {
        String sql = "UPDATE contacts SET first_name=?, middle_name=?, last_name=?, nickname=?, phone_primary=?, phone_secondary=?, email=?, linkedin_url=?, birth_date=? WHERE contact_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getFirstName());
            stmt.setString(2, c.getMiddleName());
            stmt.setString(3, c.getLastName());
            stmt.setString(4, c.getNickname());
            stmt.setString(5, c.getPhonePrimary());
            stmt.setString(6, c.getPhoneSecondary());
            stmt.setString(7, c.getEmail());
            stmt.setString(8, c.getLinkedinUrl());
            stmt.setDate(9, java.sql.Date.valueOf(c.getBirthDate()));
            stmt.setInt(10, c.getContactId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ConsoleColors.printError("Error updating contact: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a contact by ID.
     */
    public boolean deleteContact(int contactId) {
        String sql = "DELETE FROM contacts WHERE contact_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contactId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ConsoleColors.printError("Error deleting contact: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches all contacts, optionally sorted.
     * 
     * @param sortByColumn The database column name to sort by (e.g., "last_name").
     * @param ascending    True for ASC, false for DESC.
     */
    public List<Contact> findAll(String sortByColumn, boolean ascending) {
        List<Contact> list = new ArrayList<>();
        // Validate column name to prevent SQL injection (basic whitelist)
        if (!isValidColumn(sortByColumn))
            sortByColumn = "last_name";

        String order = ascending ? "ASC" : "DESC";
        String sql = "SELECT * FROM contacts ORDER BY " + sortByColumn + " " + order;

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToContact(rs));
            }
        } catch (SQLException e) {
            ConsoleColors.printError("Error listing contacts: " + e.getMessage());
        }
        return list;
    }

    /**
     * Single-field search. Performs a partial match (LIKE).
     */
    public List<Contact> searchByField(String field, String value) {
        List<Contact> list = new ArrayList<>();
        if (!isValidColumn(field))
            return list;

        String sql = "SELECT * FROM contacts WHERE " + field + " LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + value + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToContact(rs));
            }
        } catch (SQLException e) {
            ConsoleColors.printError("Search error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Multi-field search (Complex Requirement).
     * Example: Name contains "Ahmet" AND Phone contains "555".
     */
    public List<Contact> advancedSearch(String nameQuery, String phoneQuery) {
        List<Contact> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM contacts WHERE 1=1");

        if (nameQuery != null && !nameQuery.isEmpty()) {
            sql.append(" AND (first_name LIKE ? OR last_name LIKE ?)");
        }
        if (phoneQuery != null && !phoneQuery.isEmpty()) {
            sql.append(" AND (phone_primary LIKE ? OR phone_secondary LIKE ?)");
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (nameQuery != null && !nameQuery.isEmpty()) {
                stmt.setString(paramIndex++, "%" + nameQuery + "%");
                stmt.setString(paramIndex++, "%" + nameQuery + "%");
            }
            if (phoneQuery != null && !phoneQuery.isEmpty()) {
                stmt.setString(paramIndex++, "%" + phoneQuery + "%");
                stmt.setString(paramIndex++, "%" + phoneQuery + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToContact(rs));
            }
        } catch (SQLException e) {
            ConsoleColors.printError("Advanced search error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves basic statistical info for Managers.
     * 
     * @return A string summary.
     */
    public String getStats() {
        StringBuilder stats = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // Count total
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM contacts");
            if (rs.next())
                stats.append("Total Contacts: ").append(rs.getInt(1)).append("\n");

            // Oldest
            rs = stmt.executeQuery("SELECT MIN(birth_date) FROM contacts");
            if (rs.next())
                stats.append("Oldest Contact DOB: ").append(rs.getDate(1)).append("\n");

            // No LinkedIn
            rs = stmt.executeQuery("SELECT COUNT(*) FROM contacts WHERE linkedin_url IS NULL OR linkedin_url = ''");
            if (rs.next())
                stats.append("Contacts without LinkedIn: ").append(rs.getInt(1)).append("\n");

        } catch (SQLException e) {
            return "Could not calculate stats.";
        }
        return stats.toString();
    }

    private boolean isValidColumn(String col) {
        // Basic whitelist to prevent SQL Injection in ORDER BY clauses
        return col.matches("(?i)(contact_id|first_name|last_name|email|phone_primary|birth_date)");
    }

    private Contact mapRowToContact(ResultSet rs) throws SQLException {
        return new Contact(
                rs.getInt("contact_id"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getString("last_name"),
                rs.getString("nickname"),
                rs.getString("phone_primary"),
                rs.getString("phone_secondary"),
                rs.getString("email"),
                rs.getString("linkedin_url"),
                rs.getDate("birth_date").toLocalDate());
    }
}