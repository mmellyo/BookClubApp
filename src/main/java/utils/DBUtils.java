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
import model.CommonConstants;
import model.Common;


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


    public static void changeScene(Event event, String fxmlFile, String username, String userpassword ) {
        Parent root = null;


        // if user is switching with info entered => Load the next FXML file with valid info
        if (username != null && userpassword!= null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();  //loads the UI and assigns it to root

                if (fxmlFile.equals("/view/login.fxml")) {
                    loginController loginC = loader.getController();
                    loginC.setUserInfo(username, userpassword);
                }

                if (fxmlFile.equals("/view/signup.fxml")) {
                    signupController signUpC = loader.getController();
                    signUpC.setUserInfo(username, userpassword);
                }

                if (fxmlFile.equals("/view/signup2.fxml")) {
                    signup2Controller signUp2C = loader.getController();
                    signUp2C.setUserInfo(username,userpassword);
                }

                if (fxmlFile.equals("/view/signup3.fxml")) {
                    signup3Controller signUp3C = loader.getController();
                    signUp3C.setUserInfo(username,userpassword);
                }

                if (fxmlFile.equals("/view/loggedin.fxml")) {
                    loggedinController loggedinC = loader.getController();
                    loggedinC.setUsername(username);
                }


                if (fxmlFile.equals("/view/Home.fxml")) {
                    //DEBUG
                    System.out.println("hpp if entered");
                    HomeController homeC = loader.getController();
                    homeC.setUserInfo(username,userpassword);
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

    public static void signUpUser (ActionEvent event, String username, String useremail, String userpassword, String userpasswordC) {

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

            //quire DB to check if username = username...
            psCheckUserExists = connection.prepareStatement("SELECT * FROM "+ CommonConstants.DB_USERS_TABLE_NAME + " WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            //the check
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

                if (!invalidEmail && !notMatching) //if valid email & pw = pwC
                {
                    //quire DB to insert user
                    psInsert = connection.prepareStatement("INSERT INTO users (username, useremail,  `userpassword`) VALUES (?,?,?)");
                    psInsert.setString(1, username);
                    psInsert.setString(2, useremail);
                    psInsert.setString(3, userpassword);
                    psInsert.executeUpdate();

                    System.out.println("new user inserted");


                    //changeScene
                    changeScene(event, "/view/signup2.fxml", username, userpassword);
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
    }


    public static void finishSignup (ActionEvent event,String username, String userpassword, String userdateofbirth, String userphonenmbr, String userbio) {

        //declare DB connection
        Connection connection = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psChecknmbrExists = null;
        ResultSet resultSet = null;

        boolean invalidDate = false;
        boolean phoneUsed = false;
        boolean invalidPhonenmbr= false;


        try {
            //connect with DB
            connection = DriverManager.getConnection(CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            //DEBUG
            System.out.println("DB connected successfully to finish signup");

            //quire DB to check if phonenmbr = phonenmbr...
            psChecknmbrExists = connection.prepareStatement("SELECT * FROM "+ CommonConstants.DB_USERS_TABLE_NAME + " WHERE userphonenmbr = ?");
            psChecknmbrExists.setString(1, userphonenmbr);
            resultSet = psChecknmbrExists.executeQuery();

            // verifications
            //if nmbr exists
            if (resultSet.isBeforeFirst()) {
                //DEBUG
                System.out.println("phonenmbr exists, cant use this phonenmbr");
                showCustomAlert("This phone number is already in use. Please enter a different one.");
                phoneUsed = true;
            }

            //if invalid date
            if(!Common.isValidDate(userdateofbirth)) {
                System.out.println("Invalid date");
                showCustomAlert("Invalid date format! Please enter a valid date (YYYY-MM-DD).");
                invalidDate = true;
            }

            //if invalid phonenmbr
            if(!Common.isValidPhoneNumber(userphonenmbr)) {
                System.out.println("Invalid nmbr");
                showCustomAlert("Invalid phone number! It must be 10 digits and start with 0.");
                invalidPhonenmbr = true;
            }

            //if nmbr not used + valid date + valid phoennmbr:
            if (!phoneUsed && !invalidDate && !invalidPhonenmbr)
            {
                    //quire DB to update additional info
                    psUpdate = connection.prepareStatement("UPDATE users SET userdateofbirth = ?, userphonenmbr = ?, userbio = ? WHERE username = ?");
                    psUpdate.setString(1, userdateofbirth);
                    psUpdate.setString(2, userphonenmbr);
                    psUpdate.setString(3, userbio);
                    psUpdate.setString(4, username);

                    psUpdate.executeUpdate();

                //DEBUG
                System.out.println("new additional info inserted");

                //changeScene to hp
                changeScene(event, "/view/Home.fxml", username, userpassword);

                //DEBUG
                System.out.println("we are switcihng to hp");
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

            if (psChecknmbrExists != null) {
                try {
                    psChecknmbrExists.close();
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
    }




    //login from signup
    public static void prefilledLogin(ActionEvent event, String username, String userpassword) {
        //changeScene
        changeScene(event, "/view/login.fxml", username, userpassword);
    }

    //signup from login
    public static void prefilledSignup(ActionEvent event, String username, String userpassword) {
        //changeScene
        changeScene(event, "/view/signup.fxml", username, userpassword);
    }





    public static void loginUser(ActionEvent event, String username, String userpassword) throws SQLException {
        //declare DB connection
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            //connect with DB
             connection = DriverManager.getConnection(CommonConstants.DB_URL,
                    CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            //DEBUG
            System.out.println("DB connected !");

            //quire DB to check if username = username...
            //spot the username in the DB, compare PWs
            PreparedStatement checkUserExists = connection.prepareStatement(
                    "SELECT * FROM " + CommonConstants.DB_USERS_TABLE_NAME +
                            " WHERE USERNAME = ?"
            );
            checkUserExists.setString(1, username);

            resultSet = checkUserExists.executeQuery();


            // check to see if the resultset is empty  (i.e user does not exist)

            if(!resultSet.isBeforeFirst()){  //isBeforeFirst=null means resultSet is empty means user doesnt exist
                System.out.println("USER NOT FOUND, provided credentials are incorrect!");
                showCustomAlert("provided credentials are incorrect.");

            } else { //if username already exists; is PW = PW in DB?

                while (resultSet.next()){
                  String retrieveduserpassword = resultSet.getString("userpassword");
                  String retrievedemail = resultSet.getString("useremail");

                  if(retrieveduserpassword.equals(userpassword)) {
                      System.out.println("correct pw, moving to loggedin.fxml ");
                      changeScene(event, "/view/loggedin.fxml", username, userpassword );
                  } else {
                      System.out.println("Incorrect userpassword. Please try again.");
                      showCustomAlert("Incorrect userpassword. Please try again.");
                  }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //free DB , order : resultset , ps, connection
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (preparedStatement != null ) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

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
    public static void saveUserGenres(String username) {
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

        //query DB
        String query = "INSERT INTO user_genres (username, genre_id) VALUES (?, ?)";
        psInsert= connection.prepareStatement(query);
            for (int genreId : selectedGenres)
            {
                psInsert.setString(1, username);
                psInsert.setInt(2, genreId);
                psInsert.addBatch();
            }

            psInsert.executeBatch();
            //DEBUG
            System.out.println("Genres saved for user: " + username);

        } catch (SQLException e) {
        e.printStackTrace(); }
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

}





