/**
 * 
 */
package it.vermilionsands.reverie.game.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

/**
 * Basic serializable entity class
 * 
 * @author a.putzu
 * 
 */
@MappedSuperclass
@Data
public abstract class AbstractEntity implements Serializable {

  private static final long serialVersionUID = -4686897082565174317L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id = -1L;
}
