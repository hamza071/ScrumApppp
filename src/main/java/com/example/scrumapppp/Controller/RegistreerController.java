package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistreerController {

    @FXML
    private TextField gebruikersnaamField;

    @FXML
    private PasswordField wachtwoordField;

    @FXML
    private PasswordField wachtwoordField1;

    @FXML
    private Label statusLabel;

    // Methode voor registratie van een gebruiker
    @FXML
    private void handleRegistreren() {
        String gebruikersnaam = gebruikersnaamField.getText();
        String wachtwoord = wachtwoordField.getText();
        String wachtwoord1 = wachtwoordField1.getText();

        // Controleer of de velden niet leeg zijn
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty()) {
            statusLabel.setText("Gebruikersnaam mag niet leeg zijn!");
            return;
        }

        if (wachtwoord == null || wachtwoord.trim().isEmpty()) {
            statusLabel.setText("Wachtwoord mag niet leeg zijn!");
            return;
        }

        // Controleer of de wachtwoorden overeenkomen
        if (!wachtwoord.equals(wachtwoord1)) {
            statusLabel.setText("De wachtwoorden komen niet overeen!");
            return;
        }

        // Opslaan in de database
        boolean success = registerUser(gebruikersnaam, wachtwoord);
        if (success) {
            // Succesvolle registratie, geef feedback en stuur de gebruiker door naar de inlogpagina
            statusLabel.setText("Gebruikersnaam aangemaakt!"); // Bevestiging tekst
            // Voeg een kleine vertraging toe zodat het bericht wordt weergegeven
            try {
                Thread.sleep(1500);  // Wacht 1,5 seconde zodat de gebruiker de boodschap ziet
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            showLoginPage(); // Navigatie naar het inlogscherm
        } else {
            // Foutmelding als registratie mislukt
            statusLabel.setText("Registratie mislukt. Probeer het opnieuw.");
        }
    }

    // Registreer gebruiker in de database
    private boolean registerUser(String gebruikersnaam, String wachtwoord) {
        DatabaseConnection connection = new DatabaseConnection();
        try (Connection dbConnection = connection.getConnection()) {
            // Voer de SQL-query uit om de gebruiker in te voegen
            String query = "INSERT INTO users (gebruikersnaam, wachtwoord) VALUES (?, ?)";
            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                stmt.setString(2, wachtwoord);
                int result = stmt.executeUpdate(); // Voer de update uit
                return result > 0; // Als er tenminste één rij is aangemaakt, is het succesvol
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Navigeren naar het inlogscherm
    private void showLoginPage() {
        try {
            // Laad het inlogscherm
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/InlogScherm.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) statusLabel.getScene().getWindow();  // Haal het huidige window op
            stage.setScene(scene);  // Zet het inlogscherm als nieuwe scene
            stage.show();  // Toon de inlogpagina
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode voor het tonen van alertberichten
    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}