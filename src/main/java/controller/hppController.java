package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class hppController implements Initializable {
    @FXML
    private Label label_welcome;
    private String userpassword;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setUserInfo(String username, String userpassword) {
        label_welcome.setText("welocome "+ username);
        this.userpassword = userpassword;
    }
}
