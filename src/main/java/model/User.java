package model;

import utils.DBManager;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class User {
    private String username;
    private String email;
    private String hashedPass;
    private String gender;
    private int user_id;
    private Date dob;
    private byte[] pfp;

    private List<Book> favorites = new ArrayList<>();
    private List<Book> liked = new ArrayList<>();
    private List<Book> saved = new ArrayList<>();

    private Set<String> favGenres = new HashSet<>();
    private List<Integer> memberOf = new ArrayList<>();
    private List<Integer> adminOf = new ArrayList<>();


    public User(String username, int user_id, byte[] pfp, Set<String> favGenres, List<Integer> memberOf, List<Integer> adminOf) {
        this.username = username;
        this.user_id = user_id;
        this.adminOf = adminOf;
        this.memberOf = memberOf;
        this.pfp = pfp;
        this.favGenres = favGenres;
    }

    public int getUser_id() {
        return user_id;
    }

    public List<Integer> getMemberOf() {
        return memberOf;
    }

    public List<Integer> getAdminOf() {
        return adminOf;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPfp() {
        return pfp;
    }

    public Set<String> getFavGenres() {
        return favGenres;
    }

    public User(int user_id) {
        String sql = "SELECT " +
                "u.username, u.user_pfp, " +
                "g.genre_name, " +
                "c.club_id AS admin_club_id, " +
                "c2.club_id AS member_club_id " +
                "FROM users u " +
                "LEFT JOIN user_genres ug ON u.user_id = ug.user_id " +
                "LEFT JOIN genres g ON ug.genre_id = g.genre_id " +
                "LEFT JOIN clubs c ON c.admin_id = u.user_id " +
                "LEFT JOIN members m ON u.user_id = m.user_id " +
                "LEFT JOIN clubs c2 ON m.club_id = c2.club_id " +
                "WHERE u.user_id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();

            String username = null;
            String email = null;
            java.sql.Date dob = null;
            String gender = null;
            byte[] pfpBytes = null;
            this.user_id = user_id;
            while (rs.next()) {
                if (this.username == null) {
                    // Only set user info once
                    this.username = rs.getString("username");

                    Blob blob = rs.getBlob("user_pfp");
                    if (blob != null) {
                        this.pfp = blob.getBytes(1, (int) blob.length());
                    }
                }

                // Add genre
                String genre = rs.getString("genre_name");
                if (genre != null && !this.favGenres.contains(genre)) {
                    this.favGenres.add(genre);
                }



                // Add admin club
                int admin_club_id = rs.getInt("admin_club_id");
                if (!rs.wasNull()) {
                    this.adminOf.add(admin_club_id);
                }


                // Add member club
                int member_club_id = rs.getInt("member_club_id");
                if (!rs.wasNull()) {
                    this.memberOf.add(member_club_id);
                }
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
