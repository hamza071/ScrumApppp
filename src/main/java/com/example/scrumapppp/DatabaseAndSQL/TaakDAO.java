package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaakDAO implements CrudRepository<Taak>{

    public List<Taak> getTakenByUserstoryId(int userstoryId) {
        List<Taak> taken = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM taak WHERE Userstory_ID = ?")) {
            stmt.setInt(1, userstoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Taak taak = new Taak(
                        rs.getInt("Taak_ID"),
                        rs.getInt("Userstory_ID"),
                        rs.getString("titel"),
                        rs.getBoolean("is_done")
                );
                taken.add(taak);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return taken;
    }

    @Override
    public Taak create(Taak entity){
        return createTaak(entity.getUserstoryId(), entity.getTitel());
    }
    @Override
    public void update(Taak entity){
        updateTaakStatus(entity.getTaakId(), entity.isDone());
    }

    @Override
    public void delete(int id){
        deleteTaak(id);
    }

    public Taak createTaak(int userstoryId, String titel) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO taak (Userstory_ID, titel, is_done) VALUES (?, ?, 0)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userstoryId);
            stmt.setString(2, titel);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int taakId = generatedKeys.getInt(1);
                    return new Taak(taakId, userstoryId, titel, false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateTaakStatus(int taakId, boolean isDone) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE taak SET is_done = ? WHERE Taak_ID = ?")) {
            stmt.setBoolean(1, isDone);
            stmt.setInt(2, taakId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTaak(int taakId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM taak WHERE Taak_ID = ?")) {
            stmt.setInt(1, taakId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
