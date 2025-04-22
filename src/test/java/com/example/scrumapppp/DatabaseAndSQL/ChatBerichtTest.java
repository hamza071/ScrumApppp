package com.example.scrumapppp.DatabaseAndSQL;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ChatBerichtTest {
    ChatBericht bericht = new ChatBericht("Team4", "Sandile", "Bombardilo Crocodilo!", LocalDateTime.of(2024, 4, 22, 12, 30));
    ChatDAO dao = new ChatDAO();

//    @BeforeEach
//    void setup() throws SQLException {
//        dao = new ChatDAO();
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
    public void testVoegBerichtToeEnOphalen() {

        //Voegt een bericht toe binnen de database
        dao.voegBerichtToe(bericht);

        //Berichten moet in Team2 gaan
        List<ChatBericht> berichten = dao.getBerichtenVoorGroep("Team4");
        assertEquals(1, berichten.size());

        //Checkt of de gegevens van de gemaakte object overeenkomt hieronder
        ChatBericht uitDB = berichten.get(0);
//        for(int i = 0; i < berichten.size(); i++){
//            System.out.println(berichten.get(i));
//        }

        assertEquals("Team4", uitDB.getGroep());
        assertEquals("Sandile", uitDB.getGebruiker());
        assertEquals("Bombardilo Crocodilo!", uitDB.getBericht());
        assertEquals(LocalDateTime.of(2024, 4, 22, 12, 30), uitDB.getTijd());
    }

    @Test
    public void testGeenBerichtenVoorOnbekendeGroep() {
        ChatDAO dao = new ChatDAO();

        List<ChatBericht> berichten = dao.getBerichtenVoorGroep("Onbekend");
        assertTrue(berichten.isEmpty());
    }
}