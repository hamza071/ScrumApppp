package com.example.scrumapppp.DatabaseAndSQL;

public class Userstory {
    private int userstoryId;
    private int lijstId;
    private String titel;
    private String beschrijving;

    public Userstory(int userstoryId, int lijstId, String titel, String beschrijving) {
        this.userstoryId = userstoryId;
        this.lijstId = lijstId;
        this.titel = titel;
        this.beschrijving = beschrijving;
    }

    public int getUserstoryId() {
        return userstoryId;
    }

    public int getLijstId() {
        return lijstId;
    }

    public String getTitel() {
        return titel;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    // Nieuw toegevoegd
    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }
}
