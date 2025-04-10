package com.example.scrumapppp.Controller;

import com.example.scrumapppp.DatabaseAndSQL.LijstDAO;
import com.example.scrumapppp.DatabaseAndSQL.UserstoryDAO;
import com.example.scrumapppp.DatabaseAndSQL.Lijst;
import com.example.scrumapppp.DatabaseAndSQL.Userstory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import java.util.List;
import java.util.Optional;

public class ScrumController {

    @FXML
    private HBox boardHBox;

    private LijstDAO lijstDAO;
    private UserstoryDAO userstoryDAO;

    @FXML
    private void initialize() {
        lijstDAO = new LijstDAO();
        userstoryDAO = new UserstoryDAO();

        int teamId = 1; //UserSession.getTeamId(); // 🔥 Gebruikt nu de juiste team ID van de user

        // Haal alle lijsten op voor het team
        List<Lijst> lijsten = lijstDAO.getLijstenByTeamId(teamId);
        for (Lijst lijst : lijsten) {
            VBox lijstBox = maakLijst(lijst);
            boardHBox.getChildren().add(lijstBox);
        }

        // Voeg knop toe om nieuwe lijst te maken
        Button voegLijstToeKnop = new Button("+ Voeg een lijst toe");
        voegLijstToeKnop.setOnAction(e -> maakNieuweLijst(teamId));
        boardHBox.getChildren().add(voegLijstToeKnop);
    }

    private void maakNieuweLijst(int teamId) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe Lijst");
        dialog.setHeaderText(null);
        dialog.setContentText("Geef een naam voor de nieuwe lijst:");

        Optional<String> resultaat = dialog.showAndWait();
        resultaat.ifPresent(naam -> {
            // Voeg lijst toe aan database
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

        // Haal alle userstories voor deze lijst
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
            // Later popup voor beschrijving/chat
        });
        userStoriesBox.getChildren().add(userStoryKnop);
    }
}
