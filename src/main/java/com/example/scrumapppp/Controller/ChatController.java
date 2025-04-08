package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import com.example.scrumapppp.Session.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ChatController {

    @FXML
    private ListView<String> gebruikersListView;

    @FXML
    private Label teamName;

    @FXML
    private TextField textMessage;

    private String selectedUser;

    @FXML
    public void initialize() {
        showMessage();
        showTeamName();

        gebruikersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedUser = newValue;
                System.out.println("Selected user: " + selectedUser);
            }
        });
    }

//    Dit kunnen we gebruiken om een text te pinnen. Anders kunnen we met behulp van de user story gewoon een Chat ID aan elkaar koppelen
    @FXML
    private void pinMessage() {
        String message = textMessage.getText();
        if (selectedUser != null && !message.isEmpty()) {
            System.out.println("Sending message to " + selectedUser + ": " + message);
        } else {
            System.out.println("No user selected or message is empty.");
        }
    }

    @FXML
    public void createMessage() {
        String message = textMessage.getText();

        if (message.isEmpty()) {
            System.out.println("Message mag niet leeg zijn");
            return;
        }

        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        String getUserData = "SELECT Gebruiker_ID, Team_ID FROM gebruiker WHERE naam = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(getUserData)) {
            preparedStatement.setString(1, UserSession.getUsername());

            ResultSet queryResult = preparedStatement.executeQuery();

            if (queryResult.next()) {
                int gebruikerID = queryResult.getInt("Gebruiker_ID");
                int teamID = queryResult.getInt("Team_ID");
                System.out.println("Gebruikers ID: "+ gebruikerID+"\nTeam ID: "+ teamID);

                String insertMessage = "INSERT INTO chat (Team_ID, Gebruiker_ID, timestamp, bericht) VALUES (?, ?, ?, ?)";

                try (PreparedStatement insertStatement = connectDB.prepareStatement(insertMessage)) {
                    insertStatement.setInt(1, teamID);
                    insertStatement.setInt(2, gebruikerID);
                    insertStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    insertStatement.setString(4, message);

                    insertStatement.executeUpdate();
                    System.out.println("Bericht succesvol opgeslagen!");

                    textMessage.clear(); // Clear input after sending
                    showMessage(); // Refresh chat list
                }
            } else {
                System.out.println("Gebruiker niet gevonden.");
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
        ObservableList<String> gebruikersList = FXCollections.observableArrayList();
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        String showAllUser = "SELECT g.naam, c.timestamp, c.bericht " +
                "FROM chat c " +
                "JOIN gebruiker g ON c.Gebruiker_ID = g.Gebruiker_ID " +
                "WHERE c.Team_ID = ? " +
                "ORDER BY c.timestamp ASC";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(showAllUser)) {
            preparedStatement.setInt(1, teamID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String naam = resultSet.getString("naam");
                    Timestamp timestamp = resultSet.getTimestamp("timestamp");
                    String bericht = resultSet.getString("bericht");

                    String formatted = naam + " [" + timestamp.toString() + "]: " + bericht;
                    gebruikersList.add(formatted);
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
}
