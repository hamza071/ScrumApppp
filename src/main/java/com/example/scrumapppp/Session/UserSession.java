package com.example.scrumapppp.Session;

public class UserSession {
    private static UserSession setSession;

    private int id;
    private String username;
    private int teamId; // 🛑 Geveranderd: van email naar teamId

    private UserSession(int id, String username, int teamId) {
        this.id = id;
        this.username = username;
        this.teamId = teamId;
    }

    public static void setSession(int id, String username, int teamId) {
        if (setSession == null) {
            setSession = new UserSession(id, username, teamId);
            System.out.println("Session created: " + id + ", " + username + ", Team ID: " + teamId);
        } else {
            System.out.println("Session already exists.");
        }
    }

    public static UserSession getSession() {
        return setSession;
    }

    public static String getUsername() {
        if (setSession != null) {
            return setSession.username;
        } else {
            return "No user logged in";
        }
    }

    public static int getId() {
        if (setSession != null) {
            return setSession.id;
        } else {
            return 0;
        }
    }

    public static int getTeamId() {
        if (setSession != null) {
            return setSession.teamId;
        } else {
            return 0;
        }
    }

    public static void clearSession() {
        setSession = null;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", teamId=" + teamId +
                '}';
    }
}
