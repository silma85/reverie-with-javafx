/**
 * 
 */
package it.vermilionsands.reverie.game.service;

import it.vermilionsands.reverie.game.Randomizer;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author alessandro.putzu
 *
 */
@Component
@Setter
@Getter
public class CommandResponder {

  @Autowired
  private Randomizer randomizer;

  @Autowired
  private CommandMatcher commandMatcher;

  private TextArea commandReceiver;

  private TextField commander;

  private List<String> history = new ArrayList<String>();

  private int historyIndex = 0;

  public int incrementHistoryIndex(final int step) {
    historyIndex += step;

    if (historyIndex < 0) {
      historyIndex = 0;
    }

    if (historyIndex >= history.size()) {
      historyIndex = history.size() - 1;
    }

    return historyIndex;
  }

  /**
   * Responds to command and writes to the provided TextArea.
   * 
   * @param command
   */
  public void respondToCommand(String command) {

    String response = commandMatcher.match(command);

    commandReceiver.setText(response);
    commander.clear();
  }

}
