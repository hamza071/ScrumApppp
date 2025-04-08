package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistreerController {

    @FXML
    private TextField gebruikersnaamField;

    @FXML
    private PasswordField wachtwoordField;

    @FXML
    private PasswordField wachtwoordField1;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleRegistreren() {
        String gebruikersnaam = gebruikersnaamField.getText();
        String wachtwoord = wachtwoordField.getText();
        String wachtwoord1 = wachtwoordField1.getText();

        // Debugging: Controleer of de velden niet leeg zijn
        System.out.println("Gebruikersnaam: " + gebruikersnaam);
        System.out.println("Wachtwoord: " + wachtwoord);
        System.out.println("Herhaal wachtwoord: " + wachtwoord1);

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

        // Controleer of de gebruikersnaam al bestaat in de database
        if (isGebruikersnaamInGebruik(gebruikersnaam)) {
            statusLabel.setText("Deze gebruikersnaam is al in gebruik. Kies een andere.");
            return;
        }

        // Hash het wachtwoord voor opslag
        String hashedWachtwoord = hashWachtwoord(wachtwoord);
        if (hashedWachtwoord == null) {
            statusLabel.setText("Er is een fout opgetreden bij het hashen van het wachtwoord.");
            return;
        }

        // Opslaan in de database
        boolean success = registerUser(gebruikersnaam, hashedWachtwoord);
        if (success) {
            statusLabel.setText("Gebruikersnaam aangemaakt!");
            // Show success message and navigate to login screen
            showAlert("Registratie succesvol", "Je account is aangemaakt. Je wordt doorgestuurd naar de loginpagina.", AlertType.INFORMATION);

            // Navigeren naar de loginpagina
            navigateToLogin();
        } else {
            statusLabel.setText("Registratie mislukt. Probeer het opnieuw.");
        }
    }

    // Methode om te controleren of de gebruikersnaam al in de database staat
    private boolean isGebruikersnaamInGebruik(String gebruikersnaam) {
        DatabaseConnection connection = new DatabaseConnection();
        try (Connection dbConnection = connection.getConnection()) {
            String query = "SELECT COUNT(*) FROM gebruiker WHERE naam = ?";
            System.out.println("Uitvoeren van query: " + query);  // Debugging: Print query

            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Aantal gebruikers met deze naam: " + count);  // Debugging: Aantal resultaten
                    if (count > 0) {
                        // De gebruikersnaam bestaat al
                        return true;
                    }
                } else {
                    System.out.println("Geen resultaat gevonden voor gebruikersnaam.");  // Debugging: Geen resultaat
                }
            }
        } catch (SQLException e) {
            showAlert("Databasefout", "Er is een fout opgetreden bij het controleren van de gebruikersnaam.", AlertType.ERROR);
            e.printStackTrace();
        }
        return false;
    }

    // Registreer gebruiker in de database
    private boolean registerUser(String gebruikersnaam, String wachtwoord) {
        DatabaseConnection connection = new DatabaseConnection();
        try (Connection dbConnection = connection.getConnection()) {
            String query = "INSERT INTO gebruiker (naam, wachtwoord) VALUES (?, ?)";
            System.out.println("Uitvoeren van registratie query: " + query);  // Debugging: Print query

            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                stmt.setString(2, wachtwoord); // Wachtwoord als gehasht
                int result = stmt.executeUpdate();
                System.out.println("Aangepaste rijen: " + result);  // Debugging: Aantal gewijzigde rijen
                return result > 0;
            }
        } catch (SQLException e) {
            showAlert("Databasefout", "Er is een fout opgetreden bij het registreren van je account. Probeer het later opnieuw.", AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    // Methode om het wachtwoord te hashen
    private String hashWachtwoord(String wachtwoord) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(wachtwoord.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Navigeren naar de loginpagina
    private void navigateToLogin() {
        try {
            // Laad het FXML-bestand van de inlogpagina
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/InlogScherm.fxml"));
            Scene loginScene = new Scene(loader.load());

            // Haal de huidige stage op en verander de scene naar de loginpagina
            Stage currentStage = (Stage) statusLabel.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle("Inloggen");

            // Zorg ervoor dat het scherm gecentreerd wordt
            currentStage.centerOnScreen();

            // Toon het scherm
            currentStage.show();
        } catch (IOException e) {
            showAlert("Fout", "Er is een fout opgetreden bij het laden van de inlogpagina.", AlertType.ERROR);
            e.printStackTrace();
        }
    }
    // Alert popup (optioneel te gebruiken)
    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}