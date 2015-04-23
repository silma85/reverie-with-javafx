/**
 * 
 */
package it.vermilionsands.reverie.game.service.internal;

import it.vermilionsands.reverie.configuration.Constants;
import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.domain.GameState;
import it.vermilionsands.reverie.game.domain.Item;
import it.vermilionsands.reverie.game.domain.PlayerCharacter;
import it.vermilionsands.reverie.game.domain.Room;
import it.vermilionsands.reverie.game.domain.Sexes;
import it.vermilionsands.reverie.game.repository.ItemRepository;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author alessandro.putzu
 *
 */
@Service
public class ItemService {

  private final static Logger log = LoggerFactory.getLogger(ItemService.class);

  @Autowired
  private Messages messages;

  @Autowired
  private ItemRepository itemRepository;

  /**
   * Initialize items.
   * 
   * @return
   */
  public boolean initItems() {

    // Delete all items.
    itemRepository.deleteAll();

    // Gather item data.
    ArrayList<Item> items = new ArrayList<Item>();

    String[] itemBlocks = messages.get(Constants.ITEM_ALL_KEY).split(Constants.SEPARATOR);

    for (String code : itemBlocks) {

      final String itemRootKey = Constants.ITEM_PREFIX + "." + code;

      Item item = new Item();
      item.setCode(code);
      item.setTitle(itemRootKey);
      item.setDescription(itemRootKey + Constants.ITEM_DESCRIPTION_SUFFIX);
      item.setKeywords(messages.get(itemRootKey + Constants.ITEM_KEYWORDS_SUFFIX));
      item.setGender(Sexes.valueOf(messages.get(itemRootKey + Constants.ITEM_GENRE_SUFFIX)));

      // If has a no pickup description, it's not pickupable.
      item.setPickupable(StringUtils.isEmpty(messages.get(itemRootKey + Constants.ITEM_NOPICKUP_SUFFIX)));

      log.info("Adding item {}...", item);

      items.add(item);
    }

    itemRepository.save(items);

    return true;
  }

  /**
   * Check if the provided command can be done on the provided item. There are 3 condition groups:
   * <ul>
   * <li>flip: item(s) that have to be in flipped state;</li>
   * <li>have: item(s) that have to be in your possession;</li>
   * <li>room: the room you have to be in.</li>
   * </ul>
   * Conditions can be global (directly prefixed with .action) or action-specific, prefixed with the command (for
   * example .action.leggi.flip specifies required flipped items for the action "leggi").
   * 
   * Eugh. This method is... eugh.<br />
   * <br />
   * 
   * @param item
   * @param command
   * @return
   */
  public boolean validateCommand(final Item item, final String command, final GameState state) {

    // If the action is on the list, it must be validated. 
    if (!StringUtils.isEmpty(messages.get(item.getTitle() + Constants.ITEM_ACTION_SUFFIX))) {
      final String[] actionList = messages.get(item.getTitle() + Constants.ITEM_ACTION_SUFFIX).split(Constants.SEPARATOR);
      if (Arrays.asList(actionList).contains(command)) {

        // Check if global conditions apply (that is, global as long as the action is on the fliplist).
        final String gFlip = messages.get(item.getTitle() + Constants.ITEM_ACTION_SUFFIX + ".flip");
        if (!StringUtils.isEmpty(gFlip)) {
          for (String flipItemCode : gFlip.split(Constants.SEPARATOR)) {
            if (!this.checkFlipped(flipItemCode)) {
              return false;
            }
          }
        }

        final String gRoom = messages.get(item.getTitle() + Constants.ITEM_ACTION_SUFFIX + ".room");
        if (!StringUtils.isEmpty(gRoom)) {
          final Room room = state.getCurrentRoom();
          if (!room.getCode().equals(gRoom)) {
            return false;
          }
        }

        final String gHave = messages.get(item.getTitle() + Constants.ITEM_ACTION_SUFFIX + ".have");
        if (!StringUtils.isEmpty(gHave)) {
          final PlayerCharacter pc = state.getPlayerCharacter();
          for (String haveItem : gHave.split(Constants.SEPARATOR)) {
            if (!this.checkInInventory(pc, haveItem)) {
              return false;
            }
          }
        }

        // Check if specific conditions apply.
        final String lFlip = messages.get(item.getTitle() + Constants.ITEM_ACTION_SUFFIX + "." + command + ".flip");
        if (!StringUtils.isEmpty(lFlip)) {
          for (String flipItemCode : lFlip.split(Constants.SEPARATOR)) {
            if (!this.checkFlipped(flipItemCode)) {
              return false;
            }
          }
        }

        final String lRoom = messages.get(item.getTitle() + Constants.ITEM_ACTION_SUFFIX + "." + command + ".room");
        if (!StringUtils.isEmpty(lRoom)) {
          final Room room = state.getCurrentRoom();
          if (!room.getCode().equals(lRoom)) {
            return false;
          }
        }

        final String lHave = messages.get(item.getTitle() + Constants.ITEM_ACTION_SUFFIX + "." + command + ".have");
        if (!StringUtils.isEmpty(lHave)) {
          final PlayerCharacter pc = state.getPlayerCharacter();
          for (String haveItem : lHave.split(Constants.SEPARATOR)) {
            if (!this.checkInInventory(pc, haveItem)) {
              return false;
            }
          }
        }
      }
    }

    return true;
  }

  private boolean checkFlipped(final String code) {

    final Item item = this.findByCode(code);
    if (item == null) {
      return false;
    }

    return item.isFlipped();
  }

  private boolean checkInInventory(final PlayerCharacter pc, final String itemKeyword) {

    final Item item = this.findByKeywords(itemKeyword);
    if (item == null) {
      return false;
    }

    return pc.has(item);
  }

  /**
   * Since it is such a pain, get the articled short description for an item.
   * 
   * @param item
   * @return
   */
  public String getArticledDescription(final Item item) {
    final String article = item.getGender() == Sexes.M ? messages.get("items.article.m") : (messages
            .get(item.getTitle()).substring(0, 1).matches("aeiou") ? messages.get("items.article.fe") : messages
            .get("items.article.f"));
    final String articledDescription = article + messages.get(item.getTitle());

    return articledDescription;
  }

  public String lookItem(final Item item) {
    return messages.get(item.getDescription());
  }

  public Item findByKeywords(final String keywords) {
    return itemRepository.findByKeywordsContaining(keywords);
  }

  public Item findByCode(final String code) {
    return itemRepository.findByCode(code);
  }

  public Item save(final Item item) {
    return itemRepository.save(item);
  }
}
