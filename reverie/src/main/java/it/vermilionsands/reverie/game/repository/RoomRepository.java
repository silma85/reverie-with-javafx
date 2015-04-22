package it.vermilionsands.reverie.game.repository;

import it.vermilionsands.reverie.game.domain.Room;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {

  public Room findByCode(String code);

  @Modifying
  @Query("update Room r set r.north = :new where r.north.new = :old")
  public int updateNorth(final @Param("new") Room newRoom, final @Param("old") String old);

  @Modifying
  @Query("update Room r set r.south = :new where r.south.new = :old")
  public int updateSouth(final @Param("new") Room newRoom, final @Param("old") String old);

  @Modifying
  @Query("update Room r set r.west = :new where r.west.new = :old")
  public int updateWest(final @Param("new") Room newRoom, final @Param("old") String old);

  @Modifying
  @Query("update Room r set r.east = :new where r.east.new = :old")
  public int updateEast(final @Param("new") Room newRoom, final @Param("old") String old);

  @Modifying
  @Query("update Room r set r.up = :new where r.up.new = :old")
  public int updateUp(final @Param("new") Room newRoom, final @Param("old") String old);

  @Modifying
  @Query("update Room r set r.down = :new where r.down.new = :old")
  public int updateDown(final @Param("new") Room newRoom, final @Param("old") String old);
}
