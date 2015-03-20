/**
 * 
 */
package it.silma.reverie.config;

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
	public static final String NSWE_SUFFIX = ".nswe";
	public static final String NSWE_DESC_SUFFIX = ".nswedesc";
	public static final String DESCRIPTION_SUFFIX = ".description";
	public static final String UP_DOWN_SUFFIX = ".ud";
	public static final String UP_DOWN_DESC_SUFFIX = ".uddesc";
	public static final String SEPARATOR = ";";
	public static final String SEPARATOR_OPTION = Pattern.quote("|");
	public static final String ALL_BLOCKS_KEY = ROOM_PREFIX + ".all.blocks";
	public static final String ALL_BLOCKS = Messages.get(ALL_BLOCKS_KEY);

	public static final String PATH_TO_RES = "it/silma/reverie/res/";

	public final static String REVERIE_ICON = "youthrive-spiral-icon.png";

	public final static String COMMAND_ACTION = "command";
	public final static String COMMAND_REFUSED = "command_refused";
	public final static String COMMAND_ACCEPTED = "command_accepted";
}
