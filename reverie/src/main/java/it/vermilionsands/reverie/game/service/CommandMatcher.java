/**
 * 
 */
package it.vermilionsands.reverie.game.service;

import it.vermilionsands.reverie.configuration.Constants;
import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.Randomizer;
import it.vermilionsands.reverie.game.domain.Directions;
import it.vermilionsands.reverie.game.domain.GameState;
import it.vermilionsands.reverie.game.domain.Item;
import it.vermilionsands.reverie.game.domain.Room;
import it.vermilionsands.reverie.game.service.internal.GameService;
import it.vermilionsands.reverie.game.service.internal.ItemService;
import it.vermilionsands.reverie.game.service.internal.PlayerCharacterService;
import it.vermilionsands.reverie.game.service.internal.RoomService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
  private GameService gameService;

  @Autowired
  private ItemService itemService;

  @Autowired
  private RoomService roomService;

  @Autowired
  private PlayerCharacterService pcService;

  @PostConstruct
  public void init() {
    this.metaPattern = Pattern.compile(messages.get("reverie.gui.command.list.meta"));
    this.directionPattern = Pattern.compile(messages.get("reverie.gui.command.list.directions"));
    this.iActionPattern = Pattern.compile(messages.get("reverie.gui.command.list.intransitive"));
    this.tActionPattern = Pattern.compile(messages.get("reverie.gui.command.list.transitive"));
    this.itemsPattern = Pattern.compile(messages.get("items.all.pattern"));
  }

  private Pattern metaPattern;
  private Pattern directionPattern;
  private Pattern iActionPattern;
  private Pattern tActionPattern;
  private Pattern itemsPattern;

  public String match(String command) {

    // If command is empty, it could be the first room.
    final GameState state = gameService.getCurrentState();
    if (Constants.ROOM_DEFAULT.equals(state.getCurrentRoom().getCode())) {
      return this.advanceRoom(state, Directions.U);
    }

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
    // TODO still trim articles!
    final boolean hasItem = hasAction && command.trim().length() > tActionMatcher.end(); // Meaning there's more than an action here.

    if (hasAction && hasItem) {
      return doAsCommand(tActionMatcher.group(), command.substring(tActionMatcher.end(), command.length()).trim());
    } else if (hasAction) {
      return randomizer.rollString(messages.get("reverie.gui.command.refused.transitive"), command.trim());
    }

    return randomizer.rollString(messages.get("reverie.gui.command.refused"));
  }

  private String doAsCommand(final String command, final String itemKeywords) {

    // Get gamestate. Response is context-sensitive...
    final GameState state = gameService.getCurrentState();

    final List<Item> items = itemService.narrowToPresent(state, itemService.findByKeywords(itemKeywords));
    Item item = null;
    if (items.isEmpty()) {
      return randomizer.rollString(messages.get("items.command.refused.unfound"), itemKeywords);
    } else if (items.size() == 1) {
      item = items.get(0);
    } else {
      return this.disambiguateItems(items);
    }

    // Return an action- and item-specific description, if any.
    final String actionSpecificMessage = messages.get(item.getTitle() + ".actions." + command
            + (item.isFlipped() ? Constants.ITEM_DESCRIPTION_FLIPPED_SUFFIX : Constants.ITEM_DESCRIPTION_SUFFIX));
    if (!StringUtils.isEmpty(actionSpecificMessage)) {
      // TODO more complex execution? Or the facilities in flipactions are already enough?
      return actionSpecificMessage;
    }

    final boolean actionValid = itemService.validateCommand(item, command, state);

    // If command is a flipaction and item was already flipped, return flipped(-action-specific)-desc
    if (actionValid && item.isFlipped() && itemService.isFlipAction(item, command)) {
      final String alreadyFlippedSpecificMessage = messages.get(item.getTitle() + Constants.ITEM_FLIP_SUFFIX + "."
              + command + ".already");
      final String alreadyFlippedMessage = messages.get(item.getTitle() + Constants.ITEM_FLIP_SUFFIX + ".already");
      if (!StringUtils.isEmpty(alreadyFlippedSpecificMessage))
        return alreadyFlippedSpecificMessage;
      else if (!StringUtils.isEmpty(alreadyFlippedMessage))
        return alreadyFlippedMessage;
      else
        return messages.get("items.flip.already");
    }

    // Validate action.
    if (!actionValid) {
      String specificNoFlip = messages.get(item.getTitle() + ".actions." + command + Constants.ITEM_NOFLIP_SUFFIX);
      return StringUtils.isEmpty(specificNoFlip) ? messages.get(item.getTitle() + Constants.ITEM_NOFLIP_SUFFIX)
              : specificNoFlip;
    }

    // Flip if this is a flip-action for this item
    if (gameService.flipItem(state, item, command)) {
      return messages.get(item.getTitle() + Constants.ITEM_FLIP_SUFFIX);
    }

    // Process more generic actions.
    switch (command) {
    case "prendi":
    case "raccogli":
      return gameService.pickupItem(item);

      // Not direction commands, but related to
    case "sali":
    case "entra":
      final String toRoom = messages.get(String.format("%s.actions.%s.toroom", item.getTitle(), command));
      final Room next = roomService.findByCode(toRoom);
      gameService.advanceRoom(state, next);
      return messages.get(String.format("%s.actions.%s.toroom.description", item.getTitle(), command));

    case "osserva":
    case "esamina":
    case "guarda":
      return itemService.lookItem(item);

    default:
      return randomizer.rollString(messages.get("items.command.unfound"), messages.get(item.getTitle()));
    }
  }

  private String disambiguateItems(final List<Item> items) {

    final StringBuilder sb = new StringBuilder();
    for (Item item : items) {
      sb.append(messages.get(item.getTitle()));

      if (items.indexOf(item) == items.size() - 2) {
        sb.append(" o ");
      } else if (items.indexOf(item) == items.size() - 1) {
        sb.append("?");
      } else {
        sb.append(", ");
      }
    }

    return String.format(messages.get("items.disambiguate"), sb.toString());
  }

  private String doAsCommand(String command) {

    // Get gamestate. Response is context-sensitive...
    final GameState state = gameService.getCurrentState();

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
      return gameService.getVersion();

    case "inventario":
    case "i":
    case "zaino":
      return pcService.getInventoryText(state.getPlayerCharacter());

      // Direction commands
    case "cammina":
    case "prosegui":
      if (!StringUtils.isEmpty(state.getLastDirection())) {
        return doAsCommand(state.getLastDirection());
      } else {
        return messages.get("items.command.refused.direction", command);
      }
    case "n":
    case "north":
    case "nord":
      return advanceRoom(state, Directions.N);
    case "s":
    case "south":
    case "sud":
      return advanceRoom(state, Directions.S);
    case "w":
    case "o":
    case "west":
    case "ovest":
      return advanceRoom(state, Directions.W);
    case "e":
    case "east":
    case "est":
      return advanceRoom(state, Directions.E);
    case "u":
    case "up":
    case "su":
    case "sali":
      return advanceRoom(state, Directions.U);
    case "d":
    case "down":
    case "giu":
    case "scendi":
      return advanceRoom(state, Directions.D);

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
      return "";
    case "canta":
      return randomizer.rollString(messages.get("reverie.creatures.self.sing"));

    default:
      log.warn(String.format("Attention: command %s should exist, but was not found in CommandMatcher.doAsCommand!",
              command));
      return messages.get(Constants.COMMAND_NOT_FOUND);
    }

  }

  /**
   * @param state
   * @param dir
   * @return
   */
  private String advanceRoom(final GameState state, Directions dir) {
    if (gameService.tryAdvanceRoom(state, dir)) {
      return messages.get(Constants.COMMAND_ACCEPTED);
    } else {
      return messages.get("reverie.gui.command.refused.direction");
    }
  }
}
