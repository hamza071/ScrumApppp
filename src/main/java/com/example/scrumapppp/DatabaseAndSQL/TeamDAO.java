package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamDAO {

    // ✅ Methode om een team aan te maken via DatabaseManager
    public Team createTeam(String teamNaam) {
        String query = "INSERT INTO team (naam) VALUES (?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, teamNaam);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int teamId = generatedKeys.getInt(1);
                        return new Team(teamId, teamNaam); // ✅ Nieuw team-object met ID en naam
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Fout bij aanmaken team: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // ❌ Als het niet gelukt is
    }
}
