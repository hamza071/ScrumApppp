package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseManager;
import com.example.scrumapppp.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamSelectController {

    // FXML-velden voor de gebruikersinterface
    @FXML
    private TextField teamIdField;

    @FXML
    private Button joinTeamButton;

    @FXML
    private Button createTeamButton;

    // Methode die wordt uitgevoerd wanneer de gebruiker probeert zich bij een team aan te sluiten
    @FXML
    private void handleJoinButtonClick(ActionEvent event) {
        // Verkrijg het team ID van de tekstveld
        String input = teamIdField.getText().trim();

        // Controleer of het invoerveld leeg is
        if (input.isEmpty()) {
            toonAlert("Fout", "Voer een team ID in.");
            return;
        }

        // Probeer het team ID om te zetten naar een integer
        int teamId;
        try {
            teamId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            toonAlert("Fout", "Team ID moet een getal zijn.");
            return;
        }

        // Verbind met de database om te controleren of het team ID bestaat
        try (Connection conn = DatabaseManager.getConnection()) {
            // Query om te controleren of het team met de gegeven ID bestaat
            PreparedStatement checkTeam = conn.prepareStatement("SELECT naam FROM team WHERE Team_ID = ?");
            checkTeam.setInt(1, teamId);
            ResultSet rs = checkTeam.executeQuery();

            // Als het team bestaat, koppel de gebruiker aan dit team
            if (rs.next()) {
                PreparedStatement updateUser = conn.prepareStatement(
                        "UPDATE gebruiker SET Team_ID = ? WHERE Gebruiker_ID = ?"
                );
                updateUser.setInt(1, teamId);  // Zet het team ID voor de gebruiker
                updateUser.setInt(2, UserSession.getID());  // Gebruik de ID van de ingelogde gebruiker
                updateUser.executeUpdate();

                // Update de sessie van de gebruiker met het nieuwe team ID
                UserSession.setTeamID(teamId);

                // Open het Scrum Board voor dit team
                openScrumBoard(teamId);
            } else {
                // Als het team niet bestaat, toon een foutmelding
                toonAlert("Team niet gevonden", "Geen team met ID: " + teamId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            toonAlert("Databasefout", "Er ging iets mis met de database.");
        }
    }

    // Methode die wordt uitgevoerd wanneer de gebruiker een nieuw team wil aanmaken
    @FXML
    private void handleCreateButtonClick(ActionEvent event) {
        try {
            // Laad het scherm voor het aanmaken van een team
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/TeamScherm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Maak een nieuw venster voor het team scherm
            Stage stage = new Stage();
            stage.setTitle("Team Scherm");
            stage.setScene(scene);
            stage.setFullScreen(true);  // Zet het venster fullscreen
            stage.show();

            // Sluit het huidige venster (TeamSelect)
            Stage huidigeStage = (Stage) createTeamButton.getScene().getWindow();
            huidigeStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            toonAlert("Fout", "Kon het teamcreatiescherm niet openen.");
        }
    }

    // Methode om het Scrum Board scherm te openen na succesvolle teamselectie
    private void openScrumBoard(int teamId) {
        try {
            // Laad het Scrum Board scherm
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/ScrumScherm.fxml"));
            Parent root = loader.load();

            // Verkrijg de controller van het Scrum Board scherm en geef het team ID door
            ScrumController controller = loader.getController();
            controller.setTeamId(teamId);

            // Maak een nieuw venster voor het Scrum Board
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Scrum Board");
            stage.setScene(scene);
            stage.setFullScreen(true);  // Zet het venster fullscreen
            stage.show();

            // Sluit het huidige venster (TeamSelect)
            Stage huidigeStage = (Stage) teamIdField.getScene().getWindow();
            huidigeStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            toonAlert("Fout", "Kan Scrum Board niet openen.");
        }
    }

    // Methode om een waarschuwingsalert te tonen
    private void toonAlert(String titel, String bericht) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titel);
        alert.setHeaderText(null);
        alert.setContentText(bericht);
        alert.showAndWait();
    }
}