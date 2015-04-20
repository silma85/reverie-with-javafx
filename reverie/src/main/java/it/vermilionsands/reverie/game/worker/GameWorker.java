/**
 * 
 */
package it.vermilionsands.reverie.game.worker;

import it.vermilionsands.reverie.game.domain.PlayerCharacter;
import it.vermilionsands.reverie.service.PlayerCharacterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manages the main loop, input / output operations
 * 
 * @author a.putzu
 * 
 */
@Component
public class GameWorker extends AbstractWorker {

  @Autowired
  private PlayerCharacterService pCService;

  public void createDefaultPlayer() {

    PlayerCharacter player = pCService.createPC("Adventurer");
  }

}
