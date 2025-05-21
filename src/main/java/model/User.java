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

    private List<Book> favorites;
    private List<Book> liked;
    private List<Book> saved;

    private Set<String> favGenres;
    private List<Club> memberOf;
    private List<Club> adminOf;


    public User(String username, int user_id, byte[] pfp, Set<String> favGenres, List<Club> memberOf, List<Club> adminOf) {
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

    public User(int user_id) {
        this.user_id = user_id;
        this.favGenres = new HashSet<>();
        this.adminOf = new ArrayList<>();
        this.memberOf = new ArrayList<>();

        try (
                Connection connection = DriverManager.getConnection(
                        CommonConstants.DB_URL,
                        CommonConstants.DB_USERNAME,
                        CommonConstants.DB_PASSWORD
                );
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT u.username, u.user_pfp, " +
                                "g.genre_name, " +
                                "c.club_id AS admin_club_id, " +
                                "c2.club_id AS member_club_id " +
                                "FROM users u " +
                                "LEFT JOIN user_genres ug ON u.user_id = ug.user_id " +
                                "LEFT JOIN genres g ON ug.genre_id = g.genre_id " +
                                "LEFT JOIN clubs c ON c.admin_id = u.user_id " +
                                "LEFT JOIN members m ON u.user_id = m.user_id " +
                                "LEFT JOIN clubs c2 ON m.club_id = c2.club_id " +
                                "WHERE u.user_id = ?"
                )
        ) {
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();

            Set<Integer> adminClubIds = new HashSet<>();
            Set<Integer> memberClubIds = new HashSet<>();

            while (rs.next()) {
                if (this.username == null) {
                    this.username = rs.getString("username");

                    Blob blob = rs.getBlob("user_pfp");
                    if (blob != null) {
                        this.pfp = blob.getBytes(1, (int) blob.length());
                    }
                }

                String genre = rs.getString("genre_name");
                if (genre != null) {
                    this.favGenres.add(genre);
                }

               /* int adminId = rs.getInt("admin_club_id");
                if (!rs.wasNull() && adminClubIds.add(adminId)) {
                    this.adminOf.add(new Club(adminId));  // Use a constructor that takes an ID
                }

                int memberId = rs.getInt("member_club_id");
                if (!rs.wasNull() && memberClubIds.add(memberId)) {
                    this.memberOf.add(new Club(memberId));  // Use a constructor that takes an ID
                }*/
            }

            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Error initializing User with user_id " + user_id, e);
        }
    }}
