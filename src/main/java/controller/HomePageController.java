package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Book;
import model.Club;
import model.User;
import utils.DBUtils;

import java.io.*;
import java.sql.*;
import java.util.*;

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
    @FXML
    private HBox popular;
    
    @FXML
    private HBox profile;
    @FXML
    private HBox library;


    private User user;
    private String userpassword;
    private int userId;


    public void setUserInfo(int userId) {
        this.userId = userId;
        this.user = new User(userId);
        initialize(); // Load data only after user is set
    }


    @FXML
    public void initialize()  {

        profile.setOnMouseClicked(event -> navigateToProfile());
        library.setOnMouseClicked(event -> navigateToLibrary());
        
        user = new User(userId);
        System.out.println("user_id in login: "+ userId);
        //merging both the clubs the user is admin of and member of without duplicates
        List<Integer> mergedClubs = new ArrayList<>(user.getAdminOf());
        mergedClubs.addAll(user.getMemberOf());


        //adding the clubs to the side pane
        addClubCardsToVBox(mergedClubs, rightSide);

        //changing byte to img to fill the pfp circle
        byte[] pfpBytes = user.getPfp();
        if (pfpBytes != null && pfpBytes.length > 0) {
            InputStream is = new ByteArrayInputStream(pfpBytes);
            Image image = new Image(is);
            pfp.setFill(new ImagePattern(image));
        } else {
            // Optionally use a default placeholder image
            Image defaultImage = new Image(getClass().getResource("/view/img/pp.png").toExternalForm());
            pfp.setFill(new ImagePattern(defaultImage));
        }


        //setting the username label
        username.setText(user.getUsername());
        //setting up the genre buttons
        setupButtons();


        try {
            addBookCardsToHBox(Book.generateFyb(user), fyb);
            addBookCardsToHBox(Book.getPopularBooks(), popular);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void navigateToLibrary() {
    }

    private void navigateToProfile() {
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
            List<Book> result = Book.searchBooks(searchText);
            switchToResultPage(result);

        }
    }

    private void switchToResultPage(List<Book> result) {
        try {
            // Gettign the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Result.fxml"));
            Parent root = loader.load();

            // Pass the results to the ResultPageController
            ResultPageController controller = loader.getController();
            controller.setResult(result, userId);

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
    private void addClub(MouseEvent mouseEvent) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateClub.fxml"));
            Parent root = loader.load();

            CreateClubController controller = loader.getController();
            controller.initialise(user.getUser_id());

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED); // Removes the title bar
            stage.setScene(new Scene(root));

            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addClubCardsToVBox(List<Integer> clubs, VBox container) {

        try {
            Set<Integer> seen = new HashSet<>();
            for (int club_id : clubs) {
                if (!seen.add(club_id)) continue;
                Club club = Club.getClub(club_id);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClubCard.fxml"));
                HBox card = loader.load();

                ClubCardController controller = loader.getController();
                if (club.getCoverImage() != null) {
                    InputStream is = new ByteArrayInputStream(club.getCoverImage());
                    controller.setClubCard(club.getName(), String.valueOf(club.getCount()),new Image(is));
                } else {
                    // Use a default placeholder image if cover image is missing
                    Image coverImage = new Image(getClass().getResource("/images/club-logo.png").toExternalForm());
                    controller.setClubCard(club.getName(), String.valueOf(club.getCount()),coverImage);
                }

                int finalClubId = club_id;
                card.setOnMouseClicked(event -> {
                    FXMLLoader loaderr = new FXMLLoader(getClass().getResource("/view/ClubView.fxml"));
                    DBUtils.changeScene(event,"/view/ClubView.fxml", userId, null );
                    System.out.println("click on club " + club_id + " with user id " + userId);

                });

                VBox.setMargin(card, new Insets(10));
                container.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addBookCardsToHBox (List<Book> books, HBox container) {
        try {
            for (Book book : books) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookCard.fxml"));
                AnchorPane card = loader.load();

                BookCardController controller = loader.getController();
                controller.setBookCard(book);
                card.setOnMouseClicked(event -> {
                    try {
                        FXMLLoader detailLoader = new FXMLLoader(getClass().getResource("/view/BookPage.fxml"));
                        Parent detailRoot = detailLoader.load();

                        //BookPageController detailController = detailLoader.getController();
                        //detailController.initializeWithBook(book); // pass full book object or just book ID

                        Stage stage = new Stage();
                        stage.setTitle("Book Details");
                        stage.setScene(new Scene(detailRoot));
                        stage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                HBox.setMargin(card, new Insets(25));
                container.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openNotification(MouseEvent mouseEvent) {

    }
}