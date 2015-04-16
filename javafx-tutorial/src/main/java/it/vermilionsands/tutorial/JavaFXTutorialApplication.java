/**
 * 
 */
package it.vermilionsands.tutorial;

import it.vermilionsands.tutorial.javafx.JavaFXTutorial;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring initialization entry point.
 * 
 * @author alessandro.putzu
 *
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class JavaFXTutorialApplication {

  public static void main(String[] args) {
    JavaFXTutorial.launchInternal(args);
  }

}
