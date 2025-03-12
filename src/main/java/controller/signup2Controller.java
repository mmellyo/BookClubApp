package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.DBUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class signup2Controller implements Initializable {
    @FXML
    private ImageView image_previousPage2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //create Tooltip
        Tooltip tooltip = new Tooltip("Go to previous page");
        tooltip.setShowDuration(Duration.seconds(10)); // Show for 10 seconds
        tooltip.setShowDelay(Duration.ZERO); // No delay before showing
        tooltip.setStyle("-fx-background-color:  #d6bc99; -fx-text-fill:  #4a2f1e;");
        //install
        Tooltip.install(image_previousPage2, tooltip);

        // handle click
        image_previousPage2.setOnMouseClicked(event ->
                DBUtils.changeScene(event,"/view/signup.fxml", null, null )
        );
    }
}
