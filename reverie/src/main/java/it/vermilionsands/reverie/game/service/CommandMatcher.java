/**
 * 
 */
package it.vermilionsands.reverie.game.service;

import it.vermilionsands.reverie.configuration.Constants;
import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.Randomizer;
import it.vermilionsands.reverie.game.domain.GameState;
import it.vermilionsands.reverie.game.domain.Room;
import it.vermilionsands.reverie.game.service.internal.GameService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger log = LoggerFactory.getLogger(CommandMatcher.class);

  @Autowired
  private Messages messages;

  @Autowired
  private Randomizer randomizer;

  @Autowired
  private GameService worker;

  @PostConstruct
  public void init() {
    metaPattern = Pattern.compile(messages.get("reverie.gui.command.list.meta"));
    directionPattern = Pattern.compile(messages.get("reverie.gui.command.list.directions"));
    iActionPattern = Pattern.compile(messages.get("reverie.gui.command.list.intransitive"));
    tActionPattern = Pattern.compile(messages.get("reverie.gui.command.list.transitive"));
  }

  private Pattern metaPattern;
  private Pattern directionPattern;
  private Pattern iActionPattern;
  private Pattern tActionPattern;

  public String match(String command) {

    // Meta commands
    final Matcher metaMatcher = metaPattern.matcher(command);

    if (metaMatcher.matches()) {
      return doAsCommand(metaMatcher.group());
    }

    // Direction commands
    final Matcher directionMatcher = directionPattern.matcher(command);

    if (directionMatcher.matches()) {
      return doAsCommand(directionMatcher.group());
    }

    // Intransitive action commands
    final Matcher iActionMatcher = iActionPattern.matcher(command);

    if (iActionMatcher.matches()) {
      return doAsCommand(iActionMatcher.group());
    }

    // Transitive action commands
    final Matcher tActionMatcher = tActionPattern.matcher(command);

    final boolean hasAction = tActionMatcher.find();
    final boolean hasItem = command.length() > tActionMatcher.end(); // Meaning there's more than an action here.
    if (hasAction && hasItem) {

      String itemKeyword = command.substring(tActionMatcher.end()).trim();
      if (!worker.checkItemPresent(itemKeyword)) {
        return randomizer.rollString(messages.get("reverie.gui.command.refused.unfound"), itemKeyword);
      }

      return doAsCommand(tActionMatcher.group(), itemKeyword);

    } else if (hasAction) {
      return randomizer.rollString(messages.get("reverie.gui.command.refused.transitive"), command);
    }

    // Possible common errors.
    if (command.split(" ")[0].matches("(are|ere|ire)$")) {
      return messages.get("reverie.gui.command.refused.infinitive");
    }

    return randomizer.rollString(messages.get("reverie.gui.command.refused"));
  }

  private String doAsCommand(String command, String item) {

    // Get gamestate. Response is context-sensitive...
    final GameState state = worker.getCurrentState();

    // Next room placeholder.
    Room next = null;

    switch (command) {
    case "osserva":
    case "esamina":
    case "guarda":
    case "apri":
    case "chiudi":
    case "spingi":
    case "tira":
    case "lancia":
    case "picchia":
    case "calcia":
    case "colpisci":
    case "raccogli":
    case "lascia":
    case "prendi":
    case "impugna":
    case "estrai":
    case "usa":
    case "sfodera":
    case "rinfodera":
    case "lava":
    case "affila":

      break;

    default:
      break;
    }

    return "";
  }

  private String doAsCommand(String command) {

    // Get gamestate. Response is context-sensitive...
    final GameState state = worker.getCurrentState();

    // Next room placeholder.
    Room next = null;

    switch (command) {
    // Meta commands
    case "who":
    case "whois":
    case "chi":
    case "ciao":
      return messages.get("reverie.gui.game.about");

    case "versione":
    case "v":
      return String.format("%s v%s", messages.get("reverie.gui.title"), messages.get("reverie.version"));

      // Direction commands
    case "n":
    case "north":
    case "nord":
      next = state.getCurrentRoom().getNorth();
      return advanceRoom(state, next);
    case "s":
    case "south":
    case "sud":
      next = state.getCurrentRoom().getSouth();
      return advanceRoom(state, next);
    case "w":
    case "o":
    case "west":
    case "ovest":
      next = state.getCurrentRoom().getWest();
      return advanceRoom(state, next);
    case "e":
    case "east":
    case "est":
      next = state.getCurrentRoom().getEast();
      return advanceRoom(state, next);
    case "u":
    case "up":
    case "su":
      next = state.getCurrentRoom().getUp();
      return advanceRoom(state, next);
    case "d":
    case "down":
    case "giu":
      next = state.getCurrentRoom().getDown();
      return advanceRoom(state, next);

      // Intransitivi
    case "parla":
      return randomizer.rollString(messages.get("reverie.creatures.self.chatter"), new String[] {
              randomizer.rollName(), String.valueOf(randomizer.roll(7, 149)) });
    case "minaccia":
      return "";
    case "salta":
      return "";
    case "siediti":
    case "siedi":

    default:
      log.warn(String.format("Attention: command %s should exist, but was not found in CommandMatcher.doAsCommand!",
              command));
      return messages.get(Constants.COMMAND_NOT_FOUND);
    }

  }

  /**
   * @param state
   * @param next
   * @return
   */
  private String advanceRoom(final GameState state, Room next) {
    if (next != null) {
      worker.advanceRoom(state, next);
      return messages.get(Constants.COMMAND_ACCEPTED);
    } else {
      return messages.get("reverie.gui.command.refused.direction");
    }
  }
}
