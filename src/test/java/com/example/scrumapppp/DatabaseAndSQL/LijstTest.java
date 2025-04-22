package com.example.scrumapppp.DatabaseAndSQL;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class LijstTest {
    LijstDAO dao = new LijstDAO();

//    @Before
//    public void setup() throws SQLException {
//        dao = new LijstDAO();
//
//        try (Connection conn = DatabaseManager.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // Maak de lijst-tabel aan
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS lijst (
//                    Lijst_ID INT AUTO_INCREMENT PRIMARY KEY,
//                    Team_ID INT NOT NULL,
//                    naam VARCHAR(255) NOT NULL,
//                    Sprint_ID INT NOT NULL
//                );
//            """);
//
//            // Maak leeg voor elke test
//            stmt.execute("DELETE FROM lijst;");
//        }
//    }

    @Test
    public void maakEenLijstAan() {
        Lijst nieuweLijst = dao.createLijst(1, 100, "To Do");

        assertEquals("To Do", nieuweLijst.getNaam());
        assertEquals(1, nieuweLijst.getTeamId());
        assertEquals(100, nieuweLijst.getSprintId());
    }

    @Test
    public void haalLijstenOpVoorSprint() {
        dao.createLijst(2, 200, "To Do");
        dao.createLijst(2, 200, "In Progress");
        dao.createLijst(2, 201, "Done"); // andere sprint

        List<Lijst> lijsten = dao.getLijstenBySprintId(200);

        assertEquals(2, lijsten.size());
        assertTrue(lijsten.stream().anyMatch(l -> l.getNaam().equals("To Do")));
        assertTrue(lijsten.stream().anyMatch(l -> l.getNaam().equals("In Progress")));
    }

    @Test
    public void geenLijstenVoorNietBestaandeSprint() {
        List<Lijst> lijsten = dao.getLijstenBySprintId(999);
        assertTrue(lijsten.isEmpty());
    }
}
