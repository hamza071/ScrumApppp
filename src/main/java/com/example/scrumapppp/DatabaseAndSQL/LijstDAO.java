package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LijstDAO {

    public List<Lijst> getLijstenByTeamId(int teamId) {
        List<Lijst> lijsten = new ArrayList<>();
        String sql = "SELECT * FROM lijst WHERE Team_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Lijst lijst = new Lijst(
                        rs.getInt("Lijst_ID"),
                        rs.getInt("Team_ID"),
                        rs.getString("naam")
                );
                lijsten.add(lijst);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lijsten;
    }

    public Lijst createLijst(int teamId, String naam) {
        String sql = "INSERT INTO lijst (Team_ID, naam) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, teamId);
            stmt.setString(2, naam);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int lijstId = generatedKeys.getInt(1);
                    return new Lijst(lijstId, teamId, naam);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
