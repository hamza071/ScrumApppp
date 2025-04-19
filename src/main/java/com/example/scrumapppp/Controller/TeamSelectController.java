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

    @FXML
    private TextField teamIdField;

    @FXML
    private Button joinTeamButton;

    @FXML
    private Button createTeamButton;

    @FXML
    private void handleJoinButtonClick(ActionEvent event) {
        String input = teamIdField.getText().trim();

        if (input.isEmpty()) {
            toonAlert("Fout", "Voer een team ID in.");
            return;
        }

        int teamId;
        try {
            teamId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            toonAlert("Fout", "Team ID moet een getal zijn.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement checkTeam = conn.prepareStatement("SELECT naam FROM team WHERE Team_ID = ?");
            checkTeam.setInt(1, teamId);
            ResultSet rs = checkTeam.executeQuery();

            if (rs.next()) {
                // ✅ Team bestaat - gebruiker koppelen
                PreparedStatement updateUser = conn.prepareStatement(
                        "UPDATE gebruiker SET Team_ID = ? WHERE Gebruiker_ID = ?"
                );
                updateUser.setInt(1, teamId);
                updateUser.setInt(2, UserSession.getID());
                updateUser.executeUpdate();

                UserSession.setTeamID(teamId);

                openScrumBoard(teamId); // ScrumBoard openen
            } else {
                toonAlert("Team niet gevonden", "Geen team met ID: " + teamId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            toonAlert("Databasefout", "Er ging iets mis met de database.");
        }
    }

    @FXML
    private void handleCreateButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/TeamScherm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle("Team Scherm");
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

            Stage huidigeStage = (Stage) createTeamButton.getScene().getWindow();
            huidigeStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            toonAlert("Fout", "Kon het teamcreatiescherm niet openen.");
        }
    }

    private void openScrumBoard(int teamId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/ScrumScherm.fxml"));
            Parent root = loader.load();

            ScrumController controller = loader.getController();
            controller.setTeamId(teamId);  // ✅ Geef het team ID door aan controller

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Scrum Board");
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

            Stage huidigeStage = (Stage) teamIdField.getScene().getWindow();
            huidigeStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            toonAlert("Fout", "Kan Scrum Board niet openen.");
        }
    }

    private void toonAlert(String titel, String bericht) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titel);
        alert.setHeaderText(null);
        alert.setContentText(bericht);
        alert.showAndWait();
    }
}
