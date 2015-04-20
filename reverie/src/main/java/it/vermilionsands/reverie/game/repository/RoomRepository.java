package it.vermilionsands.reverie.game.repository;

import it.vermilionsands.reverie.game.domain.rooms.Room;

import org.springframework.data.repository.CrudRepository;

public interface RoomRepository extends CrudRepository<Room, Long> {

}
