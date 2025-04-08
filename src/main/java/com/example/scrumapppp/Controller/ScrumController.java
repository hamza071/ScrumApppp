package com.example.scrumapppp.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.util.List;
import java.util.Optional;

public class ScrumController {

    @FXML
    private HBox boardHBox;

    @FXML
    private void initialize() {
        // Voeg startknop toe om lijsten toe te voegen
        Button voegLijstToeKnop = new Button("+ Voeg een lijst toe");
        voegLijstToeKnop.setOnAction(e -> maakNieuweLijst());
        boardHBox.getChildren().add(voegLijstToeKnop);
    }

    private void maakNieuweLijst() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe Lijst");
        dialog.setHeaderText(null);
        dialog.setContentText("Geef een naam voor de nieuwe lijst:");

        Optional<String> resultaat = dialog.showAndWait();
        resultaat.ifPresent(naam -> {
            VBox lijstBox = maakLijst(naam);
            // Voeg nieuwe lijst vóór de "Voeg lijst toe" knop
            boardHBox.getChildren().add(boardHBox.getChildren().size() - 1, lijstBox);
        });
    }

    private VBox maakLijst(String titel) {
        VBox lijstBox = new VBox(10);
        lijstBox.setPrefWidth(250);
        lijstBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color: #cccccc;");

        Label titelLabel = new Label(titel);
        titelLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox userStoriesBox = new VBox(5);

        Button voegKaartToeKnop = new Button("+ Voeg een kaart toe");
        voegKaartToeKnop.setOnAction(e -> maakNieuweUserStory(userStoriesBox));

        lijstBox.getChildren().addAll(titelLabel, userStoriesBox, voegKaartToeKnop);
        return lijstBox;
    }

    private void maakNieuweUserStory(VBox userStoriesBox) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe User Story");
        dialog.setHeaderText(null);
        dialog.setContentText("Geef een titel voor de user story:");

        Optional<String> resultaat = dialog.showAndWait();
        resultaat.ifPresent(titel -> {
            Button userStoryKnop = new Button(titel);
            userStoryKnop.setMaxWidth(Double.MAX_VALUE);
            userStoryKnop.setOnAction(e -> {
                // Later kan je hier popup maken voor beschrijving/chat
                System.out.println("Geklikt op user story: " + titel);
            });
            userStoriesBox.getChildren().add(userStoryKnop);
        });
    }
}
