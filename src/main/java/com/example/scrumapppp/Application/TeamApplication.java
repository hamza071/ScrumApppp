package com.example.scrumapppp.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TeamApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/TeamScherm.fxml"));

            VBox root = loader.load();
            Scene scene = new Scene(root);

            primaryStage.setTitle("TeamFlow - Scrumboard");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true); // Zet de applicatie in full-screen modus
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}