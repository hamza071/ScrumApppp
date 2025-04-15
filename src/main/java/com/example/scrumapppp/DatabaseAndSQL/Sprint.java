package com.example.scrumapppp.DatabaseAndSQL;

public class Sprint {
    private int sprintId;
    private int teamId;
    private String naam;

    public Sprint(int sprintId, int teamId, String naam) {
        this.sprintId = sprintId;
        this.teamId = teamId;
        this.naam = naam;
    }

    public int getSprintId() {
        return sprintId;
    }

    public int getTeamId() {
        return teamId;
    }

    public String getNaam() {
        return naam;
    }

    @Override
    public String toString() {
        return naam; // dit is belangrijk voor ComboBox weergave
    }
}
