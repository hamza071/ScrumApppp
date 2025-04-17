package com.example.scrumapppp.DatabaseAndSQL;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {

    public void voegBerichtToe(ChatBericht bericht) {
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO chat_berichten (groep_naam, gebruiker, bericht, tijd)
                VALUES (?, ?, ?, ?)
            """);
            stmt.setString(1, bericht.getGroep());
            stmt.setString(2, bericht.getGebruiker());
            stmt.setString(3, bericht.getBericht());
            stmt.setTimestamp(4, Timestamp.valueOf(bericht.getTijd()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ChatBericht> getBerichtenVoorGroep(String groep) {
        List<ChatBericht> berichten = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT * FROM chat_berichten WHERE groep_naam = ? ORDER BY tijd ASC
            """);
            stmt.setString(1, groep);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                berichten.add(new ChatBericht(
                        rs.getString("groep_naam"),
                        rs.getString("gebruiker"),
                        rs.getString("bericht"),
                        rs.getTimestamp("tijd").toLocalDateTime()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return berichten;
    }
}
