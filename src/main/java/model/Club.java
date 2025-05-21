package model;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
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

    public Club(int club_id, String name, String description, byte[] coverImage, int admin_id) {
        this.club_id = club_id;
        this.name = name;
        this.description = description;
        this.coverImage = coverImage;
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



