/**
 * 
 */
package it.vermilionsands.reverie.game.service.internal;

import it.vermilionsands.reverie.configuration.Constants;
import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.Randomizer;
import it.vermilionsands.reverie.game.domain.Directions;
import it.vermilionsands.reverie.game.domain.GameState;
import it.vermilionsands.reverie.game.domain.Item;
import it.vermilionsands.reverie.game.domain.PlayerCharacter;
import it.vermilionsands.reverie.game.domain.Room;
import it.vermilionsands.reverie.game.repository.GameStateRepository;
import it.vermilionsands.reverie.gui.MainPaneController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Manages the main loop, input / output operations
 * 
 * @author a.putzu
 * 
 */
@Service
@Data
public class GameService {

  private static final Logger log = LoggerFactory.getLogger(GameService.class);

  private final Set<Room> visitedRooms = new HashSet<Room>();

  @Autowired
  private Randomizer randomizer;

  @Autowired
  private PlayerCharacterService pcService;

  @Autowired
  private RoomService roomService;

  @Autowired
  private ItemService itemService;

  @Autowired
  private GameStateRepository gameRepository;

  @Autowired
  private MainPaneController controller;

  @Autowired
  private Messages messages;

  @PostConstruct
  public void init() {
    final boolean items = itemService.initItems();
    final boolean rooms = roomService.initRooms();
    final PlayerCharacter pc = pcService.createPC();

    if (rooms && items) {
      final Room starting = roomService.getStartingRoom();

      GameState state = new GameState();
      state.setPlayerCharacter(pc);
      state.setCurrentRoom(starting);
      state = gameRepository.save(state);

      StringBuffer briefing = new StringBuffer();
      briefing.append(randomizer.rollString(messages.get("reverie.gui.intro.a"), pc.getName()))
              .append(messages.get("reverie.gui.intro.b"))
              .append(randomizer.rollString(messages.get("reverie.gui.intro.c")))
              .append(messages.get("reverie.gui.intro.d"));

      controller.getAdventureText().setText(briefing.toString());
      controller.getAdventureCommandResponses().setText(messages.get("command.intro"));

      this.refreshStatusText(state);

    } else {
      log.error("Fatal. Exiting...");
    }
  }

  public String getVersion() {
    return String.format("%s v%s", messages.get("reverie.gui.title"), messages.get("reverie.version"));
  }

  public GameState getCurrentState() {
    return gameRepository.findTopByOrderBySaveDateDesc();
  }

  /**
   * Try to go in a direction and change room (if not closed) and print a message (if applicable).
   * 
   * @param state
   * @param direction
   * @return
   */
  public String goToDirection(GameState state, Directions direction) {

    final Room current = state.getCurrentRoom();
    final Room next = Directions.getInDirection(current, direction);

    if (next != null) {
      this.goToRoom(state, next);
      visitedRooms.add(next);

      final String entering = messages.get(next.getTitle() + ".entering");
      if (StringUtils.isEmpty(entering) || visitedRooms.contains(next))
        return messages.get("command.success.default");
      else
        return entering;
    } else {
      String specificMessage = roomService.paddedSplit(
              messages.get(current.getTitle() + Constants.ROOM_NSWE_DESC_SUFFIX + ".not"), 4)[Directions
              .indexOf(direction)];
      return StringUtils.isEmpty(specificMessage) ? randomizer.rollString(messages.get("command.refused.direction"))
              : specificMessage;
    }
  }

  /**
   * @param state
   * @param next
   */
  public void goToRoom(GameState state, final Room next) {
    state.setCurrentRoom(next);
    state = gameRepository.save(state);

    this.refreshRoomText(next);
    this.refreshStatusText(state);
    this.printRandomAmbience(next);
  }

  /**
   * Prints random ambience with a probability of 1/8. Random ambiences are block-based.
   * 
   * @param room
   */
  private void printRandomAmbience(final Room room) {
    final String ambienceOptions = messages.get(room.getBlock() + ".ambience");
    final String specAmbienceOptions = messages.get(room.getTitle() + ".ambience");

    if (!StringUtils.isEmpty(ambienceOptions) && randomizer.roll(0, 160) > 140) {
      controller.getAdventureCommandResponses().appendText("\n" + randomizer.rollString(ambienceOptions));
    }

    if (!StringUtils.isEmpty(specAmbienceOptions) && randomizer.roll(0, 160) > 140) {
      controller.getAdventureCommandResponses().appendText("\n" + randomizer.rollString(specAmbienceOptions));
    }
  }

  private void refreshStatusText(final GameState state) {
    final String statusText = String.format("%s %s - %s", pcService.getInfoText(state.getPlayerCharacter()),
            this.getVersion(), messages.get(state.getCurrentRoom().getTitle()));
    controller.getStatusText().setText(statusText);
  }

  private void refreshRoomText(Room next) {
    controller.getAdventureText().setText(messages.get(next.getDescription()));
    controller.getAdventureText().appendText(roomService.getConnectionsText(next));
    controller.getAdventureText().appendText(roomService.getItemsText(next));
  }

  public String pickupItem(final Item item) {

    // If it is pickupable and not already in your backpack...
    if (!item.isPickupable() || !item.isTangible()) {
      final String nopickupDesc = messages.get(item.getTitle() + Constants.ITEM_NOPICKUP_SUFFIX);
      final String nopickup = "default".equals(nopickupDesc) ? randomizer.rollString(
              messages.get("items.nopickup.default"), messages.get(item.getTitle())) : nopickupDesc;
      return nopickup;
    }

    final GameState state = this.getCurrentState();

    // This works because if we're here, the item is either in the room or our pack.
    if (!state.getCurrentRoom().has(item)) {
      return messages.get("items.pickup.already", messages.get(item.getTitle()));
    }

    // Remove the item from the room and put it in the pc's inventory.
    final Room room = state.getCurrentRoom();
    final PlayerCharacter pc = state.getPlayerCharacter();
    room.getItems().remove(item);
    pc.getItems().add(item);

    roomService.save(room);
    pcService.save(pc);

    this.refreshRoomText(room);

    return messages.get("items.pickup", messages.get(item.getTitle()));
  }

  public String dropItem(final Item item) {

    if (!item.isTangible()) {
      final String nodropDesc = messages.get(item.getTitle() + Constants.ITEM_NODROP_SUFFIX);
      final String nodrop = "default".equals(nodropDesc) ? randomizer.rollString(
              messages.get("items.nopickup.default"), messages.get(item.getTitle())) : nodropDesc;
      return nodrop;
    }

    final GameState state = this.getCurrentState();

    // Remove the item from the pc's inventory and put it in the room.
    final Room room = state.getCurrentRoom();
    final PlayerCharacter pc = state.getPlayerCharacter();
    room.getItems().add(item);
    pc.getItems().remove(item);

    roomService.save(room);
    pcService.save(pc);

    this.refreshRoomText(room);

    return messages.get("items.drop", messages.get(item.getTitle()));
  }

  /**
   * Flip items and do consequences (see items.properties).
   * 
   * @param state
   * @param item
   * @param command
   * @return
   */
  public boolean flipItem(final GameState state, Item item, final String command) {

    // If flipActions is empty, use the whole action list.
    String flipActions = messages.get(item.getTitle() + ".flip.actions");
    if (StringUtils.isEmpty(flipActions)) {
      flipActions = messages.get(item.getTitle() + ".actions");
    }

    // If this is not a flip-action, return (no action is done right now, so no message is given) and go on with non-flip actions.
    if (!Arrays.asList(flipActions.split(Constants.SEPARATOR)).contains(command)) {
      return false;
    }

    // If item was flipped already, return silently.
    if (item.isFlipped()) {
      return true;
    }

    // Else, change item state.
    item = doFlipItem(state, item);

    for (Item itemFlipped : itemService.listByCodes(item.getTitle() + ".flip.items")) {
      // Let's not create recursive chains...
      itemFlipped = doFlipItem(state, itemFlipped);
    }

    return true;
  }

  /**
   * Do the actual item flipping routine.
   * 
   * @param state
   * @param item
   * @return
   */
  private Item doFlipItem(final GameState state, Item item) {
    item.setFlipped(true);
    item.setDescription(item.getTitle() + Constants.ITEM_DESCRIPTION_FLIPPED_SUFFIX);
    item = itemService.save(item);

    // Do consequences if applicable.
    Room room = state.getCurrentRoom();
    PlayerCharacter pc = state.getPlayerCharacter();

    for (Item itemRemoved : itemService.listByCodes(item.getTitle() + ".flip.remove")) {
      room.getItems().remove(itemRemoved);
      pc.getItems().remove(itemRemoved);
    }

    for (Room roomFlipped : roomService.listByCodes(item.getTitle() + ".flip.rooms")) {
      roomService.openToDirections(roomFlipped.getCode());
    }

    for (Item itemAdded : itemService.listByCodes(item.getTitle() + ".flip.create.room")) {
      room.getItems().add(itemAdded);
      roomService.save(room);
    }

    for (Item itemAdded : itemService.listByCodes(item.getTitle() + ".flip.create.player")) {
      pc.getItems().add(itemAdded);
      pcService.save(pc);
    }

    this.refreshRoomText(room);

    return item;
  }

  /**
   * Apply conditions and do an intransitive command, with consequences.
   * See commands.properties for details
   * 
   * @param verb
   * @return
   */
  public String doIntransitiveCommand(final String verb) {

    final String message = messages.get("command." + verb);

    // TODO check conditions first

    // TODO apply actions if conditions are met (and they are if method did not return)

    // Finally, return message
    if (!StringUtils.isEmpty(message))
      return randomizer.rollString(message);

    return null;
  }
}
