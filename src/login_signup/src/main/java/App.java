import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        primaryStage.initStyle(StageStyle.UNDECORATED);

        //debug
        //System.out.println(getClass().getResource("/view/login.fxml"));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/signup.fxml"));
        Parent root = fxmlLoader.load();

        //primaryStage.setTitle("Login Window"); // Optional: set a title
        Scene scene = new Scene(root, 1238, 700);
        primaryStage.setScene(scene);

       // primaryStage.setMaximized(true); // Start in full-screen mode
        primaryStage.setResizable(true); // Allow window resizing

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
