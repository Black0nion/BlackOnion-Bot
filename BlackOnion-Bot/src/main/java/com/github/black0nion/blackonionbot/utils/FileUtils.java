package com.github.black0nion.blackonionbot.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

public class FileUtils {
	
	public static String readFromFile(File file) {
		try {
			return String.join(" ", Files.asCharSource(file, StandardCharsets.UTF_8).readLines());
		} catch (IOException e) {
			if (!(e instanceof FileNotFoundException))
				e.printStackTrace();
		}
		return null;
	}
	
	public static void appendToFile(String fileName, String input) {
		try {
			final File file = new File(fileName);
			final File parentFile = file.getParentFile();
			if (parentFile != null)
				parentFile.mkdirs();
			if (!file.exists())
				file.createNewFile();
			Files.asCharSink(file, StandardCharsets.UTF_8, FileWriteMode.APPEND);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
