package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.*;
import utils.DBUtils;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClubViewController {

    private int forumId;
    private int userId = 2;
    private int clubId ;
    private int adminId=1;

    @FXML
    private VBox chatSideBar;
    @FXML
    private VBox ForumsVbox;
    @FXML
    private Label clubNameLabel;
    @FXML
    private ImageView clubCoverPicture;
    @FXML
    private ComboBox<String> ComboBox;
    @FXML
    private TextField messageField;
    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private VBox chatContainer;
    private VBox generalChatBox = new VBox();
    private VBox announcementChatBox = new VBox();
    private VBox recommendationChatBox = new VBox();
    private VBox quotesChatBox = new VBox();


    @FXML
    public void initialize() throws SQLException {

        //load all clubs
        loadClubs();

        ComboBox.getItems().addAll("Option 1", "Option 2", "Option 3");
    }


    private void handleChat(int forumId) {
        System.out.println("chat clicked on club id :" + clubId + " forum " + forumId);
        chatScrollPane.setContent(generalChatBox);
        generalChatBox.setPrefWidth(800);
        generalChatBox.setMaxWidth(800);
        generalChatBox.setMinWidth(800);

        loadChatHistory(forumId, clubId,"Chat");

        messageField.setDisable(false);
        messageField.setPromptText("Type your message...");
    }

   @FXML
   public void handleAnnouncements(int forumId) {
       chatScrollPane.setContent(announcementChatBox);
       announcementChatBox.setPrefWidth(800);
       announcementChatBox.setMaxWidth(800);
       announcementChatBox.setMinWidth(800);

       loadChatHistory(forumId, clubId, "Announcement");

       if (userId == adminId) {
           messageField.setDisable(false);
           messageField.setPromptText("Enter your announcement...");
       } else {
           messageField.setDisable(true);
           messageField.setPromptText("Only admins can send announcements.");
       }

       System.out.println("loading the chat");
   }


    @FXML
    public void handleRecommendations() {
       chatScrollPane.setContent(recommendationChatBox);
       recommendationChatBox.setPrefWidth(800);
       recommendationChatBox.setMaxWidth(800);
       recommendationChatBox.setMinWidth(800);

     //  forumId = 3;
    //   loadChatHistory(forumId, clubId);

        messageField.setDisable(false);
        messageField.setPromptText("Type your recommendation...");
    }

    public void handleQuotes(int forumId) {
        chatScrollPane.setContent(quotesChatBox);
        quotesChatBox.setPrefWidth(800);
        quotesChatBox.setMaxWidth(800);
        quotesChatBox.setMinWidth(800);

        loadChatHistory(forumId, clubId, "Quotes");

        messageField.setDisable(false);
        messageField.setPromptText("Type a Quote...");
    }





    @FXML
    public void handleSendMessage() throws SQLException {
        String msg = messageField.getText().trim();
        if (msg.isEmpty()) return;

        System.out.println("current : forumid " + forumId + " clubid : " + clubId);
        DBUtils.sendMessage(userId, forumId, msg,clubId);
        messageField.clear();

        String forumType = Forums.getForumType(forumId);
        loadChatHistory(forumId, clubId,forumType);
    }

    private void loadChatHistory(int forumid, int clubid, String target) {
        VBox targetBox = switch (target) {
            case "Chat" -> generalChatBox;
            case "Quotes" -> quotesChatBox;
            case "Announcement" -> announcementChatBox;
        //    case 4-> quotesChatBox;
            default -> generalChatBox;
        };


        targetBox.getChildren().clear();
        List<MessageModel> messages = DBUtils.loadLast10Messages(forumid, clubid);
        System.out.println("loading chat now... ");

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
                targetBox.getChildren().add(userContainer);
            }


            //Message and timestamp in VBox
            VBox messageGroup = new VBox();
            messageGroup.setSpacing(2);


            Label messageLabel = new Label(msg.getContent());
            messageLabel.setWrapText(true);
            messageLabel.setPadding(new Insets(10));

            if (target.equals("Quotes")) {
                // Quote styling
                messageLabel = new Label("“" + msg.getContent() + "”");
                messageLabel.setStyle("-fx-background-color: #fdf6e3; " +
                        "-fx-border-color: #ccc; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-font-style: italic; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #555;");
            } else {
                messageLabel.setStyle("-fx-background-color: " + (isCurrentUser ? "#d6cdb9" : "#ffffff") + ";" +
                        "-fx-background-radius: 10; -fx-font-size: 14px;");
            }


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


            targetBox.getChildren().add(messageContainer);
            previousUserId = msg.getUserId();
        }

        // Scroll to bottom
        targetBox.heightProperty().addListener((obs, oldVal, newVal) -> chatScrollPane.setVvalue(1.0));
    }


    private byte[] loadUserProfilePicture(int userId) {
        User user = new User(userId);
        return user.getPfp();
    }



    private void loadClubs() {
        try {
            List<Club> clubs = Club.fetchAllClubs();
            chatSideBar.getChildren().clear();

            for (Club club : clubs) {
                Image image = Club.displayClubCover(club.getCoverImage());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);

                // Make the image circular
                Circle clip = new Circle(25, 25, 25); // x, y, radius
                imageView.setClip(clip);

                // Optional shadow effect
                DropShadow ds = new DropShadow();
                ds.setRadius(5);
                ds.setOffsetX(1);
                ds.setOffsetY(2);
                ds.setColor(Color.gray(0.3));
                imageView.setEffect(ds);



                VBox clubBox = new VBox(5, imageView);
                clubBox.setAlignment(Pos.CENTER);
                clubBox.setPadding(new Insets(10));
                clubBox.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                // Tooltip
                Tooltip tooltip = new Tooltip(club.getName());
                Tooltip.install(clubBox, tooltip);

                // Hover effects
                clubBox.setOnMouseEntered(e -> {
                    clubBox.setStyle("-fx-background-color: #cbbd9e; -fx-background-radius: 10;");
                });

                clubBox.setOnMouseExited(e -> {
                    // Remove background only if not selected
                    if (club.getClub_id() != this.clubId) {
                        clubBox.setStyle("-fx-background-color: transparent;");
                    }
                });

                // Highlight selected club
                if (club.getClub_id() == this.clubId) {
                    clubBox.setStyle("-fx-background-color: #cbbd9e; -fx-border-radius: 10; -fx-background-radius: 10;");
                }


                // Click to select (update current club_id)
                clubBox.setOnMouseClicked(e -> {
                    System.out.println(club.getName() + " is clicked");
                    this.clubId = club.getClub_id();

                    //update label
                    clubNameLabel.setText(club.getName());

                    //update pdp
                    Image cover = club.displayClubCover(club.getCoverImage());
                    if (cover != null) {
                        clubCoverPicture.setImage(cover);
                    }

                    loadClubs(); // Refresh to highlight selected

                    //load forums for club clicked
                    try {
                        loadForums(clubId);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                chatSideBar.getChildren().add(clubBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadForums(int clubId) throws SQLException {
        System.out.println("load forums :");
        List<Forums> forums = Forums.getForums(clubId);


        for (Forums forum : forums) {
            //crate a forum btn
            Button forumButton = new Button(forum.getForumName());
            forumButton.getStyleClass().add("ghost-button");
            System.out.println("init id :" + forum.getForumId() );


            // load chats
            forumButton.setOnMouseClicked(e -> {

                int forumId = forum.getForumId();
                this.forumId = forumId;
                String forumType = null;

                try {
                    forumType = forum.getForumType(forumId);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("id : "+forumId +  "the forum :" +forum.getForumName() + "is of type :" + forumType);

                if (forumType.equals("Chat")){
                    System.out.println("loading chat of 'chat'");
                    handleChat(forumId);
                }
                if (forumType.equals("Quotes")){
                    System.out.println("loading chat of 'Quotes'");
                    handleQuotes(forumId);
                }
                if (forumType.equals("Announcement")){
                    System.out.println("loading chat of 'Announcement'");
                    handleAnnouncements(forumId);
                }

            });
            ForumsVbox.getChildren().add(forumButton);


        }

    }






}


