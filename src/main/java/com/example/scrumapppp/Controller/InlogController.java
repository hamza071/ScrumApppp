package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseManager;
import com.example.scrumapppp.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InlogController {

    // FXML-annotaties voor de UI-elementen
    @FXML
    private TextField gebruikersnaamField;  // Veld voor gebruikersnaam
    @FXML
    private PasswordField wachtwoordField;  // Veld voor wachtwoord
    @FXML
    private Label statusLabel;  // Label voor statusberichten

    // Methode voor inloggen wanneer de gebruiker op "Inloggen" klikt
    @FXML
    private void handleInloggen(ActionEvent event) {
        String gebruikersnaam = gebruikersnaamField.getText();
        String wachtwoord = wachtwoordField.getText();

        // Haal het opgeslagen wachtwoord-hash op voor de opgegeven gebruikersnaam
        String storedPasswordHash = getStoredPasswordHash(gebruikersnaam);

        // Controleer of het opgeslagen wachtwoord-hash niet null is
        if (storedPasswordHash != null) {
            // Hash het ingevoerde wachtwoord en vergelijk het met het opgeslagen hash
            String enteredPasswordHash = hashWachtwoord(wachtwoord);

            // Als de hashes overeenkomen, is het inloggen gelukt
            if (enteredPasswordHash.equals(storedPasswordHash)) {
                statusLabel.setText("Inloggen gelukt!");

                try (Connection connectDB = DatabaseManager.getConnection()) {
                    String query = "SELECT Gebruiker_ID, Team_ID FROM gebruiker WHERE naam = ?";
                    PreparedStatement preparedStatement = connectDB.prepareStatement(query);
                    preparedStatement.setString(1, gebruikersnaam);
                    ResultSet queryResult = preparedStatement.executeQuery();

                    // Haal de Gebruiker_ID en Team_ID op uit de database
                    if (queryResult.next()) {
                        int id = queryResult.getInt("Gebruiker_ID");
                        int teamID = queryResult.getInt("Team_ID");

                        // Sla de gebruikerssessie op
                        UserSession.setSession(id, gebruikersnaam, teamID);

                        // Laad het nieuwe scherm na inloggen
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/TeamSelect.fxml"));
                        Scene homeScene = new Scene(loader.load());

                        // Zet het nieuwe scherm in het huidige venster
                        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                        stage.setScene(homeScene);
                        stage.setFullScreen(true);  // Zet het scherm in fullscreen-modus
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    statusLabel.setText("Er is een fout opgetreden bij het laden van de gebruikersgegevens.");
                }

            } else {
                // Wachtwoorden komen niet overeen
                statusLabel.setText("Ongeldige gebruikersnaam of wachtwoord.");
            }
        } else {
            // Gebruikersnaam bestaat niet in de database
            statusLabel.setText("Gebruikersnaam bestaat niet.");
        }
    }

    // Haal het opgeslagen wachtwoord-hash op voor de gegeven gebruikersnaam uit de database
    private String getStoredPasswordHash(String gebruikersnaam) {
        try (Connection dbConnection = DatabaseManager.getConnection()) {
            String query = "SELECT wachtwoord FROM gebruiker WHERE naam = ?";
            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                ResultSet rs = stmt.executeQuery();

                // Als de gebruikersnaam gevonden wordt, geef het wachtwoord terug
                if (rs.next()) {
                    return rs.getString("wachtwoord");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Databasefout", "Er is een fout opgetreden bij het ophalen van het wachtwoord.", AlertType.ERROR);
        }
        return null;
    }

    // Methode om het ingevoerde wachtwoord te hashen voor veilige vergelijking
    private String hashWachtwoord(String wachtwoord) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");  // Gebruik SHA-256 voor het hashen
            byte[] hashBytes = digest.digest(wachtwoord.getBytes());  // Genereer de hash van het wachtwoord
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));  // Converteer de bytes naar een hexadecimale string
            }
            return hexString.toString();  // Retourneer de hexadecimale string
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Methode voor de registratiepagina, die de gebruiker naar het registratieformulier stuurt
    @FXML
    private void handleRegistreren(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/RegistreerScherm.fxml"));
            Scene registerScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(registerScene);  // Zet de registratiepagina als nieuwe scène
            stage.setFullScreen(true);  // Zet het scherm in fullscreen-modus
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Methode om een alert weer te geven met een titel, content en type
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();  // Toon de alert en wacht op de reactie van de gebruiker
    }
}