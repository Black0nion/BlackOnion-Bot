package com.github.ahitm_2020_2025.blackonionbot.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.io.Files;

public class FileUtils {
	public static void writeToFile(String fileName, String input) { 
	    try {
	    	new File("files/" + fileName + ".ahitm").delete();
		    BufferedWriter writer = new BufferedWriter(new FileWriter("files/" + fileName + ".ahitm"));
		    writer.write(input);
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String readFromFile(String fileName) {
		try {
		    BufferedReader reader = new BufferedReader(new FileReader("files/" + fileName + ".ahitm"));
		    String output = reader.readLine();
		    reader.close();
		    return output;
		} catch (IOException e) {
			if (!(e instanceof FileNotFoundException))
				e.printStackTrace();
		}
		return null;
	}
	
	public static String readFromFile(File file) {
		try {
		    BufferedReader reader = new BufferedReader(new FileReader(file));
		    String output = reader.readLine();
		    reader.close();
		    return output;
		} catch (IOException e) {
			if (!(e instanceof FileNotFoundException))
				e.printStackTrace();
		}
		return null;
	}
	
	
	public static String readFromFile(String fileName, String alternativeText) {
		if (readFromFile(fileName) != null)
			return readFromFile(fileName);
		writeToFile(fileName, alternativeText);
		return alternativeText;
	}
	
	public static ArrayList<String> readArrayListFromFile(String fileName) {
	    List<String> lines = Collections.emptyList();
	    try {
	    	new File("files/" + fileName + ".ahitm").createNewFile();
	    	lines = Files.readLines(new File("files/" + fileName + ".ahitm"), StandardCharsets.UTF_8);
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return new ArrayList<String>(lines);
	}
	
	public static void appendToFile(String fileName, String input) {
		try {
			if (!new File("files/" + fileName + ".ahitm").exists())
				new File("files/" + fileName + ".ahitm").createNewFile();
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("files/" + fileName + ".ahitm", true)));
		    out.println(input);
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
