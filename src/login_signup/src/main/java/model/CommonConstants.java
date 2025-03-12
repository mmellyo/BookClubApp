package model;
import java.awt.*;

public class CommonConstants {
    // color hex values
    public static final Color PRIMARY_COLOR = Color.decode("#000814");        //brown
    public static final Color SECONDARY_COLOR = Color.decode("#001D3D");      //beige
    public static final Color TEXT_COLOR = Color.decode("#FFC300");           //lighter beige

    // mysql credentials

    // url of db in this format -> jdbc:mysql:ip_address/schema-name
    public static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/bookclubdb";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "Mellybookclub!";
    public static final String DB_USERS_TABLE_NAME = "users";
}