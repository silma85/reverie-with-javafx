/**
 * 
 */
package it.vermilionsands.reverie.game.worker;

import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.Randomizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Command matcher
 * 
 * @author alessandro.putzu
 *
 */
@Component
public class CommandMatcher {

  @Autowired
  private Messages messages;

  @Autowired
  private Randomizer randomizer;

  public String match(String command) {

    Pattern metaPattern = Pattern.compile(messages.get("reverie.gui.command.list.meta"));
    Matcher metaMatcher = metaPattern.matcher(command);

    if (metaMatcher.matches()) {
      return doAsCommand(metaMatcher.group());
    }

    return randomizer.rollString(messages.get("reverie.gui.command.refused"));
  }

  private String doAsCommand(String command) {

    switch (command) {
    case "who":
    case "whois":
      return messages.get("reverie.gui.game.about");

    case "versione":
      return messages.get("reverie.gui.title") + messages.get("reverie.version");

    default:
      return "";
    }

  }
}
