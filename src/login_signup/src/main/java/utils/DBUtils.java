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
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;

public class DBUtils {

    //ALERT
    public static double xOffset = 0;
    public static double yOffset = 0;

    public static void showCustomAlert(String DisplayAlert) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED); // Removes the title bar
        stage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with other windows

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f5ebc7; -fx-border-color: #f5ebc7; -fx-border-width: 2;");

        Label label = new Label(DisplayAlert);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.DARKRED);

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

        vbox.getChildren().addAll(label, closeButton);

        Scene scene = new Scene(vbox, 400, 150);
        stage.setScene(scene);
        stage.showAndWait(); // Waits for the user to close the alert before returning
    }

    //changeScene
    public static void changeScene(Event event, String fxmlFile, String username, String password ) {
        Parent root = null;

        // if user is switching with info entered => Load the next FXML file with valid info
        if (username != null && password!= null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();  //loads the UI and assigns it to root
                loggedinController loggedinC  = loader.getController();
                loggedinC.setUserInfo(username,password);


            } catch (IOException e) {
                System.out.println("Error loading FXML case1 : " + e.getMessage());

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
    

    public static void signUpUser (ActionEvent event, String username, String useremail, String password, String passwordC) {

        //declare DB connection
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;



        try {
            //connect with DB
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookclubdb", "root", "Mellybookclub!");

            //quire DB to check if username = username...
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            //the check
            if (resultSet.isBeforeFirst()) {             //isbeforefirst = isEmpty
                System.out.println("user exists");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("u cant use this username");
                alert.show();

            } else //username not used:
            {
                //quire DB to insert user
                psInsert = connection.prepareStatement("INSERT INTO users (username, useremail,  `password `) VALUES (?,?,?)");
                psInsert.setString(1, username);
                psInsert.setString(2, useremail);
                psInsert.setString(3, password);
                psInsert.executeUpdate();


                //changeScene : loggedin
                changeScene(event, "/view/loggedin.fxml", username, password);
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
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/bookclubdb", "root", "Mellybookclub!");

            //quire DB to check if username = username...
            //spot the username in the DB, compare PWs
            preparedStatement = connection.prepareStatement("SELECT password, useremail FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            //check if username exists
            if(!resultSet.isBeforeFirst()) {
                System.out.println("usernotfounf in DB");
                Alert alert= new Alert(Alert.AlertType.ERROR);
                alert.setContentText("provided credentials are incorrect!");
                alert.show();

            } else { //if user exists; is PW = PW in DB?

                while (resultSet.next()){
                  String retrievedPassword = resultSet.getString("password");
                  String retrievedemail = resultSet.getString("useremail");
                  if(retrievedPassword.equals(password)) {
                      changeScene(event, "/view/loggedin.fxml", username, password );
                  } else {
                      System.out.println("password didnt macth");
                      Alert alert = new Alert(Alert.AlertType.ERROR);
                      alert.setContentText("the provided credentials are incirrect");
                      alert.show();
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

    }}
