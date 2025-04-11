package com.example.scrumapppp.Controller;

import com.example.scrumapppp.Handlers.ChatMessage;
import com.example.scrumapppp.Handlers.UserstoryList;
import com.example.scrumapppp.Session.UserSession;
import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserstoryKiesSchermController {

    @FXML
    private ListView<UserstoryList> userstoryListListView;

    @FXML
    private Text teamName;

    private String selectedUserstory;


    // Method to initialize the screen and load user stories
    public void initialize() {
        showTeamName();
        loadUserstories();  // Load user stories into the ListView

        // Selecting a user story from the ListView
        userstoryListListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedUserstory = newValue.toString();
                int selectedUserstoryId = newValue.getId(); // Je hebt deze al in je UserstoryList class neem ik aan?

                // 🔐 Sla het op in de sessie
                UserSession.setSelectedUserstoryId(selectedUserstoryId);

                System.out.println("Selected userstory: " + selectedUserstory + " (ID: " + selectedUserstoryId + ")");
            }
        });

    }

    // Method to fetch the team name and display it
    public void showTeamName() {
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        String getTeamName = "SELECT t.naam FROM Team t JOIN gebruiker g ON t.Team_ID = g.Team_ID WHERE g.naam = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(getTeamName)) {
            preparedStatement.setString(1, UserSession.getUsername());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String naam = resultSet.getString("naam");
                System.out.println(naam);
                teamName.setText(naam);  // Set label text
            } else {
                teamName.setText("Team niet gevonden");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connectDB != null) connectDB.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to fetch the user stories from the database
    public void loadUserstories() {
        ObservableList<UserstoryList> userstories = FXCollections.observableArrayList();
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        String query = "SELECT Userstory_ID, titel, beschrijving, status FROM userstory WHERE Team_ID = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setInt(1, UserSession.getTeamID());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("Userstory_ID");
                String text = resultSet.getString("titel");
                String beschrijving = resultSet.getString("beschrijving");
                String status = resultSet.getString("status");

                userstories.add(new UserstoryList(id, text, beschrijving, status));  // Add the user story to the list
            }

            // Set the ListView with the user stories
            userstoryListListView.setItems(userstories);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connectDB != null) connectDB.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void updateUserstoryWithChat() {
        // Stap 1: Ophalen van geselecteerde user story
        UserstoryList selectedUserstory = userstoryListListView.getSelectionModel().getSelectedItem();

        if (selectedUserstory == null) {
            System.out.println("Geen userstory geselecteerd.");
            return;
        }

        int selectedUserstoryId = selectedUserstory.getId();

        // Stel dat je Chat_ID via UserSession meekrijgt, bijvoorbeeld:
        int selectedChatId = UserSession.getSelectedUserstoryId();  // Zorg dat deze bestaat

        // Stap 2: Database update uitvoeren
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        String updateQuery = "UPDATE chat SET Userstory_ID = ? WHERE Chat_ID = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, selectedUserstoryId);
            preparedStatement.setInt(2, selectedChatId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Chatbericht succesvol gekoppeld aan userstory ID: " + selectedUserstoryId);
            } else {
                System.out.println("Geen chatbericht geüpdatet.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connectDB != null) connectDB.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
