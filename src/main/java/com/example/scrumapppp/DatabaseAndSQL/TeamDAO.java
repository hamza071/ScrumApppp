package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamDAO {

    // Maak een instantie van DatabaseConnection
    private DatabaseConnection databaseConnection;
    private Connection connection;

    public TeamDAO() {
        this.databaseConnection = new DatabaseConnection();  // Maak een instantie van DatabaseConnection
        this.connection = databaseConnection.getConnection();  // Verkrijg de verbinding
    }

    // Methode om een team te maken
    public Team createTeam(String teamNaam) {
        String query = "INSERT INTO teams (naam) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, teamNaam);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int teamId = generatedKeys.getInt(1);
                        return new Team(teamId, teamNaam);  // Return het aangemaakte team met gegenereerd ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Retourneer null als er iets misgaat
    }
}