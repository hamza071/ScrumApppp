package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DBTest;
import com.example.scrumapppp.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterController {
    @FXML
    DBTest databaseConnection = new DBTest();
    Connection connection = databaseConnection.getConnection();



    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    protected void onRegisterButtonClick(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();

        System.out.println("Registering User: " + username + " with Email: " + email);
        // Add your registration logic here (e.g., validation, database insertion, etc.)

        registerUser(username, email, event);
    }

    private void registerUser(String userName, String email, ActionEvent event) {
        connection = databaseConnection.getConnection();


        String insertQuery = "INSERT INTO gebruiker (naam, email) VALUES (?, ?)";
        String selectData = "SELECT Gebruiker_ID, email FROM gebruiker WHERE naam = ?";



        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, email);

            System.out.println(preparedStatement);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User registered successfully!");
//            User is succesfully created, but it doesn't select the user in order to login afterwards😭
                try(PreparedStatement preparedStatementSelect = connection.prepareStatement(selectData)){
                    System.out.println("Check the select statement");
                    preparedStatementSelect.setString(1, userName);
//                    preparedStatementSelect.setString(2, email);
                    System.out.println("UserName: " + userName);
//                    System.out.println("Email: " + email);

                    System.out.println("Execute 1");

                    ResultSet queryResult = preparedStatementSelect.executeQuery();

                    if (queryResult.next()){
                        int id = queryResult.getInt("Gebruiker_ID");
//                        String userName = queryResult.getString("username");
//                        String email = queryResult.getString("email");

                        UserSession.setSession(id, userName, email);
                    } else {
                        System.out.println("No user found with the provided username.");
                    }



                    // Redirect to home page
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/Team.fxml"));
                    Scene homeScene = new Scene(loader.load());
                    Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                    stage.setScene(homeScene);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("An error occurred while registerring in.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error registering user.");
        }
    }

}
