package com.example.scrumapppp.Handlers;

public class TaakList {
    private int id;
    private String titel;

    public TaakList(int id, String titel) {
        this.id = id;
        this.titel = titel;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + " | " + titel;
    }
}