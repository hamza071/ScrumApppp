package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DBTest;
import com.example.scrumapppp.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
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
    private void handleRegistreren(ActionEvent event) {
        System.out.println("Register button clicked!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/Register.fxml"));
            Scene registerScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(registerScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = gebruikersnaamField.getText();

        if (username.isEmpty()) {
            statusLabel.setText("Gebruikersnaam mag niet leeg zijn!");
            return;
        }

        validateLogin(username, event);
    }

    private void validateLogin(String username, ActionEvent event) {
        DBTest connectionNow = new DBTest();
        Connection connectDB = connectionNow.getConnection();

        String verifyLoginQuery = "SELECT Gebruiker_ID, email FROM gebruiker WHERE naam = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLoginQuery)) {
            preparedStatement.setString(1, username);
            ResultSet queryResult = preparedStatement.executeQuery();

            if (queryResult.next()) {
                int id = queryResult.getInt("Gebruiker_ID");
                String email = queryResult.getString("email");

                UserSession.setSession(id, username, email);

                // Load the home scene after successful login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/Team.fxml"));
                Scene homeScene = new Scene(loader.load());

                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(homeScene);
            } else {
                statusLabel.setText("Ongeldige gebruikersnaam.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            statusLabel.setText("Er is een fout opgetreden bij het inloggen.");
        }
    }
}
