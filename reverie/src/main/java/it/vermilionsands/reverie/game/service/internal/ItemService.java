/**
 * 
 */
package it.vermilionsands.reverie.game.service.internal;

import it.vermilionsands.reverie.configuration.Constants;
import it.vermilionsands.reverie.configuration.Messages;
import it.vermilionsands.reverie.game.domain.Item;
import it.vermilionsands.reverie.game.repository.ItemRepository;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author alessandro.putzu
 *
 */
@Service
public class ItemService {

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
      item.setDescription(messages.get(itemRootKey + Constants.ITEM_DESCRIPTION_SUFFIX));

      // If has a no pickup description, it's not pickupable.
      final String nopickup = messages.get(itemRootKey + Constants.ITEM_NOPICKUP_SUFFIX);
      item.setPickupable(StringUtils.isEmpty(nopickup));
    }

    return true;
  }
}
