package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;

public class ScrumController {

    @FXML
    private HBox boardHBox;

    private LijstDAO lijstDAO;
    private UserstoryDAO userstoryDAO;
    private TaakDAO taakDAO;
    private int teamId = 1; // tijdelijk hardcoded

    @FXML
    private void initialize() {
        lijstDAO = new LijstDAO();
        userstoryDAO = new UserstoryDAO();
        taakDAO = new TaakDAO();

        laadBoard();

        Button voegLijstToeKnop = new Button("+ Voeg een lijst toe");
        voegLijstToeKnop.setOnAction(e -> maakNieuweLijst());
        boardHBox.getChildren().add(voegLijstToeKnop);

        // 🔥 Auto-refresh elke 5 seconden
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> laadBoard())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void laadBoard() {
        boardHBox.getChildren().clear();
        List<Lijst> lijsten = lijstDAO.getLijstenByTeamId(teamId);
        for (Lijst lijst : lijsten) {
            VBox lijstBox = maakLijst(lijst);
            boardHBox.getChildren().add(lijstBox);
        }
    }

    private void maakNieuweLijst() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe Lijst");
        dialog.setHeaderText(null);
        dialog.setContentText("Geef een naam voor de nieuwe lijst:");

        Optional<String> resultaat = dialog.showAndWait();
        resultaat.ifPresent(naam -> {
            Lijst nieuweLijst = lijstDAO.createLijst(teamId, naam);
            if (nieuweLijst != null) {
                VBox lijstBox = maakLijst(nieuweLijst);
                boardHBox.getChildren().add(boardHBox.getChildren().size() - 1, lijstBox);
            }
        });
    }

    private VBox maakLijst(Lijst lijst) {
        VBox lijstBox = new VBox(10);
        lijstBox.setPrefWidth(250);
        lijstBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color: #cccccc;");

        Label titelLabel = new Label(lijst.getNaam());
        titelLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox userStoriesBox = new VBox(5);

        userStoriesBox.setOnDragOver(event -> {
            if (event.getGestureSource() != userStoriesBox && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        userStoriesBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int userstoryId = Integer.parseInt(db.getString());
                userstoryDAO.updateUserstoryLijst(userstoryId, lijst.getLijstId());
                laadBoard();
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        List<Userstory> userstories = userstoryDAO.getUserstoriesByLijstId(lijst.getLijstId());
        for (Userstory userstory : userstories) {
            voegUserstoryToeAanBox(userStoriesBox, userstory);
        }

        Button voegKaartToeKnop = new Button("+ Voeg een kaart toe");
        voegKaartToeKnop.setOnAction(e -> maakNieuweUserStory(lijst, userStoriesBox));

        lijstBox.getChildren().addAll(titelLabel, userStoriesBox, voegKaartToeKnop);
        return lijstBox;
    }

    private void maakNieuweUserStory(Lijst lijst, VBox userStoriesBox) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe User Story");
        dialog.setHeaderText(null);
        dialog.setContentText("Geef een titel voor de user story:");

        Optional<String> resultaat = dialog.showAndWait();
        resultaat.ifPresent(titel -> {
            Userstory nieuweUserstory = userstoryDAO.createUserstory(lijst.getLijstId(), titel, "");
            if (nieuweUserstory != null) {
                voegUserstoryToeAanBox(userStoriesBox, nieuweUserstory);
            }
        });
    }

    private void voegUserstoryToeAanBox(VBox userStoriesBox, Userstory userstory) {
        Button userStoryKnop = new Button(userstory.getTitel());
        userStoryKnop.setMaxWidth(Double.MAX_VALUE);

        userStoryKnop.setOnAction(e -> openUserstoryTakenPopup(userstory));

        userStoryKnop.setOnDragDetected(event -> {
            Dragboard db = userStoryKnop.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(userstory.getUserstoryId()));
            db.setContent(content);
            event.consume();
        });

        userStoriesBox.getChildren().add(userStoryKnop);
    }

    private void openUserstoryTakenPopup(Userstory userstory) {
        Stage popupStage = new Stage();
        popupStage.setTitle("User Story: " + userstory.getTitel());

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        Label beschrijvingLabel = new Label("Beschrijving:");
        TextArea beschrijvingArea = new TextArea(userstory.getBeschrijving());
        beschrijvingArea.setWrapText(true);

        Button opslaanBtn = new Button("Opslaan Beschrijving");
        opslaanBtn.setOnAction(event -> {
            String nieuweBeschrijving = beschrijvingArea.getText();
            userstory.setBeschrijving(nieuweBeschrijving);
            userstoryDAO.updateUserstoryBeschrijving(userstory.getUserstoryId(), nieuweBeschrijving);
        });

        Label takenLabel = new Label("Taken:");
        VBox takenBox = new VBox(5);

        List<Taak> taken = taakDAO.getTakenByUserstoryId(userstory.getUserstoryId());
        for (Taak taak : taken) {
            HBox taakItem = maakTaakItem(taak, takenBox);
            takenBox.getChildren().add(taakItem);
        }

        Button voegTaakToeBtn = new Button("+ Voeg Taak Toe");
        voegTaakToeBtn.setOnAction(event -> {
            TextInputDialog taakDialog = new TextInputDialog();
            taakDialog.setTitle("Nieuwe Taak");
            taakDialog.setHeaderText(null);
            taakDialog.setContentText("Geef een titel voor de taak:");
            Optional<String> taakResultaat = taakDialog.showAndWait();
            taakResultaat.ifPresent(taakTitel -> {
                Taak nieuweTaak = taakDAO.createTaak(userstory.getUserstoryId(), taakTitel);
                if (nieuweTaak != null) {
                    HBox nieuweTaakItem = maakTaakItem(nieuweTaak, takenBox);
                    takenBox.getChildren().add(nieuweTaakItem);
                }
            });
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button verwijderUserstoryBtn = new Button("❌ Verwijder User Story");
        verwijderUserstoryBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        verwijderUserstoryBtn.setOnAction(event -> {
            userstoryDAO.deleteUserstory(userstory.getUserstoryId());
            popupStage.close();
            laadBoard();
        });

        HBox deleteBox = new HBox();
        deleteBox.setStyle("-fx-alignment: bottom-right;");
        deleteBox.getChildren().add(verwijderUserstoryBtn);

        layout.getChildren().addAll(
                beschrijvingLabel, beschrijvingArea, opslaanBtn,
                takenLabel, takenBox, voegTaakToeBtn,
                spacer, deleteBox
        );

        Scene scene = new Scene(layout, 400, 600);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private HBox maakTaakItem(Taak taak, VBox takenBox) {
        CheckBox taakCheckBox = new CheckBox(taak.getTitel());
        taakCheckBox.setSelected(taak.isDone());
        taakCheckBox.setOnAction(e -> taakDAO.updateTaakStatus(taak.getTaakId(), taakCheckBox.isSelected()));

        Button deleteTaakBtn = new Button("🗑️");
        deleteTaakBtn.setOnAction(e -> {
            taakDAO.deleteTaak(taak.getTaakId());
            takenBox.getChildren().removeIf(node -> node == deleteTaakBtn.getParent());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox taakItem = new HBox(5, taakCheckBox, spacer, deleteTaakBtn);
        taakItem.setFillHeight(true);
        return taakItem;
    }
}
