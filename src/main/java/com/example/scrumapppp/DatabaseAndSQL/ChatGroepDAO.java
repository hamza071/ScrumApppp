package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatGroepDAO {

    /**
     * Voegt een nieuwe chatgroep toe aan de database.
     * @param teamId Het team waartoe de groep behoort.
     * @param naam De naam van de groep.
     */
    public void voegGroepToe(int teamId, String naam) {
        String sql = "INSERT INTO chat_groepen (team_id, naam) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teamId);
            stmt.setString(2, naam);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Fout bij toevoegen van chatgroep: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Haalt alle groepsnamen op voor een specifiek team.
     * @param teamId Het team-ID.
     * @return Lijst met groepsnamen.
     */
    public List<String> getGroepsnamenVoorTeam(int teamId) {
        List<String> groepen = new ArrayList<>();
        String sql = "SELECT naam FROM chat_groepen WHERE team_id = ? ORDER BY naam ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                groepen.add(rs.getString("naam"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Fout bij ophalen van chatgroepen: " + e.getMessage());
            e.printStackTrace();
        }

        return groepen;
    }
}
