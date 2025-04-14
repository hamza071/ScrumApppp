package com.example.scrumapppp.Handlers;

public class UserstoryList {
    private int id;
    private String displayText;
    private String beschrijving;

    public UserstoryList(int id, String displayText, String beschrijving){
        this.id = id;
        this.displayText = displayText;
        this.beschrijving = beschrijving;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString(){
        return id + " | " + displayText;
    }


}
