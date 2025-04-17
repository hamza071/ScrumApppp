package com.example.scrumapppp.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scrumapppp/ChatsScherm.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setTitle("TeamFlow - Chat");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true); // optioneel
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
