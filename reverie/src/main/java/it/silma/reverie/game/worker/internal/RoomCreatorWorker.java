package it.silma.reverie.game.worker.internal;

import it.silma.reverie.config.Constants;
import it.silma.reverie.config.Messages;
import it.silma.reverie.game.entity.rooms.Room;
import it.silma.reverie.game.worker.AbstractWorker;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Bootstrap class used to initialize rooms for a new game or testing purposes.
 * 
 * @author a.putzu
 * 
 */
public class RoomCreatorWorker extends AbstractWorker {

	private static RoomCreatorWorker instance;

	private RoomCreatorWorker() {
		super();
	}

	/**
	 * Populate rooms
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean createRooms() throws IOException {

		// Delete all rooms
		super.executeNonSelectingNamedQuery("deleteRooms");

		// Gather room data
		ArrayList<Room> roomList = new ArrayList<Room>();

		String[] blocks = Constants.ALL_BLOCKS.split(Constants.SEPARATOR);

		for (String block : blocks) {
			String[] rooms = Messages.get(Constants.ALL_BLOCKS_KEY + "." + block).split(Constants.SEPARATOR);

			String blockKey = Constants.ROOM_PREFIX + "." + block;

			for (String room : rooms) {

				String roomKey = blockKey + "." + room;

				Room obj = new Room();

				// Popolamento Room
				obj.setCode(room);
				obj.setTitle(Messages.get(roomKey));
				obj.setDescription(Messages.get(roomKey + Constants.DESCRIPTION_SUFFIX));

				String nsweAll = Messages.get(roomKey + Constants.NSWE_SUFFIX);

				if (nsweAll != null) {
					String[] nswe = paddedSplit(nsweAll, 4);
					obj.setNorth(nswe[0]);
					obj.setSouth(nswe[1]);
					obj.setWest(nswe[2]);
					obj.setEast(nswe[3]);
				}

				String udAll = Messages.get(roomKey + Constants.UP_DOWN_SUFFIX);

				if (udAll != null) {
					String[] ud = paddedSplit(udAll, 2);
					obj.setUp(ud[0]);
					obj.setDown(ud[1]);
				}

				roomList.add(obj);
			}
		}

		// Persist rooms
		return super.persistCollection(roomList);
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

	public static RoomCreatorWorker getInstance() {

		if (instance == null)
			instance = new RoomCreatorWorker();

		return instance;
	}
}
