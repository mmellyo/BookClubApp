package model;
import java.awt.*;

public class CommonConstants {
    // color hex values
    public static final Color PRIMARY_COLOR = Color.decode("#000814");        //brown
    public static final Color SECONDARY_COLOR = Color.decode("#001D3D");      //beige
    public static final Color TEXT_COLOR = Color.decode("#FFC300");           //lighter beige

    // mysql credentials

    // url of db in this format -> jdbc:mysql:ip_address/schema-name
    public static final String DB_URL = "jdbc:mysql://localhost:3306/nbookinidb";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "Mellybookclub";
    public static final String DB_USERS_TABLE_NAME = "users";
    public static final String DB_BOOKS_TABLE_NAME = "books";
    public static final String DB_LIKED_BOOKS_TABLE_NAME = "liked_books";
    public static final String DB_AUTHOR_TABLE_NAME = "author";
    public static final String DB_WRITTEN_BY_TABLE_NAME = "written_by";





}