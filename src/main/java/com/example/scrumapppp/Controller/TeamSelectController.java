package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.DatabaseConnection;
import com.example.scrumapppp.Session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.*;

public class TeamSelectController {

    @FXML
    private ComboBox<Integer> teamComboBox;

    @FXML
    private TextField teamNameField;

    @FXML
    private Button joinTeamButton;

    @FXML
    private Button createTeamButton;

    @FXML
    public void initialize() {
        DatabaseConnection db = new DatabaseConnection();
        try (Connection conn = db.getConnection()) {
            String sql = "SELECT Team_ID FROM team";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                teamComboBox.getItems().add(rs.getInt("Team_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Fout", "Kan teams niet laden.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleJoinTeam() {
        Integer geselecteerdTeamId = teamComboBox.getValue();

        if (geselecteerdTeamId == null) {
            showAlert("Geen team geselecteerd", "Selecteer een team om te joinen.", Alert.AlertType.WARNING);
            return;
        }

        int gebruikerId = UserSession.getID();
        DatabaseConnection db = new DatabaseConnection();

        try (Connection conn = db.getConnection()) {
            String updateQuery = "UPDATE gebruiker SET team_id = ? WHERE Gebruiker_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setInt(1, geselecteerdTeamId);
            stmt.setInt(2, gebruikerId);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                showAlert("Succes", "Je bent succesvol gejoined aan team " + geselecteerdTeamId, Alert.AlertType.INFORMATION);
            } else {
                showAlert("Mislukt", "Kon je niet toevoegen aan team.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Fout", "Databasefout bij team joinen.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCreateTeam() {
        String name = teamNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert("Leeg veld", "Teamnaam mag niet leeg zijn.", Alert.AlertType.WARNING);
            return;
        }

        DatabaseConnection db = new DatabaseConnection();

        try (Connection conn = db.getConnection()) {
            String insertQuery = "INSERT INTO team (naam) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setString(1, name);
            int inserted = stmt.executeUpdate();

            if (inserted > 0) {
                showAlert("Team aangemaakt", "Team '" + name + "' is succesvol aangemaakt!", Alert.AlertType.INFORMATION);
                teamComboBox.getItems().clear();
                initialize(); // refresh lijst
            } else {
                showAlert("Mislukt", "Team aanmaken mislukt.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Fout", "Databasefout bij team aanmaken.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
