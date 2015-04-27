package it.vermilionsands.reverie.game.repository;

import it.vermilionsands.reverie.game.domain.Item;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ItemRepository extends CrudRepository<Item, Long> {

  public List<Item> findByKeywordsContaining(String keyword);

  public Item findByCode(String itemCode);

}
