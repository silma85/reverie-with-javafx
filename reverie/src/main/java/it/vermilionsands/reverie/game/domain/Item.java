/**
 * 
 */
package it.vermilionsands.reverie.game.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Item entity
 * 
 * @author a.putzu
 * 
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Item extends RootEntity {

  private static final long serialVersionUID = 4507407040450397111L;

  @Basic
  private String keywords;

  @Basic
  private String title;

  @Basic
  private String code;

  @Basic
  private String description;

  @Basic
  private boolean flipped = false;

  @Basic
  private boolean pickupable = true;
}
