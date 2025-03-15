package controller;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Popup;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    @FXML
    private TextField searchField; // Reference from FXML

    private final Popup popup = new Popup();
    private final VBox popupContent = new VBox(); // VBox to hold suggestions


    //ADDDED BY MELLY : to share data between scenes
    private String username;
    private String userpassword;
    public void setUserInfo(String username, String userpassword) {
        this.username = username;
        this.userpassword = userpassword;

    }

    @FXML
    public void initialize() {
        setupAutoCompletePopup();
        setupButtons();
    }

    private void setupAutoCompletePopup() {
        popupContent.setStyle("-fx-background-color: #baaa87; -fx-border-color: #baaa87; -fx-padding: 5; -fx-border-radius: 0 0 10 10; -fx-background-radius: 0 0 10 10;");
        popup.getContent().add(popupContent);

        // Update Popup Location When Search Bar Moves
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                popup.hide();
            } else {
                fetchAndShowSuggestions(newText);
            }
        });

        // Hide popup when pressing ESC
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                popup.hide();
            }
        });

        // Hide popup when losing focus
        searchField.focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (!isFocused) popup.hide();
        });
    }

    private void fetchAndShowSuggestions(String query) {
        Task<List<BookSuggestion>> task = new Task<>() {
            @Override
            protected List<BookSuggestion> call() {
                return fetchBookSuggestions(query);
            }
        };

        task.setOnSucceeded(event -> {
            List<BookSuggestion> suggestions = task.getValue();
            Platform.runLater(() -> showPopup(suggestions));
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
        });

        new Thread(task).start(); // Run on a separate thread
    }

    private void showPopup(List<BookSuggestion> suggestions) {
        if (suggestions.isEmpty()) {
            popup.hide();
            return;
        }

        popupContent.getChildren().clear();

        for (BookSuggestion book : suggestions) {
            HBox itemBox = new HBox(10); // Horizontal box for image + text
            itemBox.setAlignment(Pos.CENTER_LEFT);
            itemBox.setStyle("-fx-padding: 5; -fx-cursor: hand;");

            // Label for book title
            Label titleLabel = new Label(book.getTitle());
            titleLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

            // Image for book cover
            ImageView bookImage = new ImageView();
            bookImage.setFitWidth(40); // Adjust size as needed
            bookImage.setFitHeight(60);

            if (book.getImageUrl() != null) {
                Image image = new Image(book.getImageUrl(), true); // Asynchronous loading
                bookImage.setImage(image);
            }

            itemBox.getChildren().addAll(bookImage, titleLabel);

            // Click Event
            itemBox.setOnMouseClicked(event -> {
                searchField.setText(book.getTitle());
                popup.hide();
            });

            // Hover Effect
            itemBox.setOnMouseEntered(event -> itemBox.setStyle("-fx-padding: 5; -fx-background-color: #E0E0E0;"));
            itemBox.setOnMouseExited(event -> itemBox.setStyle("-fx-padding: 5;"));

            popupContent.getChildren().add(itemBox);
        }

        // Position Popup Below Search Bar
        Bounds bounds = searchField.localToScreen(searchField.getBoundsInLocal());
        popup.setX(bounds.getMinX());
        popup.setY(bounds.getMaxY() - 5);
        popupContent.setPrefWidth(329);
        popup.setAutoHide(true); // Hide when clicking outside
        popup.show(searchField.getScene().getWindow());
    }

    private List<BookSuggestion> fetchBookSuggestions(String query) {
        List<BookSuggestion> results = new ArrayList<>();

        try {
            String apiUrl = "https://openlibrary.org/search.json?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray docs = jsonObject.getJSONArray("docs");

            List<BookSuggestion> allBooks = new ArrayList<>();

            for (int i = 0; i < docs.length(); i++) {
                JSONObject book = docs.getJSONObject(i);
                String title = book.optString("title", "Unknown Title");
                String olid = book.has("cover_edition_key") ? book.getString("cover_edition_key") : null;

                String imageUrl = (olid != null) ? "https://covers.openlibrary.org/b/olid/" + olid + "-M.jpg" : null;

                allBooks.add(new BookSuggestion(title, imageUrl));
            }

            // **Prioritize matches**
            results = allBooks.stream()
                    .filter(book -> book.getTitle().toLowerCase().startsWith(query.toLowerCase())) // Titles that start with query
                    .sorted(Comparator.comparingInt(book -> book.getTitle().length())) // Shorter titles first
                    .collect(Collectors.toList());

            // If no exact match, try words that start with the query
            if (results.isEmpty()) {
                results = allBooks.stream()
                        .filter(book -> Arrays.stream(book.getTitle().toLowerCase().split(" "))
                                .anyMatch(word -> word.startsWith(query.toLowerCase())))
                        .sorted(Comparator.comparingInt(book -> book.getTitle().length())) // Shorter titles first
                        .collect(Collectors.toList());
            }

            // If still empty, fallback to any part of title
            if (results.isEmpty()) {
                results = allBooks.stream()
                        .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase()))
                        .sorted(Comparator.comparingInt(book -> book.getTitle().indexOf(query.toLowerCase()))) // Prioritize closer matches
                        .collect(Collectors.toList());
            }

            // **Limit results to 4 items**
            return results.stream().limit(4).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }



    // Helper class to store book details
    private static class BookSuggestion {
        private final String title;
        private final String imageUrl;

        public BookSuggestion(String title, String imageUrl) {
            this.title = title;
            this.imageUrl = imageUrl;
        }

        public String getTitle() {
            return title;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }

    @FXML
    private HBox genreBox; // The HBox containing the genre buttons

    private void setupButtons() {
        for (Node node : genreBox.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setOnAction(this::openGenrePage);
            }
        }
    }

    private void openGenrePage(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String genre = clickedButton.getText(); // Assuming the button text is the genre name
        loadGenrePage(genre);
    }

    private void loadGenrePage(String genre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Genre.fxml"));
            Parent root = loader.load();

            // Pass the genre to the GenreController
            GenreController controller = loader.getController();
            controller.setGenre(genre);

            // Create a new stage for the genre page
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED); // Removes the title bar
            stage.setScene(new Scene(root));

            // Close the current home window
            Stage homeStage = (Stage) genreBox.getScene().getWindow();
            homeStage.close();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}