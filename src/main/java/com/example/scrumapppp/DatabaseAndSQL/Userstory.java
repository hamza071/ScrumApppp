package com.example.scrumapppp.DatabaseAndSQL;

public class Userstory extends WerkItem {
    private int lijstId;
    private String beschrijving;
    private String epic;

    public Userstory(int userstoryId, int lijstId, String titel, String beschrijving, String epic) {
        super(userstoryId, titel);
        this.lijstId = lijstId;
        this.beschrijving = beschrijving;
        this.epic = epic;
    }

    public Userstory(int userstoryId, int lijstId, String titel, String beschrijving) {
        this(userstoryId, lijstId, titel, beschrijving, null);
    }

    public int getUserstoryId() {
        return id;
    }

    public int getLijstId() {
        return lijstId;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public String getEpic() {
        return epic;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public void setEpic(String epic) {
        this.epic = epic;
    }

    @Override
    public String getType() {
        return "Userstory";
    }

    @Override
    public String toString() {
        return titel;
    }
}
