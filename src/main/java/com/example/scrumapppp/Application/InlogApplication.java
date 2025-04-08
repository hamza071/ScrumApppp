package com.example.scrumapppp.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InlogApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/InlogScherm.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Inloggen");
        primaryStage.setScene(scene);

        // Maak het venster full-screen
        primaryStage.setMaximized(true); // Maximaliseer het venster
        primaryStage.setFullScreen(true); // Zet op full-screen (druk op ESC om te sluiten)

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}