package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import com.example.scrumapppp.Handlers.ChatMessage;
import com.example.scrumapppp.Handlers.TaakList;
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

public class TaakGekoppeldSchermController {

    @FXML
    private ListView<ChatMessage> gebruikersListView;

    @FXML
    private ListView<TaakList> taakListLView;

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
                int selectedChatId = newValue.getId(); // get Chat_ID from the ChatMessage object
                System.out.println("Selected user: " + selectedUser);

                loadTaakForChat(selectedChatId); // ✅ Call the updated method here
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
// Method to fetch the user stories from the database
    public void loadTaakForChat(int chatId) {
        ObservableList<TaakList> taken = FXCollections.observableArrayList();
        DatabaseConnection connectionNow = new DatabaseConnection();
        Connection connectDB = connectionNow.getConnection();

        String query = "SELECT t.Taak_ID, t.titel " +
                "FROM taak t " +
                "JOIN chat_taak ct ON t.Taak_ID = ct.Taak_ID " +
                "WHERE ct.Chat_ID = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setInt(1, chatId);
            System.out.println("🛠️ Query: " + preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("Taak_ID");
                String titel = resultSet.getString("titel");

                taken.add(new TaakList(id, titel));
            }

            taakListLView.setItems(taken);  // Show only linked taken

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
