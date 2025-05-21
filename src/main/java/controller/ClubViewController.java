package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.MessageModel;
import model.User;
import utils.DBUtils;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;

public class ClubViewController {
    @FXML
    private ComboBox<String> ComboBox;

    @FXML
    private VBox chatBox;

    @FXML
    private ScrollPane chatScrollPane; // Add this in FXML to wrap the VBox

    @FXML
    private TextField messageField;

    private int userId = 1;   // Set appropriately
    private int forumId = 1;  // Set appropriately

    @FXML
    public void initialize() {
        ComboBox.getItems().addAll("Option 1", "Option 2", "Option 3");
        loadChatHistory();
    }
    private byte[] loadUserProfilePicture(int userId) {
        User user = new User(userId);
        return user.getPfp();
    }

    @FXML
    public void handleSendMessage() {
        String msg = messageField.getText().trim();
        if (msg.isEmpty()) return;

        DBUtils.sendMessage(userId, forumId, msg);
        messageField.clear();
        loadChatHistory();
    }

    private void loadChatHistory() {
        chatBox.getChildren().clear();
        List<MessageModel> messages = DBUtils.loadLast10Messages(forumId);

        int previousUserId = -1;

        for (int i = messages.size() - 1; i >= 0; i--) {
            MessageModel msg = messages.get(i);
            boolean isCurrentUser = msg.getUserId() == userId;


            //Show username if user changed
            if (msg.getUserId() != previousUserId) {
                Label usernameLabel = new Label(isCurrentUser ? "You" : msg.getUsername());
                usernameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #888888; -fx-font-size: 13px;");
                HBox userContainer = new HBox(usernameLabel);
                userContainer.setPadding(new Insets(10, 0, 0, 50));
                userContainer.setAlignment(isCurrentUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                chatBox.getChildren().add(userContainer);
            }


            //Message and timestamp in VBox
            VBox messageGroup = new VBox();
            messageGroup.setSpacing(2);

            Label messageLabel = new Label(msg.getContent());
            messageLabel.setWrapText(true);
            messageLabel.setPadding(new Insets(10));
            messageLabel.setStyle("-fx-background-color: " + (isCurrentUser ? "#d6cdb9" : "#ffffff") + ";" +
                    "-fx-background-radius: 10; -fx-font-size: 14px;");
            messageLabel.setMaxWidth(300);

            Label timeLabel = new Label(msg.getFormattedTime());
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999;");
            timeLabel.setPadding(new Insets(0, 5, 0, 5));
            timeLabel.setAlignment(Pos.CENTER_RIGHT);

            messageGroup.getChildren().addAll(messageLabel, timeLabel);


            //PFP
            byte[] imageBytes = loadUserProfilePicture(msg.getUserId());
            Circle profileCircle = new Circle(20);  // bigger circle for better quality

            if (imageBytes != null && imageBytes.length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                Image pfp = new Image(bis, 40, 40, true, true);  // specify width, height, preserveRatio, smooth
                profileCircle.setFill(new ImagePattern(pfp));
            } else {
                URL imageUrl = getClass().getResource("/view/img/pp.png");
                if (imageUrl != null) {
                    Image fallback = new Image(imageUrl.toExternalForm(), 40, 40, true, true);
                    profileCircle.setFill(new ImagePattern(fallback));
                } else {
                    System.out.println("Default image not found.");
                }
            }

            VBox pfpContainer = new VBox(profileCircle);
            pfpContainer.setPadding(new Insets(0, 0, 5, 0));

            HBox messageContainer = new HBox(10);
            messageContainer.setPadding(new Insets(2, 0, 10, 0));
            messageContainer.setAlignment(isCurrentUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            if (isCurrentUser) {
                messageContainer.getChildren().addAll(messageGroup);
            } else {
                messageContainer.getChildren().addAll(pfpContainer, messageGroup);
               // HBox.setMargin(profileCircle, new Insets(-4, 0, 0, 0)); // shift image 4px upwards
            }


            chatBox.getChildren().add(messageContainer);
            previousUserId = msg.getUserId();
        }

        // Scroll to bottom
        chatBox.heightProperty().addListener((obs, oldVal, newVal) -> chatScrollPane.setVvalue(1.0));
    }

}


