package com.example.scrumapppp.DatabaseAndSQL;

public class Team {

    private int id;
    private String naam;

    // Constructor
    public Team(int id, String naam) {
        this.id = id;
        this.naam = naam;
    }

    // Getters en setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }
}
