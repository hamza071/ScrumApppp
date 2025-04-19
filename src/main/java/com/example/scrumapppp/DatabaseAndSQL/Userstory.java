package com.example.scrumapppp.DatabaseAndSQL;

public class Userstory {
    private int userstoryId;
    private int lijstId;
    private String titel;
    private String beschrijving;
    private String epic; // Nieuw veld

    // Constructor met epic
    public Userstory(int userstoryId, int lijstId, String titel, String beschrijving, String epic) {
        this.userstoryId = userstoryId;
        this.lijstId = lijstId;
        this.titel = titel;
        this.beschrijving = beschrijving;
        this.epic = epic;
    }

    // Constructor zonder epic (voor backwards compatibility)
    public Userstory(int userstoryId, int lijstId, String titel, String beschrijving) {
        this(userstoryId, lijstId, titel, beschrijving, null);
    }

    // Getters
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

    public String getEpic() {
        return epic;
    }

    // Setters
    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public void setEpic(String epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return titel;
    }
}
