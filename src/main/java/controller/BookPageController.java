package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Book;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
    private Button add;
    @FXML
    private ImageView cover;

    public void setBookInfo(Book book)
    {
        this.book = book;

        // Set the labels with the book info
        title.setText(book.getTitle());
        name.setText(book.getAuthor());  // Assuming Book has getAuthorName()
        summary.setText(book.getDescription());
        bio.setText(book.getAuthorBio(book.getBook_id()));    // Assuming Book has getAuthorBio()

        // Set the cover image if available
        if (book.getCoverBytes() != null) {
            InputStream is = new ByteArrayInputStream(book.getCoverBytes());
            Image image = new Image(is, 150, 200, true, true);
            cover.setImage(image);
        } else {
            cover.setImage(null);  // No image available
        }
    }


}
