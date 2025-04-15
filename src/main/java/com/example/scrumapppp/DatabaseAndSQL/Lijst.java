package com.example.scrumapppp.DatabaseAndSQL;

public class Lijst {
    private int lijstId;
    private int teamId;
    private String naam;
    private int sprintId; // ✅ toegevoegd

    public Lijst(int lijstId, int teamId, String naam, int sprintId) {
        this.lijstId = lijstId;
        this.teamId = teamId;
        this.naam = naam;
        this.sprintId = sprintId;
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

    public int getSprintId() {
        return sprintId;
    }
}
