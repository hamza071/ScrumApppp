package com.example.scrumapppp.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TeamController {

    @FXML
    private TextField scrumboardNaamField;

    @FXML
    private TextField teamNaamField;

    @FXML
    private Button createButton;

    // De methode handleCreateButtonClick wordt aangeroepen vanuit de FXML file via onMouseClicked
    @FXML
    private void handleCreateButtonClick() {
        String scrumboardNaam = scrumboardNaamField.getText();
        String teamNaam = teamNaamField.getText();

        if (scrumboardNaam != null && !scrumboardNaam.trim().isEmpty() && teamNaam != null && !teamNaam.trim().isEmpty()) {
            System.out.println("Gecreëerd! Scrumboard Naam: " + scrumboardNaam + ", Team Naam: " + teamNaam);
        } else {
            System.out.println("Vul beide namen in!");
        }
    }
}