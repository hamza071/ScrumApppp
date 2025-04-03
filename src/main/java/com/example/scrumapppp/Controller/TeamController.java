package com.example.scrumapppp.Controller;
import com.example.scrumapppp.Session.UserSession;


import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class TeamController {
    @FXML
    private Text usernameText;

    public void initialize(){
        usernameText.setText("Welkom " + UserSession.getUsername());
        UserSession.getData();
    }
}
