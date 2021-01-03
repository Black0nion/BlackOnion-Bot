package com.github.ahitm_2020_2025.blackonionbot.utils;

import java.io.File;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import com.google.common.hash.Hashing;

public class Utils {
	public static String removeMarkdown(String text) {
		return text.replace("_", "\\_").replace("*", "\\*");
	}

	public static CharSequence getStringFromList(String[] args) {
		String str = "";
		ArrayList<String> argsArray = new ArrayList<>(Arrays.asList(args));
		argsArray.remove(0);
		for (String s : argsArray) {
			str += s + " ";
		}
		return str;
	}
	
	public static String round(String decimal, double number) {
		DecimalFormat df = new DecimalFormat(decimal);
		df.setRoundingMode(RoundingMode.CEILING);
	    return df.format(number);
	}
	
	public static boolean compareSHA256(String hashed, String unhashed) {
		String sha256hex = Hashing.sha256()
				  .hashString(unhashed, StandardCharsets.UTF_8)
				  .toString();
		return hashed.equals(sha256hex);
	}
	
	public static String hashSHA256(String input) {
		return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
	}
	
	public static String getCountryFromCode(String code) {
		return new JSONObject(FileUtils.readFromFile(new File("resources/countrycodes.json"))).getString(code);
	}
}
