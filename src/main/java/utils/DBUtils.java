//communicate with DB :
// sign user up / log user in
// change scenes
//Validation Methods
//Date Formatting
//File Handling ??
//Logging Methods (for debugging)
//Common Constants (e.g., predefined messages, max book count)
package utils;

import controller.loggedinController;
import controller.loginController;
import controller.signupController;
import javafx.animation.ScaleTransition;
import javafx.event.Event;
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
import model.Common;
import model.CommonConstants;

import java.io.IOException;
import java.sql.*;

public class DBUtils {

    //ALERT
    public static double xOffset = 0;
    public static double yOffset = 0;

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


    public static void changeScene(Event event, String fxmlFile, String username, String password ) {
        Parent root = null;


        // Store username for next window
        if (username != null) {
            Common.SessionManager.setUsername(username);
        }

        // if user is switching with info entered => Load the next FXML file with valid info
        if (username != null && password!= null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();  //loads the UI and assigns it to root


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


    public static void signUpUser (ActionEvent event, String username, String useremail, String password, String passwordC) {

        //declare DB connection
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;



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

            } else //username not used:
            {
                //quire DB to insert user
                psInsert = connection.prepareStatement("INSERT INTO users (username, useremail,  `password`) VALUES (?,?,?)");
                psInsert.setString(1, username);
                psInsert.setString(2, useremail);
                psInsert.setString(3, password);
                psInsert.executeUpdate();

                System.out.println("new user inserted");

                //changeScene
                changeScene(event, "/view/signup2.fxml", username, password);
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


    public static void loginUser(ActionEvent event, String username, String password) throws SQLException {
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
                  String retrievedPassword = resultSet.getString("password");
                  String retrievedemail = resultSet.getString("useremail");

                  if(retrievedPassword.equals(password)) {
                      System.out.println("correct pw, moving to loggedin.fxml ");
                      changeScene(event, "/view/loggedin.fxml", username, password );
                  } else {
                      System.out.println("Incorrect password. Please try again.");
                      showCustomAlert("Incorrect password. Please try again.");
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



    //not used but i might need it later
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


//// If switching to sign-up, pre-fill the username
//                if (fxmlFile.equals("/view/signup.fxml")) {
//        signupController signUpC = loader.getController();
//        signUpC.prefillUsername(Common.SessionManager.getUsername());
//    }
//
//    // If switching to login, pre-fill the username (by clicking from signup -> login)
//                if (fxmlFile.equals("/view/login.fxml")) {
//        loginController loginC = loader.getController();
//        loginController.setUserInfo(username);
//    }


}





