package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.Lijst;
import com.example.scrumapppp.DatabaseAndSQL.LijstDAO;
import com.example.scrumapppp.DatabaseAndSQL.Userstory;
import com.example.scrumapppp.DatabaseAndSQL.UserstoryDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ScrumController {

    @FXML
    private HBox boardHBox;

    private LijstDAO lijstDAO;
    private UserstoryDAO userstoryDAO;
    private int teamId = 1; // tijdelijk hardcoded

    @FXML
    private void initialize() {
        lijstDAO = new LijstDAO();
        userstoryDAO = new UserstoryDAO();

        laadBoard();

        // Voeg knop toe om nieuwe lijst te maken
        Button voegLijstToeKnop = new Button("+ Voeg een lijst toe");
        voegLijstToeKnop.setOnAction(e -> maakNieuweLijst());
        boardHBox.getChildren().add(voegLijstToeKnop);
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

        // 👉 Maak deze lijst dropbaar
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

                // Update de userstory naar de nieuwe lijst
                userstoryDAO.updateUserstoryLijst(userstoryId, lijst.getLijstId());

                // Refresh het board zodat alles goed staat
                laadBoard();

                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // Haal userstories op
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

        userStoryKnop.setOnAction(e -> {
            System.out.println("Geklikt op userstory: " + userstory.getTitel());
            // hier kan popup voor beschrijving komen
        });

        // 👉 Maak userstory sleebaar
        userStoryKnop.setOnDragDetected(event -> {
            Dragboard db = userStoryKnop.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(userstory.getUserstoryId()));
            db.setContent(content);
            event.consume();
        });

        userStoriesBox.getChildren().add(userStoryKnop);
    }
}
