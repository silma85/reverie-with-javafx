/**
 * 
 */
package it.vermilionsands.reverie.game.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;

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
public class PlayerCharacter extends AbstractEntity {

  private static final long serialVersionUID = -404972380884928153L;

  @Basic
  private String name;

  public enum Sexes {
    M,
    F
  }

  @Basic
  private Sexes sex;

  @Basic
  private int luck;

}
