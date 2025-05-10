//package controller;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import javafx.scene.input.MouseEvent;
//import java.io.IOException;
//
//public class BaseController {
//
//    public void goToPreviousPage(MouseEvent event, String previousFXMLPath) {
//        try {
//
//            // Load the previous FXML file
//            Parent previousPage = FXMLLoader.load(getClass().getResource(previousFXMLPath));
//
//            // Get the current stage (window)
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//
//            // Set the new scene
//            stage.setScene(new Scene(previousPage));
//            stage.show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
////    public void goToNextPage(MouseEvent event, String nextFXMLPath) {
////        try {
////
////            // Load the next FXML file
////            Parent nextPage = FXMLLoader.load(getClass().getResource(nextFXMLPath));
////
////            // Get the current stage (window)
////            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
////
////            // Set the new scene
////            stage.setScene(new Scene(nextPage));
////            stage.show();
////
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
//
//}
