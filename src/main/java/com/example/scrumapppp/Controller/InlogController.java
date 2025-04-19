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

    @FXML
    private TextField gebruikersnaamField;

    @FXML
    private PasswordField wachtwoordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleInloggen(ActionEvent event) {
        String gebruikersnaam = gebruikersnaamField.getText();
        String wachtwoord = wachtwoordField.getText();

        String storedPasswordHash = getStoredPasswordHash(gebruikersnaam);

        if (storedPasswordHash != null) {
            String enteredPasswordHash = hashWachtwoord(wachtwoord);

            if (enteredPasswordHash.equals(storedPasswordHash)) {
                statusLabel.setText("Inloggen gelukt!");

                try (Connection connectDB = DatabaseManager.getConnection()) {
                    String query = "SELECT Gebruiker_ID, Team_ID FROM gebruiker WHERE naam = ?";
                    PreparedStatement preparedStatement = connectDB.prepareStatement(query);
                    preparedStatement.setString(1, gebruikersnaam);
                    ResultSet queryResult = preparedStatement.executeQuery();

                    if (queryResult.next()) {
                        int id = queryResult.getInt("Gebruiker_ID");
                        int teamID = queryResult.getInt("Team_ID");

                        UserSession.setSession(id, gebruikersnaam, teamID);

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/TeamSelect.fxml"));
                        Scene homeScene = new Scene(loader.load());

                        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                        stage.setScene(homeScene);
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

    private String getStoredPasswordHash(String gebruikersnaam) {
        try (Connection dbConnection = DatabaseManager.getConnection()) {
            String query = "SELECT wachtwoord FROM gebruiker WHERE naam = ?";
            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                ResultSet rs = stmt.executeQuery();

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

    @FXML
    private void handleRegistreren(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/RegistreerScherm.fxml"));
            Scene registerScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(registerScene);
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
