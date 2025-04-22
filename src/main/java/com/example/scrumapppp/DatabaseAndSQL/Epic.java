package com.example.scrumapppp.DatabaseAndSQL;

public class Epic extends WerkItem {
    private int userstoryId;

    public Epic(int epicId, int userstoryId, String titel) {
        super(epicId, titel);
        this.userstoryId = userstoryId;
    }

    public int getEpicId() {
        return id;
    }

    public int getUserstoryId() {
        return userstoryId;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    @Override
    public String getType() {
        return "Epic";
    }

    @Override
    public String toString() {
        return titel;
    }
}
