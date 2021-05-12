package com.github.black0nion.blackonionbot.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.bot.BotSecrets;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.google.common.hash.Hashing;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class Utils {

	private static ArrayList<CachedUserInfo> cachedUserInfo = new ArrayList<>();
	
	public static String removeMarkdown(String text) {
		return text.replace("_", "\\_").replace("*", "\\*");
	}

	//TODO: overarbeit
	public static CharSequence removeFirstArg(String[] args) {
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
	
	public static double roundToDouble(String decimal, double number) {
		DecimalFormat df = new DecimalFormat(decimal);
		df.setRoundingMode(RoundingMode.CEILING);
		return Double.valueOf(df.format(number).replace(",", "."));
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
	
	public static boolean isDiscordUser(String token) {
		return getUserInfoFromToken(token).has("id");
	}
	
	public static String getUserIdFromToken(String token) {
			JSONObject userInfo = getUserInfoFromToken(token);
			if (userInfo.has("id"))
				return userInfo.getString("id");
			else
				return null;
	}
	
	public static int gc() {
		ArrayList<CachedUserInfo> found = new ArrayList<>();
		for (CachedUserInfo userInfo : cachedUserInfo) {
			if (Duration.between(userInfo.getRefreshDate().toInstant(), Instant.now()).toHours() >= 2)
				found.add(userInfo);
		}
		
		cachedUserInfo.removeAll(found);
		return found.size();
	}
	
	public static JSONObject getUserInfoFromToken(String token) {
		for (int i = 0; i < cachedUserInfo.size(); i++) {
			CachedUserInfo userInfo = cachedUserInfo.get(i);
			if (userInfo.getToken().equals(token)) {
				if (Duration.between(userInfo.getRefreshDate().toInstant(), Instant.now()).toHours() >= 2) {
					cachedUserInfo.remove(i);
					CachedUserInfo refreshed = userInfo.refresh(token);
					cachedUserInfo.add(refreshed);
					userInfo = userInfo.refresh(token);
					return userInfo.getInfo().has("id") ? userInfo.getInfo() : null;
				} else {
					return userInfo.getInfo().has("id") ? userInfo.getInfo() : null;
				}
			}
		}
		
		CachedUserInfo newInfo = new CachedUserInfo(new JSONObject(getUserInfoFromTokenResponse(token).getBody()), token);
		
		cachedUserInfo.add(newInfo);
		
		return newInfo.getInfo().has("id") ? newInfo.getInfo() : null;
	}
	
	public static HttpResponse<String> getUserInfoFromTokenResponse(String token) {
		try {
			Unirest.setTimeouts(0, 0);
			return Unirest.get("https://discord.com/api/users/@me")
			  .header("Authorization", "Bearer " + token)
			  .asString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Deprecated
	public static HttpResponse<String> getTokenFromCode(String code) {
		CredentialsManager manager = Bot.getCredentialsManager();
		if (!manager.has("client_id") || !manager.has("client_secret") || !manager.has("redirect_uri")) {
			Logger.log(LogMode.ERROR, LogOrigin.API, "DiscordAuthSettings isn't filled correctly!");
			return null;
		}
		try {
			Unirest.setTimeouts(0, 0);
			return Unirest.post("https://discord.com/api/oauth2/token")
					  .header("Content-Type", "application/x-www-form-urlencoded")
					  .field("code", code)
					  .field("client_id", manager.getString("client_id"))
					  .field("client_secret", manager.getString("client_secret"))
					  .field("grant_type", "authorization_code")
					  .field("redirect_uri", manager.getString("redirect_uri"))
					  .field("scope", "identify")
					  .asString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isAdmin(String token) {
		return BotSecrets.isAdmin(Long.valueOf(getUserInfoFromToken(token).getString("id")));
	}
	
	public static BufferedImage deepCopy(@NotNull final BufferedImage bufferedImage) {
        final ColorModel cm = bufferedImage.getColorModel();
        final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        final WritableRaster raster = bufferedImage.copyData(bufferedImage.getRaster().createCompatibleWritableRaster());

        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
	
	public static<T> T[] subArray(T[] array, int beg) {
		return subArray(array, beg, array.length-1);
	}
	
	public static<T> T[] subArray(T[] array, int beg, int end) {
	       return Arrays.copyOfRange(array, beg, end + 1);
	}
	 
	public static String getPleaseUse(Guild guild, User author, Command command) {
		return LanguageSystem.getTranslation("pleaseuse", author, guild) + " " + getCommandHelp(guild, author, command);
	}
	
	public static String getCommandHelp(Guild guild, User author, Command command) {
		return BotInformation.getPrefix(guild) + command.getCommand()[0] + " " + command.getSyntax();
	}
}
