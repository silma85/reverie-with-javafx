/**
 * 
 */
package it.vermilionsands.reverie.game.service.internal;

import it.vermilionsands.reverie.configuration.Constants;
import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.Randomizer;
import it.vermilionsands.reverie.game.domain.GameState;
import it.vermilionsands.reverie.game.domain.Item;
import it.vermilionsands.reverie.game.domain.PlayerCharacter;
import it.vermilionsands.reverie.game.domain.Room;
import it.vermilionsands.reverie.game.repository.ItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
  private Randomizer randomizer;

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
      final String[] actionList = messages.get(item.getTitle() + Constants.ITEM_ACTION_SUFFIX).split(
              Constants.SEPARATOR);
      if (Arrays.asList(actionList).contains(command)) {

        // Check if global conditions apply (that is, global as long as the action is on the fliplist).
        if (!StringUtils.isEmpty(this.getValidationKey(item, null, "flipitems"))) {
          for (String flipItemCode : this.getValidationKey(item, null, "flipitems").split(Constants.SEPARATOR))
            if (!this.checkFlipped(flipItemCode))
              return false;
        }

        if (!StringUtils.isEmpty(this.getValidationKey(item, null, "inroom"))) {
          final Room room = state.getCurrentRoom();
          if (!room.getCode().equals(this.getValidationKey(item, null, "inroom"))) {
            return false;
          }
        }

        if (!StringUtils.isEmpty(this.getValidationKey(item, null, "haveitems"))) {
          final PlayerCharacter pc = state.getPlayerCharacter();
          for (String haveItem : this.getValidationKey(item, null, "haveitems").split(Constants.SEPARATOR))
            if (!this.checkInInventory(pc, haveItem))
              return false;
        }

        // Check if specific conditions apply.
        if (!StringUtils.isEmpty(this.getValidationKey(item, command, "flipitems"))) {
          for (String flipItemCode : this.getValidationKey(item, command, "flipitems").split(Constants.SEPARATOR))
            if (!this.checkFlipped(flipItemCode))
              return false;
        }

        if (!StringUtils.isEmpty(this.getValidationKey(item, command, "inroom"))) {
          final Room room = state.getCurrentRoom();
          if (!room.getCode().equals(this.getValidationKey(item, command, "inroom"))) {
            return false;
          }
        }

        if (!StringUtils.isEmpty(this.getValidationKey(item, command, "haveitems"))) {
          final PlayerCharacter pc = state.getPlayerCharacter();
          for (String haveItem : this.getValidationKey(item, command, "haveitems").split(Constants.SEPARATOR))
            if (!this.checkInInventory(pc, haveItem))
              return false;
        }
      }
    }

    return true;
  }

  private String getValidationKey(final Item item, final String command, final String option) {
    return messages.get(item.getTitle() + Constants.ITEM_ACTION_SUFFIX + (command == null ? "." : "." + command + ".")
            + option);
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

  public String lookItem(final Item item) {
    final String description = messages.get(item.getDescription());

    if (StringUtils.isEmpty(description)) {
      return randomizer.rollString(messages.get("items.look.default"), messages.get(item.getTitle()));
    }

    return description;
  }

  public List<Item> listByCodes(final String codes) {
    List<Item> items = new ArrayList<Item>();

    final String createItemsInv = messages.get(codes);
    if (!StringUtils.isEmpty(createItemsInv)) {
      for (String itemCode : createItemsInv.split(Constants.SEPARATOR)) {
        final Item item = this.findByCode(itemCode);
        items.add(item);
      }
    }

    return items;
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
