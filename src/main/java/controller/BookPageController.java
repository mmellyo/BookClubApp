package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Book;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class BookPageController {

    private Book book;
    @FXML
    private Label title;
    @FXML
    private Label name;
    @FXML
    private Label summary;
    @FXML
    private Label bio;
    @FXML
    private Button later;
    @FXML
    private Button liked;
    @FXML
    private Button read;
    @FXML
    private ImageView cover;
    private int userid;

    @FXML
    private HBox profile;
    @FXML
    private HBox library;
    @FXML
    private HBox home;

    public void setBookInfo(Book book, int userid)
    {
        library.setOnMouseClicked(event -> navigateToLibrary(userid));
        this.book = book;
        this.userid = userid;
        // Set the labels with the book info
        title.setText(book.getTitle());
        name.setText(book.getAuthor());  // Assuming Book has getAuthorName()
        summary.setText(book.getDescription());
        bio.setText(book.getAuthorBio(book.getBook_id()));    // Assuming Book has getAuthorBio()

        // Set the cover image if available
        if (book.getCoverBytes() != null) {
            try (InputStream is = new ByteArrayInputStream(book.getCoverBytes())) {
                Image image = new Image(is, 150, 200, true, true);
                cover.setImage(image);
            } catch (Exception e) {
                e.printStackTrace(); // Debug if image creation failed
            }
        } else {
            cover.setImage(null);  // No image available
        }
    }

    private void navigateToLibrary(int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MyLibrary.fxml"));
            Parent root = loader.load();

            MyLibraryController controller = loader.getController();
            System.out.println("user_id in switching: " + userId);
            controller.setUserInfo(userId); // This should now work

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (the one that triggered the event)
            Stage currentStage = (Stage) library.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void addToLater(ActionEvent event) throws IOException {
        try {
            Book.addToLater(userid, book);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addToRead(ActionEvent event) throws IOException {
        try {
            Book.addToRead(userid, book);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addToLiked(ActionEvent event) throws IOException {
        try {
            Book.addToLiked(userid, book);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
