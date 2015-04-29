/**
 * 
 */
package it.vermilionsands.reverie.game.domain;

/**
 * Directions enum.
 * 
 * @author alessandro.putzu
 *
 */
public enum Directions {

  N,
  S,
  W,
  E,
  U,
  D;

  public static Directions get(final int index) {
    return Directions.values()[index];
  }

  public static Room getInDirection(final Room room, final Directions direction) {
    switch (direction) {
    case N:
      return room.getNorth();

    case S:
      return room.getSouth();

    case W:
      return room.getWest();

    case E:
      return room.getEast();

    case U:
      return room.getUp();

    case D:
      return room.getDown();
    }

    return null;
  }
}
