package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Parent;

import java.time.LocalDateTime;
import java.util.List;

public class ChatController {

    @FXML private ListView<String> groepListView;
    @FXML private Button nieuweGroepBtn;
    @FXML private TextArea chatTextArea;
    @FXML private TextField inputField;
    @FXML private Label groepTitelLabel;

    private final int teamId = 1;

    private final SprintDAO sprintDAO = new SprintDAO();
    private final LijstDAO lijstDAO = new LijstDAO();
    private final UserstoryDAO userstoryDAO = new UserstoryDAO();
    private final TaakDAO taakDAO = new TaakDAO();
    private final ChatDAO chatDAO = new ChatDAO();
    private final ChatGroepDAO chatGroepDAO = new ChatGroepDAO();

    private String huidigeGroep = null;
    private Timeline autoRefreshTimer;

    @FXML
    public void initialize() {
        groepListView.setOnMouseClicked(this::laadChatVoorGroep);
        laadAlleGroepen();
    }

    private void laadAlleGroepen() {
        groepListView.getItems().setAll(chatGroepDAO.getGroepsnamenVoorTeam(teamId));
    }

    private void laadChatVoorGroep(MouseEvent e) {
        String geselecteerdeGroep = groepListView.getSelectionModel().getSelectedItem();
        if (geselecteerdeGroep != null) {
            huidigeGroep = geselecteerdeGroep;
            groepTitelLabel.setText("Groep: " + huidigeGroep);
            startAutoRefresh();
            laadChatVoorHuidigeGroep();
        }
    }

    private void startAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }

        autoRefreshTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> laadChatVoorHuidigeGroep()));
        autoRefreshTimer.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimer.play();
    }

    private void laadChatVoorHuidigeGroep() {
        if (huidigeGroep != null) {
            List<ChatBericht> berichten = chatDAO.getBerichtenVoorGroep(huidigeGroep);
            chatTextArea.clear();
            for (ChatBericht b : berichten) {
                chatTextArea.appendText(b.getFormatted() + "\n");
            }
        }
    }

    @FXML
    private void verstuurBericht() {
        String bericht = inputField.getText().trim();
        if (!bericht.isEmpty() && huidigeGroep != null) {
            String gebruiker = "Hamza"; // Later vervangen met ingelogde gebruiker

            ChatBericht nieuwBericht = new ChatBericht(
                    huidigeGroep,
                    gebruiker,
                    bericht,
                    LocalDateTime.now()
            );

            chatDAO.voegBerichtToe(nieuwBericht);
            inputField.clear();
            laadChatVoorHuidigeGroep(); // Direct tonen
        }
    }

    @FXML
    private void openGroepPopup() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nieuwe Chatgroep");
        dialog.setHeaderText("Kies niveau voor de nieuwe chatgroep");

        ComboBox<Sprint> sprintComboBox = new ComboBox<>();
        ComboBox<Lijst> lijstComboBox = new ComboBox<>();
        ComboBox<Userstory> userStoryComboBox = new ComboBox<>();
        ComboBox<Taak> taakComboBox = new ComboBox<>();
        ComboBox<String> epicComboBox = new ComboBox<>();
        epicComboBox.getItems().add("– Geen –");

        sprintComboBox.getItems().addAll(sprintDAO.getSprintsByTeamId(teamId));

        sprintComboBox.setOnAction(e -> {
            Sprint s = sprintComboBox.getValue();
            lijstComboBox.getItems().setAll(s != null ? lijstDAO.getLijstenBySprintId(s.getSprintId()) : List.of());
            userStoryComboBox.getItems().clear();
            taakComboBox.getItems().clear();
        });

        lijstComboBox.setOnAction(e -> {
            Lijst l = lijstComboBox.getValue();
            userStoryComboBox.getItems().setAll(l != null ? userstoryDAO.getUserstoriesByLijstId(l.getLijstId()) : List.of());
            taakComboBox.getItems().clear();
        });

        userStoryComboBox.setOnAction(e -> {
            Userstory us = userStoryComboBox.getValue();
            taakComboBox.getItems().setAll(us != null ? taakDAO.getTakenByUserstoryId(us.getUserstoryId()) : List.of());
        });

        VBox content = new VBox(10,
                new Label("Sprint:"), sprintComboBox,
                new Label("Lijst:"), lijstComboBox,
                new Label("User Story:"), userStoryComboBox,
                new Label("Taak:"), taakComboBox,
                new Label("Epic (optioneel):"), epicComboBox
        );
        dialog.getDialogPane().setContent(content);

        ButtonType makenBtnType = new ButtonType("Maak groep", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(makenBtnType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogBtn -> {
            if (dialogBtn == makenBtnType) {
                StringBuilder naam = new StringBuilder();
                if (sprintComboBox.getValue() != null) naam.append(sprintComboBox.getValue().getNaam());
                if (lijstComboBox.getValue() != null) naam.append(" > ").append(lijstComboBox.getValue().getNaam());
                if (userStoryComboBox.getValue() != null) naam.append(" > ").append(userStoryComboBox.getValue().getTitel());
                if (taakComboBox.getValue() != null) naam.append(" > ").append(taakComboBox.getValue().getTitel());
                if (epicComboBox.getValue() != null && !epicComboBox.getValue().equals("– Geen –"))
                    naam.append(" > ").append(epicComboBox.getValue());

                String groepsnaam = naam.toString();
                if (!groepsnaam.isBlank()) {
                    chatGroepDAO.voegGroepToe(teamId, groepsnaam);
                    laadAlleGroepen(); // vernieuw de lijst
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void gaTerugNaarBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/ScrumScherm.fxml"));
            Parent root = loader.load();

            // Indien nodig kan hier teamId worden meegegeven aan ScrumController
            // ScrumController controller = loader.getController();
            // controller.setTeamId(teamId);

            Stage huidigeStage = (Stage) groepListView.getScene().getWindow();
            huidigeStage.setScene(new Scene(root));
            huidigeStage.setTitle("Scrum Board");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fout");
            alert.setHeaderText(null);
            alert.setContentText("Kon niet terugkeren naar het board.");
            alert.showAndWait();
        }
    }
}
