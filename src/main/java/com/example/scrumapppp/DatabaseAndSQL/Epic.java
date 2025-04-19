package com.example.scrumapppp.DatabaseAndSQL;

public class Epic {
    private int epicId;
    private int userstoryId;
    private String titel;

    public Epic(int epicId, int userstoryId, String titel) {
        this.epicId = epicId;
        this.userstoryId = userstoryId;
        this.titel = titel;
    }

    public int getEpicId() {
        return epicId;
    }

    public int getUserstoryId() {
        return userstoryId;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    @Override
    public String toString() {
        return titel;
    }

}
