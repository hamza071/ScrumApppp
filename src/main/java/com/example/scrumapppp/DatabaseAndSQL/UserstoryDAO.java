package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserstoryDAO implements CrudRepository<Userstory>{

    //    Interface toegepast
    @Override
    public Userstory create(Userstory entity) {
        return createUserstory(entity.getLijstId(), entity.getTitel(), entity.getBeschrijving());
    }

    @Override
    public void update(Userstory entity) {
        updateUserstoryBeschrijving(entity.getUserstoryId(), entity.getBeschrijving());
    }

    @Override
    public void delete(int id) {
        deleteUserstory(id);
    }

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
                     "INSERT INTO userstory (Lijst_ID, titel, beschrijving) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

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

    // ⭐ Voor slepen/verplaatsen naar een andere lijst
    public void updateUserstoryLijst(int userstoryId, int nieuweLijstId) {
        String sql = "UPDATE userstory SET Lijst_ID = ? WHERE Userstory_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nieuweLijstId);
            stmt.setInt(2, userstoryId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUserstoryBeschrijving(int userstoryId, String nieuweBeschrijving) {
        String sql = "UPDATE userstory SET beschrijving = ? WHERE Userstory_ID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nieuweBeschrijving);
            stmt.setInt(2, userstoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUserstory(int userstoryId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM userstory WHERE Userstory_ID = ?")) {
            stmt.setInt(1, userstoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
