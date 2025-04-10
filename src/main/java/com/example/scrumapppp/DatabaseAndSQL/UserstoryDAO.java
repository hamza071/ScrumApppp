package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserstoryDAO {

    public List<Userstory> getUserstoriesByLijstId(int lijstId) {
        List<Userstory> userstories = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM userstory WHERE Lijst_ID = ?")) {

            stmt.setInt(1, lijstId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Userstory userstory = new Userstory(
                        rs.getInt("Userstory_ID"),
                        rs.getInt("Lijst_ID"),
                        rs.getString("titel"),
                        rs.getString("beschrijving")
                );
                userstories.add(userstory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userstories;
    }

    public Userstory createUserstory(int lijstId, String titel, String beschrijving) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO userstory (Lijst_ID, titel, beschrijving) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, lijstId);
            stmt.setString(2, titel);
            stmt.setString(3, beschrijving);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return new Userstory(id, lijstId, titel, beschrijving);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
