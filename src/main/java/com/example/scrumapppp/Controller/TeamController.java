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

    @FXML
    private TextField teamNaamField;

    @FXML
    private Label errorLabel;

    private final TeamDAO teamDAO = new TeamDAO();
    private int teamId;

    @FXML
    private void handleCreateButtonClick(ActionEvent event) {
        String teamNaam = teamNaamField.getText().trim();

        if (teamNaam.isEmpty()) {
            showError("Je moet een teamnaam invoeren!");
            return;
        }

        Team team = teamDAO.createTeam(teamNaam);
        if (team != null) {
            teamId = team.getId();

            // ✅ Koppel gebruiker aan het nieuwe team
            koppelGebruikerAanTeam(teamId);

            // ✅ Ga door naar het Scrumboard
            openScrumBoard();
        } else {
            showError("Het aanmaken van het team is mislukt.");
        }
    }

    private void koppelGebruikerAanTeam(int teamId) {
        int gebruikerId = UserSession.getID(); // juiste methode

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE gebruiker SET Team_ID = ? WHERE Gebruiker_ID = ?"
            );
            stmt.setInt(1, teamId);
            stmt.setInt(2, gebruikerId);
            stmt.executeUpdate();

            // update sessie
            UserSession.setTeamID(teamId);
            System.out.println("Gebruiker gekoppeld aan team ID: " + teamId);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Kon gebruiker niet koppelen aan het team.");
        }
    }

    private void openScrumBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/ScrumScherm.fxml")); // ✅ Aangepaste locatie
            Scene scene = new Scene(loader.load());

            ScrumController controller = loader.getController();
            controller.setTeamId(teamId);

            Stage stage = new Stage();
            stage.setTitle("Scrum Board");
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

            ((Stage) teamNaamField.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Kon het Scrumboard niet openen.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
}
