package it.silma.reverie.test;

import static org.junit.Assert.assertEquals;
import it.silma.reverie.config.Messages;
import it.silma.reverie.game.entity.rooms.Room;
import it.silma.reverie.game.worker.GameWorker;
import it.silma.reverie.game.worker.internal.RoomCreatorWorker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

/**
 * @author a.putzu
 * 
 */
public class TestReverieMain {

	@Test
	public void testProperties() {
		assertEquals("dull katana", Messages.get("item.dullkatana"));
	}

	// @Test
	public void testDataCreation() {

		GameWorker wk = GameWorker.getInstance();
		wk.createDefaultPlayer();

		assertEquals(1, wk.getPlayerCount());
	}

	@Test
	public void testRoomsPropertyGathering() throws IOException {

		Messages.setLocale(Locale.ENGLISH);
		RoomCreatorWorker creator = RoomCreatorWorker.getInstance();
		creator.createRooms();

		GameWorker wk = GameWorker.getInstance();

		System.out.println(wk.getRoom("floor"));

		List<Room> rooms = wk.getRooms();
		System.out.println(rooms);
	}
}
