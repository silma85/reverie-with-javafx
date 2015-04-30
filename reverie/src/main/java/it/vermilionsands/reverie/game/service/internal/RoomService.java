package it.vermilionsands.reverie.game.service.internal;

import it.vermilionsands.reverie.configuration.Constants;
import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.domain.Directions;
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
    this.setItems(savedRooms);

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

  public String[] paddedSplit(String all, int padding) {
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

  /**
   * Return formatted room connection text. If a direction does not correspond to a Room, return the closed connection
   * text. Else the open connection text. This is for the description, not for responses to direction commands.
   * 
   * @param room
   * @return
   */
  public String getConnectionsText(final Room room) {

    final StringBuffer sb = new StringBuffer(" ");

    final String[] directionPrefixes = this.paddedSplit(
            messages.get(Constants.ROOM_DIRECTIONS_PREFIX + Constants.ROOM_NSWE_SUFFIX), 4);
    final String[] closedConnections = this.paddedSplit(
            messages.get(room.getTitle() + Constants.ROOM_NSWE_DESC_SUFFIX + ".closed"), 4);
    final String[] openConnections = this.paddedSplit(messages.get(room.getTitle() + Constants.ROOM_NSWE_DESC_SUFFIX),
            4);

    // Check connections.
    for (int i = 0; i < openConnections.length; i++) {
      if (Directions.getInDirection(room, Directions.get(i)) == null) {
        sb.append(!StringUtils.isEmpty(closedConnections[i]) ? String
                .format(closedConnections[i], directionPrefixes[i]) + " " : "");
      } else {
        sb.append(!StringUtils.isEmpty(openConnections[i]) ? String.format(openConnections[i], directionPrefixes[i])
                + " " : "");
      }
    }

    sb.append("\n");

    return sb.toString();
  }

  public String getItemsText(final Room room) {

    final StringBuffer sb = new StringBuffer("\n");

    for (Item item : room.getItems()) {
      final String key = item.isFlipped() ? item.getTitle() + Constants.ITEM_AMBIENCE_FLIPPED_SUFFIX : item.getTitle()
              + Constants.ITEM_AMBIENCE_SUFFIX;
      final String ambience = StringUtils.isEmpty(messages.get(key)) ? "" : messages.get(key) + "\n";
      sb.append(ambience);
    }

    return sb.toString();
  }

  /**
   * Open a room's directions.
   * 
   * @param code
   */
  public void openToDirections(final String openOptions) {

    final List<Room> roomsToUpdate = new ArrayList<>();

    // String is in form room$direction$destination;room$direction$destination...
    for (String triplet : openOptions.split(Constants.SEPARATOR)) {

      final String[] splitlet = triplet.split(Constants.SEPARATOR_TRIPLET);
      final Room room = roomRepository.findByCode(splitlet[0]);
      final Room dest = roomRepository.findByCode(splitlet[2]);

      Directions direction = Directions.valueOf(splitlet[1]);

      switch (direction) {
      case N:
        room.setNorth(dest);
        break;

      case S:
        room.setSouth(dest);
        break;

      case W:
        room.setWest(dest);
        break;

      case E:
        room.setEast(dest);
        break;

      case U:
        room.setUp(dest);
        break;

      case D:
        room.setDown(dest);
        break;
      }

      roomsToUpdate.add(room);
    }

    roomRepository.save(roomsToUpdate);
  }

  /**
   * Close a room's directions.
   * 
   * @param code
   */
  public void closeToDirections(final String closeOptions) {

    final List<Room> roomsToUpdate = new ArrayList<>();

    // String is in form room$direction$destination;room$direction$destination...
    for (String triplet : closeOptions.split(Constants.SEPARATOR)) {

      final String[] splitlet = triplet.split(Constants.SEPARATOR_TRIPLET);
      final Room room = roomRepository.findByCode(splitlet[0]);

      Directions direction = Directions.valueOf(splitlet[1]);

      switch (direction) {
      case N:
        room.setNorth(null);
        break;

      case S:
        room.setSouth(null);
        break;

      case W:
        room.setWest(null);
        break;

      case E:
        room.setEast(null);
        break;

      case U:
        room.setUp(null);
        break;

      case D:
        room.setDown(null);
        break;
      }

      roomsToUpdate.add(room);
    }

    roomRepository.save(roomsToUpdate);
  }

  @Transactional
  private void setItems(final Iterable<Room> rooms) {

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
