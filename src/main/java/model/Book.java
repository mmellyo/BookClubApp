package model;

import javafx.scene.image.Image;
import utils.DBManager;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Book {
    private String title;
    private String author;
    private String description;
    private byte[] coverBytes;
    private int book_id;

    public Book(String title, String author, byte[] coverBytes, String description) {
        this.title = title;
        this.author = author;
        this.coverBytes = coverBytes;
        this.description = description;
    }
    public Book(String title, byte[] coverBytes) {
        this.title = title;
        this.coverBytes = coverBytes;
    }

    public Book(String title, byte[] coverBytes, int book_id) {
        this.title = title;
        this.coverBytes = coverBytes;
        this.book_id = book_id;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public byte[] getCoverBytes() {
        return coverBytes;
    }

    public Image getCoverImage() {
        if (coverBytes != null && coverBytes.length > 0) {
            return new Image(new ByteArrayInputStream(coverBytes));
        }
        return null;
    }

    public int getBook_id() {
        return book_id;
    }

    //db stuff

    public static List<Book> searchBooks (String input) throws SQLException {
        // stock the results in a tree set to not allow duplicate values
        List<Book> result = new ArrayList<>();

        String sql = "SELECT DISTINCT b.book_id, b.title, b.cover_picture " +
                "FROM books b " +
                "WHERE b.title LIKE ? " +
                "UNION " +
                "SELECT DISTINCT b.book_id, b.title, b.cover_picture " +
                "FROM books b " +
                "JOIN written_by wb ON wb.book_id = b.book_id " +
                "JOIN author a ON a.author_id = wb.author_id " +
                "WHERE a.author_first_name LIKE ? OR a.author_last_name LIKE ? ";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String pattern = "%" + input + "%";
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setString(3, pattern);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                Blob coverBlob = rs.getBlob("cover_picture");
                byte[] coverBytes = coverBlob != null ? coverBlob.getBytes(1, (int) coverBlob.length()) : null;
                result.add(new Book(title, coverBytes, id));
            }
        }

        return result;
    }

    public static List<Book> generateFyb (User user) throws SQLException {

        List<Book> books = new ArrayList<>();

        String sql = "SELECT DISTINCT b.book_id, b.title, b.cover_picture " +
                "FROM books b " +
                "JOIN books_genres bg ON b.book_id = bg.book_id " +
                "JOIN genres g ON bg.genre_id = g.genre_id " +
                "WHERE g.genre_name IN (" +
                user.getFavGenres().stream().map(g -> "?").collect(Collectors.joining(", ")) +
                ") LIMIT 4";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            int i = 1;
            for (String genre : user.getFavGenres()) {
                stmt.setString(i++, genre);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                Blob coverBlob = rs.getBlob("cover_picture");
                byte[] coverBytes = coverBlob != null ? coverBlob.getBytes(1, (int) coverBlob.length()) : null;
                books.add(new Book(title, coverBytes, id));
            }
        }
        return books;
    }

    public static List<Book> getBooksByGenre (String genre) throws SQLException {
        List<Book> books = new ArrayList<>();

        String sql = "SELECT DISTINCT b.book_id, b.title, b.cover_picture "+
                "FROM books b " +
                "JOIN books_genres bg ON bg.book_id = b.book_id " +
                "JOIN genres g ON bg.genre_id = g.genre_id " +
                "WHERE LOWER(g.genre_name) = LOWER(?) ";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {


            statement.setString(1, genre.toLowerCase());

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                Blob coverBlob = rs.getBlob("cover_picture");
                byte[] coverBytes = coverBlob != null ? coverBlob.getBytes(1, (int) coverBlob.length()) : null;
                books.add(new Book(title, coverBytes, id));
            }
        }
        return books;
    }

    public static List<Book> getPopularBooks () throws SQLException {
        List<Book> books = new ArrayList<>();

        String sql = "SELECT b.book_id, b.title, b.cover_picture, COUNT(lb.user_id) AS likes " +
                "FROM books b " +
                "JOIN liked_books lb ON b.book_id = lb.book_id " +
                "GROUP BY b.book_id, b.title, b.cover_picture " +
                "ORDER BY likes DESC " +
                "LIMIT 4 ";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                Blob coverBlob = rs.getBlob("cover_picture");
                byte[] coverBytes = coverBlob != null ? coverBlob.getBytes(1, (int) coverBlob.length()) : null;
                books.add(new Book(title, coverBytes, id));
            }
        }


        return books;
    }
    public static List<Book> getAllBooks () throws SQLException {
        List<Book> books = new ArrayList<>();

        String sql = "SELECT DISTINCT b.book_id, b.title, b.cover_picture "+
                "FROM books b ";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                Blob coverBlob = rs.getBlob("cover_picture");
                byte[] coverBytes = coverBlob != null ? coverBlob.getBytes(1, (int) coverBlob.length()) : null;
                books.add(new Book(title, coverBytes, id));
            }
        }
        return books;
    }

}
