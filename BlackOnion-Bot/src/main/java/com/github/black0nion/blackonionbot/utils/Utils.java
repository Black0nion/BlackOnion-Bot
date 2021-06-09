package com.github.black0nion.blackonionbot.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogMode;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.google.common.hash.Hashing;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

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

    public static String removeMarkdown(final String text) {
	return text.replace("_", "\\_").replace("*", "\\*");
    }

    public static String[] removeFirstArg(final String[] input) {
	return Arrays.copyOfRange(input, 1, input.length);
    }

    public static String round(final String decimal, final double number) {
	final DecimalFormat df = new DecimalFormat(decimal);
	df.setRoundingMode(RoundingMode.CEILING);
	return df.format(number);
    }

    public static double roundToDouble(final String decimal, final double number) {
	final DecimalFormat df = new DecimalFormat(decimal);
	df.setRoundingMode(RoundingMode.CEILING);
	return Double.valueOf(df.format(number).replace(",", "."));
    }

    public static boolean compareSHA256(final String hashed, final String unhashed) {
	final String sha256hex = Hashing.sha256().hashString(unhashed, StandardCharsets.UTF_8).toString();
	return hashed.equals(sha256hex);
    }

    public static String hashSHA256(final String input) {
	return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }

    public static String getCountryFromCode(final String code) {
	return new JSONObject(String.join("\n", new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream("/countrycodes.json"))).lines().collect(Collectors.joining()))).getString(code);
    }

    public static boolean isDiscordUser(final String token) {
	return getUserInfoFromToken(token).has("id");
    }

    public static String getUserIdFromToken(final String token) {
	final JSONObject userInfo = getUserInfoFromToken(token);
	if (userInfo.has("id")) return userInfo.getString("id");
	else return null;
    }

    // TODO: cache
    @Nullable
    public static JSONObject getUserInfoFromToken(final String token) {
	final JSONObject obj = new JSONObject(getUserInfoFromTokenResponse(token).getBody());

	return obj.has("id") ? obj : null;
    }

    public static HttpResponse<String> getUserInfoFromTokenResponse(final String token) {
	try {
	    Unirest.setTimeouts(0, 0);
	    return Unirest.get("https://discord.com/api/users/@me").header("Authorization", "Bearer " + token).asString();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static HttpResponse<String> getTokenFromCode(final String code) {
	final CredentialsManager manager = Bot.getCredentialsManager();
	if (!manager.has("client_id") || !manager.has("client_secret") || !manager.has("redirect_uri")) {
	    Logger.log(LogMode.ERROR, LogOrigin.API, "DiscordAuthSettings isn't filled correctly!");
	    return null;
	}
	try {
	    Unirest.setTimeouts(0, 0);
	    return Unirest.post("https://discord.com/api/oauth2/token").header("Content-Type", "application/x-www-form-urlencoded").field("code", code).field("client_id", manager.getString("client_id")).field("client_secret", manager.getString("client_secret")).field("grant_type", "authorization_code").field("redirect_uri", manager.getString("redirect_uri")).field("scope", "identify").asString();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static BufferedImage deepCopy(@NotNull final BufferedImage bufferedImage) {
	final ColorModel cm = bufferedImage.getColorModel();
	final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	final WritableRaster raster = bufferedImage.copyData(bufferedImage.getRaster().createCompatibleWritableRaster());

	return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static <T> T[] subArray(final T[] array, final int beg) {
	return subArray(array, beg, array.length - 1);
    }

    public static <T> T[] subArray(final T[] array, final int beg, final int end) {
	return Arrays.copyOfRange(array, beg, end + 1);
    }

    public static boolean isLong(final String input) {
	try {
	    Long.parseLong(input.trim());
	    return true;
	} catch (final Exception e) {
	    return false;
	}
    }

    /**
     * @return if the given Object equals to one of the other given Objects
     */
    @SafeVarargs
    public static <T> boolean equalsOne(final T input, final T... comparison) {
	return Arrays.asList(comparison).contains(input);
    }

    /**
     * @return if the given String equals to one of the other given Strings
     *         (ignoring case)
     */
    public static boolean equalsOneIgnoreCase(final String input, final String... comparison) {
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
	numbersUnicode.put(10, "U+1F51F");
    }

    /**
     * @param guild
     * @param author
     * @param channel
     * @param permissions
     * @return missing permissions?
     */
    public static boolean handleRights(final BlackGuild guild, final BlackUser author, final TextChannel channel, final Permission... permissions) {
	if (channel == null) {
	    if (!guild.getSelfMember().hasPermission(permissions)) return true;
	} else if (!guild.getSelfMember().hasPermission(channel, permissions)) {
	    if (channel != null) {
		channel.sendMessage(Utils.noRights(guild, author, permissions)).queue();
	    }
	    return true;
	}
	return false;
    }

    public static MessageEmbed noRights(final BlackGuild guild, final BlackUser author, final Permission... missingPermissions) {
	return EmbedUtils.getErrorEmbed(author, guild).addField("idonthavepermissions", LanguageSystem.getTranslation("requiredpermissions", author, guild) + "\n" + getPermissionString(missingPermissions), false).build();
    }

    public static String getPermissionString(final Permission... permissions) {
	String output = "```";
	for (int i = 0; i < permissions.length; i++) {
	    output += "- " + permissions[i].getName() + (i == permissions.length - 1 ? "```" : "\n");
	}
	return output;
    }

    public static <T> T[] concatenate(final T[] a, final T[] b) {
	final int aLen = a.length;
	final int bLen = b.length;

	@SuppressWarnings("unchecked")
	final T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
	System.arraycopy(a, 0, c, 0, aLen);
	System.arraycopy(b, 0, c, aLen, bLen);

	return c;
    }

    public static <T> Object[] toObjectArray(final T[] input) {
	return Arrays.asList(input).stream().map(map -> (Object) map).toArray();
    }

    public static String parseDate(final long diff) {
	final long diffSeconds = diff / 1000 % 60;
	final long diffMinutes = diff / (60 * 1000) % 60;
	final long diffHours = diff / (60 * 60 * 1000) % 24;
	final long diffDays = diff / (24 * 60 * 60 * 1000);

	return (diffDays != 0 ? diffDays + " days" : "") + (diffHours != 0 ? " " + diffHours + " hours" : "") + (diffMinutes != 0 ? " " + diffMinutes + " minutes" : "") + (diffSeconds != 0 ? " " + diffSeconds + " seconds" : "");
    }
}