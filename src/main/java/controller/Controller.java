package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private Button cancelbutton;
    @FXML
    private Button loginbutton;

    @FXML
    private Label errorlabel;

    @FXML
    private TextField usernamefield;
    @FXML
    private TextField passwordfield;



    @FXML
    // Get the current stage and close it
    public void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelbutton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void loginButtonOnAction(ActionEvent e) {


        if (usernamefield.getText().isBlank() && passwordfield.getText().isBlank()) {
            errorlabel.setText("entre username & password");
        } else {
            errorlabel.setText("you are logging in...");
        }
    }

}