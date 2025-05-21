package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import model.Book;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ResultPageController {
    @FXML
    private AnchorPane rootPane;
    private List<Button> bookButtons = new ArrayList<>();
    private int currentPage = 0;
    private List<Book> result;

    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;

    private void updateNavButtons() {
        prevButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * 12 >= result.size());
    }

    @FXML
    public void initialize() {
        setList();
    }

    public void setResult(List<Book> result) {
        this.result = result;
        loadInitialBooks();
    }

    private void setList(){
        for (int i = 1; i <= 12; i++) {
            Button btn = (Button) rootPane.lookup("#book" + i);
            if (btn != null) {
                bookButtons.add(btn);
            }
        }
    }

    private void loadInitialBooks() {
        addBooksToUI(result, currentPage);
    }

    @FXML
    private void nextPage(ActionEvent event) {
        if ((currentPage + 1) * 12 < result.size()) {
            currentPage += 1;
            addBooksToUI(result, currentPage);
        }

    }
    @FXML
    private void previousPage(ActionEvent event) {
        if (currentPage > 0) {
            currentPage -= 1;
            addBooksToUI(result, currentPage);
        }
    }

    private void addBooksToUI(List<Book> books, int page) {
        int start = page * 12;
        for (int i = 0; i < 12; i++) {
            Button button = bookButtons.get(i);

            int index = start + i;
            if (index < books.size()) {
                Book book = books.get(index);
                button.setText(book.getTitle());
                ImageView coverImage = new ImageView();
                if (book.getCoverBytes() != null) {
                    InputStream is = new ByteArrayInputStream(book.getCoverBytes());
                    Image image = new Image(is, 150, 200, true, true);
                    coverImage.setImage(image);
                }
                button.setGraphic(coverImage);
            } else {
                button.setText("");
                button.setGraphic(null);
            }
        }
    }
}
