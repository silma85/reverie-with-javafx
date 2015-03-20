package it.silma.reverie.config;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {

	private static final String CREATURES_BUNDLE_NAME = "it.silma.reverie.config.lang.creatures";
	private static final String ITEMS_BUNDLE_NAME = "it.silma.reverie.config.lang.items";
	private static final String MAIN_BUNDLE_NAME = "it.silma.reverie.config.lang.main";
	private static final String ROOMS_BUNDLE_NAME = "it.silma.reverie.config.lang.rooms";

	private static ResourceBundle CREATURES_BUNDLE = ResourceBundle.getBundle(CREATURES_BUNDLE_NAME);
	private static ResourceBundle ITEMS_BUNDLE = ResourceBundle.getBundle(ITEMS_BUNDLE_NAME);
	private static ResourceBundle MAIN_BUNDLE = ResourceBundle.getBundle(MAIN_BUNDLE_NAME);
	private static ResourceBundle ROOMS_BUNDLE = ResourceBundle.getBundle(ROOMS_BUNDLE_NAME);

	public static void setLocale(Locale locale) {
		CREATURES_BUNDLE = ResourceBundle.getBundle(CREATURES_BUNDLE_NAME, locale);
		ITEMS_BUNDLE = ResourceBundle.getBundle(ITEMS_BUNDLE_NAME, locale);
		MAIN_BUNDLE = ResourceBundle.getBundle(MAIN_BUNDLE_NAME, locale);
		ROOMS_BUNDLE = ResourceBundle.getBundle(ROOMS_BUNDLE_NAME, locale);
	}

	private Messages() {
	}

	public static String get(String key) {

		String message = null;

		try {

			message = CREATURES_BUNDLE.getString(key);
			return message;

		} catch (MissingResourceException e) {

		}

		try {

			message = ITEMS_BUNDLE.getString(key);
			return message;

		} catch (MissingResourceException e) {

		}

		try {

			message = MAIN_BUNDLE.getString(key);
			return message;

		} catch (MissingResourceException e) {

		}

		try {

			message = ROOMS_BUNDLE.getString(key);
			return message;

		} catch (MissingResourceException e) {

		}

		return null;
	}

	// Constanti letterali
	public static final String VERSION = Messages.get("reverie.version.major") + "."
			+ Messages.get("reverie.version.minor");

	public static final String CREATURE_PLAYER_NAME_PREFIXES = Messages.get("reverie.creatures.name.prefixes");
	public static final String CREATURE_PLAYER_NAME_SUFFIXES = Messages.get("reverie.creatures.name.suffixes");

	public static final String GUI_TITLE = Messages.get("reverie.gui.title") + Messages.VERSION;
	public static final String GUI_COMMANDS = Messages.get("reverie.gui.command.list");
	public static final String GUI_COMMAND_REFUSED = Messages.get("reverie.gui.command.refused");
}
