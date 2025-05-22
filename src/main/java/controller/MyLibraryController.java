package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.BookModel;
import model.User;
import utils.DBUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MyLibraryController implements Initializable {

    @FXML
    private HBox LikedList;
    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private HBox ReadLaterList;
    @FXML
    private HBox ReadList;
    @FXML
    private Label likedLabel;
    @FXML
    private Label readLaterLabel;
    @FXML
    private Label readLabel;
    private User user;
    @FXML
    private HBox home;

    private int userid;

    public void setUserInfo(int userId) {
        this.userid = userId;
        this.user = new User(userId);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        home.setOnMouseClicked(event -> navigateToHome());
        loadLikedBooks();
        loadReadLaterBooks();
        loadReadBooks();

        mainScrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            double scroll = newVal.doubleValue();

            // Approximate vertical positions of each list
            double likedPos = 0.0;       // top
            double readLaterPos = 0.5;   // middle
            double readPos = 1.0;        // bottom

            // Focus scaling: maximum at center of viewport
            double scaleLiked = getFocusScale(scroll, likedPos);
            double scaleReadLater = getFocusScale(scroll, readLaterPos);
            double scaleRead = getFocusScale(scroll, readPos);

            // Apply scales
            LikedList.setScaleX(scaleLiked);
            LikedList.setScaleY(scaleLiked);

            ReadLaterList.setScaleX(scaleReadLater);
            ReadLaterList.setScaleY(scaleReadLater);

            ReadList.setScaleX(scaleRead);
            ReadList.setScaleY(scaleRead);

            // Apply to labels
            likedLabel.setScaleX(scaleLiked);
            likedLabel.setScaleY(scaleLiked);
            readLaterLabel.setScaleX(scaleReadLater);
            readLaterLabel.setScaleY(scaleReadLater);
            readLabel.setScaleX(scaleRead);
            readLabel.setScaleY(scaleRead);
        });
    }
    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Home.fxml"));
            Parent root = loader.load();

            HomePageController controller = loader.getController();
            System.out.println("user_id in switching: " + userid);
            controller.setUserInfo(userid); // This should now work

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (the one that triggered the event)
            Stage currentStage = (Stage) LikedList.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private double getFocusScale(double scroll, double sectionPos) {
        double distance = Math.abs(scroll - sectionPos);
        double maxScale = 1.2;
        double minScale = 0.8;
        return maxScale - Math.min(distance * 2, 1.0) * (maxScale - minScale);
    }


    public int imageWidth = 120;
    public int imageHeight = 180;
    public int arc = 10;
    private void loadLikedBooks() {
        try {
            List<BookModel> likedBooks = DBUtils.fetchLikedBooks(1);

            LikedList.getChildren().clear();

            for (BookModel book : likedBooks) {

                //IMAGE
                byte[] imageBytes = book.getCover();

                // Convert BLOB to Image
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                Image bookImage = new Image(bis);

                ImageView imageView = new ImageView(bookImage);
                imageView.setFitWidth(imageWidth);
                imageView.setFitHeight(imageHeight);


                //radius
                Rectangle clip = new Rectangle(imageWidth, imageHeight);
                clip.setArcWidth(arc);
                clip.setArcHeight(arc);
                imageView.setClip(clip);

                DropShadow ds = new DropShadow();
                ds.setRadius(100); // Increase blur radius
                ds.setOffsetX(5); // Horizontal offset
                ds.setOffsetY(5); // Vertical offset
                ds.setColor(Color.gray(0.2)); // Darker shadow color (lower is darker)
                ds.setSpread(0.3); // Increase spread to make it more pronounced
                imageView.setEffect(ds);

                //TITLE
                Label titleLabel = new Label(book.getTitle());
                titleLabel.setWrapText(true);
                titleLabel.setMaxWidth(imageWidth);
                titleLabel.setAlignment(Pos.CENTER);
                titleLabel.setStyle("-fx-font-family: 'Corbel'; -fx-font-size: 12px; -fx-text-alignment: center; -fx-alignment: center;");

                //AUTHOR
                String authorLabelLn = book.getAuthorLn();
                String authotLabelFn  = book.getAuthorFn();
                String fullAuthorName = authotLabelFn + " " + authorLabelLn;
                Label authorLabel = new Label(fullAuthorName);
                authorLabel.setAlignment(Pos.CENTER);
                authorLabel.setStyle("-fx-font-family: 'Corbel'; -fx-text-fill: #666666; -fx-font-size: 11px; -fx-text-alignment: center; -fx-alignment: center;");
                authorLabel.setWrapText(true);
                authorLabel.setMaxWidth(imageWidth);

                //BOOK
                VBox vbox = new VBox(imageView, titleLabel, authorLabel);
                vbox.setSpacing(5);
                vbox.setPadding(new Insets(10, 0, 10, 0));
                VBox.setVgrow(titleLabel, Priority.ALWAYS);
                VBox.setVgrow(authorLabel, Priority.ALWAYS);
                vbox.setStyle("-fx-cursor: hand;");
               // vbox.setAlignment(Pos.CENTER);


                vbox.setOnMouseEntered(e -> vbox.setStyle("-fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
                vbox.setOnMouseExited(e -> vbox.setStyle("-fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));

                vbox.setOnMouseClicked(e -> {
                    System.out.println("Clicked on: " + book.getTitle());
                });

                LikedList.getChildren().add(vbox);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadReadLaterBooks() {
        try {
            List<BookModel> readLaterList = DBUtils.fetchReadLaterBooks(1);

            ReadLaterList.getChildren().clear();

            for (BookModel book : readLaterList) {

                //IMAGE
                byte[] imageBytes = book.getCover();

                // Convert BLOB to Image
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                Image bookImage = new Image(bis);

                ImageView imageView = new ImageView(bookImage);
                imageView.setFitWidth(imageWidth);
                imageView.setFitHeight(imageHeight);
                //radius
                Rectangle clip = new Rectangle(imageWidth, imageHeight);
                clip.setArcWidth(arc);
                clip.setArcHeight(arc);
                imageView.setClip(clip);


                //TITLE
                Label titleLabel = new Label(book.getTitle());
                titleLabel.setWrapText(true);
                titleLabel.setMaxWidth(imageWidth);
                titleLabel.setAlignment(Pos.CENTER);
                titleLabel.setStyle("-fx-font-family: 'Corbel'; -fx-font-size: 12px; -fx-text-alignment: center; -fx-alignment: center;");

                //AUTHOR
                String authorLabelLn = book.getAuthorLn();
                String authotLabelFn  = book.getAuthorFn();
                String fullAuthorName = authotLabelFn + " " + authorLabelLn;
                Label authorLabel = new Label(fullAuthorName);
                authorLabel.setAlignment(Pos.CENTER);
                authorLabel.setStyle("-fx-font-family: 'Corbel'; -fx-text-fill: #666666; -fx-font-size: 11px; -fx-text-alignment: center; -fx-alignment: center;");
                authorLabel.setWrapText(true);
                authorLabel.setMaxWidth(imageWidth);

                //BOOK
                VBox vbox = new VBox(imageView, titleLabel, authorLabel);
                vbox.setSpacing(5);
                vbox.setPadding(new Insets(10, 0, 10, 0));
                VBox.setVgrow(titleLabel, Priority.ALWAYS);
                VBox.setVgrow(authorLabel, Priority.ALWAYS);
                vbox.setStyle("-fx-cursor: hand;");
                // vbox.setAlignment(Pos.CENTER);


                vbox.setOnMouseEntered(e -> vbox.setStyle("-fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
                vbox.setOnMouseExited(e -> vbox.setStyle("-fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));

                vbox.setOnMouseClicked(e -> {
                    System.out.println("Clicked on: " + book.getTitle());
                });

                ReadLaterList.getChildren().add(vbox);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadReadBooks() {
        try {
            List<BookModel> readList = DBUtils.fetchReadBooks(1);

            ReadList.getChildren().clear();

            for (BookModel book : readList) {

                //IMAGE
                byte[] imageBytes = book.getCover();

                // Convert BLOB to Image
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                Image bookImage = new Image(bis);

                ImageView imageView = new ImageView(bookImage);
                imageView.setFitWidth(imageWidth);
                imageView.setFitHeight(imageHeight);
                //radius
                Rectangle clip = new Rectangle(imageWidth, imageHeight);
                clip.setArcWidth(arc);
                clip.setArcHeight(arc);
                imageView.setClip(clip);


                //TITLE
                Label titleLabel = new Label(book.getTitle());
                titleLabel.setWrapText(true);
                titleLabel.setMaxWidth(imageWidth);
                titleLabel.setAlignment(Pos.CENTER);
                titleLabel.setStyle("-fx-font-family: 'Corbel'; -fx-font-size: 12px; -fx-text-alignment: center; -fx-alignment: center;");

                //AUTHOR
                String authorLabelLn = book.getAuthorLn();
                String authotLabelFn  = book.getAuthorFn();
                String fullAuthorName = authotLabelFn + " " + authorLabelLn;
                Label authorLabel = new Label(fullAuthorName);
                authorLabel.setAlignment(Pos.CENTER);
                authorLabel.setStyle("-fx-font-family: 'Corbel'; -fx-text-fill: #666666; -fx-font-size: 11px; -fx-text-alignment: center; -fx-alignment: center;");
                authorLabel.setWrapText(true);
                authorLabel.setMaxWidth(imageWidth);

                //BOOK
                VBox vbox = new VBox(imageView, titleLabel, authorLabel);
                vbox.setSpacing(5);
                vbox.setPadding(new Insets(10, 0, 10, 0));
                VBox.setVgrow(titleLabel, Priority.ALWAYS);
                VBox.setVgrow(authorLabel, Priority.ALWAYS);
                vbox.setStyle("-fx-cursor: hand;");
                // vbox.setAlignment(Pos.CENTER);


                vbox.setOnMouseEntered(e -> vbox.setStyle("-fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
                vbox.setOnMouseExited(e -> vbox.setStyle("-fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));

                vbox.setOnMouseClicked(e -> {
                    System.out.println("Clicked on: " + book.getTitle());
                });

                ReadList.getChildren().add(vbox);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
