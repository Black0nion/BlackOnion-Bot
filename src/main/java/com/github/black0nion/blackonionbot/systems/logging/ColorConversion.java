package com.github.black0nion.blackonionbot.systems.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class ColorConversion extends ForegroundCompositeConverterBase<ILoggingEvent> {
  @Override
  protected String getForegroundColorCode(ILoggingEvent event) {
    return switch (event.getLevel().toInt()) {
      case Level.DEBUG_INT -> ANSIConstants.CYAN_FG;
      case Level.INFO_INT -> ANSIConstants.BLUE_FG;
      case Level.WARN_INT -> ANSIConstants.YELLOW_FG;
      case Level.ERROR_INT -> ANSIConstants.RED_FG;
      default -> ANSIConstants.DEFAULT_FG;
    };
  }
}
