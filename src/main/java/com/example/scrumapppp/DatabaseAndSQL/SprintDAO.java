package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SprintDAO {

    public List<Sprint> getSprintsByTeamId(int teamId) {
        List<Sprint> sprints = new ArrayList<>();

        String query = "SELECT * FROM sprint WHERE Team_ID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Sprint sprint = new Sprint(
                        rs.getInt("Sprint_ID"),
                        rs.getInt("Team_ID"),
                        rs.getString("naam")
                );
                sprints.add(sprint);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sprints;
    }

    public Sprint createSprint(int teamId, String naam) {
        String query = "INSERT INTO sprint (Team_ID, naam) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, teamId);
            stmt.setString(2, naam);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return new Sprint(rs.getInt(1), teamId, naam);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
