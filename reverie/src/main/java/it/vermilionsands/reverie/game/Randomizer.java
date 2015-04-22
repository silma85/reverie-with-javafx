/**
 * 
 */
package it.vermilionsands.reverie.game;

import it.vermilionsands.reverie.configuration.Constants;
import it.vermilionsands.reverie.configuration.Messages;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Generates randomicity
 * 
 * @author a.putzu
 * 
 */
@Component
public class Randomizer {

  private Random seed;

  @Autowired
  private Messages messages;

  public int roll() {

    if (seed == null) {
      seed = new Random(new Date().getTime());
    }

    return seed.nextInt();
  }

  public int roll(int bound) {
    return roll(0, bound);
  }

  public int roll(int lower, int upper) {

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

  public String rollString(String optionsAll, Object... args) {
    String[] options = optionsAll.split(Constants.SEPARATOR_OPTION);

    for (int i = 0; i < args.length; i++) {
      args[i] = StringUtils.capitalize(args[i].toString());
    }

    return String.format(options[roll(options.length)], args);
  }

  public String rollName() {
    return rollString(messages.get(Constants.CREATURE_PLAYER_NAME_PREFIXES))
            + rollString(messages.get(Constants.CREATURE_PLAYER_NAME_SUFFIXES));
  }

}
