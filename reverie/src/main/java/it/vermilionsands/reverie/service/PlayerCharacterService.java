/**
 * 
 */
package it.vermilionsands.reverie.service;

import it.vermilionsands.reverie.game.Randomizer;
import it.vermilionsands.reverie.game.domain.PlayerCharacter;
import it.vermilionsands.reverie.game.domain.PlayerCharacter.Sexes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author alessandro.putzu
 *
 */
@Service
public class PlayerCharacterService {

  @Autowired
  private Randomizer randomizer;

  public PlayerCharacter createPC(String name) {

    PlayerCharacter pc = new PlayerCharacter();
    pc.setName(name);
    pc.setLuck(0);
    pc.setSex(Sexes.values()[randomizer.roll(2)]);

    return pc;
  }

}
