package com.example.scrumapppp.DatabaseAndSQL;

public class ChatGroep {
    private int id;
    private String naam;
    private int teamId;

    public ChatGroep(int id, String naam, int teamId) {
        this.id = id;
        this.naam = naam;
        this.teamId = teamId;
    }

    public String getNaam() {
        return naam;
    }

    public int getTeamId() {
        return teamId;
    }

    @Override
    public String toString() {
        return naam;
    }
}
