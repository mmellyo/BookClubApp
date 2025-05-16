package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.BookModel;
import utils.DBUtils;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MyLibraryController implements Initializable {

    @FXML
    private HBox LikedList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadLikedBooks();
    }




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
                imageView.setFitWidth(100);
                imageView.setFitHeight(160);

                //TITLE
                Label titleLabel = new Label(book.getTitle());
                titleLabel.setWrapText(true);
                titleLabel.setMaxWidth(100);
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
                authorLabel.setMaxWidth(90);

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

}
