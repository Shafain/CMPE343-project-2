package com.cmpe343.project2.dao;

import com.cmpe343.project2.db.DatabaseConnection;
import com.cmpe343.project2.model.Contact;
import com.cmpe343.project2.model.SearchCriteria;
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
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getFirstName());
            stmt.setString(2, emptyToNull(c.getMiddleName()));
            stmt.setString(3, c.getLastName());
            stmt.setString(4, emptyToNull(c.getNickname()));
            stmt.setString(5, c.getPhonePrimary());
            stmt.setString(6, emptyToNull(c.getPhoneSecondary()));
            stmt.setString(7, c.getEmail());
            stmt.setString(8, emptyToNull(c.getLinkedinUrl()));
            if (c.getBirthDate() == null) {
                stmt.setNull(9, Types.DATE);
            } else {
                stmt.setDate(9, java.sql.Date.valueOf(c.getBirthDate()));
            }

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
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getFirstName());
            stmt.setString(2, emptyToNull(c.getMiddleName()));
            stmt.setString(3, c.getLastName());
            stmt.setString(4, emptyToNull(c.getNickname()));
            stmt.setString(5, c.getPhonePrimary());
            stmt.setString(6, emptyToNull(c.getPhoneSecondary()));
            stmt.setString(7, c.getEmail());
            stmt.setString(8, emptyToNull(c.getLinkedinUrl()));
            if (c.getBirthDate() == null) {
                stmt.setNull(9, Types.DATE);
            } else {
                stmt.setDate(9, java.sql.Date.valueOf(c.getBirthDate()));
            }
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
     * Re-inserts a previously deleted contact keeping its original ID and timestamps.
     */
    public boolean restoreContact(Contact c) {
        String sql = "INSERT INTO contacts (contact_id, first_name, middle_name, last_name, nickname, phone_primary,"
                + " phone_secondary, email, linkedin_url, birth_date, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, c.getContactId());
            stmt.setString(2, c.getFirstName());
            stmt.setString(3, emptyToNull(c.getMiddleName()));
            stmt.setString(4, c.getLastName());
            stmt.setString(5, emptyToNull(c.getNickname()));
            stmt.setString(6, c.getPhonePrimary());
            stmt.setString(7, emptyToNull(c.getPhoneSecondary()));
            stmt.setString(8, c.getEmail());
            stmt.setString(9, emptyToNull(c.getLinkedinUrl()));

            if (c.getBirthDate() == null) {
                stmt.setNull(10, Types.DATE);
            } else {
                stmt.setDate(10, java.sql.Date.valueOf(c.getBirthDate()));
            }

            if (c.getCreatedAt() == null) {
                stmt.setNull(11, Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(11, Timestamp.valueOf(c.getCreatedAt()));
            }

            if (c.getUpdatedAt() == null) {
                stmt.setNull(12, Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(12, Timestamp.valueOf(c.getUpdatedAt()));
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            ConsoleColors.printError("Error restoring contact: " + e.getMessage());
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
    public List<Contact> searchByCriteria(SearchCriteria criteria) {
        List<Contact> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM contacts WHERE 1=1");

        if (criteria.getFirstName() != null && !criteria.getFirstName().isBlank()) {
            sql.append(" AND first_name LIKE ?");
        }
        if (criteria.getLastName() != null && !criteria.getLastName().isBlank()) {
            sql.append(" AND last_name LIKE ?");
        }
        if (criteria.getPhone() != null && !criteria.getPhone().isBlank()) {
            sql.append(" AND (phone_primary LIKE ? OR phone_secondary LIKE ?)");
        }
        if (criteria.getEmail() != null && !criteria.getEmail().isBlank()) {
            sql.append(" AND email LIKE ?");
        }
        if (criteria.getBirthMonth() != null) {
            sql.append(" AND MONTH(birth_date) = ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (criteria.getFirstName() != null && !criteria.getFirstName().isBlank()) {
                stmt.setString(idx++, "%" + criteria.getFirstName() + "%");
            }
            if (criteria.getLastName() != null && !criteria.getLastName().isBlank()) {
                stmt.setString(idx++, "%" + criteria.getLastName() + "%");
            }
            if (criteria.getPhone() != null && !criteria.getPhone().isBlank()) {
                stmt.setString(idx++, "%" + criteria.getPhone() + "%");
                stmt.setString(idx++, "%" + criteria.getPhone() + "%");
            }
            if (criteria.getEmail() != null && !criteria.getEmail().isBlank()) {
                stmt.setString(idx++, "%" + criteria.getEmail() + "%");
            }
            if (criteria.getBirthMonth() != null) {
                stmt.setInt(idx++, criteria.getBirthMonth());
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

            rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM contacts WHERE linkedin_url IS NOT NULL AND linkedin_url <> ''");
            if (rs.next())
                stats.append("With LinkedIn URLs: ").append(rs.getInt(1)).append("\n");

            rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM contacts WHERE linkedin_url IS NULL OR linkedin_url = ''");
            if (rs.next())
                stats.append("Without LinkedIn URLs: ").append(rs.getInt(1)).append("\n");

            rs = stmt.executeQuery(
                    "SELECT first_name, COUNT(*) as cnt FROM contacts GROUP BY first_name HAVING cnt > 1 ORDER BY cnt DESC LIMIT 3");
            stats.append("Most common first names: ");
            while (rs.next()) {
                stats.append(rs.getString("first_name")).append(" (" + rs.getInt("cnt") + "), ");
            }
            stats.append("\n");

            rs = stmt.executeQuery(
                    "SELECT last_name, COUNT(*) as cnt FROM contacts GROUP BY last_name HAVING cnt > 1 ORDER BY cnt DESC LIMIT 3");
            stats.append("Most common last names: ");
            while (rs.next()) {
                stats.append(rs.getString("last_name")).append(" (" + rs.getInt("cnt") + "), ");
            }
            stats.append("\n");

            // Oldest
            rs = stmt.executeQuery("SELECT MIN(birth_date) FROM contacts");
            if (rs.next())
                stats.append("Oldest Contact DOB: ").append(rs.getDate(1)).append("\n");
            rs = stmt.executeQuery("SELECT MAX(birth_date) FROM contacts");
            if (rs.next())
                stats.append("Youngest Contact DOB: ").append(rs.getDate(1)).append("\n");

            rs = stmt.executeQuery(
                    "SELECT AVG(TIMESTAMPDIFF(YEAR, birth_date, CURDATE())) FROM contacts WHERE birth_date IS NOT NULL");
            if (rs.next())
                stats.append("Average Age: ").append(String.format("%.1f", rs.getDouble(1))).append(" years\n");

        } catch (SQLException e) {
            return "Could not calculate stats.";
        }
        return stats.toString();
    }

    private boolean isValidColumn(String col) {
        // Basic whitelist to prevent SQL Injection in ORDER BY clauses
        return col.matches("(?i)(contact_id|first_name|middle_name|last_name|nickname|email|phone_primary|phone_secondary|birth_date|linkedin_url|created_at|updated_at)");
    }

    private Contact mapRowToContact(ResultSet rs) throws SQLException {
        Date birth = rs.getDate("birth_date");
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
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
                birth == null ? null : birth.toLocalDate(),
                created == null ? null : created.toLocalDateTime(),
                updated == null ? null : updated.toLocalDateTime());
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}