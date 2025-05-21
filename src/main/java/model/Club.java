package model;

import utils.DBManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class Club {
    private String name;
    private String description;
    private byte[] coverImage;
    private int admin_id;
    private List<Integer> discussed_books;
    private int member_count;

    public Club(String name, String description,byte[] coverImage, int admin_id, List<Integer> discussed_books) {
        this.name = name;
        this.coverImage = coverImage;
        this.description = description;
        this.admin_id = admin_id;
        this.discussed_books = discussed_books;
    }

    public Club(String name, String description, byte[] coverBytes, int id) {
        this.name = name;
        this.coverImage = coverImage;
        this.description = description;
        this.admin_id = admin_id;
    }

    public String getName() {
        return name;
    }

    public byte[] getCoverImage() {
        return coverImage;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public String getDescription() {
        return description;
    }

    public void setCount(int count){
        this.member_count = count;
    }
    public int getCount()
    {
        return this.member_count;
    }
    // db related stuff

    public static int createClub(String name, String description, int adminId, File coverPic) throws SQLException, IOException {
        String sql = "INSERT INTO clubs (club_name, club_description, admin_id, cover_picture) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
             FileInputStream fis = new FileInputStream(coverPic)) {

            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setInt(3, adminId);
            stmt.setBinaryStream(4, fis, (int) coverPic.length());  // cover_picture is BLOB

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // return generated club ID
                } else {
                    throw new SQLException("Creating club failed, no ID obtained.");
                }
            }
        }
    }


    public static void addBooksDiscussed(int clubId, List<Integer> bookIds) throws SQLException {
        if (bookIds == null || bookIds.size() < 1 || bookIds.size() > 5) {
            throw new IllegalArgumentException("You must provide between 1 and 5 books.");
        }

        String sql = "INSERT INTO books_discussed (club_id, book_id) VALUES (?, ?)";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int bookId : bookIds) {
                stmt.setInt(1, clubId);
                stmt.setInt(2, bookId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public static void joinClub (int club_id, int user_id) throws SQLException {
        String sql ="INSERT INTO members (club_id, user_id, last_seen, date_joined) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, club_id);
            stmt.setInt(2, user_id);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }

    public static void leaveClub (int club_id, int user_id) throws SQLException {
        String sql = "DELETE FROM members WHERE club_id = ? AND user_id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, club_id);
            stmt.setInt(2, user_id);


        }
    }

    public static void removeClub (int club_id)throws SQLException {
        String sql = "DELETE FROM Clubs WHERE club_id = ? ";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, club_id);
        }
    }

    public static Club getClub(int club_id) throws SQLException {
        Club club;

        String sql = "SELECT club_id, club_name, club_description, cover_picture, admin_id FROM clubs WHERE club_id = ?";
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, club_id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("club_id");
                String name = resultSet.getString("club_name");
                String description = resultSet.getString("club_description");
                Blob coverBlob = resultSet.getBlob("cover_picture");
                byte[] coverBytes = coverBlob != null ? coverBlob.getBytes(1, (int) coverBlob.length()) : null;

                club = new Club(name, description, coverBytes, id);
            } else {
                System.out.println("No club found with the given ID: " + club_id);
                return null;
            }
        }

        // Corrected: use the correct SQL for counting members
        String countSql = "SELECT COUNT(*) AS member_count FROM members WHERE club_id = ?";
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(countSql)) {

            statement.setInt(1, club_id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int members = rs.getInt("member_count");
                club.setCount(members);
            }
        }

        return club;
    }

}
