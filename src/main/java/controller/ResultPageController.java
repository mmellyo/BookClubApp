package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Book;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
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
    private Button previousButton;
    @FXML
    private HBox profile;
    @FXML
    private HBox library;
    @FXML
    private HBox home;

    private int userid;

    private void updateNavButtons() {
        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * 12 >= result.size());
    }

    @FXML
    public void initialize() {
        home.setOnMouseClicked(event -> navigateToHome());
        profile.setOnMouseClicked(event -> navigateToProfile());
        library.setOnMouseClicked(event -> navigateToLibrary());
    }

    private void navigateToLibrary() {
    }

    private void navigateToProfile() {
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
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setResult(List<Book> result, int user_id) {
        this.result = result;
        this.userid = user_id;
        setList();
        loadInitialBooks();
        updateNavButtons();
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
                button.setOnAction(e -> openBookPage(book));
                button.setDisable(false);  // Enable button because a book is available
            } else {
                button.setText("");
                button.setGraphic(null);
                button.setDisable(true);
                button.setOnAction(null);// Disable button because no book here
            }
        }
    }

    private void openBookPage(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookPage.fxml"));
            Parent root = loader.load();

            BookPageController controller = loader.getController();
            controller.setBookInfo(book, userid); // This should now work
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (the one that triggered the event)
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
