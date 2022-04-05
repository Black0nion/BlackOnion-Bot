package com.github.black0nion.blackonionbot.utils.config;

import java.util.HashMap;

public record BotMetadata(String version, HashMap<String, String> authors,
    HashMap<String, String> blackonion_authors, int lines_of_code, int files) {
  public BotMetadata() {
    this("N/A", new HashMap<>(), new HashMap<>(), -1, -1);
  }
}
