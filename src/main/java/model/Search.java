package model;

import utils.DBManager;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Search {

    public static Set<Book> searchBooks (String input) throws SQLException {
        // stock the results in a tree set to not allow duplicate values
        Set<Book> result = new TreeSet<>();

        String sql = "SELECT DISTINCT b.title, b.cover_picture " +
                "FROM books b " +
                "LEFT JOIN written_by wb ON b.book_id = wb.book_id " +
                "LEFT JOIN author a ON wb.author_id = a.author_id " +
                "WHERE b.title LIKE ? " +
                "OR a.author_first_name LIKE ? " +
                "OR a.author_last_name LIKE ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String pattern = "%" + input + "%";
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setString(3, pattern);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                Blob coverBlob = rs.getBlob("cover_picture");
                byte[] coverBytes = coverBlob != null ? coverBlob.getBytes(1, (int) coverBlob.length()) : null;
                result.add(new Book(title, coverBytes));
            }
        }

        return result;
    }

    public static Set<Book> generateFyb (User user) throws SQLException {

        Set<Book> fyBooks = new TreeSet<>();

        String sql = "SELECT DISTINCT b.book_id, b.title, b.cover_picture " +
                "FROM books b " +
                "JOIN books_genres bg ON b.book_id = bg.book_id " +
                "JOIN genres g ON bg.genre_id = g.genre_id " +
                "WHERE g.genre_name IN (" +
                user.getFavGenres().stream().map(g -> "?").collect(Collectors.joining(", ")) +
                ") LIMIT 4";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            Iterator it = user.getFavGenres().iterator();
            while(it.hasNext())
            {
                int i = 0;
                stmt.setString(i + 1, (String) it.next());
                i++;
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                Blob coverBlob = rs.getBlob("cover_picture");
                byte[] coverBytes = coverBlob != null ? coverBlob.getBytes(1, (int) coverBlob.length()) : null;
                fyBooks.add(new Book(title, coverBytes, id));
            }
        }
        return fyBooks;
    }

}
