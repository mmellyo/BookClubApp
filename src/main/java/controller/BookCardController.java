package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Book;

import java.io.ByteArrayInputStream;

public class BookCardController {

    @FXML
    private ImageView coverPic;
    @FXML
    private Label bookName;

    private String book_id;

    public void setBookCard(Book book) {
        coverPic.setImage(new Image(new ByteArrayInputStream(book.getCoverBytes())));
        bookName.setText(book.getTitle());
        this.book_id = book.getTitle();
    }

    public void goToBookPage(MouseEvent mouseEvent) {
    }
}
