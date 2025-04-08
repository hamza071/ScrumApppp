package com.example.scrumapppp.Handlers;

public class ChatMessage {
    private int id;
    private String displayText;

    public ChatMessage(int id, String displayText) {
        this.id = id;
        this.displayText = displayText;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return displayText;
    }
}
