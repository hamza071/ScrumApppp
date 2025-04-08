package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DATABASE_NAME = "scrumapp";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "sybau"; // Zorg ervoor dat dit correct is
    private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?useSSL=false&serverTimezone=UTC";

    private Connection databaseLink;

    // Maak de verbinding en geef deze terug
    public Connection getConnection() {
        try {
            // Laad de MySQL JDBC-driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Probeer verbinding te maken
            databaseLink = DriverManager.getConnection(URL, DATABASE_USER, DATABASE_PASSWORD);
            System.out.println("Connection established successfully.");
        } catch (ClassNotFoundException e) {
            // Driver niet gevonden
            System.err.println("MySQL JDBC Driver not found. Add the driver to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            // Fout bij het verbinden met de database
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
        }
        return databaseLink; // Retourneer de verbinding (kan null zijn als er een fout is)
    }

    // Voeg een methode toe om de verbinding netjes af te sluiten
    public void closeConnection() {
        if (databaseLink != null) {
            try {
                databaseLink.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close the connection.");
                e.printStackTrace();
            }
        }
    }
}