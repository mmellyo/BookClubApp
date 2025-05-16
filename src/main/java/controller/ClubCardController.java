package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class ClubCardController {

    @FXML
    private Label clubName;
    @FXML
    private Label numberOfMembers;
    @FXML
    private Circle coverImage;

    public void setClubCard (String name, String number, Image image)
    {
        clubName.setText(name);
        numberOfMembers.setText(number);
        coverImage.setFill(new ImagePattern(image));
    }
    public void goToClub(MouseEvent mouseEvent) {

    }
}
