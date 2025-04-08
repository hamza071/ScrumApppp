package com.example.scrumapppp.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistreerApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Laad het FXML bestand voor het registratiescherm
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/RegistreerScherm.fxml"));
        Scene scene = new Scene(loader.load());

        // Zet de titel van de window en toon het
        primaryStage.setTitle("Registreren");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);  // Optioneel: zet de applicatie full screen
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}