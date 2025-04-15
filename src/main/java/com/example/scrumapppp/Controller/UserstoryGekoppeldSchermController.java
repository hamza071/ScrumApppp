package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import com.example.scrumapppp.Handlers.ChatMessage;
import com.example.scrumapppp.Handlers.UserstoryList;
import com.example.scrumapppp.Session.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class UserstoryGekoppeldSchermController {

    @FXML
    private ListView<ChatMessage> gebruikersListView;

    @FXML
    private ListView<UserstoryList> userstoryListListView;

    @FXML
    private Text teamName;

    @FXML
    private TextField textMessage;

    private String selectedUser;

    @FXML
    public void initialize() {
        showMessage();
        showTeamName();

        // Selecting a message from the ListView
        gebruikersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedUser = newValue.toString();
                System.out.println("Selected user: " + selectedUser);
            }
        });
    }

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

    public void showMessage() {
        int teamID = UserSession.getTeamID();
        ObservableList<ChatMessage> gebruikersList = FXCollections.observableArrayList();
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        String query = "SELECT c.Chat_ID, g.naam, c.timestamp, c.bericht " +
                "FROM chat c " +
                "JOIN gebruiker g ON c.Gebruiker_ID = g.Gebruiker_ID " +
                "WHERE c.Team_ID = ? " +
                "ORDER BY c.timestamp ASC";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setInt(1, teamID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int chatId = resultSet.getInt("Chat_ID");
                    String naam = resultSet.getString("naam");
                    Timestamp timestamp = resultSet.getTimestamp("timestamp");
                    String bericht = resultSet.getString("bericht");

                    String formatted = naam + " [" + timestamp.toString() + "]: " + bericht;
                    gebruikersList.add(new ChatMessage(chatId, formatted));
                }
            }

            gebruikersListView.setItems(gebruikersList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connectDB != null) {
                    connectDB.close();
                }
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

        String query = "SELECT Userstory_ID, titel, beschrijving FROM userstory WHERE Team_ID = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {

            preparedStatement.setInt(1, UserSession.getTeamID());
            System.out.println("😂:"+ preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("Userstory_ID");
                String text = resultSet.getString("titel");
                String beschrijving = resultSet.getString("beschrijving");

                userstories.add(new UserstoryList(id, text, beschrijving));  // Add the user story to the list
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
    public void transferToChat(ActionEvent event){
        System.out.println("Userstory button clicked!");
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
