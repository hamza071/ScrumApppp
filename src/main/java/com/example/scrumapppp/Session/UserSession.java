package com.example.scrumapppp.Session;

//This class will show the userdata after the user is logged in.
public class UserSession {
    private static UserSession setSession;

    private int id;
    private String username;
    private int teamID;// Add other fields as needed
    // Add this in UserSession.java
    private static int selectedUserstoryId;


    private UserSession(int id, String username, int teamID)
    {
        this.id = id;
        this.username = username;
        this.teamID = teamID;
    }

    // Singleton instance getter
    public static void setSession(int id, String username, int teamID) {
        if (setSession == null) {
            setSession = new UserSession(id, username, teamID);
            System.out.println("Session created: " + id + ", " + username +  ", " + teamID);
        } else if(UserSession.getSession() == null){
            System.out.println("User is not logged in!");
        } else{
            System.out.println("Session has already been created🤓.");
        }
    }

    public static UserSession getSession(){
        return setSession;
    }

    public static String getUsername(){
        if (setSession != null) {
            return setSession.username;  // Return the username from
            // the current session
        } else {
            return "No user logged in";  // Return a default message if no session is active
        }
    }


    public static int getID(){
        if (setSession != null) {
            return setSession.id;  // Return the username from the current session
        } else {
            return 0;  // Return a default message if no session is active
        }
    }

    public static int getTeamID(){
        if (setSession != null) {
            return setSession.teamID;  // Return the username from the current session
        } else {
            return 0;  // Return a default message if no session is active
        }
    }

    public static String getData(){
        return "UserSession{" +
                "id=" + setSession.id +
                ", username='" + setSession.username + '\'' +
                ", Team ID='" + setSession.teamID + '\'' +
                '}';
    }

    // 💀Clear the session
    public static void clearSession() {
        setSession = null;
    }

//    Chat and Userstory, outside the user-id class
//    Chat
private static int selectedChatId = 0;

    public static void setSelectedChatId(int chatId) {
        selectedChatId = chatId;
    }

    public static int getSelectedChatId() {
        return selectedChatId;
    }

//    Userstory
    public static void setSelectedUserstoryId(int id) {
        selectedUserstoryId = id;
        System.out.println("Selected Userstory ID set: " + id);
    }

    public static int getSelectedUserstoryId() {
        return selectedUserstoryId;
    }



    // Optional: Add a toString method for debugging
    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", Team ID='" + teamID + '\'' +
                '}';
    }
}