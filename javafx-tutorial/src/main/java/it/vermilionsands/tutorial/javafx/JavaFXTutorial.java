package it.vermilionsands.tutorial.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaFXTutorial extends Application {

  private static final Logger log = LoggerFactory.getLogger(JavaFXTutorial.class);

  @Override
  public void start(final Stage primaryStage) {
    try {
      VBox root = (VBox) FXMLLoader.load(getClass().getResource("MainPane.fxml"));

      // Button btnNuke = new Button();
      // btnNuke.setId("shiny-orange");
      // btnNuke.setText("Nuke Moscow");
      // btnNuke.setOnAction(new EventHandler<ActionEvent>() {
      //
      // @Override
      // public void handle(ActionEvent event) {
      // log.info("You genocidal monster! Millions die...");
      // }
      // });
      //
      // Button btnExit = new Button();
      // btnExit.setText("Out of here");
      // btnExit.setOnAction(new EventHandler<ActionEvent>() {
      //
      // @Override
      // public void handle(ActionEvent event) {
      // primaryStage.close();
      // }
      // });
      //
      // root.getChildren().add(btnNuke);
      // root.getChildren().add(btnExit);

      // Create a Scene
      Scene scene = new Scene(root);
      scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

      // Set the scene on the primary stage
      primaryStage.setResizable(false);
      primaryStage.getIcons().add(new Image("icons/nuclear.png"));
      primaryStage.setTitle("Nuclear Launch Detected");
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (Exception e) {
      log.error("Error initializing the Scene graph", e);
    }
  }

  public static void launchInternal(String[] args) {
    launch(args);
  }
}
