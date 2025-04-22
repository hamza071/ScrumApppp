package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.*;
import com.example.scrumapppp.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TeamController {

    // FXML-velden voor de gebruikersinterface
    @FXML
    private TextField teamNaamField;

    @FXML
    private Label errorLabel;

    // De DAO voor het werken met teams in de database
    private final TeamDAO teamDAO = new TeamDAO();

    // Het team ID van het aangemaakte team
    private int teamId;

    // Methode die wordt uitgevoerd wanneer de gebruiker probeert een team aan te maken
    @FXML
    private void handleCreateButtonClick(ActionEvent event) {
        // Verkrijg de teamnaam van de tekstveld
        String teamNaam = teamNaamField.getText().trim();

        // Controleer of de teamnaam leeg is
        if (teamNaam.isEmpty()) {
            showError("Je moet een teamnaam invoeren!");
            return;
        }

        // Probeer een nieuw team aan te maken via de DAO
        Team team = teamDAO.createTeam(teamNaam);
        if (team != null) {
            // Als het team succesvol is aangemaakt, haal het team ID op
            teamId = team.getId();

            // Koppel de gebruiker aan het nieuwe team
            koppelGebruikerAanTeam(teamId);

            // Ga door naar het Scrum Board
            openScrumBoard();
        } else {
            // Toon een foutmelding als het aanmaken van het team is mislukt
            showError("Het aanmaken van het team is mislukt.");
        }
    }

    // Methode om de gebruiker aan het nieuwe team te koppelen in de database
    private void koppelGebruikerAanTeam(int teamId) {
        int gebruikerId = UserSession.getID(); // Haal het gebruiker ID uit de sessie

        try (Connection conn = DatabaseManager.getConnection()) {
            // SQL query om het team ID voor de gebruiker bij te werken
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE gebruiker SET Team_ID = ? WHERE Gebruiker_ID = ?"
            );
            stmt.setInt(1, teamId);
            stmt.setInt(2, gebruikerId);
            stmt.executeUpdate();

            // Werk de sessie bij met het nieuwe team ID
            UserSession.setTeamID(teamId);
            System.out.println("Gebruiker gekoppeld aan team ID: " + teamId);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Kon gebruiker niet koppelen aan het team.");
        }
    }

    // Methode om het Scrum Board te openen na succesvolle team creatie en koppeling
    private void openScrumBoard() {
        try {
            // Laad het Scrum Board scherm
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/ScrumScherm.fxml"));
            Scene scene = new Scene(loader.load());

            // Verkrijg de controller van het Scrum Board scherm en geef het team ID door
            ScrumController controller = loader.getController();
            controller.setTeamId(teamId);

            // Maak een nieuw venster voor het Scrum Board
            Stage stage = new Stage();
            stage.setTitle("Scrum Board");
            stage.setScene(scene);
            stage.setFullScreen(true);  // Zet het venster fullscreen
            stage.show();

            // Sluit het huidige venster (Team scherm)
            ((Stage) teamNaamField.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Kon het Scrumboard niet openen.");
        }
    }

    // Methode om een foutmelding weer te geven in de interface
    private void showError(String message) {
        errorLabel.setText(message);  // Zet de foutmelding
        errorLabel.setVisible(true);  // Maak de foutmelding zichtbaar
    }

    // Methode om het team ID in te stellen (voor gebruik bij navigeren)
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
}