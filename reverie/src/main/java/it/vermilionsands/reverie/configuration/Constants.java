/**
 * 
 */
package it.vermilionsands.reverie.configuration;

import java.util.regex.Pattern;

/**
 * Constants values and strings
 * 
 * @author a.putzu
 * 
 */
public enum Constants {

  ;

  public static final String ERROR_NO_RESOURCE = "Irreversible error finding resource ";

  // Costanti di uso interno
  public static final String ROOM_PREFIX = "rooms";
  public static final String ROOM_NSWE_SUFFIX = ".nswe";
  public static final String ROOM_NSWE_DESC_SUFFIX = ".nswedesc";
  public static final String ROOM_DESCRIPTION_SUFFIX = ".description";
  public static final String ROOM_UD_SUFFIX = ".ud";
  public static final String ROOM_UD_DESC_SUFFIX = ".uddesc";
  public static final String ROOM_DIRECTIONS_PREFIX = "rooms.directions";
  public static final String ROOM_ALL_KEY = ROOM_PREFIX + ".all.blocks";
  public static final String ROOM_FLIP_SUFFIX = ".flip";
  public static final String ROOM_ITEM_SUFFIX = ".items";

  public static final String ITEM_PREFIX = "items";
  public static final String ITEM_DESCRIPTION_SUFFIX = ".description";
  public static final String ITEM_DESCRIPTION_FLIPPED_SUFFIX = ".description.flipped";
  public static final String ITEM_NOFLIP_SUFFIX = ".noflip";
  public static final String ITEM_FLIP_SUFFIX = ".flip";
  public static final String ITEM_LOOK_SUFFIX = ".look";
  public static final String ITEM_NOPICKUP_SUFFIX = ".nopickup";
  public static final String ITEM_NODROP_SUFFIX = ".nodrop";
  public static final String ITEM_TANGIBLE_SUFFIX = ".tangible";
  public static final String ITEM_ALL_KEY = ITEM_PREFIX + ".all";
  public static final String ITEM_KEYWORDS_SUFFIX = ".keywords";
  public static final String ITEM_GENRE_SUFFIX = ".genre";
  public static final String ITEM_ACTION_SUFFIX = ".actions";
  public static final String ITEM_AMBIENCE_SUFFIX = ".ambience";
  public static final String ITEM_AMBIENCE_FLIPPED_SUFFIX = ".ambience.flipped";

  public static final String SEPARATOR = ";";
  public static final String SEPARATOR_OPTION = Pattern.quote("|");

  public final static String REVERIE_ICON = "spiral-icon.png";

  public final static String COMMAND_ACTION = "command";
  public final static String COMMAND_ACCEPTED = "reverie.gui.command.success.default";
  public final static String COMMAND_NOT_FOUND = "reverie.gui.command.unfound";

  public final static String CREATURE_PLAYER_NAME_PREFIXES = "reverie.creatures.name.prefixes";
  public final static String CREATURE_PLAYER_NAME_SUFFIXES = "reverie.creatures.name.suffixes";

  public final static String ROOM_DEFAULT = "intro";
  public final static String ROOM_INITIAL = "pillar";
}
