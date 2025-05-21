package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Forums {
    private  int forum_id;
    private String forum_name;


    public Forums(int forum_id, String forum_name) {
        this.forum_id = forum_id;
        this.forum_name = forum_name;
    }

    public static List<Forums> getForums(int clubid) {
        List<Forums> forums = new ArrayList<>();

        String query = "SELECT * FROM forums WHERE club_id = ?";

        try (
                Connection connection = DriverManager.getConnection(
                        CommonConstants.DB_URL,
                        CommonConstants.DB_USERNAME,
                        CommonConstants.DB_PASSWORD
                );
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setInt(1, clubid);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int forumId = resultSet.getInt("forum_id");
                String forumName = resultSet.getString("forum_name");

                Forums forum = new Forums(forumId, forumName);
                forums.add(forum);

                System.out.println("Forum ID: " + forumId + ", forum name: " + forumName);
            }

            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return forums;
    }


    public String getForumName(){
        return forum_name;
    }

    public  int getForumId(){
        return forum_id;
    }


    public static String getForumType(int forumId) throws SQLException {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String typeName = null;

        try {
            connection = DriverManager.getConnection(
                    CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME,
                    CommonConstants.DB_PASSWORD
            );

            System.out.println("DB connected!");

            String query = "SELECT types.types_name FROM types " +
                    "JOIN forums_types ON types.type_id = forums_types.type_id " +
                    "WHERE forums_types.forum_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, forumId);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                typeName = resultSet.getString("types_name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }

        return typeName;
    }


}




