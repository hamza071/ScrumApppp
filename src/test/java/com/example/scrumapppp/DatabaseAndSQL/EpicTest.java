package com.example.scrumapppp.DatabaseAndSQL;

import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;


public class EpicTest {

    private Epic epicNew = new Epic(1, 1, "Test");
    private EpicDAO dao = new EpicDAO();

//    @Before
//    public void setup() throws SQLException {
//        dao = new EpicDAO();
//
//        try (Connection conn = DatabaseManager.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // Maak de epic-tabel aan
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS epic (
//                    Epic_ID INT AUTO_INCREMENT PRIMARY KEY,
//                    Userstory_ID INT NOT NULL,
//                    titel VARCHAR(255) NOT NULL
//                );
//            """);
//
//            // Maak leeg voor elke test
//            stmt.execute("DELETE FROM epic;");
//        }
//    }

    @Test
    public void maakEenEpicAan() {
        Epic epic = dao.createEpic(10, "Login scherm bouwen");
        assertNotNull(epic);
        assertEquals("Login scherm bouwen", epic.getTitel());
        assertEquals(10, epic.getUserstoryId());
        assertTrue(epic.getEpicId() > 0);
    }

    @Test
    public void haalEpicsOpVoorUserstory() {
        dao.createEpic(5, "Database setup");
        dao.createEpic(5, "User login");
        dao.createEpic(6, "UI Design");

        List<Epic> epics = dao.getEpicsByUserstoryId(5);

        assertEquals(2, epics.size());
        assertTrue(epics.stream().anyMatch(e -> e.getTitel().equals("Database setup")));
        assertTrue(epics.stream().anyMatch(e -> e.getTitel().equals("User login")));
    }

    @Test
    public void updateEpicTitel() {
        Epic epic = dao.createEpic(9, "Oude titel");
        dao.updateEpicTitel(epic.getEpicId(), "Nieuwe titel");

        List<Epic> epics = dao.getEpicsByUserstoryId(9);
        assertEquals(1, epics.size());
        assertEquals("Nieuwe titel", epics.get(0).getTitel());
    }

    @Test
    public void verwijderEpic() {
        Epic epic = dao.createEpic(8, "Weg te gooien epic");
        dao.deleteEpic(epic.getEpicId());

        List<Epic> over = dao.getEpicsByUserstoryId(8);
        assertTrue(over.isEmpty());
    }
}