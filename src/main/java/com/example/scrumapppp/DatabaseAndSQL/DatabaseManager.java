package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // ✨ DuckDNS adres en juiste poort + juiste opties
    private static final String URL = "jdbc:mysql://scrumboard.duckdns.org:3307/scrumapp?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "mySuperSecretPassword";

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Verbonden met database via DuckDNS!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Kan niet verbinden met database: " + e.getMessage());
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }
}
