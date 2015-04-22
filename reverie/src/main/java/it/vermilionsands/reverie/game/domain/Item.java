/**
 * 
 */
package it.vermilionsands.reverie.game.domain;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

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

  @OneToMany(orphanRemoval = true)
  private List<String> keywords;

  @Basic
  private String code;

  @Basic
  private String description;

  @Basic
  private boolean flipped = false;

  @Basic
  private boolean pickupable = true;

  @Basic
  private Sexes sex;
}