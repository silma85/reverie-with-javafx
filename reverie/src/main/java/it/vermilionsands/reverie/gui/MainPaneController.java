package it.vermilionsands.reverie.gui;

import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.service.CommandResponder;
import it.vermilionsands.reverie.game.service.internal.GameService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import javax.annotation.PostConstruct;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import javafx.scene.image.Image;

/**
 * This is also a configuration bean.
 * 
 * @author alessandro.putzu
 *
 */
@Getter
public class MainPaneController {

  @Autowired
  private CommandResponder commandResponder;

  @Autowired
  private GameService gameService;

  @Autowired
  private Messages messages;

  @PostConstruct
  public void init() {
    // this.adventureText.setText(messages.get("system.debug.message"));

    this.commandResponder.setCommandReceiver(adventureCommandResponses);
    this.commandResponder.setCommander(adventureCommands);

    this.adventureCommands.requestFocus();
  }

  /*
   * FXML Fields
   */
  @FXML
  private Node root;

  @FXML
  private TextArea adventureText;

  @FXML
  private TextField adventureCommands;

  @FXML
  private TextArea adventureCommandResponses;

  @FXML
  private Text statusText;

  @FXML
  private MenuItem fileExit;

  @FXML
  private MenuItem debugReloadPatterns;

  @FXML
  private MenuItem debugReloadItems;

  @FXML Image adventureImage;

  private void sayButtonAction() {
    commandResponder.getHistory().add(adventureCommands.getText());
    commandResponder.incrementHistoryIndex(1);
    commandResponder.respondToCommand(adventureCommands.getText());
  }

  @FXML
  private void keyPressedAction(KeyEvent event) {

    if (event.isConsumed())
      return;

    switch (event.getCode()) {
    case UP:
    case NUMPAD8:
      commandResponder.incrementHistoryIndex(-1);
      adventureCommands.setText(commandResponder.getHistory().get(commandResponder.getHistoryIndex()));

      break;

    case DOWN:
    case NUMPAD2:
      commandResponder.incrementHistoryIndex(1);
      adventureCommands.setText(commandResponder.getHistory().get(commandResponder.getHistoryIndex()));

      break;

    case ENTER:
      this.sayButtonAction();
      this.adventureCommands.requestFocus();
      break;

    default:
      break;
    }
  }

  @FXML
  public void fileExitAction(ActionEvent event) {
    Platform.exit();
  }

  @FXML
  public void reloadPatternAction(ActionEvent event) {
    commandResponder.getCommandMatcher().init();
    this.adventureCommandResponses.setText(messages.get("reverie.gui.debug.reload.actions"));
  }

  @FXML
  public void reloadObjectsAction(ActionEvent event) {
    gameService.init();
    this.adventureCommandResponses.setText(messages.get("reverie.gui.debug.reload.objects"));
  }
}
