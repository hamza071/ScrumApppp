package com.example.scrumapppp.DatabaseAndSQL;

public class Taak extends WerkItem {
    private int userstoryId;
    private boolean isDone;

    public Taak(int taakId, int userstoryId, String titel, boolean isDone) {
        super(taakId, titel);
        this.userstoryId = userstoryId;
        this.isDone = isDone;
    }

    public int getTaakId() {
        return id;
    }

    public int getUserstoryId() {
        return userstoryId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public String getType() {
        return "Taak";
    }

    @Override
    public String toString() {
        return titel;
    }
}
