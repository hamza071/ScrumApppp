package com.example.scrumapppp.DatabaseAndSQL;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class TaakTest {

    private TaakDAO dao = new TaakDAO();

//    @Before
//    public void setup() throws SQLException {
//        dao = new TaakDAO();
//
//        try (Connection conn = DatabaseManager.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // Maak de taak-tabel als die nog niet bestaat
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS taak (
//                    Taak_ID INT AUTO_INCREMENT PRIMARY KEY,
//                    Userstory_ID INT NOT NULL,
//                    titel VARCHAR(255) NOT NULL,
//                    is_done BOOLEAN DEFAULT FALSE
//                );
//            """);
//
//            // Leeg de tabel voor een schone testomgeving
//            stmt.execute("DELETE FROM taak;");
//        }
//    }

    @Test
    public void testCreateTaak() {
        Taak taak = dao.createTaak(1, "Frontend login bouwen");
        assertNotNull(taak);
        assertEquals(1, taak.getUserstoryId());
        assertEquals("Frontend login bouwen", taak.getTitel());
        assertFalse(taak.isDone());
        assertTrue(taak.getTaakId() > 0);
    }


    @Test
    public void testUpdateTaakStatus() {
        Taak taak = dao.createTaak(4, "Unit test schrijven");
        assertFalse(taak.isDone());

        dao.updateTaakStatus(taak.getTaakId(), true);

        List<Taak> taken = dao.getTakenByUserstoryId(4);
        assertEquals(1, taken.size());
        assertTrue(taken.get(0).isDone());
    }

    @Test
    public void testDeleteTaak() {
        Taak taak = dao.createTaak(5, "Opkuisen logging");
        assertNotNull(taak);

        dao.deleteTaak(taak.getTaakId());

        List<Taak> taken = dao.getTakenByUserstoryId(5);
        assertTrue(taken.isEmpty());
    }

    @Test
    public void testGeenTakenVoorOnbekendeUserstory() {
        List<Taak> taken = dao.getTakenByUserstoryId(999);
        assertTrue(taken.isEmpty());
    }
}
