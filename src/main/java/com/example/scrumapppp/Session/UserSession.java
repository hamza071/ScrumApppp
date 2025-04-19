package com.example.scrumapppp.Session;

//This class will show the userdata after the user is logged in.
public class UserSession {
    private static UserSession setSession;

    private int id;
    private String username;
    private int teamID;
    private static int selectedUserstoryId;

    private UserSession(int id, String username, int teamID) {
        this.id = id;
        this.username = username;
        this.teamID = teamID;
    }

    // Singleton instance setter
    public static void setSession(int id, String username, int teamID) {
        if (setSession == null) {
            setSession = new UserSession(id, username, teamID);
            System.out.println("Session created: " + id + ", " + username + ", " + teamID);
        } else if (UserSession.getSession() == null) {
            System.out.println("User is not logged in!");
        } else {
            System.out.println("Session has already been created 🤓.");
        }
    }

    // ✅ Nieuw toegevoegd: teamID bijwerken
    public static void setTeamID(int newTeamID) {
        if (setSession != null) {
            setSession.teamID = newTeamID;
            System.out.println("Team ID updated in session: " + newTeamID);
        }
    }

    public static UserSession getSession() {
        return setSession;
    }

    public static String getUsername() {
        return setSession != null ? setSession.username : "No user logged in";
    }

    public static String getNaam() {  // ✅ Toegevoegd voor gemak
        return getUsername();
    }

    public static int getID() {
        return setSession != null ? setSession.id : 0;
    }

    public static int getTeamID() {
        return setSession != null ? setSession.teamID : 0;
    }

    public static String getData() {
        return "UserSession{" +
                "id=" + setSession.id +
                ", username='" + setSession.username + '\'' +
                ", Team ID='" + setSession.teamID + '\'' +
                '}';
    }

    public static void clearSession() {
        setSession = null;
    }

    public static void setSelectedUserstoryId(int id) {
        selectedUserstoryId = id;
        System.out.println("Selected Userstory ID set: " + id);
    }

    public static int getSelectedUserstoryId() {
        return selectedUserstoryId;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", Team ID='" + teamID + '\'' +
                '}';
    }
}
