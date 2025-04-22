package com.example.scrumapppp.DatabaseAndSQL;

public abstract class WerkItem {
    protected int id;
    protected String titel;

    public WerkItem(int id, String titel) {
        this.id = id;
        this.titel = titel;
    }

    public int getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public abstract String getType();
}
