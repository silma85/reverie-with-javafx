package it.vermilionsands.reverie.game.service.internal;

import it.vermilionsands.reverie.configuration.Constants;
import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.domain.GameState;
import it.vermilionsands.reverie.game.domain.Item;
import it.vermilionsands.reverie.game.domain.Room;
import it.vermilionsands.reverie.game.repository.RoomRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Bootstrap class used to initialize rooms for a new game or testing purposes.
 * 
 * @author a.putzu
 * 
 */
@Service
public class RoomService {

  private final static Logger log = LoggerFactory.getLogger(RoomService.class);

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private ItemService itemService;

  @Autowired
  private Messages messages;

  @Autowired
  private GameService gameService;

  public Room getStartingRoom() {
    return roomRepository.findByCode(Constants.ROOM_DEFAULT);
  }

  /**
   * Populate rooms
   * 
   * @return
   * @throws IOException
   */
  public boolean initRooms() {

    // Gather room data
    final ArrayList<Room> roomList = new ArrayList<Room>();

    final String[] blocks = messages.get("rooms.all.blocks").split(Constants.SEPARATOR);

    for (String block : blocks) {
      String[] rooms = messages.get(Constants.ROOM_ALL_KEY + "." + block).split(Constants.SEPARATOR);

      String blockKey = Constants.ROOM_PREFIX + "." + block;

      for (String code : rooms) {

        Room room = new Room();

        // Check if item was already present and, if so, load it.
        Room oldRoom = roomRepository.findByCode(code);
        if (oldRoom != null)
          room = oldRoom;

        String roomKey = blockKey + "." + code;

        // Popolamento Room
        room.setCode(code);
        room.setBlock(blockKey);
        room.setTitle(roomKey);
        room.setDescription(roomKey + Constants.ROOM_DESCRIPTION_SUFFIX);

        log.info("Creating room: " + code);
        roomList.add(room);
      }
    }

    // Persist rooms
    final Iterable<Room> savedRooms = roomRepository.save(roomList);
    this.createRoomConnections(savedRooms);
    this.setRoomItems(savedRooms);

    return true;
  }

  /**
   * @param roomKey
   * @param obj
   */
  private void createRoomConnections(Iterable<Room> rooms) {

    for (Room room : rooms) {
      String roomKey = room.getTitle();

      String nsweAll = messages.get(roomKey + Constants.ROOM_NSWE_SUFFIX);
      if (!StringUtils.isEmpty(nsweAll)) {
        String[] nswe = paddedSplit(nsweAll, 4);
        room.setNorth(roomRepository.findByCode(nswe[0]));
        room.setSouth(roomRepository.findByCode(nswe[1]));
        room.setWest(roomRepository.findByCode(nswe[2]));
        room.setEast(roomRepository.findByCode(nswe[3]));
      }

      String udAll = messages.get(roomKey + Constants.ROOM_UD_SUFFIX);
      if (!StringUtils.isEmpty(udAll)) {
        String[] ud = paddedSplit(udAll, 2);
        room.setUp(roomRepository.findByCode(ud[0]));
        room.setDown(roomRepository.findByCode(ud[1]));
      }

      log.info("Connecting room: " + room.toString());

      roomRepository.save(room);
    }
  }

  private String[] paddedSplit(String all, int padding) {
    String[] split = all.split(Constants.SEPARATOR);

    if (split.length < padding) {
      String[] padded = new String[padding];

      for (int i = 0; i < split.length; i++) {
        padded[i] = split[i];
      }

      for (int i = split.length; i < padding; i++) {
        padded[i] = "";
      }

      return padded;
    }

    return split;
  }

  public String getRoomConnectionsText(final Room room) {

    final StringBuffer sb = new StringBuffer("\n\n");

    final String[] nswePrefixes = messages.get(Constants.ROOM_DIRECTIONS_PREFIX + Constants.ROOM_NSWE_SUFFIX).split(
            Constants.SEPARATOR);
    final String[] connections = messages.get(room.getTitle() + Constants.ROOM_NSWE_DESC_SUFFIX).split(
            Constants.SEPARATOR);

    for (int i = 0; i < connections.length; i++) {
      sb.append(!StringUtils.isEmpty(connections[i]) ? String.format(connections[i], nswePrefixes[i]) + "\n" : "");
    }

    sb.append("\n");

    return sb.toString();
  }

  public String getRoomItemsText(final Room room) {

    final StringBuffer sb = new StringBuffer();

    for (Item item : room.getItems()) {
      final String key = item.isFlipped() ? item.getTitle() + Constants.ITEM_AMBIENCE_FLIPPED_SUFFIX : item.getTitle()
              + Constants.ITEM_AMBIENCE_SUFFIX;
      final String ambience = StringUtils.isEmpty(messages.get(key)) ? "" : messages.get(key) + "\n";
      sb.append(ambience);
    }

    return sb.toString();
  }

  /**
   * Replaces a room with the designated flip room.
   * 
   * @param code
   */
  public void flipRoom(final String oldRoomCode) {
    final Room oldRoom = roomRepository.findByCode(oldRoomCode);
    final String newRoomCode = messages.get(oldRoom.getTitle() + Constants.ROOM_FLIP_SUFFIX);
    final Room newRoom = roomRepository.findByCode(newRoomCode);

    // Change all directions.
    roomRepository.updateNorth(newRoom, oldRoomCode);
    roomRepository.updateSouth(newRoom, oldRoomCode);
    roomRepository.updateWest(newRoom, oldRoomCode);
    roomRepository.updateEast(newRoom, oldRoomCode);
    roomRepository.updateUp(newRoom, oldRoomCode);
    roomRepository.updateDown(newRoom, oldRoomCode);

    // Check we haven't already picked up some items.
    final GameState state = gameService.getCurrentState();
    for (Item item : newRoom.getItems())
      if (state.getPlayerCharacter().getItems().contains(item))
        newRoom.getItems().remove(item);

    roomRepository.save(newRoom);
  }

  @Transactional
  private void setRoomItems(final Iterable<Room> rooms) {

    for (Room room : rooms) {
      final String[] items = messages.get(room.getTitle() + Constants.ROOM_ITEM_SUFFIX).split(Constants.SEPARATOR);
      for (String itemCode : items) {

        if (StringUtils.isEmpty(itemCode)) {
          log.info("No items on room {}, moving on...", room.getTitle());
          continue;
        }

        Item item = itemService.findByCode(itemCode);

        if (item == null) {
          log.warn("Item {} is present in {}'s list, but not in the DB! Check your properties please...", itemCode,
                  room.getTitle());
          continue;
        }

        room.getItems().add(item);
      }

      this.save(room);
    }
  }

  public Room save(final Room room) {
    return roomRepository.save(room);
  }

  public Room findByCode(final String code) {
    return roomRepository.findByCode(code);
  }

  public List<Room> listByCodes(final String codes) {
    List<Room> rooms = new ArrayList<Room>();

    final String roomCodes = messages.get(codes);
    if (!StringUtils.isEmpty(roomCodes)) {
      for (String roomCode : roomCodes.split(Constants.SEPARATOR)) {
        final Room room = roomRepository.findByCode(roomCode);
        rooms.add(room);
      }
    }

    return rooms;
  }
}
