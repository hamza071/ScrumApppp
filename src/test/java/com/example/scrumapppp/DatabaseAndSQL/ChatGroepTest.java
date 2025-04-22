package com.example.scrumapppp.DatabaseAndSQL;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;


public class ChatGroepTest {
    ChatGroepDAO dao = new ChatGroepDAO();

//    @BeforeEach
//    void setup() throws SQLException {
//        dao = new ChatGroepDAO();
//        try (Connection conn = DatabaseManager.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // Maak testtabel aan
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS chat_berichten (
//                    groep_naam VARCHAR(255),
//                    gebruiker VARCHAR(255),
//                    bericht TEXT,
//                    tijd TIMESTAMP
//                );
//            """);
//
//            // Leeg maken vóór elke test
//            stmt.execute("DELETE FROM chat_berichten;");
//        }
//    }

    @Test
    public void voegEenGroepToe_enControleerOfDezeBestaat() {
        int teamId = 4;
        String groepNaam = "Team Rocket";

        dao.voegGroepToe(teamId, groepNaam);

        List<String> groepen = dao.getGroepsnamenVoorTeam(teamId);

        assertEquals(1, groepen.size());
        assertEquals("Team Rocket", groepen.get(0));
    }

    @Test
    public void legeGroepVoorNietBestaandTeam() {
        List<String> groepen = dao.getGroepsnamenVoorTeam(999);
        assertTrue(groepen.isEmpty());
    }
}