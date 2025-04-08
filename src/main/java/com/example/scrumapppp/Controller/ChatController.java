package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import com.example.scrumapppp.Handlers.ChatMessage;
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
    private ListView<ChatMessage> gebruikersListView;

    @FXML
    private Label teamName;

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

    @FXML
    private void pinMessage() {
        String message = textMessage.getText();
        if (selectedUser != null && !message.isEmpty()) {
            System.out.println("Sending message to " + selectedUser + ": " + message);
        } else {
            System.out.println("No user selected or message is empty.");
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

    @FXML
    private void deleteMessage() {
        ChatMessage selectedMessage = gebruikersListView.getSelectionModel().getSelectedItem();
        if (selectedMessage != null) {
            int chatId = selectedMessage.getId();

            DatabaseConnection connectionNow = new DatabaseConnection();
            Connection connectDB = connectionNow.getConnection();

            String deleteQuery = "DELETE FROM chat WHERE Chat_ID = ?";

            try (PreparedStatement preparedStatement = connectDB.prepareStatement(deleteQuery)) {
                preparedStatement.setInt(1, chatId);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Message deleted successfully!");
                    showMessage(); // Refresh the ListView
                } else {
                    System.out.println("Failed to delete message.");
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
        } else {
            System.out.println("No message selected for deletion.");
        }
    }

    @FXML
    private void updateMessage() {
        ChatMessage selectedMessage = gebruikersListView.getSelectionModel().getSelectedItem();
        String newMessage = textMessage.getText();

        if (selectedMessage != null && !newMessage.isEmpty()) {
            int chatId = selectedMessage.getId();

            DatabaseConnection connectionNow = new DatabaseConnection();
            Connection connectDB = connectionNow.getConnection();

            String updateQuery = "UPDATE chat SET bericht = ? WHERE Chat_ID = ?";

            try (PreparedStatement preparedStatement = connectDB.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newMessage);
                preparedStatement.setInt(2, chatId);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Message updated successfully!");
                    showMessage(); // Refresh the ListView
                } else {
                    System.out.println("Failed to update message.");
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
        } else {
            System.out.println("No message selected for update or the message is empty.");
        }
    }

}
