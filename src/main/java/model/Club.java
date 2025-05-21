package model;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
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
    private List<Integer> discussed_books;
    private int member_count;

    public Club(int club_id, String name, String description, byte[] coverImage, int admin_id) {
        this.club_id = club_id;
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


    public Club(int club_id) {
        this.club_id = club_id;

        try (
                Connection connection = DriverManager.getConnection(
                        CommonConstants.DB_URL,
                        CommonConstants.DB_USERNAME,
                        CommonConstants.DB_PASSWORD
                );
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT club_name, club_description, cover_picture, admin_id FROM clubs WHERE club_id = ?"
                )
        ) {
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

    public static Image displayClubCover(byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            return new Image(bis);
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

    public void setCount(int count){
        this.member_count = count;
    }
    public int getCount()
    {
        return this.member_count;
    }

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

    public static List<Club> fetchAllClubs() throws SQLException {
        List<Club> clubs = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(
                    CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME,
                    CommonConstants.DB_PASSWORD
            );

            System.out.println("DB connected!");

            String query = "SELECT * FROM clubs";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

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
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }

        return clubs;
    }








}



