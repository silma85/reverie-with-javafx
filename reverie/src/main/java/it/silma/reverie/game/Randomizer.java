/**
 * 
 */
package it.silma.reverie.game;

import it.silma.reverie.config.Constants;
import it.silma.reverie.config.Messages;

import java.util.Date;
import java.util.Random;

/**
 * Generates randomicity
 * 
 * @author a.putzu
 * 
 */
public class Randomizer {

	private static Random seed;

	public static int roll() {

		if (seed == null) {
			seed = new Random(new Date().getTime());
		}

		return seed.nextInt();
	}

	public static int roll(int bound) {
		return roll(0, bound);
	}

	public static int roll(int lower, int upper) {

		if (seed == null) {
			seed = new Random(new Date().getTime());
		}

		if (upper < 0)
			upper = -upper;

		if (lower < 0)
			lower = -lower;

		// TODO arbitrary bound
		if (upper <= lower) {
			upper = lower + 3;
		}

		if (upper == lower)
			return roll();

		int bound = upper - lower;

		return seed.nextInt(bound) + lower;
	}

	public static String rollString(String optionsAll) {

		String[] options = optionsAll.split(Constants.SEPARATOR_OPTION);

		return options[roll(options.length)];
	}

	public static String rollName() {
		return rollString(Messages.CREATURE_PLAYER_NAME_PREFIXES) + rollString(Messages.CREATURE_PLAYER_NAME_SUFFIXES);
	}
}
