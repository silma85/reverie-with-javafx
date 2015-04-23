/**
 * 
 */
package it.vermilionsands.reverie.game.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Needed for decoupling between action validation and feedback messages on the GUI.
 * 
 * @author alessandro.putzu
 *
 */
@Getter
@AllArgsConstructor
public enum ItemActionCodes {

  FAILURE_FLIPPED_ITEMS("flip"),
  FAILURE_HAVE_ITEMS("have"),
  FAILURE_ROOM("room"),
  SUCCESS("success")

  ;

  private String code;
}
