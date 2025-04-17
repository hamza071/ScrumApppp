package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LijstDAO {

    public List<Lijst> getLijstenBySprintId(int sprintId) {
        List<Lijst> lijsten = new ArrayList<>();
        String sql = "SELECT * FROM lijst WHERE Sprint_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sprintId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Lijst lijst = new Lijst(
                        rs.getInt("Lijst_ID"),
                        rs.getInt("Team_ID"),
                        rs.getString("naam"),
                        rs.getInt("Sprint_ID")
                );
                lijsten.add(lijst);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lijsten;
    }

    public Lijst createLijst(int teamId, int sprintId, String naam) {
        String sql = "INSERT INTO lijst (Team_ID, naam, Sprint_ID) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, teamId);
            stmt.setString(2, naam);
            stmt.setInt(3, sprintId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int lijstId = rs.getInt(1);
                    return new Lijst(lijstId, teamId, naam, sprintId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
