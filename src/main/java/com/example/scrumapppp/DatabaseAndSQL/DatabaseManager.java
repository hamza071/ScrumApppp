package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/scrumapp";
    private static final String USER = "root";
    private static final String PASSWORD = "Hallo1234";

    private static Connection connection;

    public static Connection getConnection() {
        try {
            // ⛔ Controleer of de verbinding gesloten of null is
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Verbonden met database.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Kan niet verbinden met database: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔒 Databaseverbinding gesloten.");
            } catch (SQLException e) {
                System.out.println("❌ Fout bij sluiten van database: " + e.getMessage());
            }
        }
    }
}
