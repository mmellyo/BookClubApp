package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenrePageController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label genreHeader;
    private List<Button> bookButtons = new ArrayList<Button>();
    @FXML
    private String genre;
    private int currentPage = 0;
    private int userid;
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

    private void setList(){
        for (int i = 1; i <= 12; i++) {
            Button btn = (Button) rootPane.lookup("#book" + i);
            if (btn != null) {
                bookButtons.add(btn);
            }
        }
    }
    // replacing the word genre with the actual genre navigated to
    public void setGenre(String genre, int userid) {
        this.genre = genre;
        genreHeader.setText(genre);
        this.userid= userid;

        try {
            loadInitialBooks();
            home.setOnMouseClicked(event -> navigateToHome());
            profile.setOnMouseClicked(event -> navigateToProfile());
            library.setOnMouseClicked(event -> navigateToLibrary());// now genre is definitely not null
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    // Load initial books

   private void loadInitialBooks() throws SQLException {
        List<Book> books = fetchBooks(genre, currentPage);
        addBooksToUI(books);
    }

    @FXML
    private void nextPage(ActionEvent event) throws SQLException {
        currentPage++;
        List<Book> books = fetchBooks(genre, currentPage + 1);
        addBooksToUI(books);
    }
    @FXML
    private void previousPage(ActionEvent event) throws SQLException {
        if (currentPage > 0) currentPage--;
        List<Book> books = fetchBooks(genre, currentPage - 1);
        addBooksToUI(books);
    }


     private List<Book> fetchBooks(String genre, int page) throws SQLException {

            List<Book> books = new ArrayList<>();
         if(genre.equals("All"))
         {
             List<Book> AllBooks = Book.getAllBooks();
             System.out.println("Total books found: " + AllBooks.size()); // <--- debug

             int start = page * 12;
             int end = Math.min(start + 12, AllBooks.size());
             for (int i = start; i < end; i++) {
                 books.add(AllBooks.get(i));
             }
         }
         else{
             List<Book> AllBooks = Book.getBooksByGenre(genre);
             System.out.println("Total books found: " + AllBooks.size()); // <--- debug

             int start = page * 12;
             int end = Math.min(start + 12, AllBooks.size());
             for (int i = start; i < end; i++) {
                 books.add(AllBooks.get(i));
             }
         }

        return books;
    }


    private void addBooksToUI(List<Book> books) {
        setList();
        for (int i = 0; i < bookButtons.size(); i++) {
            Button button = bookButtons.get(i);
            if (i < books.size()) {
                Book book = books.get(i);
                button.setText(book.getTitle());
                ImageView coverImage = new ImageView();
                if (book.getCoverBytes() != null) {
                    InputStream is = new ByteArrayInputStream(book.getCoverBytes());
                    Image image = new Image(is, 150, 200, true, true);
                    coverImage.setImage(image);
                }
                button.setGraphic(coverImage);
                button.setOnAction(e -> openBookPage(book));
            } else {
                button.setText("");
                button.setGraphic(null);
                button.setDisable(true);
                button.setVisible(false);
            }
        }
        try {
            int totalBooks = Book.getBooksByGenre(genre).size();
            int maxPages = (int) Math.ceil(totalBooks / 12.0);

            previousButton.setDisable(currentPage <= 0);
            nextButton.setDisable(currentPage >= maxPages - 1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void openBookPage(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookPage.fxml"));
            Parent root = loader.load();

            BookPageController controller = loader.getController();
            controller.setBookInfo(book); // This should now work

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
