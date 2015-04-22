/**
 * 
 */
package it.vermilionsands.reverie.game.service.internal;

import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.domain.GameState;
import it.vermilionsands.reverie.game.domain.Item;
import it.vermilionsands.reverie.game.domain.PlayerCharacter;
import it.vermilionsands.reverie.game.domain.Room;
import it.vermilionsands.reverie.game.repository.GameStateRepository;
import it.vermilionsands.reverie.game.repository.ItemRepository;
import it.vermilionsands.reverie.gui.MainPaneController;

import javax.annotation.PostConstruct;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  private PlayerCharacterService pCService;

  @Autowired
  private RoomService roomService;

  @Autowired
  private GameStateRepository gameRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private MainPaneController controller;

  @Autowired
  private Messages messages;

  @PostConstruct
  public void init() {
    PlayerCharacter pc = pCService.createPC();
    boolean rooms = roomService.initRooms();

    if (rooms) {
      Room starting = roomService.getStartingRoom();

      GameState state = new GameState();
      state.setCurrentRoom(starting);
      state.setPlayerCharacter(pc);

      gameRepository.save(state);

      controller.getAdventureText().setText(messages.get(starting.getDescription()));
      controller.getAdventureText().appendText(roomService.getRoomConnectionsText(starting));
    } else {
      log.error("Fatal. Exiting...");
    }
  }

  public GameState getCurrentState() {
    return gameRepository.findTopByOrderBySaveDateDesc();
  }

  public void advanceRoom(GameState state, Room next) {
    state.setCurrentRoom(next);

    controller.getAdventureText().setText(messages.get(next.getDescription()));
    controller.getAdventureText().appendText(roomService.getRoomConnectionsText(next));

    roomService.setRoomItems(next);

    gameRepository.save(state);
  }

  public boolean checkItemPresent(String itemKeyword) {

    final Item item = itemRepository.findByKeywordsIn(itemKeyword);

    if (item != null) {
      if (getCurrentState().getCurrentRoom().has(item) || getCurrentState().getPlayerCharacter().has(item)) {
        return true;
      }
    }

    return false;
  }
}
