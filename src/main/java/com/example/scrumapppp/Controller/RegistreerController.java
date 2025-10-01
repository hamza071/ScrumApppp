package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistreerController {

    // FXML-annotaties voor de UI-elementen
    @FXML private TextField gebruikersnaamField;  // Veld voor gebruikersnaam
    @FXML private PasswordField wachtwoordField;  // Veld voor wachtwoord
    @FXML private PasswordField wachtwoordField1; // Veld voor herhaling wachtwoord
    @FXML private Label statusLabel;  // Label voor statusberichten

    // Methode voor registratie wanneer de gebruiker op "Registreren" klikt
    @FXML
    private void handleRegistreren() {
        String gebruikersnaam = gebruikersnaamField.getText();
        String wachtwoord = wachtwoordField.getText();
        String wachtwoord1 = wachtwoordField1.getText();

        // Controleer of de gebruikersnaam leeg is
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty()) {
            statusLabel.setText("Gebruikersnaam mag niet leeg zijn!");
            return;
        }

        // Controleer of het wachtwoord leeg is
        if (wachtwoord == null || wachtwoord.trim().isEmpty()) {
            statusLabel.setText("Wachtwoord mag niet leeg zijn!");
            return;
        }

        // Controleer of de wachtwoorden overeenkomen
        if (!wachtwoord.equals(wachtwoord1)) {
            statusLabel.setText("De wachtwoorden komen niet overeen!");
            return;
        }

        // Controleer of de gebruikersnaam al in gebruik is
        if (isGebruikersnaamInGebruik(gebruikersnaam)) {
            statusLabel.setText("Deze gebruikersnaam is al in gebruik. Kies een andere.");
            return;
        }

        // Hash het wachtwoord voor veilige opslag
        String hashedWachtwoord = hashWachtwoord(wachtwoord);
        if (hashedWachtwoord == null) {
            statusLabel.setText("Er is een fout opgetreden bij het hashen van het wachtwoord.");
            return;
        }

        // Probeer de gebruiker te registreren
        boolean success = registerUser(gebruikersnaam, hashedWachtwoord);
        if (success) {
            statusLabel.setText("Gebruiker aangemaakt!");
            showAlert("Registratie succesvol", "Je account is aangemaakt.", Alert.AlertType.INFORMATION);

            // Optioneel: velden leegmaken
            gebruikersnaamField.clear();
            wachtwoordField.clear();
            wachtwoordField1.clear();
        } else {
            statusLabel.setText("Registratie mislukt. Probeer het opnieuw.");
        }
    }

    // Methode om naar de inlogpagina te navigeren
    @FXML
    private void handleInloggen() {
        navigateToLogin();
    }

    // Controleer of de gebruikersnaam al in gebruik is in de database
    private boolean isGebruikersnaamInGebruik(String gebruikersnaam) {
        try (Connection dbConnection = DatabaseManager.getConnection()) {
            String query = "SELECT COUNT(*) FROM gebruiker WHERE naam = ?";
            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;  // Als count groter is dan 0, is de gebruikersnaam in gebruik
                }
            }
        } catch (SQLException e) {
            showAlert("Databasefout", "Er is een fout opgetreden bij het controleren van de gebruikersnaam.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        return false;
    }

    // Methode om een gebruiker in te voegen in de database
    private boolean registerUser(String gebruikersnaam, String wachtwoord) {
        try (Connection dbConnection = DatabaseManager.getConnection()) {
            String query = "INSERT INTO gebruiker (naam, wachtwoord) VALUES (?, ?)";
            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                stmt.setString(2, wachtwoord);
                int result = stmt.executeUpdate();  // Voer de INSERT uit in de database
                return result > 0;  // Als er 1 of meer rijen zijn toegevoegd, was de registratie succesvol
            }
        } catch (SQLException e) {
            showAlert("Databasefout", "Er is een fout opgetreden bij het registreren van je account. Probeer het later opnieuw.", Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    // Methode om het wachtwoord te hashen voor veilige opslag
    private String hashWachtwoord(String wachtwoord) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");  // Gebruik SHA-256 hash algoritme
            byte[] hashBytes = digest.digest(wachtwoord.getBytes());  // Maak de hash
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));  // Converteer naar hexadecimale weergave
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Methode om naar het inlogscherm te navigeren
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/InlogScherm.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage currentStage = (Stage) statusLabel.getScene().getWindow();
            currentStage.setScene(loginScene);  // Zet het inlogscherm als nieuwe scène
            currentStage.setTitle("Inloggen");

            currentStage.centerOnScreen();  // Centreer het scherm op het scherm
            currentStage.setFullScreen(true);  // Zet het scherm in fullscreen-modus
            currentStage.show();
        } catch (IOException e) {
            showAlert("Fout", "Er is een fout opgetreden bij het laden van de inlogpagina.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Methode om een alert weer te geven met een titel, content en type
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}