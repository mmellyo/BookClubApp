package model;

public class Common {


    public static class SessionManager {
        public static String username;

        public static void setUsername(String user) {
            username = user;
        }

        public static String getUsername() {
            return username;
        }
    }

}
