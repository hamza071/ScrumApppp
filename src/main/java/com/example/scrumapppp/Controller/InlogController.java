package com.example.scrumapppp.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class InlogController {

    @FXML
    private TextField gebruikersnaamField;

    @FXML
    private PasswordField wachtwoordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleInloggen() {
        String gebruikersnaam = gebruikersnaamField.getText();
        String wachtwoord = wachtwoordField.getText();

        if (gebruikersnaam.equals("admin") && wachtwoord.equals("1234")) {
            statusLabel.setText("Inloggen gelukt!");
        } else {
            statusLabel.setText("Ongeldige gebruikersnaam of wachtwoord.");
        }
    }
}
