package com.example.scrumapppp.DatabaseAndSQL;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class UserstoryTest {

    private UserstoryDAO dao = new UserstoryDAO();

//    @Before
//    public void setup() throws SQLException {
//        dao = new UserstoryDAO();
//
//        try (Connection conn = DatabaseManager.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS userstory (
//                    Userstory_ID INT AUTO_INCREMENT PRIMARY KEY,
//                    Lijst_ID INT NOT NULL,
//                    titel VARCHAR(255) NOT NULL,
//                    beschrijving TEXT
//                );
//            """);
//
//            stmt.execute("DELETE FROM userstory;");
//        }
//    }

    @Test
    public void testCreateUserstory() {
        Userstory story = dao.createUserstory(1, "Login functionaliteit", "Gebruiker kan inloggen via e-mail");
        assertNotNull(story);
        assertTrue(story.getUserstoryId() > 0);
        assertEquals("Login functionaliteit", story.getTitel());
        assertEquals("Gebruiker kan inloggen via e-mail", story.getBeschrijving());
        assertEquals(1, story.getLijstId());
    }

    @Test
    public void testUpdateBeschrijving() {
        Userstory story = dao.createUserstory(1, "Chat systeem", "Gebruiker kan chatten");
        String test = "Gebruiker kan realtime chatten met anderen";
        dao.updateUserstoryBeschrijving(1, test);

        List<Userstory> result = dao.getUserstoriesByLijstId(1);
        Userstory uitDB = result.get(0);
        assertEquals("Gebruiker kan realtime chatten met anderen", uitDB.getBeschrijving());
    }

    @Test
    public void testDeleteUserstory() {
        Userstory story = dao.createUserstory(1, "Verwijder mij", "Ik ga weg");
        dao.deleteUserstory(story.getUserstoryId());

        List<Userstory> result = dao.getUserstoriesByLijstId(1);
        assertTrue(result.isEmpty());
    }
}
