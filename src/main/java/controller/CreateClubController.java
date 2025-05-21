package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Book;
import model.Club;
import org.controlsfx.control.SearchableComboBox;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateClubController {

    @FXML
    private TextField name;
    @FXML
    private TextField description;
    @FXML
    private ComboBox<Book> bookComboBox;
    private final ObservableList<Book> bookResults = FXCollections.observableArrayList();
    private int user_id;
    @FXML
    private HBox selectedBooksBox;
    private final Set<Integer> selectedBookIds = new HashSet<>();
    private final List<Book> selectedBooks = new ArrayList<>();

    @FXML
    private Button choose;
    @FXML
    private Button cancel;
    @FXML
    private Button confirm;

    private File selectedImageFile = null;

    @FXML
    public void initialise(int user_id) throws IOException {
        bookComboBox.setItems(bookResults);
        bookComboBox.setEditable(true);
        this.user_id = user_id;
        System.out.println("Trying to create club with adminId: " + this.user_id);
        bookComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Book book) {
                return book == null ? "" : book.getTitle();
            }

            @Override
            public Book fromString(String string) {
                return null;
            }
        });

        bookComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 2) {
                try {
                    updateComboBoxResults(newVal);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                bookResults.clear();
            }
        });

        bookComboBox.setOnAction(event -> {
            Book selected = bookComboBox.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selectedBookIds.contains(selected.getBook_id())) return;

                if (selectedBooks.size() >= 4) {
                    new Alert(Alert.AlertType.WARNING, "You can only select up to 4 books.").showAndWait();
                    return;
                }

                selectedBookIds.add(selected.getBook_id());
                selectedBooks.add(selected);
                addBookToHBox(selected);
                bookComboBox.getEditor().clear();
                bookComboBox.getSelectionModel().clearSelection();
            }
        });
    }

    private void updateComboBoxResults(String keyword) throws SQLException {
        List<Book> found = Book.searchBooks(keyword); // sync call
        bookResults.setAll(found);
        bookComboBox.show();
    }

    private void addBookToHBox(Book book) {
        ImageView imageView = new ImageView(book.getCoverImage());
        imageView.setFitWidth(70);
        imageView.setFitHeight(100);

        VBox bookCard = new VBox(5, imageView);
        bookCard.setAlignment(Pos.CENTER);
        selectedBooksBox.setMargin(bookCard, new Insets(15));
        selectedBooksBox.getChildren().add(bookCard);
    }

    @FXML
    public void choose() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) choose.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            selectedImageFile = file;
            System.out.println("Selected image: " + file.getAbsolutePath());
        }
    }

    @FXML
    public void confirm() {
        String clubName = name.getText().trim();
        String clubDesc = description.getText().trim();

        if (clubName.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a club name.").showAndWait();
            return;
        }

        if (selectedBooks.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select at least one book.").showAndWait();
            return;
        }

        if (selectedImageFile == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a picture for the club.").showAndWait();
            return;
        }

        try {
            // Assuming createClub takes (String name, String desc, List<Book>, File image)
            int club_id = Club.createClub(clubName, clubDesc, user_id ,selectedImageFile);
            List<Integer> bookid = new ArrayList<>();
            for(Book books : selectedBooks)
            {
                bookid.add(books.getBook_id());
            }
            Club.addBooksDiscussed(club_id, bookid);
            new Alert(Alert.AlertType.INFORMATION, "Club created successfully!").showAndWait();

            // Close window
            Stage stage = (Stage) confirm.getScene().getWindow();
            stage.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to create club: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    public void cancel() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }
}
