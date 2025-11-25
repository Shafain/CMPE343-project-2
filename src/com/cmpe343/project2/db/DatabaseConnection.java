package com.cmpe343.project2.db;

import com.cmpe343.project2.util.ConsoleColors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the JDBC connection to the MySQL database.
 * Implements the Singleton pattern to ensure only one connection instance is
 * used.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/cmpe343_project2?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "myuser";
    private static final String PASSWORD = "1234";

    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
    }

    /**
     * Returns the active database connection.
     * Establishes a new connection if one does not exist or is closed.
     * 
     * @return Connection object.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load driver implicitly in modern JDBC, but good practice for older envs
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    ConsoleColors.printError("MySQL JDBC Driver not found. Add the library to your classpath.");
                    System.exit(1);
                }

                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                // ConsoleColors.printInfo("Database connected successfully."); // Optional
                // logging
            }
        } catch (SQLException e) {
            ConsoleColors.printError("Failed to connect to database: " + e.getMessage());
            ConsoleColors.printWarning("Ensure MySQL is running and the database 'cmpe343_project2' exists.");
            System.exit(1);
        }
        return connection;
    }

    /**
     * Closes the connection explicitly when the application shuts down.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                ConsoleColors.printInfo("Database connection closed.");
            }
        } catch (SQLException e) {
            ConsoleColors.printError("Error closing connection: " + e.getMessage());
        }
    }
}