package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DBTest {
    public static void main(String[] args) {
        String databaseName = "scrumapp"; // Of "mydatabase"
        String databaseUser = "root";
        String databasePassword = "mySuperSecretPassword"; // ← zet hier jouw wachtwoord
        String url = "jdbc:mysql://scrumboard.duckdns.org:3307/" + databaseName
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";


        try {
            System.out.println("🔍 JDBC-driver laden...");
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("🌐 Verbinden met database via DuckDNS...");
            Connection connection = DriverManager.getConnection(url, databaseUser, databasePassword);
            System.out.println("✅ Verbonden met de database!");

            // Simpele testquery op de 'gebruiker' tabel
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM gebruiker");

            System.out.println("📦 Inhoud van de tabel 'gebruiker':");
            while (resultSet.next()) {
                int id = resultSet.getInt("Gebruiker_ID");
                String naam = resultSet.getString("naam");
                String email = resultSet.getString("email");
                System.out.println("- ID: " + id + ", Naam: " + naam + ", Email: " + email);
            }

            // Resources sluiten
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("🔒 Verbinding gesloten.");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBC-driver niet gevonden.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ SQL-fout:");
            e.printStackTrace();
        }
    }
}
