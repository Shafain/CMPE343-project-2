package com.cmpe343.project2.db;

import com.cmpe343.project2.util.ConsoleColors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the lifecycle of the JDBC connection to the MySQL database using a
 * lightweight singleton. The class is intentionally minimal so that every call
 * to {@link #getConnection()} follows the same repeatable sequence:
 * <ol>
 * <li>Check whether a connection already exists or has been closed.</li>
 * <li>Load the MySQL driver class to guarantee driver discovery on legacy
 *     runtimes.</li>
 * <li>Open a new connection with the configured credentials.</li>
 * <li>Return the cached connection for reuse throughout the application.</li>
 * </ol>
 * The explicit {@link #closeConnection()} method mirrors that lifecycle by
 * walking through validation and cleanup steps so the console application can
 * release resources gracefully when exiting.
 *
 * @author Raul Ibrahimov
 * @author Akhmed Nazarov
 * @author Omirbek Ubaidayev
 * @author Kuandyk Kyrykbayev
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
     * Retrieves a ready-to-use JDBC connection. The method intentionally walks
     * through the following steps to avoid surprises:
     * <ul>
     * <li>Verify whether a cached connection exists and is still open.</li>
     * <li>Load the MySQL driver class to support environments that require
     *     explicit driver registration.</li>
     * <li>Create a new connection using the configured URL, username and
     *     password when none is available.</li>
     * <li>Return the shared connection instance to all callers.</li>
     * </ul>
     * Any unrecoverable error prints a descriptive message and halts the
     * application because database access is critical to the program flow.
     *
     * @return Connection object guaranteed to be open when returned.
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
     * Closes the shared database connection during application shutdown. The
     * method checks that a connection exists, confirms it is still open, closes
     * it, and logs the outcome. Errors are reported to the console but do not
     * rethrow to avoid masking shutdown routines.
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