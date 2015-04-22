package it.vermilionsands.reverie.game.repository;

import it.vermilionsands.reverie.game.domain.Item;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {

  public Item findByKeywordsIn(String keyword);

  public Item findByCode(String itemCode);

}
