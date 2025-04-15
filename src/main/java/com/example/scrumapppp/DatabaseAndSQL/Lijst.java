package com.example.scrumapppp.DatabaseAndSQL;

public class Lijst {
    private int lijstId;
    private int teamId;
    private String naam;

    public Lijst(int lijstId, int teamId, String naam) {
        this.lijstId = lijstId;
        this.teamId = teamId;
        this.naam = naam;
    }

    public int getLijstId() {
        return lijstId;
    }

    public int getTeamId() {
        return teamId;
    }

    public String getNaam() {
        return naam;
    }
}
