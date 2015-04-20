/**
 * 
 */
package it.vermilionsands.reverie.game.domain.rooms;

import it.vermilionsands.reverie.game.domain.AbstractEntity;
import it.vermilionsands.reverie.game.domain.Item;

import java.util.ArrayList;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * Abstract room entity
 * 
 * @author a.putzu
 * 
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Room extends AbstractEntity {

  private static final long serialVersionUID = 3124208574523298591L;

  @Basic
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
}
