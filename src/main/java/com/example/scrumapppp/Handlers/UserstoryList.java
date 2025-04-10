package com.example.scrumapppp.Handlers;

public class UserstoryList {
    private int id;
    private String displayText;
    private String beschrijving;
    private String status;

    public UserstoryList(int id, String displayText, String beschrijving, String status){
        this.id = id;
        this.displayText = displayText;
        this.beschrijving = beschrijving;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString(){
        return displayText + " | " + displayText + " | " + status;
    }
}
