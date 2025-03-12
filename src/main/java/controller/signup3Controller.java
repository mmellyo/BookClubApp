package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    }
}
