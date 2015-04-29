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

  /**
   * Command patterns:
   * command.list.meta.who
   * command.list.meta.version
   * command.list.meta.inventory
   * command.list.directions.n
   * command.list.directions.s
   * command.list.directions.w
   * command.list.directions.e
   * command.list.directions.u
   * command.list.directions.d
   *
   * ...and also
   * 
   * command.list.transitive
   * command.list.transitive.pickup
   * command.list.transitive.putdown
   * command.list.intransitive
   */
  @PostConstruct
  public void init() {
    this.whoPattern = Pattern.compile(messages.get("command.list.meta.who"));
    this.versionPattern = Pattern.compile(messages.get("command.list.meta.version"));
    this.inventoryPattern = Pattern.compile(messages.get("command.list.meta.inventory"));
    this.directionPatternN = Pattern.compile(messages.get("command.list.directions.n"));
    this.directionPatternS = Pattern.compile(messages.get("command.list.directions.s"));
    this.directionPatternW = Pattern.compile(messages.get("command.list.directions.w"));
    this.directionPatternE = Pattern.compile(messages.get("command.list.directions.e"));
    this.directionPatternU = Pattern.compile(messages.get("command.list.directions.u"));
    this.directionPatternD = Pattern.compile(messages.get("command.list.directions.d"));
    this.iActionPattern = Pattern.compile(messages.get("command.list.intransitive"));
    this.tActionPattern = Pattern.compile(messages.get("command.list.transitive"));
    this.pickupPattern = Pattern.compile(messages.get("command.list.transitive.pickup"));
    this.putdownPattern = Pattern.compile(messages.get("command.list.transitive.putdown"));
  }

  private Pattern whoPattern;
  private Pattern versionPattern;
  private Pattern inventoryPattern;
  private Pattern directionPatternN;
  private Pattern directionPatternS;
  private Pattern directionPatternW;
  private Pattern directionPatternE;
  private Pattern directionPatternU;
  private Pattern directionPatternD;
  private Pattern iActionPattern;
  private Pattern tActionPattern;
  private Pattern pickupPattern;
  private Pattern putdownPattern;

  public String match(String command) {

    // If command is empty, it could be the first room.
    final GameState state = gameService.getCurrentState();
    if (Constants.ROOM_DEFAULT.equals(state.getCurrentRoom().getCode())) {
      return this.advanceRoom(state, Directions.U);
    }

    // Whois
    if (whoPattern.matcher(command).matches()) {
      return gameService.doIntransitiveCommand(whoPattern.matcher(command).group());
    }

    // Version
    if (versionPattern.matcher(command).matches()) {
      return gameService.doIntransitiveCommand(versionPattern.matcher(command).group());
    }

    // Inventory
    if (inventoryPattern.matcher(command).matches()) {
      return gameService.doIntransitiveCommand(inventoryPattern.matcher(command).group());
    }

    // Direction commands
    if (directionPatternN.matcher(command).matches()) {
      return advanceRoom(state, Directions.N);
    }

    if (directionPatternS.matcher(command).matches()) {
      return advanceRoom(state, Directions.S);
    }

    if (directionPatternW.matcher(command).matches()) {
      return advanceRoom(state, Directions.W);
    }

    if (directionPatternE.matcher(command).matches()) {
      return advanceRoom(state, Directions.E);
    }

    if (directionPatternU.matcher(command).matches()) {
      return advanceRoom(state, Directions.U);
    }

    if (directionPatternD.matcher(command).matches()) {
      return advanceRoom(state, Directions.D);
    }

    // Pickup commands
    final Matcher pickupMatcher = pickupPattern.matcher(command);
    if (pickupMatcher.matches()) {
      if (!StringUtils.isEmpty(command.substring(pickupMatcher.end(), command.length())))
        return doPickupCommand(state, command.substring(pickupMatcher.end(), command.length()));
      else
        return randomizer.rollString(messages.get("command.refused.transitive"), command.trim());
    }

    // Put down commands
    final Matcher putdownMatcher = putdownPattern.matcher(command);
    if (putdownMatcher.matches()) {
      if (!StringUtils.isEmpty(command.substring(putdownMatcher.end(), command.length())))
        return doPutdownCommand(state, command.substring(putdownMatcher.end(), command.length()));
      else
        return randomizer.rollString(messages.get("command.refused.transitive"), command.trim());
    }

    // Intransitive action commands
    if (iActionPattern.matcher(command).matches()) {
      return gameService.doIntransitiveCommand(iActionPattern.matcher(command).group());
    }

    // Transitive action commands
    final Matcher tActionMatcher = tActionPattern.matcher(command);

    final boolean hasAction = tActionMatcher.find();
    // TODO still trim articles!
    final boolean hasItem = hasAction && command.trim().length() > tActionMatcher.end(); // Meaning there's more than an action here.

    if (hasAction && hasItem) {
      return doOtherTransitiveCommand(state, tActionMatcher.group(),
              command.substring(tActionMatcher.end(), command.length()).trim());
    } else if (hasAction) {
      return randomizer.rollString(messages.get("command.refused.transitive"), command.trim());
    }

    return randomizer.rollString(messages.get("command.refused"));
  }

  private String doPutdownCommand(final GameState state, final String keywords) {
    final List<Item> items = itemService.narrowToPresent(state, itemService.findByKeywords(keywords));
    Item item = null;
    if (items.isEmpty()) {
      return randomizer.rollString(messages.get("items.command.refused.unfound"), keywords);
    } else if (items.size() == 1) {
      item = items.get(0);
    } else {
      return this.disambiguateItems(items);
    }

    return gameService.dropItem(item);
  }

  /**
   * Execute a pickup command.
   * 
   * @param state
   * @param keywords
   * @return
   */
  private String doPickupCommand(final GameState state, final String keywords) {
    final List<Item> items = itemService.narrowToPresent(state, itemService.findByKeywords(keywords));
    Item item = null;
    if (items.isEmpty()) {
      return randomizer.rollString(messages.get("items.command.refused.unfound"), keywords);
    } else if (items.size() == 1) {
      item = items.get(0);
    } else {
      return this.disambiguateItems(items);
    }

    return gameService.pickupItem(item);
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
   * @param dir
   * @return
   */
  private String advanceRoom(final GameState state, Directions dir) {
    if (gameService.tryAdvanceRoom(state, dir)) {
      return messages.get(Constants.COMMAND_ACCEPTED);
    } else {
      return messages.get("command.refused.direction");
    }
  }
}
