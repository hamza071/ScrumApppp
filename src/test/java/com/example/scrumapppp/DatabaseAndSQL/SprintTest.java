package com.example.scrumapppp.DatabaseAndSQL;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class SprintTest {

    private SprintDAO dao = new SprintDAO();

//    @Before
//    public void setup() throws SQLException {
//        dao = new SprintDAO();
//
//        try (Connection conn = DatabaseManager.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // Zorg dat de sprint-tabel er is
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS sprint (
//                    Sprint_ID INT AUTO_INCREMENT PRIMARY KEY,
//                    Team_ID INT NOT NULL,
//                    naam VARCHAR(255) NOT NULL
//                );
//            """);
//
//            // Maak de tabel leeg vóór elke test
//            stmt.execute("DELETE FROM sprint;");
//        }
//    }

    @Test
    public void maakEenSprintAan() {
        Sprint sprint = dao.createSprint(1, "Sprint 1");
        assertNotNull(sprint);
        assertEquals("Sprint 1", sprint.getNaam());
        assertEquals(1, sprint.getTeamId());
        assertTrue(sprint.getSprintId() > 0);
    }

    @Test
    public void haalSprintsOpVoorTeam() {
        dao.createSprint(2, "Sprint A");
        dao.createSprint(2, "Sprint B");
        dao.createSprint(3, "Sprint X");

        List<Sprint> result = dao.getSprintsByTeamId(2);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(s -> s.getNaam().equals("Sprint A")));
        assertTrue(result.stream().anyMatch(s -> s.getNaam().equals("Sprint B")));
    }

    @Test
    public void geenSprintsVoorOnbekendTeam() {
        List<Sprint> result = dao.getSprintsByTeamId(99);
        assertTrue(result.isEmpty());
    }
}
