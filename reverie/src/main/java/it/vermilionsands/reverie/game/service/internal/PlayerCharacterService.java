/**
 * 
 */
package it.vermilionsands.reverie.game.service.internal;

import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.Randomizer;
import it.vermilionsands.reverie.game.domain.Item;
import it.vermilionsands.reverie.game.domain.PlayerCharacter;
import it.vermilionsands.reverie.game.domain.Sexes;
import it.vermilionsands.reverie.game.repository.PlayerCharacterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author alessandro.putzu
 *
 */
@Service
public class PlayerCharacterService {

  @Autowired
  private Randomizer randomizer;

  @Autowired
  private Messages messages;

  @Autowired
  private PlayerCharacterRepository pcRepository;

  @Autowired
  private ItemService itemService;

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

  public String getInventoryText(final PlayerCharacter pc) {

    if (pc.getItems().isEmpty()) {
      return randomizer.rollString(messages.get("items.look.pack.empty"));
    }

    final StringBuffer sb = new StringBuffer(messages.get("items.look.pack"));
    for (Item item : pc.getItems()) {
      sb.append("\t" + StringUtils.capitalize(messages.get(item.getTitle())) + "\n");
    }

    return sb.toString();
  }

  public String getInfoText(PlayerCharacter pc) {
    return messages.get("reverie.gui.status.player", pc.getName());
  }

  public PlayerCharacter save(final PlayerCharacter pc) {
    return pcRepository.save(pc);
  }
}
