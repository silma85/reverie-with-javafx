package it.vermilionsands.reverie;

import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.gui.MainPaneController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:root-context.xml")
public class ReverieApplication extends Application {

  private static final Logger log = LoggerFactory.getLogger(ReverieApplication.class);

  private Messages messages;

  private static String[] args;

  @Override
  public void start(final Stage primaryStage) {

    // Bootstrap Spring context here.
    ApplicationContext context = SpringApplication.run(ReverieApplication.class, args);
    messages = context.getBean(Messages.class);
    MainPaneController mainPaneController = context.getBean(MainPaneController.class);

    // Create a Scene
    Scene scene = new Scene((Parent) mainPaneController.getRoot());
    scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

    // Set the scene on the primary stage
    primaryStage.setResizable(false);
    primaryStage.getIcons().add(new Image("icons/spiral-icon.png"));
    primaryStage.setTitle(String.format("%s %s", messages.get("reverie.gui.title"), messages.get("reverie.version")));
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {

    ReverieApplication.args = args;

    launch(args);
  }
}
