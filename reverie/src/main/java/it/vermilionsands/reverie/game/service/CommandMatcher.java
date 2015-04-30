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
import it.vermilionsands.reverie.game.service.internal.GameService;
import it.vermilionsands.reverie.game.service.internal.ItemService;
import it.vermilionsands.reverie.game.service.internal.PlayerCharacterService;
import it.vermilionsands.reverie.game.service.internal.RoomService;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  /**
   * grammar:
   * [verb][article?][keyword?][preposition?][keyword?]
   * 
   * I CANNOT identify keywords directly because not all keywords are known beforehand.
   * So:
   * 
   * <pre>
   *    a) Keyword 1 is text between verb.end() and preposition start (if any) or command end.
   *    b) Keyword 2 is text between preposition end (if any) and command end.
   *    c) Trim and try identification.
   * </pre>
   * 
   * <pre>
   * 1) identify all components of a sentence
   *    a) verb
   *    b) article or nothing
   *    c) keyword 1 and/or keyword 2 or nothing
   *    d) preposition or nothing
   * 2) if no verb, return
   * 3) if unknown verb, return
   * 4) if verb is direction or intransitive, process it and return result
   * 5) if verb is transitive
   *    a) Process keyword 1. If not an item, or more than one, return.
   *    b) If an item, process verb with item 1 and return result
   * 6) if verb is 3-way
   *    a) Process keyword 1. If not an item, or more than one, return.
   *    b) Process preposition. If not correct (...leniency...) return hint.
   *    c) Process keyword 2. If not an item, or more than one, return.
   *    d) Process verb with item 1 and item 2.
   * </pre>
   * 
   * @param command
   * @return
   */
  public String match(String command) {

    final GameState state = gameService.getCurrentState();

    // Case 0: if command is empty and room is initial, go up.
    if (StringUtils.isEmpty(command) && state.getCurrentRoom().getCode().equals(Constants.ROOM_DEFAULT))
      return gameService.goToDirection(state, Directions.U);

    final Matcher verbMatcher = Pattern.compile(messages.get("command.list")).matcher(command);

    // If no verb, return.
    if (!verbMatcher.matches()) {
      return randomizer.rollString(messages.get(Constants.COMMAND_REFUSED));
    }

    final Matcher articleMatcher = Pattern.compile(messages.get("command.articles")).matcher(command);
    final Matcher prepositionMatcher = Pattern.compile(messages.get("command.prepositions")).matcher(command);

    final String verb = verbMatcher.group();

    // Directions
    final Matcher directionsMatcher = Pattern.compile(messages.get("command.list.directions")).matcher(verb);
    if (directionsMatcher.matches()) {
      return this.goToDirection(state, directionsMatcher.group());
    }

    // About
    if (Arrays.asList(messages.get("command.list.meta.who").split(Constants.SEPARATOR_OPTION)).contains(verb)) {
      return messages.get("reverie.gui.game.about");
    }

    // Version
    if (Arrays.asList(messages.get("command.list.meta.version").split(Constants.SEPARATOR_OPTION)).contains(verb)) {
      return String.format("%s %s", messages.get("reverie.version"), messages.get("reverie.gui.title"));
    }

    // Inventory
    if (Arrays.asList(messages.get("command.list.meta.inventory").split(Constants.SEPARATOR_OPTION)).contains(verb)) {
      return pcService.getInventoryText(state.getPlayerCharacter());
    }

    // Intransitive-generic
    if (Arrays.asList(messages.get("command.list.intransitive").split(Constants.SEPARATOR_OPTION)).contains(verb)) {
      return gameService.doIntransitiveCommand(verb);
    }

    // TODO continue implementation of matcher

    // From this point on, we need at least an object.
    final String commandObjectKeyword = command.substring(
            articleMatcher.matches() ? articleMatcher.end() : verbMatcher.end(),
            prepositionMatcher.matches() ? prepositionMatcher.start() : command.length()).trim();
    final String commandComplement = prepositionMatcher.matches() ? command.substring(prepositionMatcher.end(),
            command.length()).trim() : null;

    final List<Item> commandObjectItems = itemService.narrowToPresent(state,
            itemService.findByKeywords(commandObjectKeyword));
    Item commandObject = null;
    if (commandObjectItems.isEmpty()) {
      return randomizer.rollString(messages.get("items.command.refused.unfound"), commandObjectKeyword);
    } else if (commandObjectItems.size() == 1) {
      commandObject = commandObjectItems.get(0);
    } else {
      return this.disambiguateItems(commandObjectItems);
    }

    return randomizer.rollString(messages.get(Constants.COMMAND_REFUSED));
  }

  /**
   * Do a generic transitive command
   * 
   * @param state
   * @param command
   * @param itemKeywords
   * @return
   */
  private String doOtherTransitiveCommand(final GameState state, final String command, final String itemKeywords) {

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

    // If all else fails...
    return randomizer.rollString(messages.get("items.command.unfound"), messages.get(item.getTitle()));
  }

  /**
   * Return an item code, or a disambiguation message.
   * 
   * @param items
   * @return
   */
  private String disambiguateItems(final List<Item> items) {

    final StringBuilder sb = new StringBuilder();
    for (Item item : items) {
      sb.append(messages.get(item.getTitle()));

      if (items.indexOf(item) == items.size() - 2) {
        sb.append(" o ");
      } else if (items.indexOf(item) == items.size() - 1) {
        sb.append("");
      } else {
        sb.append(", ");
      }
    }

    return String.format(messages.get("items.disambiguate"), sb.toString());
  }

  /**
   * @param state
   * @param string
   * @return
   */
  private String goToDirection(final GameState state, final String directionKeyword) {

    if (Arrays.asList(messages.get("command.list.directions.n").split(Constants.SEPARATOR_OPTION)).contains(
            directionKeyword))
      return gameService.goToDirection(state, Directions.N);

    if (Arrays.asList(messages.get("command.list.directions.s").split(Constants.SEPARATOR_OPTION)).contains(
            directionKeyword))
      return gameService.goToDirection(state, Directions.S);

    if (Arrays.asList(messages.get("command.list.directions.w").split(Constants.SEPARATOR_OPTION)).contains(
            directionKeyword))
      return gameService.goToDirection(state, Directions.W);

    if (Arrays.asList(messages.get("command.list.directions.e").split(Constants.SEPARATOR_OPTION)).contains(
            directionKeyword))
      return gameService.goToDirection(state, Directions.E);

    if (Arrays.asList(messages.get("command.list.directions.u").split(Constants.SEPARATOR_OPTION)).contains(
            directionKeyword))
      return gameService.goToDirection(state, Directions.U);

    if (Arrays.asList(messages.get("command.list.directions.d").split(Constants.SEPARATOR_OPTION)).contains(
            directionKeyword))
      return gameService.goToDirection(state, Directions.D);

    return "WARNING: A direction was written in the list of all directions, but not in the lists for single directions!";
  }
}
