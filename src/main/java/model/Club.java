package model;

import javafx.scene.image.Image;
import utils.DBManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Club {
    private int club_id;
    private String name;
    private String description;
    private byte[] coverImage;
    private int admin_id;
    private List<Integer> discussed_books = new ArrayList<>();
    private int member_count;

    public Club(int club_id, String name, String description, byte[] coverImage, int admin_id) {
        this.club_id = club_id;
        this.name = name;
        this.coverImage = coverImage;
        this.description = description;
        this.admin_id = admin_id;
    }

    public Club(String name, String description, byte[] coverImage, int admin_id) {
        this.name = name;
        this.coverImage = coverImage;
        this.description = description;
        this.admin_id = admin_id;
    }

    public Club(int club_id) {
        this.club_id = club_id;

        String sql = "SELECT club_name, club_description, cover_picture, admin_id FROM clubs WHERE club_id = ?";
        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, club_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.name = rs.getString("club_name");
                this.description = rs.getString("club_description");
                this.admin_id = rs.getInt("admin_id");

                Blob blob = rs.getBlob("cover_picture");
                if (blob != null) {
                    this.coverImage = blob.getBytes(1, (int) blob.length());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading club with ID " + club_id, e);
        }
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

    public int getClub_id() {
        return club_id;
    }

    public void setCount(int count) {
        this.member_count = count;
    }

    public int getCount() {
        return this.member_count;
    }

    public static Image displayClubCover(byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 0) {
            return new Image(new ByteArrayInputStream(imageBytes));
        } else {
            URL imageUrl = Club.class.getResource("/view/img/pp.png");
            if (imageUrl != null) {
                return new Image(imageUrl.toExternalForm());
            } else {
                System.out.println("Default image not found.");
                return null;
            }
        }
    }

    public static int createClub(String name, String description, int adminId, File coverPic) throws SQLException, IOException {
        String sql = "INSERT INTO clubs (club_name, club_description, admin_id, cover_picture) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
             FileInputStream fis = new FileInputStream(coverPic)) {

            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setInt(3, adminId);
            stmt.setBinaryStream(4, fis, (int) coverPic.length());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
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

    public static void leaveClub(int club_id, int user_id) throws SQLException {
        String sql = "DELETE FROM members WHERE club_id = ? AND user_id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, club_id);
            stmt.setInt(2, user_id);
            stmt.executeUpdate(); // missing in your code
        }
    }

    public static void removeClub(int club_id) throws SQLException {
        String sql = "DELETE FROM clubs WHERE club_id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, club_id);
            stmt.executeUpdate(); // missing in your code
        }
    }

    public static Club getClub(int club_id) throws SQLException {
        Club club = null;

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

                club = new Club(id, name, description, coverBytes, resultSet.getInt("admin_id"));
            } else {
                System.out.println("No club found with the given ID: " + club_id);
                return null;
            }
        }

        String countSql = "SELECT COUNT(*) AS member_count FROM members WHERE club_id = ?";
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(countSql)) {

            statement.setInt(1, club_id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                club.setCount(rs.getInt("member_count"));
            }
        }

        return club;
    }

    public static List<Club> fetchAllClubs() throws SQLException {
        List<Club> clubs = new ArrayList<>();

        String query = "SELECT * FROM clubs";
        try (Connection connection = DBManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int club_id = resultSet.getInt("club_id");
                String club_name = resultSet.getString("club_name");
                String club_description = resultSet.getString("club_description");
                byte[] cover_picture = resultSet.getBytes("cover_picture");
                int admin_id = resultSet.getInt("admin_id");

                clubs.add(new Club(club_id, club_name, club_description, cover_picture, admin_id));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return clubs;
    }
}
