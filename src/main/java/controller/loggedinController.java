package controller;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import model.User;
import utils.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class loggedinController implements Initializable {

    @FXML
    private Button button_openapp;
    @FXML
    private Button button_logout;
    @FXML
    private Label label_welcome;
    private String username;
    private int userId;

    @FXML


    public void setUserInfo(int userId) {
        User user = new User(userId);
        this.username = user.getUsername();

        label_welcome.setText("Welcome back " + username + ", You're logged in!");
        label_welcome.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label_welcome.setMinWidth(Region.USE_PREF_SIZE);
        this.userId = userId;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "/view/login.fxml", userId , null);
            }
        });

        button_openapp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "/view/Home.fxml", userId , null);

            }
        });
    }


}
