/**
 * 
 */
package it.vermilionsands.reverie.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author alessandro.putzu
 *
 */
@Component
public final class Messages {

  private final static Logger log = LoggerFactory.getLogger(Messages.class);

  @Autowired
  private MessageSource messages;

  public String get(String key) {
    return get(key, new Object[] {});
  }

  public String get(String key, Object... args) {
    try {
      return messages.getMessage(key, args, LocaleContextHolder.getLocale());
    } catch (NoSuchMessageException e) {
      log.warn("Nessun messaggio per la chiave: " + key);
      return "";
    }
  }
}
