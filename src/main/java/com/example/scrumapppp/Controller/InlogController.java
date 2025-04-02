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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EventObject;


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

    @FXML
    private void handleInloggen(ActionEvent event) {
        String gebruikersnaam = gebruikersnaamField.getText();

        validateLogin(gebruikersnaam, event);
    }

    @FXML
    private void handleRegistreren(ActionEvent event){
        System.out.println("Register button clicked!");
        try {
            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/Register.fxml"));
            Scene homeScene = new Scene(loader.load());

            // Get the current stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(homeScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void validateLogin(String username, ActionEvent event) {
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        System.out.println("Test😭1: " + username);


        String verifyLoginQuery = "SELECT Gebruiker_ID, email FROM gebruiker WHERE naam = ?";

        System.out.println(verifyLoginQuery);


        try (PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLoginQuery)) {
            preparedStatement.setString(1, username);

            ResultSet queryResult = preparedStatement.executeQuery();
            System.out.println("Test😭2: " + username);
            System.out.println(preparedStatement);

            // If a result is found, proceed with the login
            if (queryResult.next()) {

                System.out.println("Login Successful!");

                int id = queryResult.getInt("Gebruiker_ID");
//                    username = queryResult.getString("Gebruikersnaam");
                String email = queryResult.getString("email");

                UserSession.setSession(id, username, email);

                // Load the home scene after successful login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/Team.fxml"));
                Scene homeScene = new Scene(loader.load());

                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(homeScene);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
