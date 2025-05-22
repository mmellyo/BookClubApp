package controller;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import utils.DBUtils;

import java.io.IOException;
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
      private TextField tf_userpassword;
      @FXML
      private TextField tf_userpasswordC;
      private int userId;


      @Override
      public void initialize(URL url, ResourceBundle resourceBundle) {
            button_signup.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                       //handle no white space
                       if (!tf_username.getText().trim().isEmpty() &&
                           !tf_useremail.getText().trim().isEmpty() &&
                           !tf_userpassword.getText().trim().isEmpty() &&
                           !tf_userpasswordC.getText().trim().isEmpty())
                       {
                             userId= DBUtils.signUpUser(event, tf_username.getText(), tf_useremail.getText(), tf_userpassword.getText(), tf_userpasswordC.getText());
                       } else {
                             System.out.println("please fill in all info to sign up");
                             DBUtils.showCustomAlert("Please fill in all required fields to sign up.");

                       }
                  }
            });

            button_login.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                      try {
                          DBUtils.prefilledLogin(event,userId, tf_username.getText(),tf_userpassword.getText());
                      } catch (IOException e) {
                          throw new RuntimeException(e);
                      }
                  }
            });

            button_login_up.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                      try {
                          DBUtils.prefilledLogin(event,userId, tf_username.getText(),tf_userpassword.getText());
                      } catch (IOException e) {
                          throw new RuntimeException(e);
                      }
                  }
            });
      }



      public void setUserInfo(int userid, String username, String userpassword) {
            // Pre-fill fields here
            tf_username.setText(username);
            tf_userpassword.setText(userpassword);
            this.userId = userid;
      }

}
