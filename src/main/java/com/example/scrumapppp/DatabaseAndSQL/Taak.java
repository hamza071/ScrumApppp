package com.example.scrumapppp.DatabaseAndSQL;

public class Taak {
    private int taakId;
    private int userstoryId;
    private String titel;
    private boolean isDone;

    public Taak(int taakId, int userstoryId, String titel, boolean isDone) {
        this.taakId = taakId;
        this.userstoryId = userstoryId;
        this.titel = titel;
        this.isDone = isDone;
    }

    // Getters en Setters
    public int getTaakId() { return taakId; }
    public int getUserstoryId() { return userstoryId; }
    public String getTitel() { return titel; }
    public boolean isDone() { return isDone; }
}
