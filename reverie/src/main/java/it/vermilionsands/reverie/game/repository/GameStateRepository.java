package it.vermilionsands.reverie.game.repository;

import it.vermilionsands.reverie.game.domain.GameState;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameStateRepository extends CrudRepository<GameState, Long> {

  public GameState findTopByOrderBySaveDateDesc();

}
