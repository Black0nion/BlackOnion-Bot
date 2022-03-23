package com.github.black0nion.blackonionbot.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Utils {

	public static final List<Character> alphabet = new ArrayList<>();

	public static final Permission[] EMPTY_PERMISSIONS = new Permission[0];

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

	public static String getStringWithNLength(final String text, final int length) {
	    return new String(new char[length]).replace("\0", text);
	}

	public static float map(final float value, final float minInput, final float maxInput, final float minMapped, final float maxMapped) {
		return (value - minInput) / (maxInput - minInput) * (maxMapped - minMapped) + minMapped;
	}

	public static String getDuration(final long time) {
	    String result = "";
	    if (time / 3600 != 0) {
		result = String.format("%d:", time / 3600);
	    }
	    return result + String.format("%02d:%02d", (time % 3600) / 60, time % 60);
	}

	public static String escapeMarkdown(final String text) {
		return text
          .replace("\\(\\*|_|`|~|\\)", "$1")
          .replace("(\\*|_|`|~|\\)", "\\$1");
	}

	public static String[] removeFirstArg(final String[] input) {
		return Arrays.copyOfRange(input, 1, input.length);
	}

	public static double roundToDouble(final String decimal, final double number) {
		final DecimalFormat df = new DecimalFormat(decimal);
		df.setRoundingMode(RoundingMode.CEILING);
		return Double.parseDouble(df.format(number).replace(",", "."));
	}

	public static String getCountryFromCode(final String code) {
		return new JSONObject(String.join("\n", new BufferedReader(new InputStreamReader(Objects.requireNonNull(Utils.class.getResourceAsStream("/countrycodes.json")))).lines().collect(Collectors.joining()))).getString(code);
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
		if (input == null) return false;
		try {
			Long.parseLong(input.trim());
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public static boolean isLong(final Object input) {
		try {
			Long.parseLong(((String) input).trim());
			return true;
		} catch (final Exception ignored) {
			return false;
		}
	}

	public static boolean isInteger(final String input) {
		try {
			Integer.parseInt(input.trim());
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public static boolean isInteger(final Object input) {
		try {
			Integer.parseInt(((String) input).trim());
			return true;
		} catch (final Exception ignored) {
			return false;
		}
	}

	public static boolean isBoolean(String input) {
		return isBoolean((Object) input);
	}

	public static boolean isBoolean(final Object input) {
		try {
			//noinspection ResultOfMethodCallIgnored
			Boolean.parseBoolean(((String) input).trim());
            return true;
        } catch (final Exception ignored) {
            return false;
        }
	}

	/**
	 * @return if the given Object equals to one of the other given Objects
	 */
	@SuppressWarnings("unused")
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
	 * @return missing permissions?
	 */
	public static boolean handleRights(final BlackGuild guild, final BlackUser author, final TextChannel channel, final Permission... permissions) {
		if (channel == null) {
			return !guild.getSelfMember().hasPermission(permissions);
		} else if (!guild.getSelfMember().hasPermission(channel, permissions)) {
			channel.sendMessageEmbeds(Utils.noRights(guild, author, permissions)).queue();
			return true;
		}
		return false;
	}

	public static MessageEmbed noRights(final BlackGuild guild, final BlackUser author, final Permission... missingPermissions) {
		return EmbedUtils.getErrorEmbed(author, guild).addField("idonthavepermissions", LanguageSystem.getTranslation("requiredpermissions", author, guild) + "\n" + getPermissionString(missingPermissions), false).build();
	}

	public static String getPermissionString(final Permission... permissions) {
		StringBuilder output = new StringBuilder("```");
		for (int i = 0; i < permissions.length; i++) {
			output.append("- ").append(permissions[i].getName()).append(i == permissions.length - 1 ? "" : "\n");
		}
		return output + "```";
	}

	public static String getPermissionString(final CustomPermission... permissions) {
		StringBuilder output = new StringBuilder("```");
		for (int i = 0; i < permissions.length; i++) {
			output.append("- ").append(permissions[i].name()).append(i == permissions.length - 1 ? "" : "\n");
		}
		return output + "```";
	}

	public static String parseDate(final long diff) {
		final long diffSeconds = diff / 1000 % 60;
		final long diffMinutes = diff / (60 * 1000) % 60;
		final long diffHours = diff / (60 * 60 * 1000) % 24;
		final long diffDays = diff / (24 * 60 * 60 * 1000);

		return (diffDays != 0 ? diffDays + " days" : "") + (diffHours != 0 ? " " + diffHours + " hours" : "") + (diffMinutes != 0 ? " " + diffMinutes + " minutes" : "") + (diffSeconds != 0 ? " " + diffSeconds + " seconds" : "");
	}

	public static String firstLetterUppercase(final String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
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

	public static void printLogo() {
		System.out.println("""
			   ___  __         __   ____       _                  ___       __
			  / _ )/ /__  ____/ /__/ __ \\___  (_)__  ___   ____  / _ )___  / /_
			 / _  / / . |/ __/  '_/ /_/ / _ \\/ / _ \\/ _ \\ /___/ / _  / _ \\/ __/
			/____/_/\\_|_|\\__/_/\\_\\\\____/_//_/_/\\___/_//_/      /____/\\___/\\__/
			""");
	}

	private static final DecimalFormat ROUNDED_DOUBLE_DECIMALFORMAT;

	static {
		final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		ROUNDED_DOUBLE_DECIMALFORMAT = new DecimalFormat("####0.00", otherSymbols);
		ROUNDED_DOUBLE_DECIMALFORMAT.setGroupingUsed(false);
	}

	public static String arrayToString(String delimiter, StackTraceElement[] stackTrace) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < stackTrace.length; i++) {
			result.append(stackTrace[i].toString()).append(i == stackTrace.length - 1 ? "" : delimiter);
		}
		return result.toString();
	}

	public static String getJarName() {
		return new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
	}

	public static boolean runningFromJar() {
		return getJarName().contains(".jar");
	}

	@Nonnull
	public static <T> T gOD(final @Nullable T value, final @Nonnull T defaultValue) {
		return value != null ? value : defaultValue;
	}

	public static <T> List<T> add(List<T> list, T object) {
		list.add(object);
		return list;
	}

	public static String list(Object obj) {
		return "- " + obj;
	}

	public static <T extends Enum<T>> T parse(Class<T> clazz, String value) {
		for (T val : clazz.getEnumConstants()) {
			if (val.name().equalsIgnoreCase(value)) {
				return val;
			}
		}
		return null;
	}

	public static <T> T getOrReplaceMessage(Callable<T> msg, String replacedMessage) {
		try {
			return msg.call();
		} catch (Exception e) {
			throw new RuntimeException(replacedMessage, e);
		}
	}

	public static WebhookClient makeWebhookClient(Webhook webhook) {
		final WebhookClientBuilder clientBuilder = new WebhookClientBuilder(webhook.getUrl());
		clientBuilder.setThreadFactory(job -> {
			final Thread thread = new Thread(job);
			thread.setName("ContentModerator");
			thread.setDaemon(true);
			return thread;
		});

		return clientBuilder.build();
	}

	public static Webhook getWebhook(TextChannel channel, List<Webhook> webhooks) throws IOException {
		return webhooks.stream()
			.filter(Objects::nonNull)
			.filter(webhook1 -> webhook1.getOwner() != null)
			.filter(webhook1 -> webhook1.getName().equals("BlackOnion-Bot ContentModerator") && webhook1.getOwner().getIdLong() == BotInformation.SELF_USER_ID)
			.findFirst()
			.orElse(channel
				.createWebhook("BlackOnion-Bot ContentModerator")
				.setAvatar(Icon.from(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("/logo.png"))))
				.submit()
				.join());
	}

	public static JSONArray optionsToJson(List<OptionData> options) {
		final JSONArray jsonArray = new JSONArray();
		for (OptionData option : options) {
			jsonArray.put(new JSONObject()
				.put("name", option.getName())
				.put("description", option.getDescription())
				.put("type", option.getType().name())
				.put("required", option.isRequired())
			);
		}
		return jsonArray;
	}
}