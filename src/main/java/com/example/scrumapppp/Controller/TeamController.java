package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.Team;
import com.example.scrumapppp.DatabaseAndSQL.TeamDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;

public class TeamController {

    @FXML
    private TextField teamNaamField;  // Tekstveld voor teamnaam

    @FXML
    private Label errorLabel;  // Foutmelding Label

    private TeamDAO teamDAO = new TeamDAO();  // TeamDAO voor database-interactie

    private int teamId;  // teamId voor de nieuwe team

    // Deze methode wordt aangeroepen wanneer de "Creëren" knop wordt geklikt
    @FXML
    private void handleCreateButtonClick(ActionEvent event) {
        // Verkrijg de naam van het team
        String teamNaam = teamNaamField.getText().trim();

        // Controleer of de teamnaam is ingevuld
        if (teamNaam.isEmpty()) {
            showError("Je moet een teamnaam invoeren!");
            return;  // Stop als de naam niet is ingevuld
        }

        // Maak een nieuw team in de database via de TeamDAO
        Team team = teamDAO.createTeam(teamNaam);
        if (team != null) {
            // Als het team succesvol is aangemaakt, stel de teamId in
            teamId = team.getId();  // teamId wordt ingesteld op de nieuwe teamId

            // Ga naar het Scrumboard scherm
            openScrumBoard();
        } else {
            // Als het aanmaken van het team mislukt, toon een foutmelding
            showError("Het aanmaken van het team is mislukt.");
        }
    }

    // Methode om naar het Scrumboard scherm over te schakelen
    private void openScrumBoard() {
        try {
            // Laad het Scrumboard scherm
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/views/scrumboard.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());

            // Verkrijg de ScrumController en geef de teamId door
            ScrumController controller = loader.getController();
            controller.setTeamId(teamId);  // Geef de teamId door aan de ScrumController

            // Toon het Scrumboard scherm
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Log eventuele fouten
        }
    }

    // Methode om een foutmelding weer te geven onderaan het scherm
    private void showError(String message) {
        errorLabel.setText(message);  // Zet de foutmelding in de label
        errorLabel.setVisible(true);  // Maak de label zichtbaar
    }

    // Setter voor teamId (indien nodig in ScrumController)
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
}