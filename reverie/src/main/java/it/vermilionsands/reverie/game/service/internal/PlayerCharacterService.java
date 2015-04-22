/**
 * 
 */
package it.vermilionsands.reverie.game.service.internal;

import it.vermilionsands.reverie.game.Randomizer;
import it.vermilionsands.reverie.game.domain.PlayerCharacter;
import it.vermilionsands.reverie.game.domain.Sexes;
import it.vermilionsands.reverie.game.repository.PlayerCharacterRepository;

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

  @Autowired
  private PlayerCharacterRepository pcRepository;

  public PlayerCharacter createPC() {

    PlayerCharacter pc = new PlayerCharacter();
    pc.setName(randomizer.rollName());
    pc.setLuck(0);
    pc.setSex(Sexes.values()[randomizer.roll(2)]);

    pc = pcRepository.save(pc);

    return pc;
  }

  public String doPcAction(String command, String item) {
    return "";
  }
}
