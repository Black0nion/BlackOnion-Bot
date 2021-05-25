package com.github.black0nion.blackonionbot.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
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

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Utils {
	
	public static List<Character> alphabet = new ArrayList<>();
	
	static {
		alphabet.add('A');
		alphabet.add('B');
		alphabet.add('C');
		alphabet.add('D');
		alphabet.add('E');
		alphabet.add('F');
		alphabet.add('G');
		alphabet.add('H');
		alphabet.add('I');
		alphabet.add('J');
		alphabet.add('K');
		alphabet.add('L');
		alphabet.add('M');
		alphabet.add('N');
		alphabet.add('O');
		alphabet.add('P');
		alphabet.add('Q');
		alphabet.add('R');
		alphabet.add('S');
		alphabet.add('T');
		alphabet.add('U');
		alphabet.add('V');
		alphabet.add('W');
		alphabet.add('X');
		alphabet.add('Y');
		alphabet.add('Z');
	}

	private static ArrayList<CachedUserInfo> cachedUserInfo = new ArrayList<>();
	
	public static String removeMarkdown(String text) {
		return text.replace("_", "\\_").replace("*", "\\*");
	}

	public static String[] removeFirstArg(String[] input) {
		return Arrays.copyOfRange(input, 1, input.length);
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
	 
	public static String getPleaseUse(BlackGuild guild, BlackUser author, Command command) {
		return LanguageSystem.getTranslation("pleaseuse", author, guild).replace("%command%", getCommandHelp(guild, author, command));
	}
	
	public static String getCommandHelp(Guild guild, User author, Command command) {
		final String syntax = command.getSyntax();
		return "`" + BotInformation.getPrefix(guild) + command.getCommand()[0] + (syntax != null && !syntax.equalsIgnoreCase("") ? " " + syntax : "") + "`";
	}
	
	public static MessageEmbed getWrongArgument(BlackUser author, BlackGuild guild, Command command) {
		return EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, command), false).build();
	}
	
	public static <K, V> Map.Entry<K, V> getMapEntry(K key, V value) {
		return new Map.Entry<K, V>() {
			V val = value;
			
			@Override
			public V getValue() {
				return val;
			}
			
			@Override
			public K getKey() {
				return key;
			}
			
			@Override
			public V setValue(V value) {
				this.val = value;
				return value;
			}
		};
	}
	
	public static boolean isLong(String input) {
		try {
			Long.parseLong(input.trim());
			return true;
		} catch (Exception e) { return false; }
	}
	
	/**
	 * @return if the given Object equals to one of the other given Objects
	 */
	@SafeVarargs
	public static <T> boolean equalsOne(T input, T... comparison) {
		return Arrays.asList(comparison).contains(input);
	}
	
	/**
	 * @return if the given String equals to one of the other given Strings (ignoring case)
	 */
	public static boolean equalsOneIgnoreCase(String input, String... comparison) {
		return input.matches("(?i)" + String.join("|", comparison));
	}

	/**
	 * WARNING: ONLY UNTIL 10 (inclusive)
	 */
	public static final HashMap<Integer, String> numbersUnicode = new HashMap<>();
	public static final String[] emojis = new String[] { ":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":ten:" };

    static {
		numbersUnicode.put(0, "U+30U+fe0fU+20e3");
		numbersUnicode.put(1, "U+31U+fe0fU+20e3");
		numbersUnicode.put(2, "U+32U+fe0fU+20e3");
		numbersUnicode.put(3, "U+33U+fe0fU+20e3"); 
		numbersUnicode.put(4, "U+34U+fe0fU+20e3");
		numbersUnicode.put(5, "U+35U+fe0fU+20e3");
		numbersUnicode.put(6, "U+36U+fe0fU+20e3");
		numbersUnicode.put(7, "U+37U+fe0fU+20e3");
		numbersUnicode.put(8, "U+38U+fe0fU+20e3");
		numbersUnicode.put(9, "U+39U+fe0fU+20e3");
		numbersUnicode.put(10,"U+1F51F");
	}
    
    /**
     * @param guild
     * @param author
     * @param channel
     * @param permissions
     * @return missing permissions?
     */
    public static boolean handleRights(BlackGuild guild, BlackUser author, TextChannel channel, Permission... permissions) {
    	if (!guild.getSelfMember().hasPermission(channel, permissions)) {
			if (channel != null) channel.sendMessage(Utils.noRights(guild, author, permissions)).queue();
			return true;
		}
    	return false;
    }

	public static MessageEmbed noRights(BlackGuild guild, BlackUser author, Permission... missingPermissions) {
		return EmbedUtils.getErrorEmbed(author, guild).addField("idonthavepermissions", LanguageSystem.getTranslation("requiredpermissions", author, guild) + "\n" + getPermissionString(missingPermissions), false).build();
	}

	public static String getPermissionString(Permission... permissions) {
		String output = "```";
		for (int i = 0; i  < permissions.length; i++) {
			output += "- " + permissions[i].getName() + (i == permissions.length-1 ? "```" : "\n");
		}
		return output;
	}
	
	public static <T> T[] concatenate(T[] a, T[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    @SuppressWarnings("unchecked")
	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}
}