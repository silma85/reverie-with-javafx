/**
 * 
 */
package it.vermilionsands.reverie.game.service.internal;

import it.vermilionsands.reverie.configuration.Constants;
import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.domain.GameState;
import it.vermilionsands.reverie.game.domain.Item;
import it.vermilionsands.reverie.game.domain.PlayerCharacter;
import it.vermilionsands.reverie.game.domain.Room;
import it.vermilionsands.reverie.game.repository.GameStateRepository;
import it.vermilionsands.reverie.gui.MainPaneController;

import java.util.Arrays;

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
    final PlayerCharacter pc = pcService.createPC();
    final boolean rooms = roomService.initRooms();
    final boolean items = itemService.initItems();

    if (rooms && items) {
      // TODO starting items on pc also?

      final GameState state = new GameState();
      state.setPlayerCharacter(pc);

      final Room starting = roomService.getStartingRoom();

      this.advanceRoom(state, starting);
      this.refreshStatusText();
    } else {
      log.error("Fatal. Exiting...");
    }
  }

  private void refreshStatusText() {

    final GameState state = this.getCurrentState();

    controller.getStatusText().setText(pcService.getInfoText(state.getPlayerCharacter()) + this.getVersion());
  }

  public String getVersion() {
    return String.format("%s v%s", messages.get("reverie.gui.title"), messages.get("reverie.version"));
  }

  public GameState getCurrentState() {
    return gameRepository.findTopByOrderBySaveDateDesc();
  }

  public void advanceRoom(GameState state, Room next) {
    state.setCurrentRoom(next);
    roomService.setRoomItems(next);

    refreshRoomText(next);

    gameRepository.save(state);
  }

  /**
   * @param next
   */
  public void refreshRoomText(Room next) {
    controller.getAdventureText().setText(messages.get(next.getDescription()));
    controller.getAdventureText().appendText(roomService.getRoomConnectionsText(next));
    controller.getAdventureText().appendText(roomService.getRoomItemsText(next));
  }

  public boolean checkItemPresent(String itemKeyword) {

    final Item item = itemService.findByKeywords(itemKeyword);

    if (item != null) {
      if (getCurrentState().getCurrentRoom().has(item) || getCurrentState().getPlayerCharacter().has(item)) {
        return true;
      }
    }

    return false;
  }

  public String pickupItem(final GameState state, final Item item) {

    // If it is pickupable and not already in your backpack...
    if (!item.isPickupable()) {
      return messages.get(item.getTitle() + Constants.ITEM_NOPICKUP_SUFFIX);
    }

    if (!getCurrentState().getCurrentRoom().has(item)) {
      return messages.get("items.pickup.already", itemService.getArticledDescription(item));
    }

    // Remove the item from the room and put it in the pc's inventory.
    final Room room = state.getCurrentRoom();
    final PlayerCharacter pc = state.getPlayerCharacter();
    room.getItems().remove(item);
    pc.getItems().add(item);

    roomService.save(room);
    pcService.save(pc);

    this.refreshRoomText(room);

    return messages.get("items.pickup", itemService.getArticledDescription(item).toLowerCase());
  }

  // TODO maybe move to itemService?
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

    // If item was flipped, return silently.
    if (item.isFlipped()) {
      return true;
    }

    // Else, change item state.
    item.setFlipped(true);
    item.setDescription(item.getTitle() + Constants.ITEM_FLIPPED_SUFFIX);
    item = itemService.save(item);

    // Flip rooms and create objects if applicable.
    Room room = state.getCurrentRoom();
    PlayerCharacter pc = state.getPlayerCharacter();

    final String flippedRooms = messages.get(item.getTitle() + ".flip.rooms");
    if (!StringUtils.isEmpty(flippedRooms)) {
      // TODO do post-flip actions...
    }

    final String createItemsRoom = messages.get(item.getTitle() + ".flip.create.room");
    if (!StringUtils.isEmpty(createItemsRoom)) {
      for (String itemCode : createItemsRoom.split(Constants.SEPARATOR)) {
        final Item itemAdded = itemService.findByCode(itemCode);
        room.getItems().add(itemAdded);
        roomService.save(room);
      }
    }

    final String createItemsInv = messages.get(item.getTitle() + ".flip.create.player");
    if (!StringUtils.isEmpty(createItemsInv)) {
      for (String itemCode : createItemsInv.split(Constants.SEPARATOR)) {
        final Item itemAdded = itemService.findByCode(itemCode);
        pc.getItems().add(itemAdded);
        pcService.save(pc);
      }
    }

    return true;
  }
}
