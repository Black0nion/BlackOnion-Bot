package com.github.black0nion.blackonionbot.utils;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileUtils {

  public static void appendToFile(final String fileName, final String input) {
    try {
      final File file = new File(fileName);
      final File parentFile = file.getParentFile();
      if (parentFile != null) {
        parentFile.mkdirs();
      }
      if (!file.exists()) {
        file.createNewFile();
      }
      Files.asCharSink(file, StandardCharsets.UTF_8, FileWriteMode.APPEND).write(input);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
