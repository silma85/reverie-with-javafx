/**
 * 
 */
package it.silma.reverie.game.entity.rooms;

import it.silma.reverie.game.entity.AbstractEntity;
import it.silma.reverie.game.entity.Item;

import java.util.ArrayList;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.apache.openjpa.persistence.jdbc.Unique;

/**
 * 
 * Abstract room entity
 * 
 * @author a.putzu
 * 
 */
@Entity
@NamedQueries(value = { @NamedQuery(name = "getRoomByCode", query = "select r from Room r where r.code = :code"),
		@NamedQuery(name = "getRooms", query = "select r from Room r"),
		@NamedQuery(name = "deleteRooms", query = "delete from Room") })
public class Room extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Unique
	@GeneratedValue
	private int id;

	@Basic
	@Unique
	private String code;

	@Basic
	private String title;

	@Column(length = 1024)
	private String description;

	@OneToMany
	private ArrayList<Item> items;

	@Basic
	private int cPassage;

	@Basic
	private int cTurn;

	@Basic
	private String north;

	@Basic
	private String south;

	@Basic
	private String west;

	@Basic
	private String east;

	@Basic
	private String up;

	@Basic
	private String down;

	public Room() {
		// this.cPassage = 0;
		// this.cTurn = 0;
	}

	public int getcPassage() {
		return cPassage;
	}

	public int getcTurn() {
		return cTurn;
	}

	public String getDescription() {
		return description;
	}

	public String getDown() {
		return down;
	}

	public String getEast() {
		return east;
	}

	public int getId() {
		return id;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public String getNorth() {
		return north;
	}

	public String getSouth() {
		return south;
	}

	public String getUp() {
		return up;
	}

	public String getWest() {
		return west;
	}

	public void setcPassage(int cPassage) {
		this.cPassage = cPassage;
	}

	public void setcTurn(int cTurn) {
		this.cTurn = cTurn;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDown(String down) {
		this.down = down;
	}

	public void setEast(String east) {
		this.east = east;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public void setNorth(String north) {
		this.north = north;
	}

	public void setSouth(String south) {
		this.south = south;
	}

	public void setUp(String up) {
		this.up = up;
	}

	public void setWest(String west) {
		this.west = west;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "Room [id=" + id + ", code=" + code + ", title=" + title + ", description=" + description + ", items="
				+ items + ", cPassage=" + cPassage + ", cTurn=" + cTurn + ", north=" + north + ", south=" + south
				+ ", west=" + west + ", east=" + east + ", up=" + up + ", down=" + down + "]";
	}
}
