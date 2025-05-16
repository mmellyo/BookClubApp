package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Book;
import model.Club;
import model.Search;
import model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class HomePageController {

    @FXML
    private HBox genreBox;
    @FXML
    private TextField searchField; // Reference from FXML
    @FXML
    private VBox rightSide;
    @FXML
    private Circle pfp;
    @FXML
    private Label username;
    @FXML
    private HBox fyb;

    private User user;





    @FXML
    public void initialize()  {


        user = new User("1");

        //merging both the clubs the user is admin of and member of without duplicates
        Set<Club> mergedClubs = new HashSet<>(user.getAdminOf());
        mergedClubs.addAll(user.getMemberOf());


        //adding the clubs to the side pane
        addClubCardsToVBox(mergedClubs, rightSide);

        //changing byte to img to fill the pfp circle
        InputStream is = new ByteArrayInputStream(user.getPfp());
        Image image = new Image(is);
        pfp.setFill(new ImagePattern(image));

        //setting the username label
        username.setText(user.getUsername());
        //setting up the genre buttons
        setupButtons();


        try {
            addBookCardsToHBox(Search.generateFyb(user), fyb);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // setting up the genres buttons
    private void setupButtons() {
        for (Node node : genreBox.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setOnAction(this::switchToGenrePage);
            }
        }
    }


    //method to switch from home to genre page passing the genre name from the button
    private void switchToGenrePage(ActionEvent actionEvent) {
        Button clickedButton = (Button) actionEvent.getSource();
        String genre = clickedButton.getText(); // Assuming the button text is the genre name
        loadGenrePage(genre);
    }

    //a method to load the genre page depending on the button pressed
    private void loadGenrePage(String genre) {
        try {
            // Gettign the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Genre.fxml"));
            Parent root = loader.load();

            // Pass the genre to the GenrePageController
            GenrePageController controller = loader.getController();
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

    @FXML
    private void search (KeyEvent event) throws SQLException {
        if (event.getCode() == KeyCode.ENTER) {

            String searchText = searchField.getText();
            Set<Book> result = Search.searchBooks(searchText);
            switchToResultPage(result);

        }
    }

    private void switchToResultPage(Set<Book> result) {
        try {
            // Gettign the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Result.fxml"));
            Parent root = loader.load();

            // Pass the genre to the GenrePageController
            ResultPageController controller = loader.getController();
            controller.setResult(result);

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

    @FXML
    private void addClub(MouseEvent mouseEvent) {

    }

    public void addClubCardsToVBox(Set<Club> clubs, VBox container) {
        try {
            for (Club club : clubs) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClubCard.fxml"));
                HBox card = loader.load();

                ClubCardController controller = loader.getController();
                InputStream is = new ByteArrayInputStream(club.getCoverImage());
                controller.setClubCard(club.getName(), "1",new Image(is));

                VBox.setMargin(card, new Insets(10));
                container.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addBookCardsToHBox (Set<Book> books, HBox container) {
        try {
            for (Book book : books) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookCard.fxml"));
                AnchorPane card = loader.load();

                BookCardController controller = loader.getController();
                controller.setBookCard(book);

                HBox.setMargin(card, new Insets(15));
                container.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void openNotification(MouseEvent mouseEvent) {

    }
}
