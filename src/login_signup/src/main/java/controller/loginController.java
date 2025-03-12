package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    private TextField tf_password;
    @FXML
    private TextField tf_username;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    DBUtils.loginUser(event, tf_username.getText(), tf_password.getText());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        button_signup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event,"/view/signup.fxml", null, null );
            }
        });

        button_signup_up.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event,"/view/signup.fxml", null, null);
            }
        });
    }
}
