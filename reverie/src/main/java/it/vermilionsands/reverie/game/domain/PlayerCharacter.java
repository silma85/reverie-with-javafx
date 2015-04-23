/**
 * 
 */
package it.vermilionsands.reverie.game.domain;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Player character entity
 * 
 * @author a.putzu
 * 
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerCharacter extends RootEntity {

  private static final long serialVersionUID = -404972380884928153L;

  @Basic
  private String name;

  @Basic
  private Sexes sex;

  @Basic
  private int luck;

  @OneToMany(fetch = FetchType.EAGER)
  private List<Item> items;

  public boolean has(final Item item) {
    return this.items.contains(item);
  }
}
