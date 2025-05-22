package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    public void setUserInfo(int userId, String username, String userPassword) {
        System.out.println("we are passign data from logged to hp");
        this.userId = userId;
        tf_username.setText(username);           // Use directly from parameter
        tf_userpassword.setText(userPassword);
    }


}
