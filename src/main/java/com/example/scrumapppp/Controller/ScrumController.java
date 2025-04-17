package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;

public class ScrumController {

    @FXML
    private VBox mainLayout;

    private HBox boardHBox;
    private ComboBox<Sprint> sprintComboBox;
    private SprintDAO sprintDAO;
    private LijstDAO lijstDAO;
    private UserstoryDAO userstoryDAO;
    private TaakDAO taakDAO;

    private int teamId = 1; // Default teamId, kan later via de TeamController worden doorgegeven
    private Sprint geselecteerdeSprint;

    @FXML
    private void initialize() {
        sprintDAO = new SprintDAO();
        lijstDAO = new LijstDAO();
        userstoryDAO = new UserstoryDAO();
        taakDAO = new TaakDAO();

        boardHBox = new HBox(10);
        boardHBox.setStyle("-fx-padding: 15;");

        HBox topBar = maakTopBar();

        laadSprints();  // Zorgt ervoor dat de sprints worden geladen bij initialisatie.

        mainLayout.getChildren().clear();
        mainLayout.getChildren().addAll(topBar, boardHBox);

        // Zorg ervoor dat het bord elke 5 seconden wordt vernieuwd
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> laadBoard()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private HBox maakTopBar() {
        HBox topBar = new HBox(10);
        topBar.setStyle("-fx-padding: 10; -fx-background-color: #e0e0e0; -fx-border-color: #cccccc;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button nieuweLijstBtn = new Button("+ Voeg lijst toe");
        nieuweLijstBtn.setOnAction(e -> maakNieuweLijst());

        Button nieuweSprintBtn = new Button("+ Nieuwe sprint");
        nieuweSprintBtn.setOnAction(e -> maakNieuweSprint());

        sprintComboBox = new ComboBox<>();
        sprintComboBox.setPromptText("Selecteer sprint");
        sprintComboBox.setOnAction(e -> {
            geselecteerdeSprint = sprintComboBox.getValue();
            laadBoard();
        });

        Button chatBtn = new Button("Chat");
        chatBtn.setOnAction(e -> openChatVenster());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label teamLabel = new Label("Team ID: " + teamId);
        teamLabel.setStyle("-fx-font-weight: bold;");

        topBar.getChildren().addAll(
                nieuweLijstBtn,
                nieuweSprintBtn,
                sprintComboBox,
                chatBtn,
                spacer,
                teamLabel
        );
        return topBar;
    }

    private void laadSprints() {
        List<Sprint> sprints = sprintDAO.getSprintsByTeamId(teamId);
        sprintComboBox.getItems().setAll(sprints);
        if (!sprints.isEmpty()) {
            geselecteerdeSprint = sprints.get(0);
            sprintComboBox.setValue(geselecteerdeSprint);
            laadBoard();
        }
    }

    private void laadBoard() {
        boardHBox.getChildren().clear();
        if (geselecteerdeSprint == null) return;

        List<Lijst> lijsten = lijstDAO.getLijstenBySprintId(geselecteerdeSprint.getSprintId());
        for (Lijst lijst : lijsten) {
            boardHBox.getChildren().add(maakLijst(lijst));
        }
    }

    private void maakNieuweSprint() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe Sprint");
        dialog.setHeaderText(null);
        dialog.setContentText("Geef een naam voor de sprint:");

        dialog.showAndWait().ifPresent(naam -> {
            Sprint nieuwe = sprintDAO.createSprint(teamId, naam);
            if (nieuwe != null) {
                laadSprints();
                sprintComboBox.setValue(nieuwe);
            }
        });
    }

    // Voeg je andere methoden hier toe (zoals maakNieuweLijst, maakLijst, etc.)

    private void openNieuweTaakDialog(Userstory userstory) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe Taak");
        dialog.setHeaderText("Nieuwe taak toevoegen aan user story: " + userstory.getTitel());
        dialog.setContentText("Voer een titel in voor de taak:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(titel -> {
            if (titel.trim().isEmpty()) {
                showAlert("Ongeldige invoer", "De titel mag niet leeg zijn.", Alert.AlertType.WARNING);
            } else {
                Taak nieuweTaak = taakDAO.createTaak(userstory.getUserstoryId(), titel.trim());
                if (nieuweTaak != null) {
                    laadBoard();
                } else {
                    showAlert("Fout bij opslaan", "De taak kon niet worden aangemaakt.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String titel, String boodschap, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titel);
        alert.setHeaderText(null);
        alert.setContentText(boodschap);
        alert.showAndWait();
    }

    private void openChatVenster() {
        // Open chatvenster functionaliteit
    }

    // Methode om teamId via setter in te stellen (gebruik deze om teamId van buitenaf door te geven)
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
}