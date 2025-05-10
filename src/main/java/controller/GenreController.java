package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenreController {
    @FXML
    private ListView<String> bookListView; // Assuming you use a ListView to display book titles

    private String genre;

    public void setGenre(String genre) {
        this.genre = genre;
        fetchBooks();
    }

    private void fetchBooks() {
        String apiUrl = "https://openlibrary.org/subjects/" + genre.toLowerCase() + ".json";

        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.lines().collect(Collectors.joining());
                reader.close();

                JSONObject json = new JSONObject(response);
                JSONArray books = json.getJSONArray("works");

                List<String> bookTitles = new ArrayList<>();
                for (int i = 0; i < books.length(); i++) {
                    bookTitles.add(books.getJSONObject(i).getString("title"));
                }

                Platform.runLater(() -> bookListView.getItems().setAll(bookTitles));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}