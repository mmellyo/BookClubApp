package controller;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import utils.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class loggedinController implements Initializable {

    @FXML
    private Button button_enterapp;
    @FXML
    private Button button_logout;
    @FXML
    private Label label_welcome;
    @FXML
    private Label label_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "/view/login.fxml", null , null);
            }
        });
    }

    public void setUserInfo ( String username , String password ) {
        label_welcome.setText("welcome " + username);
        label_password.setText("your pw is" + password );
    }

}
