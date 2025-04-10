package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseManager;
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
    private TextField gebruikersnaamField;

    @FXML
    private PasswordField wachtwoordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleInloggen(ActionEvent event) {
        String gebruikersnaam = gebruikersnaamField.getText();
        String wachtwoord = wachtwoordField.getText();

        // Haal opgeslagen wachtwoord hash uit database
        String storedPasswordHash = getStoredPasswordHash(gebruikersnaam);

        if (storedPasswordHash != null) {
            String enteredPasswordHash = hashWachtwoord(wachtwoord);

            if (enteredPasswordHash.equals(storedPasswordHash)) {
                statusLabel.setText("Inloggen gelukt!");

                // Haal Gebruiker_ID en Team_ID op
                try (Connection conn = DatabaseManager.getConnection()) {
                    String query = "SELECT Gebruiker_ID, Team_ID FROM gebruiker WHERE naam = ?";
                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, gebruikersnaam);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        int id = resultSet.getInt("Gebruiker_ID");
                        int teamId = resultSet.getInt("Team_ID");

                        // Start de gebruikerssessie
                        UserSession.setSession(id, gebruikersnaam, teamId);

                        // Laad nieuwe scene
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/Team.fxml"));
                        Scene homeScene = new Scene(loader.load());

                        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                        stage.setScene(homeScene);
                        stage.setFullScreen(true);
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    statusLabel.setText("Fout bij laden gebruiker!");
                }
            } else {
                statusLabel.setText("Ongeldige gebruikersnaam of wachtwoord.");
            }
        } else {
            statusLabel.setText("Gebruikersnaam bestaat niet.");
        }
    }

    private String getStoredPasswordHash(String gebruikersnaam) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT wachtwoord FROM gebruiker WHERE naam = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("wachtwoord");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Databasefout", "Fout bij ophalen wachtwoord.", AlertType.ERROR);
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

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
