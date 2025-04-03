package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import com.example.scrumapppp.Session.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatController {

    @FXML
    private ListView<String> gebruikersListView;

    private String selectedUser;


    @FXML
    public void initialize() {
        showGebruiker();

        gebruikersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedUser = newValue;
                System.out.println("Selected user: " + selectedUser);
            }
        });
    }
    @FXML
    private void startChat() {
        if (selectedUser != null) {
            System.out.println("Starting chat with: " + selectedUser);
            // Open chat window or perform an action
        } else {
            System.out.println("No user selected!");
        }
    }



    public void showGebruiker() {
        String username = UserSession.getUsername();
        ObservableList<String> gebruikersList = FXCollections.observableArrayList();
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        String showAllUser = "SELECT naam FROM gebruiker WHERE naam <> ?";  // Changed column to match "naam"

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(showAllUser)) {
            preparedStatement.setString(1, username); // Set the username parameter

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String naam = resultSet.getString("naam");
                    gebruikersList.add(naam);  // Add user name to the list
                }
            }

            // Populate the ListView
            gebruikersListView.setItems(gebruikersList);

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception properly in a real application
        } finally {
            try {
                if (connectDB != null) {
                    connectDB.close(); // Close connection to avoid memory leaks
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
