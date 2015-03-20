/**
 * 
 */
package it.silma.reverie.game.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.openjpa.persistence.jdbc.Unique;

/**
 * Item entity
 * 
 * @author a.putzu
 * 
 */
@Entity
public class Item extends AbstractEntity {

	private static final long serialVersionUID = 4507407040450397111L;

	@Id
	@Unique
	@GeneratedValue
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
