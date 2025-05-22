package controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.User;
import utils.DBUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class loginController implements Initializable {

    @FXML
    private Button button_login;
    @FXML
    private Button button_signup;
    @FXML
    private Button button_signup_up;

    @FXML
    private TextField tf_userpassword;
    @FXML
    private TextField tf_username;
    private int userId;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //handle no white space
                if (tf_username.getText().trim().isEmpty() ||
                    tf_userpassword.getText().trim().isEmpty() )
                {
                    System.out.println("please fill in all info to login");
                    DBUtils.showCustomAlert("Please fill in all required fields to log in.");
                } else {
                    try {
                         userId = DBUtils.loginUser(event, tf_username.getText(), tf_userpassword.getText());
                         System.out.println("user_id in login: "+ userId);
                         switchToHomePage(userId, event);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        button_signup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    DBUtils.prefilledSignup(event,userId, tf_username.getText(), tf_userpassword.getText() );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        button_signup_up.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    DBUtils.prefilledSignup(event,userId, tf_username.getText(), tf_userpassword.getText() );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void switchToHomePage(int userId, Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Home.fxml"));
            Parent root = loader.load();

            HomePageController controller = loader.getController();
            System.out.println("user_id in switching: " + userId);
            controller.setUserInfo(userId); // This should now work

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (the one that triggered the event)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserInfo(int userId, String username, String userPassword) {
        System.out.println("we are passign data from logged to hp");
        this.userId = userId;
        tf_username.setText(username);           // Use directly from parameter
        tf_userpassword.setText(userPassword);
    }


}
