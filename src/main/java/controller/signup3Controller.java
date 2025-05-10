package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import utils.DBUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class signup3Controller implements Initializable {

    @FXML
    private ImageView image_previousPage3;
    @FXML
    private Button button_FinishSignup;
    @FXML
    private Button button_skip;

    @FXML
    private TextField tf_userdateofbirth;
    @FXML
    private TextField tf_userbio;
    @FXML
    private TextField tf_userphonenmbr;

    private String userpassword;
    private String username;




    public void setUserInfo(String username, String userpassword) {
        this.username = username;
        this.userpassword = userpassword;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //create
        Tooltip tooltip = new Tooltip("Go to previous page");
        tooltip.setShowDuration(Duration.seconds(10)); // Show for 10 seconds
        tooltip.setShowDelay(Duration.ZERO); // No delay before showing
        tooltip.setStyle("-fx-background-color:  #d6bc99; -fx-text-fill:  #4a2f1e;");
        //install tootip once not at each click
        Tooltip.install(image_previousPage3, tooltip);


        //hndle mouseclick
        image_previousPage3.setOnMouseClicked(event ->
                DBUtils.changeScene(event,"/view/signup2.fxml",null, null )
        );



        button_FinishSignup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //DEBUG
                System.out.println("you will finished");

                DBUtils.finishSignup(event, username, userpassword, tf_userdateofbirth.getText(),tf_userphonenmbr.getText() , tf_userbio.getText());

            }
        });

        button_skip.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("SKIP!!!! we are switcihng to hp");
                DBUtils.changeScene(event,"/view/Home.fxml",username, userpassword );
            }
        });

    }


}
