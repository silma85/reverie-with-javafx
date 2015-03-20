package it.silma.reverie.game.worker;

import it.silma.reverie.game.entity.AbstractEntity;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class AbstractWorker {

	protected final EntityManager em;

	public AbstractWorker() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("reverie_pu");
		em = emf.createEntityManager();
	}

	protected int executeNonSelectingNamedQuery(String query) {

		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			int result = em.createNamedQuery(query).executeUpdate();

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (tx.isActive())
				tx.commit();
		}

		return 0;
	}

	/**
	 * @param <T>
	 *            Entity type to persist
	 * @param list
	 *            List of entities to persist
	 * @return
	 */
	protected <T extends AbstractEntity> boolean persistCollection(List<T> list) {
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			for (T obj : list) {
				em.persist(obj);

				if (list.indexOf(obj) % 20 == 0) {
					em.flush();
					em.clear();
				}
			}

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}