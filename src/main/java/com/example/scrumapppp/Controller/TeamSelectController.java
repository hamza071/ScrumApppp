package com.example.scrumapppp.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TeamSelectController {

    @FXML
    private TextField teamCodeField;

    @FXML
    private TextField teamNameField;

    @FXML
    private Button joinTeamButton;

    @FXML
    private Button createTeamButton;

    @FXML
    private void handleJoinTeam() {
        String code = teamCodeField.getText();
        System.out.println("Joining team with code: " + code);
        // hier kun je straks je database call doen
    }

    @FXML
    private void handleCreateTeam() {
        String name = teamNameField.getText();
        System.out.println("Creating team with name: " + name);
        // hier kun je straks je team in database zetten
    }
}
