package com.example.scrumapppp.Application;

import java.util.ArrayList;
import java.util.List;

public class UserStoryApplication {
    private final String titel;
    private final String beschrijving;
    private final List<String> chatBerichten;

    public UserStoryApplication(String titel, String beschrijving) {
        this.titel = titel;
        this.beschrijving = beschrijving;
        this.chatBerichten = new ArrayList<>();
    }

    public String getTitel() {
        return titel;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public List<String> getChatBerichten() {
        return chatBerichten;
    }

    public void voegChatBerichtToe(String bericht) {
        chatBerichten.add(bericht);
    }
}
