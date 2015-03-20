/**
 * 
 */
package it.silma.reverie.game.worker;

import it.silma.reverie.game.entity.PlayerCharacter;
import it.silma.reverie.game.entity.rooms.Room;

import java.util.List;

import javax.persistence.Query;

/**
 * Manages the main loop, input / output operations
 * 
 * @author a.putzu
 * 
 */
public class GameWorker extends AbstractWorker {

	private static GameWorker instance;

	private GameWorker() {
		super();
	}

	public void createDefaultPlayer() {

		PlayerCharacter player = new PlayerCharacter("Adventurer");
		em.persist(player);
	}

	public PlayerCharacter getPlayerById(int id) {
		return em.find(PlayerCharacter.class, id);
	}

	public int getPlayerCount() {
		return em.createQuery("count from Player").getFirstResult();
	}

	public Room getRoom(String code) {

		Query query = em.createNamedQuery("getRoomByCode");
		query.setParameter("code", code);

		return (Room) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Room> getRooms() {

		Query query = em.createNamedQuery("getRooms");
		return (List<Room>) query.getResultList();
	}

	public static GameWorker getInstance() {

		if (instance == null)
			instance = new GameWorker();

		return instance;
	}
}
