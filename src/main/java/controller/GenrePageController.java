package controller;

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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GenrePageController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label genreHeader;
    private List<Button> bookButtons = new ArrayList<Button>();
    @FXML
    private String genre;
    private int currentPage = 1;

    private void setList(){
        for (int i = 1; i <= 12; i++) {
            Button btn = (Button) rootPane.lookup("#book" + i);
            if (btn != null) {
                bookButtons.add(btn);
            }
        }
    }
    // replacing the word genre with the actual genre navigated to
    public void setGenre(String genre) {
        this.genre = genre;
        genreHeader.setText(genre);
    }

    // Load initial books
   /*
   private void loadInitialBooks() {
        List<Book> books = fetchBooks(genre, currentPage);
        addBooksToUI(books);
    }

    @FXML
    private void nextPage(ActionEvent event) {
        List<Book> books = fetchBooks(genre, currentPage + 1);
        addBooksToUI(books);
    }
    @FXML
    private void previousPage(ActionEvent event) {
        List<Book> books = fetchBooks(genre, currentPage - 1);
        addBooksToUI(books);
    }
    */


    /* private List<Book> fetchBooks(String genre, int page) {
        List<Book> books = new ArrayList<>();
        try {
            String apiUrl = "https://openlibrary.org/subjects/" + genre.toLowerCase() + ".json?limit=12&offset=" + (page * 12);
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(reader);

            JsonNode works = root.path("works");
            for (JsonNode work : works) {
                String title = work.path("title").asText();
                String author = work.path("authors").size() > 0 ? work.path("authors").get(0).path("name").asText() : "Unknown";
                String coverId = work.path("cover_id").asText();
                String coverUrl = coverId.isEmpty() ? "" : "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";

                books.add(new Book(title, author, coverUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }*/


    private void addBooksToUI(List<Book> books) {
        setList();
        for(int i=0; i<12; i++) {
            Button button = bookButtons.get(i);
            button.setText(books.get(i).getTitle());
            ImageView coverImage = new ImageView();
            //coverImage.setImage(new Image(books.get(i).getCoverUrl(), 150, 200, true, true));
            if (books.get(i).getCoverBytes() != null) {
                InputStream is = new ByteArrayInputStream(books.get(i).getCoverBytes());
                Image image = new Image(is, 150, 200, true, true);
                coverImage.setImage(image);
            }
            button.setGraphic(coverImage);
        }
    }


}
