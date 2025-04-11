package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import com.example.scrumapppp.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
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

    @FXML
    DatabaseConnection databaseConnection = new DatabaseConnection();
    Connection connection = databaseConnection.getConnection();

    @FXML
    private TextField gebruikersnaamField;

    @FXML
    private PasswordField wachtwoordField;

    @FXML
    private Label statusLabel;

    // Methode om in te loggen
    @FXML
    private void handleInloggen(ActionEvent event) {
        String gebruikersnaam = gebruikersnaamField.getText();
        String wachtwoord = wachtwoordField.getText();

        // Haal de opgeslagen hash op van de gebruiker uit de database
        String storedPasswordHash = getStoredPasswordHash(gebruikersnaam);

        if (storedPasswordHash != null) {
            // Genereer de hash van het ingevoerde wachtwoord
            String enteredPasswordHash = hashWachtwoord(wachtwoord);

            // Vergelijk de hashes
            if (enteredPasswordHash.equals(storedPasswordHash)) {

                statusLabel.setText("Inloggen gelukt!");

                // Haal extra informatie op, zoals gebruiker ID en email
                try (Connection connectDB = databaseConnection.getConnection()) {
                    String query = "SELECT Gebruiker_ID, Team_ID FROM gebruiker WHERE naam = ?";
                    PreparedStatement preparedStatement = connectDB.prepareStatement(query);
                    preparedStatement.setString(1, gebruikersnaam);
                    ResultSet queryResult = preparedStatement.executeQuery();

                    if (queryResult.next()) {
                        int id = queryResult.getInt("Gebruiker_ID");
                        int teamID = queryResult.getInt("Team_ID");

                        // Start de gebruikerssessie
                        UserSession.setSession(id, gebruikersnaam, teamID);

                        // Laad de nieuwe scene na inloggen
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/Team.fxml"));
                        Scene homeScene = new Scene(loader.load());

                        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                        stage.setScene(homeScene);

                        // Zet de applicatie fullscreen
                        stage.setFullScreen(true);
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    statusLabel.setText("Er is een fout opgetreden bij het laden van de gebruikersgegevens.");
                }

            } else {
                statusLabel.setText("Ongeldige gebruikersnaam of wachtwoord.");
            }
        } else {
            statusLabel.setText("Gebruikersnaam bestaat niet.");
        }
    }

    // Methode om de opgeslagen wachtwoordhash op te halen uit de database
    private String getStoredPasswordHash(String gebruikersnaam) {
        try (Connection dbConnection = databaseConnection.getConnection()) {
            String query = "SELECT wachtwoord FROM gebruiker WHERE naam = ?";

            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getString("wachtwoord");  // Retourneer het gehashte wachtwoord
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Databasefout", "Er is een fout opgetreden bij het ophalen van het wachtwoord.", AlertType.ERROR);
        }
        return null;  // Geen wachtwoord gevonden
    }

    // Methode om een wachtwoord te hashen
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

    // Methode voor registreren (verwijst naar registratiepagina)
    @FXML
    private void handleRegistreren(ActionEvent event) {
        System.out.println("Register button clicked!");
        try {
            // Laad de registratie FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/RegistreerScherm.fxml"));
            Scene registerScene = new Scene(loader.load());

            // Verkrijg de huidige stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Zet de nieuwe scene
            stage.setScene(registerScene);

            // Zet fullscreen AAN
            stage.setFullScreen(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Voeg de showAlert methode hier toe
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}