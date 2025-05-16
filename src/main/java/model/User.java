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
    private String user_id;
    private Date dob;
    private byte[] pfp;

    private List<Book> favorites;
    private List<Book> liked;
    private List<Book> saved;

    private Set<String> favGenres;
    private List<Club> memberOf;
    private List<Club> adminOf;


    public User(String username, String user_id, byte[] pfp, Set<String> favGenres, List<Club> memberOf, List<Club> adminOf) {
        this.username = username;
        this.user_id = user_id;
        this.adminOf = adminOf;
        this.memberOf = memberOf;
        this.pfp = pfp;
        this.favGenres = favGenres;
    }

    public String getUser_id() {
        return user_id;
    }

    public List<Club> getMemberOf() {
        return memberOf;
    }

    public List<Club> getAdminOf() {
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

    public User(String user_id) {
        String sql = "SELECT " +
                "u.username, u.user_pfp, " +
                "g.genre_name, " +
                "c.club_name AS admin_club_name, c.cover_picture AS admin_club_cover, " +
                "c2.club_name AS member_club_name, c2.cover_picture AS member_club_cover " +
                "FROM users u " +
                "LEFT JOIN user_genres ug ON u.user_id = ug.user_id " +
                "LEFT JOIN genres g ON ug.genre_id = g.genre_id " +
                "LEFT JOIN clubs c ON c.admin_id = u.user_id " +
                "LEFT JOIN members m ON u.user_id = m.user_id " +
                "LEFT JOIN clubs c2 ON m.club_id = c2.club_id " +
                "WHERE u.user_id = ?";

        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user_id);
            ResultSet rs = stmt.executeQuery();

            String username = null;
            String email = null;
            java.sql.Date dob = null;
            String gender = null;
            byte[] pfpBytes = null;

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

                Set<String> adminClubNames = new HashSet<>();
                Set<String> memberClubNames = new HashSet<>();

                // Add admin club
                String adminName = rs.getString("admin_club_name");
                Blob adminCoverBlob = rs.getBlob("admin_club_cover");
                if (adminName != null && !adminClubNames.contains(adminName)) {
                    byte[] cover = adminCoverBlob != null ? adminCoverBlob.getBytes(1, (int) adminCoverBlob.length()) : null;
                    this.adminOf.add(new Club(adminName, cover));
                    adminClubNames.add(adminName);
                }

                // Add member club
                String memberName = rs.getString("member_club_name");
                Blob memberCoverBlob = rs.getBlob("member_club_cover");
                if (memberName != null && !memberClubNames.contains(memberName)) {
                    byte[] cover = memberCoverBlob != null ? memberCoverBlob.getBytes(1, (int) memberCoverBlob.length()) : null;
                    this.memberOf.add(new Club(memberName, cover));
                    memberClubNames.add(memberName);
                }
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
