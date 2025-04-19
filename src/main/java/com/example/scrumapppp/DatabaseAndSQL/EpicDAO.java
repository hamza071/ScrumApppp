package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EpicDAO {

    public Epic createEpic(int userstoryId, String titel) {
        String sql = "INSERT INTO epic (Userstory_ID, titel) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userstoryId);
            stmt.setString(2, titel);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    return new Epic(keys.getInt(1), userstoryId, titel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Epic> getEpicsByUserstoryId(int userstoryId) {
        List<Epic> epics = new ArrayList<>();
        String sql = "SELECT * FROM epic WHERE Userstory_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userstoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                epics.add(new Epic(
                        rs.getInt("Epic_ID"),
                        rs.getInt("Userstory_ID"),
                        rs.getString("titel")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return epics;
    }

    public void updateEpicTitel(int epicId, String nieuweTitel) {
        String sql = "UPDATE epic SET titel = ? WHERE Epic_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nieuweTitel);
            stmt.setInt(2, epicId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEpic(int epicId) {
        String sql = "DELETE FROM epic WHERE Epic_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, epicId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
