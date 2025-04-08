package com.example.scrumapppp.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TeamController {

    // Deze methode wordt aangeroepen wanneer de chat-knop wordt geklikt
    @FXML
    private void chatButtonClick(ActionEvent event) {
        System.out.println("Chat button clicked!");
        // Voeg hier de gewenste functionaliteit toe voor de chatknop
        showAlert("Chatfunctie", "De chatfunctie wordt binnenkort toegevoegd!", AlertType.INFORMATION);
    }

    // Deze methode kan voor de uitlogknop zijn
    @FXML
    private void logOutButtonClick(ActionEvent event) {
        System.out.println("Uitloggen clicked!");
        // Voeg hier de uitloglogica toe
    }

    // Een eenvoudige alert methode om een bericht te tonen
    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}