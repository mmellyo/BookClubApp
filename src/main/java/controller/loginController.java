package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.DBUtils;

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
                        DBUtils.loginUser(event, tf_username.getText(), tf_userpassword.getText());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        button_signup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.prefilledSignup(event, tf_username.getText(), tf_userpassword.getText() );
            }
        });

        button_signup_up.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.prefilledSignup(event, tf_username.getText(), tf_userpassword.getText() );
            }
        });
    }

    public  void setUserInfo(String username , String userpassword) {
        tf_username.setText(username);
        tf_userpassword.setText(userpassword);
    }
}
