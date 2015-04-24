/**
 * 
 */
package it.vermilionsands.reverie.game.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

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
public class Room extends RootEntity {

  private static final long serialVersionUID = 3124208574523298591L;

  @Basic
  private String code;

  @Basic
  private String title;

  @Column(length = 1024)
  private String description;

  @OneToMany(fetch = FetchType.EAGER)
  private Set<Item> items = new HashSet<Item>();

  @OneToOne(optional = true)
  private Room north;

  @OneToOne(optional = true)
  private Room south;

  @OneToOne(optional = true)
  private Room west;

  @OneToOne(optional = true)
  private Room east;

  @OneToOne(optional = true)
  private Room up;

  @OneToOne(optional = true)
  private Room down;

  @Override
  public String toString() {
    return "Room [code=" + code + ", title=" + title + ", description=" + description + ", items=" + items
            + (north != null ? ", north=" + north.getCode() : "") + (south != null ? ", south=" + south.getCode() : "")
            + (west != null ? ", west=" + west.getCode() : "") + (east != null ? ", east=" + east.getCode() : "")
            + (up != null ? ", up=" + up.getCode() : "") + (down != null ? ", down=" + down.getCode() : "") + "]";
  }

  public boolean has(final Item item) {
    return this.items.contains(item);
  }
}
