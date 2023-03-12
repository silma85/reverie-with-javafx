package it.vermilionsands.reverie.game.repository;

import it.vermilionsands.reverie.game.domain.PlayerCharacter;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerCharacterRepository extends CrudRepository<PlayerCharacter, Long> {

}
