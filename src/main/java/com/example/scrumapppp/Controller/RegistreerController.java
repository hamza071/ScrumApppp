package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    @FXML
    private void handleRegistreren() {
        String gebruikersnaam = gebruikersnaamField.getText();
        String wachtwoord = wachtwoordField.getText();
        String wachtwoord1 = wachtwoordField1.getText();

        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty()) {
            statusLabel.setText("Gebruikersnaam mag niet leeg zijn!");
            return;
        }

        if (wachtwoord == null || wachtwoord.trim().isEmpty()) {
            statusLabel.setText("Wachtwoord mag niet leeg zijn!");
            return;
        }

        if (!wachtwoord.equals(wachtwoord1)) {
            statusLabel.setText("De wachtwoorden komen niet overeen!");
            return;
        }

        if (isGebruikersnaamInGebruik(gebruikersnaam)) {
            statusLabel.setText("Deze gebruikersnaam is al in gebruik. Kies een andere.");
            return;
        }

        String hashedWachtwoord = hashWachtwoord(wachtwoord);
        if (hashedWachtwoord == null) {
            statusLabel.setText("Er is een fout bij het hashen van het wachtwoord.");
            return;
        }

        boolean success = registerUser(gebruikersnaam, hashedWachtwoord);
        if (success) {
            statusLabel.setText("Gebruiker aangemaakt!");
            showAlert("Registratie succesvol", "Je account is aangemaakt. Je wordt doorgestuurd naar de loginpagina.", Alert.AlertType.INFORMATION);
            navigateToLogin();
        } else {
            statusLabel.setText("Registratie mislukt. Probeer opnieuw.");
        }
    }

    private boolean isGebruikersnaamInGebruik(String gebruikersnaam) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT COUNT(*) FROM gebruiker WHERE naam = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            showAlert("Databasefout", "Fout bij controleren gebruikersnaam.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
        return false;
    }

    private boolean registerUser(String gebruikersnaam, String wachtwoord) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO gebruiker (naam, wachtwoord, Team_ID) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, gebruikersnaam);
                stmt.setString(2, wachtwoord);
                stmt.setInt(3, 1); // 🛑 Hardcoded Team_ID = 1 (later aanpassen als nodig)
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            showAlert("Databasefout", "Fout bij registreren gebruiker.", Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
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

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/InlogScherm.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage currentStage = (Stage) statusLabel.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle("Inloggen");
            currentStage.centerOnScreen();
            currentStage.show();
        } catch (IOException e) {
            showAlert("Fout", "Fout bij laden loginpagina.", Alert.AlertType.ERROR);
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
