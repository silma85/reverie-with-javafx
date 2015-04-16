package it.vermilionsands.tutorial.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class JavaFXTutorial extends Application {

  @Override
  public void start(Stage primaryStage) {
    try {
      // Create a root
      BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("MainPane.fxml"));

      // Create a Scene
      Scene scene = new Scene(root, 400, 400);
      scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

      // Set the scene on the primary stage
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void launchInternal(String[] args) {
    launch(args);
  }
}
