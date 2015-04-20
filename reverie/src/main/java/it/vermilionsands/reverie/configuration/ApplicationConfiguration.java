/**
 * 
 */
package it.vermilionsands.reverie.configuration;

import it.vermilionsands.reverie.gui.MainPaneController;

import java.io.IOException;
import java.io.InputStream;

import javafx.fxml.FXMLLoader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author alessandro.putzu
 *
 */
@Configuration
@ComponentScan
public class ApplicationConfiguration {

  @Bean
  public MainPaneController mainPaneController() throws IOException {
    return (MainPaneController) this.loadController("/it/vermilionsands/reverie/gui/MainPane.fxml");
  }

  protected Object loadController(String url) throws IOException {
    InputStream fxmlStream = null;
    try {
      fxmlStream = getClass().getResourceAsStream(url);
      FXMLLoader loader = new FXMLLoader();
      loader.load(fxmlStream);
      return loader.getController();
    } finally {
      if (fxmlStream != null) {
        fxmlStream.close();
      }
    }
  }
}
