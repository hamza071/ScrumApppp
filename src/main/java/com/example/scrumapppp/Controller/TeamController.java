package com.example.scrumapppp.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class TeamController {

    @FXML
    private ComboBox<String> teamComboBox;

    @FXML
    private TextField teamCodeField;

    @FXML
    public void initialize() {
        teamComboBox.getItems().addAll("Team 1", "Team 2", "Team 3");
    }
}
