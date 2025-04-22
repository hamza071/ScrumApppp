package com.example.scrumapppp.DatabaseAndSQL;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class TeamTest {

    private TeamDAO dao = new TeamDAO();

//    @Before
//    public void setup() throws SQLException {
//        dao = new TeamDAO();
//
//        try (Connection conn = DatabaseManager.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // Maak de team-tabel als die nog niet bestaat
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS team (
//                    id INT AUTO_INCREMENT PRIMARY KEY,
//                    naam VARCHAR(255) NOT NULL
//                );
//            """);
//
//            // Leeg de tabel voor een schone testomgeving
//            stmt.execute("DELETE FROM team;");
//        }
//    }

    @Test
    public void testCreateTeam() {
        Team team = dao.createTeam("Scrum Masters");
        assertNotNull("Team mag niet null zijn", team);
        assertTrue("Team ID moet groter dan 0 zijn", team.getId() > 0);
        assertEquals("Scrum Masters", team.getNaam());
    }

    @Test
    public void testCreateTeamWithEmptyName() {
        Team team = dao.createTeam("");
        assertNotNull("Lege naam zou technisch nog een team kunnen creëren", team);
        assertEquals("", team.getNaam());
    }
}
