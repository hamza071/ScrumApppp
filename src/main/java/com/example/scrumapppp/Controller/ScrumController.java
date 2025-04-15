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

    private int teamId = 1;
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

        laadSprints();

        mainLayout.getChildren().clear();
        mainLayout.getChildren().addAll(topBar, boardHBox);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> laadBoard()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
 //Bing Chilling
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

    private void laadBoard() {
        boardHBox.getChildren().clear();
        if (geselecteerdeSprint == null) return;

        List<Lijst> lijsten = lijstDAO.getLijstenBySprintId(geselecteerdeSprint.getSprintId());
        for (Lijst lijst : lijsten) {
            boardHBox.getChildren().add(maakLijst(lijst));
        }
    }

    private void maakNieuweLijst() {
        if (geselecteerdeSprint == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe Lijst");
        dialog.setHeaderText(null);
        dialog.setContentText("Geef een naam voor de nieuwe lijst:");

        dialog.showAndWait().ifPresent(naam -> {
            Lijst nieuweLijst = lijstDAO.createLijst(teamId, geselecteerdeSprint.getSprintId(), naam);
            if (nieuweLijst != null) laadBoard();
        });
    }

    private VBox maakLijst(Lijst lijst) {
        VBox lijstBox = new VBox(10);
        lijstBox.setPrefWidth(250);
        lijstBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-border-color: #ccc;");

        Label titelLabel = new Label(lijst.getNaam());
        titelLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox userStoriesBox = new VBox(5);

        userStoriesBox.setOnDragOver(e -> {
            if (e.getGestureSource() != userStoriesBox && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        userStoriesBox.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                userstoryDAO.updateUserstoryLijst(Integer.parseInt(db.getString()), lijst.getLijstId());
                laadBoard();
                e.setDropCompleted(true);
            } else {
                e.setDropCompleted(false);
            }
            e.consume();
        });

        for (Userstory userstory : userstoryDAO.getUserstoriesByLijstId(lijst.getLijstId())) {
            voegUserstoryToeAanBox(userStoriesBox, userstory);
        }

        Button kaartToeBtn = new Button("+ Voeg kaart toe");
        kaartToeBtn.setOnAction(e -> maakNieuweUserStory(lijst, userStoriesBox));

        lijstBox.getChildren().addAll(titelLabel, userStoriesBox, kaartToeBtn);
        return lijstBox;
    }

    private void maakNieuweUserStory(Lijst lijst, VBox userStoriesBox) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe User Story");
        dialog.setHeaderText(null);
        dialog.setContentText("Geef een titel voor de user story:");

        dialog.showAndWait().ifPresent(titel -> {
            Userstory nieuwe = userstoryDAO.createUserstory(lijst.getLijstId(), titel, "");
            if (nieuwe != null) laadBoard();
        });
    }

    private void voegUserstoryToeAanBox(VBox box, Userstory userstory) {
        Button knop = new Button(userstory.getTitel());
        knop.setMaxWidth(Double.MAX_VALUE);
        knop.setOnAction(e -> openUserstoryTakenPopup(userstory));
        knop.setOnDragDetected(e -> {
            Dragboard db = knop.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(userstory.getUserstoryId()));
            db.setContent(content);
            e.consume();
        });
        box.getChildren().add(knop);
    }

    private void openUserstoryTakenPopup(Userstory userstory) {
        Stage popup = new Stage();
        popup.setTitle("User Story: " + userstory.getTitel());

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        TextArea beschrijvingArea = new TextArea(userstory.getBeschrijving());
        beschrijvingArea.setWrapText(true);

        Button opslaan = new Button("Opslaan Beschrijving");
        opslaan.setOnAction(e -> {
            userstory.setBeschrijving(beschrijvingArea.getText());
            userstoryDAO.updateUserstoryBeschrijving(userstory.getUserstoryId(), beschrijvingArea.getText());
        });

        VBox takenBox = new VBox(5);
        for (Taak taak : taakDAO.getTakenByUserstoryId(userstory.getUserstoryId())) {
            takenBox.getChildren().add(maakTaakItem(taak, takenBox));
        }

        Button taakToevoegen = new Button("+ Voeg Taak Toe");
        taakToevoegen.setOnAction(e -> {
            TextInputDialog taakDialoog = new TextInputDialog();
            taakDialoog.setTitle("Nieuwe Taak");
            taakDialoog.setContentText("Geef een titel voor de taak:");
            taakDialoog.showAndWait().ifPresent(titel -> {
                Taak t = taakDAO.createTaak(userstory.getUserstoryId(), titel);
                if (t != null) takenBox.getChildren().add(maakTaakItem(t, takenBox));
            });
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button verwijderen = new Button("❌ Verwijder User Story");
        verwijderen.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        verwijderen.setOnAction(e -> {
            userstoryDAO.deleteUserstory(userstory.getUserstoryId());
            popup.close();
            laadBoard();
        });

        HBox deleteBox = new HBox(verwijderen);
        deleteBox.setAlignment(Pos.BOTTOM_RIGHT);

        layout.getChildren().addAll(
                new Label("Beschrijving:"), beschrijvingArea, opslaan,
                new Label("Taken:"), takenBox, taakToevoegen,
                spacer, deleteBox
        );

        popup.setScene(new Scene(layout, 400, 600));
        popup.show();
    }

    private HBox maakTaakItem(Taak taak, VBox takenBox) {
        CheckBox checkbox = new CheckBox(taak.getTitel());
        checkbox.setSelected(taak.isDone());
        checkbox.setOnAction(e -> taakDAO.updateTaakStatus(taak.getTaakId(), checkbox.isSelected()));

        Button deleteBtn = new Button("🗑️");
        deleteBtn.setOnAction(e -> {
            taakDAO.deleteTaak(taak.getTaakId());
            takenBox.getChildren().removeIf(n -> n == deleteBtn.getParent());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox taakItem = new HBox(5, checkbox, spacer, deleteBtn);
        taakItem.setFillHeight(true);
        return taakItem;
    }

    private void openChatVenster() {

    }
}