package it.vermilionsands.reverie.gui;

import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.worker.CommandResponder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.annotation.PostConstruct;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;

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
  private Messages messages;

  @PostConstruct
  public void init() {
    this.adventureText.setText(messages.get("system.debug.message"));
    this.commandResponder.setReceiver(adventureText);
    this.commandResponder.setCommander(adventureCommands);
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
  private void sayButtonAction(ActionEvent event) {
    commandResponder.respondToCommand(adventureCommands.getText());
  }
}
