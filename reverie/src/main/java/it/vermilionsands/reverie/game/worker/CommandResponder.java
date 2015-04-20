/**
 * 
 */
package it.vermilionsands.reverie.game.worker;

import it.vermilionsands.reverie.game.Randomizer;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author alessandro.putzu
 *
 */
@Component
@Setter
public class CommandResponder {

  @Autowired
  private Randomizer randomizer;

  @Autowired
  private CommandMatcher commandMatcher;

  private TextArea receiver;

  private TextField commander;

  /**
   * Responds to command and writes to the provided TextArea.
   * 
   * @param command
   */
  public void respondToCommand(String command) {

    String response = commandMatcher.match(command);

    receiver.setText(response);
    commander.clear();
  }

}
