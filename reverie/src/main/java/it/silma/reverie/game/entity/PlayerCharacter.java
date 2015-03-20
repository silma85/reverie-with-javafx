/**
 * 
 */
package it.silma.reverie.game.entity;

import it.silma.reverie.game.Randomizer;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.openjpa.persistence.jdbc.Unique;

/**
 * Player character entity
 * 
 * @author a.putzu
 * 
 */
@Entity
public class PlayerCharacter {

	@Id
	@Unique
	@GeneratedValue
	private int id;

	@Basic
	private String name;

	private enum SexEnum {
		M, F
	}

	@Basic
	private SexEnum sex;

	@Basic
	private int luck;

	public PlayerCharacter(String name, String sex, int luck) {
		this.name = name;

		if ("m".equals(sex) || "M".equals(sex) || "f".equals(sex) || "F".equals(sex))
			this.sex = SexEnum.valueOf(sex.toUpperCase());
		else {
			this.sex = SexEnum.values()[Randomizer.roll(2)];
		}
	}

	public PlayerCharacter(String name) {
		this.name = name;
		this.sex = SexEnum.values()[Randomizer.roll(2)];
		this.luck = Randomizer.roll(3);
	}

	public PlayerCharacter() {
		this.name = Randomizer.rollName();
		this.sex = SexEnum.values()[Randomizer.roll(2)];
		this.luck = Randomizer.roll(3);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SexEnum getSex() {
		return sex;
	}

	public void setSex(SexEnum sex) {
		this.sex = sex;
	}

	public int getLuck() {
		return luck;
	}

	public void setLuck(int luck) {
		this.luck = luck;
	}
}
