package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.*;
import com.example.scrumapppp.Session.UserSession;
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

    private final int teamId = UserSession.getTeamID();
    private final SprintDAO sprintDAO = new SprintDAO();
    private final LijstDAO lijstDAO = new LijstDAO();
    private final UserstoryDAO userstoryDAO = new UserstoryDAO();
    private final TaakDAO taakDAO = new TaakDAO();
    private final ChatDAO chatDAO = new ChatDAO();
    private final ChatGroepDAO chatGroepDAO = new ChatGroepDAO();
    private final EpicDAO epicDAO = new EpicDAO();

    private String huidigeGroep = null;
    private Timeline autoRefreshTimer;

    // laadt de lijst met groepen en klik
    @FXML
    public void initialize() {
        groepListView.setOnMouseClicked(this::laadChatVoorGroep);
        laadAlleGroepen();
    }
    // Haalt alle chat groepen met team id
    private void laadAlleGroepen() {
        groepListView.getItems().setAll(chatGroepDAO.getGroepsnamenVoorTeam(teamId));
    }

    // Als een groep geselecteerd wordt dan laden de chats en auto refresh
    private void laadChatVoorGroep(MouseEvent e) {
        String geselecteerdeGroep = groepListView.getSelectionModel().getSelectedItem();
        if (geselecteerdeGroep != null) {
            huidigeGroep = geselecteerdeGroep;
            groepTitelLabel.setText("Groep: " + huidigeGroep);
            startAutoRefresh();
            laadChatVoorHuidigeGroep();
        }
    }
    // refresh methode elke 8 seconden
    private void startAutoRefresh() {
        if (autoRefreshTimer != null) autoRefreshTimer.stop();

        autoRefreshTimer = new Timeline(new KeyFrame(Duration.seconds(8), e -> laadChatVoorHuidigeGroep()));
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
    // verzend bericht in database en laden in chat
    @FXML
    private void verstuurBericht() {
        String bericht = inputField.getText().trim();
        if (!bericht.isEmpty() && huidigeGroep != null) {
            String gebruiker = UserSession.getUsername();

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

    // nieuwe groep knop aanmaken
    @FXML
    private void openGroepPopup() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nieuwe Chatgroep");
        dialog.setHeaderText("Kies niveau voor de nieuwe chatgroep");

        ComboBox<Sprint> sprintComboBox = new ComboBox<>();
        ComboBox<Lijst> lijstComboBox = new ComboBox<>();
        ComboBox<Userstory> userStoryComboBox = new ComboBox<>();
        ComboBox<String> epicComboBox = new ComboBox<>();
        ComboBox<String> taakComboBox = new ComboBox<>();

        sprintComboBox.getItems().addAll(sprintDAO.getSprintsByTeamId(teamId));

        sprintComboBox.setOnAction(e -> {
            Sprint s = sprintComboBox.getValue();
            lijstComboBox.getItems().setAll(s != null ? lijstDAO.getLijstenBySprintId(s.getSprintId()) : List.of());
            userStoryComboBox.getItems().clear();
            epicComboBox.getItems().setAll("– Geen –");
            taakComboBox.getItems().setAll("– Geen –");
        });

        lijstComboBox.setOnAction(e -> {
            Lijst l = lijstComboBox.getValue();
            userStoryComboBox.getItems().setAll(l != null ? userstoryDAO.getUserstoriesByLijstId(l.getLijstId()) : List.of());
            epicComboBox.getItems().setAll("– Geen –");
            taakComboBox.getItems().setAll("– Geen –");
        });

        userStoryComboBox.setOnAction(e -> {
            Userstory us = userStoryComboBox.getValue();

            // Epics
            epicComboBox.getItems().clear();
            epicComboBox.getItems().add("– Geen –");
            if (us != null) {
                for (Epic epic : epicDAO.getEpicsByUserstoryId(us.getUserstoryId())) {
                    epicComboBox.getItems().add(epic.getTitel());
                }
            }

            // Taken
            taakComboBox.getItems().clear();
            taakComboBox.getItems().add("– Geen –");
            if (us != null) {
                for (Taak taak : taakDAO.getTakenByUserstoryId(us.getUserstoryId())) {
                    taakComboBox.getItems().add(taak.getTitel());
                }
            }
        });

        VBox content = new VBox(10,
                new Label("Sprint:"), sprintComboBox,
                new Label("Lijst:"), lijstComboBox,
                new Label("User Story:"), userStoryComboBox,
                new Label("Epic:"), epicComboBox,
                new Label("Taak:"), taakComboBox
        );
        dialog.getDialogPane().setContent(content);

        ButtonType makenBtnType = new ButtonType("Maak groep", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(makenBtnType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogBtn -> {
            if (dialogBtn == makenBtnType) {
                StringBuilder naam = new StringBuilder();

                if (sprintComboBox.getValue() != null)
                    naam.append(sprintComboBox.getValue().getNaam());

                if (lijstComboBox.getValue() != null)
                    naam.append(" > ").append(lijstComboBox.getValue().getNaam());

                if (userStoryComboBox.getValue() != null)
                    naam.append(" > ").append(userStoryComboBox.getValue().getTitel());

                if (epicComboBox.getValue() != null && !epicComboBox.getValue().equals("– Geen –"))
                    naam.append(" > ").append(epicComboBox.getValue());

                if (taakComboBox.getValue() != null && !taakComboBox.getValue().equals("– Geen –"))
                    naam.append(" > ").append(taakComboBox.getValue());

                String groepsnaam = naam.toString();
                if (!groepsnaam.isBlank()) {
                    chatGroepDAO.voegGroepToe(teamId, groepsnaam);
                    laadAlleGroepen();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
   // knop om terug te gaan naar scrumboard
    @FXML
    private void gaTerugNaarBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/ScrumScherm.fxml"));
            Parent root = loader.load();

            // TeamId opnieuw instellen op de ScrumController
            ScrumController controller = loader.getController();
            controller.setTeamId(teamId);

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