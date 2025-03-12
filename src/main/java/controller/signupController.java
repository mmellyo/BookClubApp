package controller;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static model.Common.SessionManager.username;


public class signupController implements Initializable {

      @FXML
      private Button button_login;
      @FXML
      private Button button_signup;
      @FXML
      private Button button_login_up;

      @FXML
      private TextField tf_username;
      @FXML
      private TextField tf_useremail;
      @FXML
      private TextField tf_password;
      @FXML
      private TextField tf_passwordC;



      @Override
      public void initialize(URL url, ResourceBundle resourceBundle) {
            button_signup.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                       //handle no white space
                       if (!tf_username.getText().trim().isEmpty() &&
                           !tf_useremail.getText().trim().isEmpty() &&
                           !tf_password.getText().trim().isEmpty() &&
                           !tf_passwordC.getText().trim().isEmpty())
                       {
                             DBUtils.signUpUser(event, tf_username.getText(), tf_useremail.getText(), tf_password.getText(), tf_passwordC.getText());
                       } else {
                             System.out.println("please fill in all info to sign up");
                             DBUtils.showCustomAlert("Please fill in all required fields to sign up.");

                       }
                  }
            });

            button_login.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                      DBUtils.prefilledLogin(event,tf_username.getText(),tf_password.getText());
                  }
            });

            button_login_up.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                        DBUtils.prefilledLogin(event,tf_username.getText(),tf_password.getText());
                  }
            });
      }


      public void setUserInfo(String username, String password ) {
            tf_username.setText(username);
            tf_password.setText(password);
      }
}
