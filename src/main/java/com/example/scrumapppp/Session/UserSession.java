package com.example.scrumapppp.Session;

//This class will show the userdata after the user is logged in.
public class UserSession {
    private static UserSession setSession;

    private int id;
    private String username;
    private String email;// Add other fields as needed


    private UserSession(int id, String username, String email)
    {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Singleton instance getter
    public static void setSession(int id, String username, String email) {
        if (setSession == null) {
            setSession = new UserSession(id, username, email);
            System.out.println("Session created: " + id + ", " + username);
        } else if(UserSession.getSession() == null){
            System.out.println("User is not logged in!");
        } else{
            System.out.println("Session has already been created🤓.");
        }
    }

    public static UserSession getSession(){
        return setSession;
    }

    // 💀Clear the session
    public static void clearSession() {
        setSession = null;
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


    // Optional: Add a toString method for debugging
    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

