package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.DBUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class signup2Controller implements Initializable {
    @FXML
    private ImageView image_previousPage2;
    @FXML
    private Button button_next2;

    @FXML
    private Button button_Fantasy;
    @FXML
    private Button button_Romance;
    @FXML
    private Button button_Horror;
    @FXML
    private Button button_History;
    @FXML
    private Button button_Thriller;
    @FXML
    private Button button_HistoricalFiction;
    @FXML
    private Button button_Mystery;
    @FXML
    private Button button_Business;
    @FXML
    private Button button_ReligiousSpiritual;
    @FXML
    private Button button_Science;
    @FXML
    private Button button_Psychology;
    @FXML
    private Button button_Poetry;
    @FXML
    private Button button_Literature;
    @FXML
    private Button button_SelfHelp;
    @FXML
    private Button button_ScienceFiction;
    @FXML
    private Button button_ai;

    // store selected genres
    private List<Integer> selectedGenres = new ArrayList<>();
    private String username;
    private String userpassword;


    public void setUserInfo(String username , String userpassword ) {
        this.username = username;
        this.userpassword = userpassword;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //create Tooltip
        Tooltip tooltip = new Tooltip("Go to previous page");
        tooltip.setShowDuration(Duration.seconds(10)); // Show for 10 seconds
        tooltip.setShowDelay(Duration.ZERO); // No delay before showing
        tooltip.setStyle("-fx-background-color:  #d6bc99; -fx-text-fill:  #4a2f1e;");
        //install
        Tooltip.install(image_previousPage2, tooltip);

        button_Fantasy.setUserData(1);
        button_Romance.setUserData(2);
        button_Horror.setUserData(3);
        button_History.setUserData(4);
        button_Thriller.setUserData(7);
        button_HistoricalFiction.setUserData(6);
        button_Mystery.setUserData(5);
        button_Business.setUserData(8);
        button_ReligiousSpiritual.setUserData(9);
        button_Science.setUserData(10);
        button_Psychology.setUserData(11);
        button_Poetry.setUserData(12);
        button_Literature.setUserData(13);
        button_SelfHelp.setUserData(14);
        button_ScienceFiction.setUserData(15);
        button_ai.setUserData(16);


        // handle click to previous / next
        image_previousPage2.setOnMouseClicked(event ->
                DBUtils.changeScene(event,"/view/signup.fxml", null, null )

        );
        button_next2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.saveUserGenres(username);
                //DEBUG
                System.out.println("next2 IS clicked, lets save generes for : "+ username);

                DBUtils.changeScene(event,"/view/signup3.fxml",username,userpassword);
            }
        });


        //handle genre buttons
        button_Fantasy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_Romance.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_Horror.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_Thriller.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_History.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_HistoricalFiction.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_Mystery.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_Business.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_ReligiousSpiritual.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_Science.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_Psychology.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_Poetry.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });


        button_Literature.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_SelfHelp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_ScienceFiction.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

        button_ai.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.toggleGenre(event);
            }
        });

    }

}
