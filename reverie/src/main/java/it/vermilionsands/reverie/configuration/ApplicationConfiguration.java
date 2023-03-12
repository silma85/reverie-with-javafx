/**
 * 
 */
package it.vermilionsands.reverie.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import it.vermilionsands.reverie.gui.MainPaneController;
import javafx.fxml.FXMLLoader;

/**
 * @author alessandro.putzu
 *
 */
@Configuration
@ComponentScan
public class ApplicationConfiguration {

  @Bean
  public MainPaneController mainPaneController() throws IOException {
    return (MainPaneController) this.loadController("/gui/MainPane.fxml");
  }

  protected Object loadController(String url) throws IOException {
    URL location = getClass().getResource(url);
    FXMLLoader loader = new FXMLLoader(location);
    loader.load();
    return loader.getController();
  }
}
