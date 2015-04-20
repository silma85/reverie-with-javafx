/**
 * 
 */
package it.vermilionsands.reverie.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author alessandro.putzu
 *
 */
@Component
public final class Messages {

  @Autowired
  private MessageSource messages;

  public String get(String key) {
    return messages.getMessage(key, null, LocaleContextHolder.getLocale());
  }

  public String get(String key, Object... args) {
    return messages.getMessage(key, args, LocaleContextHolder.getLocale());
  }
}
