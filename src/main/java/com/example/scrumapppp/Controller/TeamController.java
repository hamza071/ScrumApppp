package com.example.scrumapppp.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;  // Voeg MouseEvent toe

public class TeamController {

    // Deze methode wordt aangeroepen wanneer de create-knop wordt geklikt
    @FXML
    private void handleCreateButtonClick(ActionEvent event) {
        System.out.println("Create button clicked!");
        // Voeg hier de logica toe voor het aanmaken van een Scrumboard
        showAlert("Scrumboard Gecreëerd", "Het Scrumboard is succesvol aangemaakt!", AlertType.INFORMATION);
    }

    // Een eenvoudige alert methode om een bericht te tonen
    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Deze methode kan voor de chat-knop zijn
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
}