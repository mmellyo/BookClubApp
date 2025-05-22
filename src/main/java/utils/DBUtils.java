//communicate with DB :
// sign user up / log user in
// change scenes
//Validation Methods
//Date Formatting
//File Handling ??
//Logging Methods (for debugging)
//Common Constants (e.g., predefined messages, max book count)
package utils;

import controller.*;
import javafx.animation.ScaleTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import model.BookModel;
import model.CommonConstants;
import model.Common;
import model.MessageModel;


import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    //ALERT
    public static double xOffset = 0;
    public static double yOffset = 0;

    private static List<Integer> selectedGenres = new ArrayList<>();

    public static void showCustomAlert(String displayAlert) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED); // Removes the title bar
        stage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with other windows

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f5ebc7; -fx-border-color: #f5ebc7; -fx-border-width: 2;");

        Label label = new Label(displayAlert);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.DARKRED);
        label.setWrapText(true);

        // Measure text width and set the window width accordingly
        Text tempText = new Text(displayAlert);
        tempText.setFont(label.getFont());
        double textWidth = tempText.getLayoutBounds().getWidth();

        double minWidth = 400; // Minimum width
        double maxWidth = 600; // Maximum width limit
        double calculatedWidth = Math.min(maxWidth, Math.max(minWidth, textWidth + 50));

        Button closeButton = new Button("OK");
        closeButton.setStyle("-fx-background-color: #d6bc99; -fx-text-fill:  #4a2f1e; -fx-font-size: 14px; -fx-cursor: hand;");
        closeButton.setOnAction(e -> stage.close());

        // Button animation (scales up slightly on hover)
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), closeButton);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), closeButton);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        closeButton.setOnMouseEntered(e -> scaleUp.play());
        closeButton.setOnMouseExited(e -> scaleDown.play());

        // Make the window draggable
        vbox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        vbox.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // Fade-in animation
//        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), vbox);
//        fadeIn.setFromValue(0);
//        fadeIn.setToValue(1);
//        fadeIn.play();



        vbox.getChildren().addAll(label, closeButton);

        Scene scene = new Scene(vbox, calculatedWidth, 150);
        stage.setScene(scene);
        stage.showAndWait(); // Waits for the user to close the alert before returning
    }


    public static void changeScene(Event event, String fxmlFile, int userid, String userpassword ) {
        Parent root = null;


        // if user is switching with info entered => Load the next FXML file with valid info
        if (userid != -1 && userpassword!= null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();  //loads the UI and assigns it to root

              /*  if (fxmlFile.equals("/view/login.fxml")) {
                    loginController loginC = loader.getController();
                    loginC.setUserInfo(userid, userpassword);
                }*/

             /*   if (fxmlFile.equals("/view/signup.fxml")) {
                    signupController signUpC = loader.getController();
                    signUpC.setUserInfo(userid, userpassword);
                }*/

                if (fxmlFile.equals("/view/signup2.fxml")) {
                    signup2Controller signUp2C = loader.getController();
                    signUp2C.setUserInfo(userid,userpassword);
                }

                if (fxmlFile.equals("/view/signup3.fxml")) {
                    signup3Controller signUp3C = loader.getController();
                    signUp3C.setUserInfo(userid,userpassword);
                }

                if (fxmlFile.equals("/view/loggedin.fxml")) {
                    loggedinController loggedinC = loader.getController();
                    loggedinC.setUserInfo(userid);
                }


                if (fxmlFile.equals("/view/Home.fxml")) {
                    //DEBUG
                    System.out.println("hpp if entered");
                    HomePageController homeC = loader.getController();
                    homeC.setUserInfo(userid);
                }
                if (fxmlFile.equals("/view/ClubView.fxml")) {
                    //DEBUG

                    ClubViewController clubC = loader.getController();
                    clubC.setUserInfo(userid);
                }



//                // If switching to sign-up, pre-fill the username
//                if (fxmlFile.equals("/view/signup.fxml")) {
//                    signupController signUpC = loader.getController();
//                    signUpC.prefillUsername(Common.SessionManager.getUsername());
//                }

//                // If switching to login, pre-fill the username (by clicking from signup -> login)
//                if (fxmlFile.equals("/view/login.fxml")) {
//                    loginController loginC = loader.getController();
//                    loginController.setUserInfo(username);
//                }

//                loginController controller = loader.getController();
//                if (controller != null) {
//                    controller.setUserInfo(username);
//                }


            } catch (IOException e) {
                System.out.println("Error loading FXML case1 : " + e.getMessage());
                e.printStackTrace();
                //throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else // if user is switching with no info entered => Load the next FXML file
        {
            try {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            } catch (IOException e) {
                System.out.println("Error loading FXML case2 : " + e.getMessage());
                e.printStackTrace();
            }
        }

        //retrieve the Stage (window) that contains the button that was clicked.
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 1238, 700));
        stage.show();
    }

    public static int signUpUser (ActionEvent event, String username, String useremail, String userpassword, String userpasswordC) {

        //declare DB connection
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        boolean invalidEmail = false;
        boolean notMatching = false;

        try {
            //connect with DB
             connection = DriverManager.getConnection(CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            //DEBUG
            System.out.println("DB connected successfully!");

            //query DB to check if user exists or username used
            psCheckUserExists = connection.prepareStatement(
                    "SELECT * FROM "+ CommonConstants.DB_USERS_TABLE_NAME +
                            " WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            //if user exists
            if (resultSet.isBeforeFirst()) {
                System.out.println("user exists, cant use this username");
                showCustomAlert("This username is already taken. Please choose another one.");
            }
            else //username not used:

                //if invalid email
                if (!Common.isValidEmail(useremail)) {
                    System.out.println("Invalid email");
                    showCustomAlert("Invalid Email format! Please enter a valid Email.");
                    invalidEmail = true;
                }

                //if pw matching
                if (!Common.isPasswordMatching(userpassword, userpasswordC)) {
                    System.out.println("pw != pwC");
                    showCustomAlert("Passwords do not match! Please re-enter.");
                    notMatching = true;
                }

                if (!invalidEmail && !notMatching) //if valid email & the password = the confirmation password
                {
                    //quire DB to insert user
                    psInsert = connection.prepareStatement(
                            "INSERT INTO " + CommonConstants.DB_USERS_TABLE_NAME +
                                    " (username, email, `user_password`) VALUES (?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
                    psInsert.setString(1, username);
                    psInsert.setString(2, useremail);
                    psInsert.setString(3, userpassword);
                    psInsert.executeUpdate();

                    ResultSet generatedKeys = psInsert.getGeneratedKeys();
                    int userId = -1;
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1); // Get the auto-generated ID
                        System.out.println("Inserted user ID: " + userId);
                    }

                    System.out.println("new user inserted");


                    //changeScene
                    changeScene(event, "/view/signup2.fxml", userId, userpassword);
                    return userId;
                }


        } catch (SQLException e) {
            e.printStackTrace();


            //free our DB resources in final block (cuz it executes no matter what)
        } finally {
            //follow order resultset / ps / connection

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (psCheckUserExists != null) {
                try {
                    psCheckUserExists.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(psInsert != null ){
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection!= null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
return -1;
    }


    public static int finishSignup(ActionEvent event, int userId, String username, String userpassword,
                                   String userdateofbirth, String userphonenmbr, String userbio) {

        Connection connection = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psChecknmbrExists = null;
        ResultSet resultSet = null;

        boolean invalidDate = false;
        boolean phoneUsed = false;
        boolean invalidPhonenmbr = false;

        try {
            connection = DriverManager.getConnection(CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            System.out.println("DB connected successfully to finish signup");

            // Check if phone number is already used (by another user)
            psChecknmbrExists = connection.prepareStatement(
                    "SELECT * FROM " + CommonConstants.DB_USERS_TABLE_NAME +
                            " WHERE userphonenmbr = ? AND id != ?");
            psChecknmbrExists.setString(1, userphonenmbr);
            psChecknmbrExists.setInt(2, userId);
            resultSet = psChecknmbrExists.executeQuery();

            if (resultSet.isBeforeFirst()) {
                System.out.println("Phone number already used.");
                showCustomAlert("This phone number is already in use. Please enter a different one.");
                phoneUsed = true;
            }

            if (!Common.isValidDate(userdateofbirth)) {
                System.out.println("Invalid date");
                showCustomAlert("Invalid date format! Please enter a valid date (YYYY-MM-DD).");
                invalidDate = true;
            }

            if (!Common.isValidPhoneNumber(userphonenmbr)) {
                System.out.println("Invalid phone number");
                showCustomAlert("Invalid phone number! It must be 10 digits and start with 0.");
                invalidPhonenmbr = true;
            }

            if (!phoneUsed && !invalidDate && !invalidPhonenmbr) {
                psUpdate = connection.prepareStatement(
                        "UPDATE " + CommonConstants.DB_USERS_TABLE_NAME +
                                " SET user_dob = ?, userphonenmbr = ? WHERE id = ?");
                psUpdate.setString(1, userdateofbirth);
                psUpdate.setString(2, userphonenmbr);
                psUpdate.setInt(3, userId);

                psUpdate.executeUpdate();

                System.out.println("User profile updated with additional info.");

                // Move to Home page
                changeScene(event, "/view/Home.fxml", userId, userpassword);
                System.out.println("Switching to homepage");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (resultSet != null) resultSet.close(); } catch (SQLException ignored) {}
            try { if (psChecknmbrExists != null) psChecknmbrExists.close(); } catch (SQLException ignored) {}
            try { if (psUpdate != null) psUpdate.close(); } catch (SQLException ignored) {}
            try { if (connection != null) connection.close(); } catch (SQLException ignored) {}
        }

        return userId;
    }




    //login from signup
    public static void  prefilledLogin(ActionEvent event, int userid, String username, String userpassword) throws IOException {
        //changeScene
        System.out.println("we are switich prefilledLogin ");
        changeFillScene(event, "/view/login.fxml",userid, username, userpassword);
    }

    //signup from login
    public static void prefilledSignup(ActionEvent event, int userid, String username, String userpassword) throws IOException {
        //changeScene
        changeFillScene(event, "/view/signup.fxml", userid, username, userpassword);
    }



    public static void changeFillScene(Event event, String fxmlFile, int userid, String username, String userpassword) throws IOException {
        FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
        Parent root = loader.load();

        // Handle login
        if (fxmlFile.equals("/view/login.fxml")) {
            loginController loginC = loader.getController();
            //loginC.setUserInfo(userid, username, userpassword);

            // Handle signup
        } else if (fxmlFile.equals("/view/signup.fxml")) {
            signupController signupC = loader.getController();
            signupC.setUserInfo(userid, username, userpassword);
        }

        // Change the scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    public static int loginUser(ActionEvent event, String username, String userpassword) throws SQLException {
        Connection connection = null;
        PreparedStatement checkUserExists = null;
        ResultSet resultSet = null;

        int userId = -1; // Default if login fails

        try {
            connection = DriverManager.getConnection(CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            System.out.println("DB connected!");

            checkUserExists = connection.prepareStatement(
                    "SELECT * FROM " + CommonConstants.DB_USERS_TABLE_NAME + " WHERE username = ?");
            checkUserExists.setString(1, username);
            resultSet = checkUserExists.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("USER NOT FOUND, provided credentials are incorrect!");
                showCustomAlert("Provided credentials are incorrect.");
            } else {
                while (resultSet.next()) {
                    String retrieveduserpassword = resultSet.getString("user_password");

                    if (retrieveduserpassword.equals(userpassword)) {
                        userId = resultSet.getInt(1);

                        System.out.println("Correct password, moving to home.fxml");

                    } else {
                        System.out.println("Incorrect password. Please try again.");
                        showCustomAlert("Incorrect password. Please try again.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (resultSet != null) resultSet.close(); } catch (SQLException ignored) {}
            try { if (checkUserExists != null) checkUserExists.close(); } catch (SQLException ignored) {}
            try { if (connection != null) connection.close(); } catch (SQLException ignored) {}
        }

        return userId; // return -1 if login failed, or the actual ID if success
    }


    public static List<BookModel> fetchLikedBooks(int user_id) throws SQLException {
        List<BookModel> books = new ArrayList<>();

        // Declare DB connection
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Connect
            connection = DriverManager.getConnection(
                    CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME,
                    CommonConstants.DB_PASSWORD
            );

            System.out.println("DB connected!");

            //query
            String query = "SELECT b.*, a.author_first_name, a.author_last_name " +
                    "FROM " + CommonConstants.DB_BOOKS_TABLE_NAME + " b " +
                    "JOIN " + CommonConstants.DB_LIKED_BOOKS_TABLE_NAME +
                        " lb ON b.book_id = lb.book_id " +
                    "JOIN " + CommonConstants.DB_WRITTEN_BY_TABLE_NAME +
                        " wb ON wb.book_id = b.book_id " +
                    "JOIN " + CommonConstants.DB_AUTHOR_TABLE_NAME +
                        " a ON a.author_id = wb.author_id " +
                    "WHERE lb.user_id = ?";

            // Prepare statement
            statement = connection.prepareStatement(query);
            statement.setInt(1, user_id);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                byte[] cover_picture = resultSet.getBytes("cover_picture");
                String author_first_name = resultSet.getString("author_first_name");
                String author_last_name = resultSet.getString("author_last_name");
                String description = resultSet.getString("description");
                int page_nbr = resultSet.getInt("page_nbr");

                books.add(new BookModel( bookId,  title,  author_first_name, author_last_name,  description,   page_nbr, cover_picture));

                // Use or store the data as needed
                System.out.println("Liked Book: " + bookId + " - " + title);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
        return books;

    }


    public static List<BookModel> fetchReadLaterBooks(int user_id) throws SQLException {
        List<BookModel> books = new ArrayList<>();

        // Declare DB connection
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Connect
            connection = DriverManager.getConnection(
                    CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME,
                    CommonConstants.DB_PASSWORD
            );

            System.out.println("DB connected!");

            //query
            String query = "SELECT b.*, a.author_first_name, a.author_last_name " +
                    "FROM " + CommonConstants.DB_BOOKS_TABLE_NAME + " b " +
                    "JOIN " + CommonConstants.DB_READ_LATER_BOOKS_TABLE_NAME +
                    " lb ON b.book_id = lb.book_id " +
                    "JOIN " + CommonConstants.DB_WRITTEN_BY_TABLE_NAME +
                    " wb ON wb.book_id = b.book_id " +
                    "JOIN " + CommonConstants.DB_AUTHOR_TABLE_NAME +
                    " a ON a.author_id = wb.author_id " +
                    "WHERE lb.user_id = ?";

            // Prepare statement
            statement = connection.prepareStatement(query);
            statement.setInt(1, user_id);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                byte[] cover_picture = resultSet.getBytes("cover_picture");
                String author_first_name = resultSet.getString("author_first_name");
                String author_last_name = resultSet.getString("author_last_name");
                String description = resultSet.getString("description");
                int page_nbr = resultSet.getInt("page_nbr");

                books.add(new BookModel( bookId,  title,  author_first_name, author_last_name,  description,   page_nbr, cover_picture));

                // Use or store the data as needed
                System.out.println("Liked Book: " + bookId + " - " + title);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
        return books;

    }

    public static List<BookModel> fetchReadBooks(int user_id) throws SQLException {
        List<BookModel> books = new ArrayList<>();

        // Declare DB connection
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Connect
            connection = DriverManager.getConnection(
                    CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME,
                    CommonConstants.DB_PASSWORD
            );

            System.out.println("DB connected!");

            //query
            String query = "SELECT b.*, a.author_first_name, a.author_last_name " +
                    "FROM " + CommonConstants.DB_BOOKS_TABLE_NAME + " b " +
                    "JOIN " + CommonConstants.DB_READ_BOOKS_TABLE_NAME +
                    " lb ON b.book_id = lb.book_id " +
                    "JOIN " + CommonConstants.DB_WRITTEN_BY_TABLE_NAME +
                    " wb ON wb.book_id = b.book_id " +
                    "JOIN " + CommonConstants.DB_AUTHOR_TABLE_NAME +
                    " a ON a.author_id = wb.author_id " +
                    "WHERE lb.user_id = ?";

            // Prepare statement
            statement = connection.prepareStatement(query);
            statement.setInt(1, user_id);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                byte[] cover_picture = resultSet.getBytes("cover_picture");
                String author_first_name = resultSet.getString("author_first_name");
                String author_last_name = resultSet.getString("author_last_name");
                String description = resultSet.getString("description");
                int page_nbr = resultSet.getInt("page_nbr");

                books.add(new BookModel( bookId,  title,  author_first_name, author_last_name,  description,   page_nbr, cover_picture));

                // Use or store the data as needed
                System.out.println("Liked Book: " + bookId + " - " + title);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
        return books;

    }




    // genre & DB communication methods
    @FXML
    public static void toggleGenre(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        int genreId = Integer.parseInt(clickedButton.getUserData().toString());

        if (selectedGenres.contains(genreId)) {
            selectedGenres.remove(Integer.valueOf(genreId));
            //DEBUG
            System.out.println("Deselect genre");
            clickedButton.setStyle(""); // Reset button color
        } else {
            selectedGenres.add(genreId);
            //DEBUG
            System.out.println("select genre");
            clickedButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
    }


    @FXML
    public static void saveUserGenres(int userId) {
        //declare DB connection
        Connection connection = null;
        PreparedStatement psInsert = null;
        ResultSet resultSet = null;

        if (selectedGenres.isEmpty()) {
            //DEBUG
            System.out.println("No genres selected.");
            return;
        }

        //DEBUG
        System.out.println(" genres are saving");

        try {
        //connect with DB
        connection = DriverManager.getConnection(CommonConstants.DB_URL,
                CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

        //DEBUG
        System.out.println("DB connected successfully while genre selection");

        //query DB to update user genres
        String query = "INSERT INTO " +
                "user_genres"+
                " (user_id, genre_id) " +
                "VALUES (?, ?)";
        psInsert= connection.prepareStatement(query);
            for (int genreId : selectedGenres)
            {
                psInsert.setInt(1, userId);
                psInsert.setInt(2, genreId);
                psInsert.addBatch();
            }

            psInsert.executeBatch();
            //DEBUG
            System.out.println("Genres saved for user: " + userId);

        } catch (SQLException e) {
        e.printStackTrace(); }
    }


    public static List<MessageModel> loadLast10Messages(int forumId, int clubId) {
        List<MessageModel> messages = new ArrayList<>();

        String query = "SELECT u.username, m.message_content, m.created_at, m.user_id " +
                "FROM messages m " +
                "JOIN users u ON m.user_id = u.user_id " +
                "JOIN forums f ON m.forum_id = f.forum_id " +
                "WHERE m.forum_id = ? AND f.club_id = ? " +
                "ORDER BY m.created_at DESC LIMIT 20";

        try (
                Connection connection = DriverManager.getConnection(
                        CommonConstants.DB_URL,
                        CommonConstants.DB_USERNAME,
                        CommonConstants.DB_PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setInt(1, forumId);
            ps.setInt(2, clubId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String content = rs.getString("message_content");
                Timestamp time = rs.getTimestamp("created_at");

                messages.add(new MessageModel(userId, username, content, time));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }








    //not used but i might need it later:
    public static boolean checkUser(String username){
        try{
            Connection connection = DriverManager.getConnection(CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            //DEBUG
            System.out.println("DB connected successfully!");

            PreparedStatement checkUserExists = connection.prepareStatement(
                    "SELECT * FROM " + CommonConstants.DB_USERS_TABLE_NAME +
                            " WHERE USERNAME = ?"
            );
            checkUserExists.setString(1, username);

            ResultSet resultSet = checkUserExists.executeQuery();

            // check to see if the result set is empty  (i.e user does not exist)
            if(!resultSet.isBeforeFirst()){  //isBeforeFirst=null means resultSet is empty

                System.out.println("USER NOT FOUND ");
                return false;

            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        System.out.println("USER FOUND !");
        return true;
    }




    public static void sendMessage(int userId, int forumId, String msgContent, int clubid) {

        Connection connection = null;

        try {
            // Connect to DB
            connection = DriverManager.getConnection(
                    CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME,
                    CommonConstants.DB_PASSWORD
            );
            System.out.println("DB connected!");

            // 0. Validate that the forum belongs to the specified club
            String validateForumClub = "SELECT 1 FROM forums WHERE forum_id = ? AND club_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(validateForumClub)) {
                ps.setInt(1, forumId);
                ps.setInt(2, clubid);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new RuntimeException("Forum does not belong to the specified club.");
                }
            }

            // 1. Insert message
            String insertMsg = "INSERT INTO messages (user_id, forum_id, message_content) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(insertMsg)) {
                ps.setInt(1, userId);
                ps.setInt(2, forumId);
                ps.setString(3, msgContent);
                ps.executeUpdate();
            }
            System.out.println("The message is saved: " + msgContent);

            // 2. Insert notification for that club
            String notifMsg = "New message in forum: " + msgContent;
            int notifId = -1;
            String insertNotif = "INSERT INTO notifications (club_id, content, type) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(insertNotif, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, clubid);
                ps.setString(2, notifMsg);
                ps.setString(3, "msg sent");
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) notifId = rs.getInt(1);
            }
            System.out.println("Message added in forums, notification: " + notifMsg);

            // 3. Notify all users who are members of that club
            String getUsers = "SELECT user_id FROM members WHERE club_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(getUsers)) {
                ps.setInt(1, clubid);
                ResultSet rs = ps.executeQuery();

                String insertNotified = "INSERT INTO notified (notification_id, user_id) VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertNotified)) {
                    int count = 0;
                    while (rs.next()) {
                        int uId = rs.getInt("user_id");
                        insertStmt.setInt(1, notifId);
                        insertStmt.setInt(2, uId);
                        insertStmt.executeUpdate();
                        count++;
                    }
                    System.out.println("Total users notified: " + count);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // You might want to do rollback or other error handling here depending on your connection management
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }






}





