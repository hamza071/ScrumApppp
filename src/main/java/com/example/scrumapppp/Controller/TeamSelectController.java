package com.example.scrumapppp.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class TeamSelectController {

    @FXML
    private TextField teamNaamField1;

    @FXML
    private Button joinTeamButton;

    @FXML
    private Button createTeamButton;

    // Methode voor 'Create Team' knop
    @FXML
    private void handleCreateButtonClick(ActionEvent event) {
        System.out.println("Create Team button clicked");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/TeamScherm.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Team Scherm");
            stage.setScene(scene);
            stage.setFullScreen(true); // opent fullscreen
            stage.show();

            // sluit huidige venster
            ((Stage) createTeamButton.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Methode voor 'Join Team' knop (voor nu alleen debug)
    @FXML
    private void handleJoinButtonClick(ActionEvent event) {
        System.out.println("Join Team button clicked");
        // Hier later functionaliteit toevoegen
    }

    public void handleJoinTeam(ActionEvent actionEvent) {
    }

    public void handleCreateTeam(ActionEvent actionEvent) {
    }
}