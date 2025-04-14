package com.example.scrumapppp.Controller;

import com.example.scrumapppp.Handlers.ChatMessage;
import com.example.scrumapppp.Handlers.TaakList;
import com.example.scrumapppp.Session.UserSession;
import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaakKiesSchermController {

    @FXML
    private ListView<TaakList> taakListLView;

    @FXML
    private Text teamName;

    private String selectedUserstory;


    // Method to initialize the screen and load user stories
    public void initialize() {
        showTeamName();
        loadTaken();  // Load user stories into the ListView

        System.out.println("Chat gekoppeld met ID😭: " + UserSession.getSelectedChatId());

        // Selecting a user story from the ListView
        taakListLView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
    public void loadTaken() {
        ObservableList<TaakList> userstories = FXCollections.observableArrayList();
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        String query = "SELECT Taak_ID, titel FROM taak WHERE Team_ID = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {

            preparedStatement.setInt(1, UserSession.getTeamID());
            System.out.println("😂:"+ preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("Taak_ID");
                String text = resultSet.getString("titel");

                userstories.add(new TaakList(id, text));  // Add the user story to the list
            }

            // Set the ListView with the user stories
            taakListLView.setItems(userstories);

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

    public void updateUserstoryWithChat() {
        System.out.println("😅Test😅");
        // Step 1: Get the selected user story
        TaakList selectedUserstory = taakListLView.getSelectionModel().getSelectedItem();

        if (selectedUserstory == null) {
            System.out.println("Geen userstory geselecteerd.");
            return;
        }

        int selectedUserstoryId = selectedUserstory.getId();

        // Assuming Chat_ID is obtained from UserSession
        int selectedChatId = UserSession.getSelectedChatId();  // Ensure this exists

        // Step 2: Perform the database insert into chat_userstory table
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        String insertQuery = "INSERT INTO chat_taak (Chat_ID, Taak_ID) VALUES(?, ?)";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, selectedChatId);
            preparedStatement.setInt(2, selectedUserstoryId);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("😭 Chatbericht succesvol gekoppeld aan userstory ID: " + selectedUserstoryId);
            } else {
                System.out.println("Geen chatbericht toegevoegd.");
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


    @FXML
    public void transferToChat(ActionEvent event){
        System.out.println("Userstory button clicked!");
        updateUserstoryWithChat();
        try {
            // Laad de registratie FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/Chat.fxml"));
            Scene userstoryScene = new Scene(loader.load());


            // Verkrijg de huidige stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Zet de nieuwe scene
            stage.setScene(userstoryScene);

            // Zet fullscreen AAN
            stage.setFullScreen(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
