package it.vermilionsands.reverie.game.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.PreUpdate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class GameState extends RootEntity {

  private static final long serialVersionUID = -7068270265275637646L;

  @Basic
  private String saveCode;

  @Basic
  private Date saveDate = new Date();

  @Basic
  private String lastDirection;

  @OneToOne
  private PlayerCharacter playerCharacter;

  @OneToOne
  private Room currentRoom;

  @PreUpdate
  public void setSaveDate() {
    this.saveDate = new Date();
  }
}
