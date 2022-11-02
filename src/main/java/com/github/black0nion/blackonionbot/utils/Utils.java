package com.github.black0nion.blackonionbot.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import com.github.black0nion.blackonionbot.Main;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.ygimenez.model.InteractPage;
import com.github.ygimenez.model.Page;
import com.google.common.collect.Lists;
import com.google.gson.internal.Primitives;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class Utils {
	private Utils() {}

	public static final List<Character> ALPHABET = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');

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

	public static double roundToDouble(final String decimal, final double number) {
		final DecimalFormat df = new DecimalFormat(decimal);
		df.setRoundingMode(RoundingMode.CEILING);
		return Double.parseDouble(df.format(number).replace(",", "."));
	}

	private static final JSONObject COUNTRIES_JSON = new JSONObject(String.join("\n", new BufferedReader(new InputStreamReader(requireNonNull(Utils.class.getResourceAsStream("/countrycodes.json")))).lines().collect(Collectors.joining())));

	public static String getCountryFromCode(final String code) {
		return COUNTRIES_JSON.getString(code);
	}

	public static BufferedImage deepCopy(@NotNull final BufferedImage bufferedImage) {
		final ColorModel cm = bufferedImage.getColorModel();
		final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		final WritableRaster raster = bufferedImage.copyData(bufferedImage.getRaster().createCompatibleWritableRaster());

		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
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
			if (input instanceof Number) return true;
			Long.parseLong(input.toString().trim());
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

	public static boolean isBoolean(String input) {
		return isBoolean((Object) input);
	}

	public static boolean isBoolean(final Object input) {
		if (input instanceof Boolean) return true;
		return input != null && (input.toString().trim().equalsIgnoreCase("true") || input.toString().trim().equalsIgnoreCase("false"));
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
	 * (ignoring case)
	 */
	public static boolean equalsOneIgnoreCase(final String input, final String... comparison) {
		return input.matches("(?i)" + String.join("|", comparison));
	}

	/**
	 * WARNING: ONLY UNTIL 10 (inclusive)
	 */
	public static final Map<Integer, String> NUMBERS_UNICODE = new HashMap<>();
	public static final String[] EMOJIS = new String[] { ":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":ten:" };

	static {
		NUMBERS_UNICODE.put(0, "U+30U+fe0fU+20e3");
		NUMBERS_UNICODE.put(1, "U+31U+fe0fU+20e3");
		NUMBERS_UNICODE.put(2, "U+32U+fe0fU+20e3");
		NUMBERS_UNICODE.put(3, "U+33U+fe0fU+20e3");
		NUMBERS_UNICODE.put(4, "U+34U+fe0fU+20e3");
		NUMBERS_UNICODE.put(5, "U+35U+fe0fU+20e3");
		NUMBERS_UNICODE.put(6, "U+36U+fe0fU+20e3");
		NUMBERS_UNICODE.put(7, "U+37U+fe0fU+20e3");
		NUMBERS_UNICODE.put(8, "U+38U+fe0fU+20e3");
		NUMBERS_UNICODE.put(9, "U+39U+fe0fU+20e3");
		NUMBERS_UNICODE.put(10, "U+1F51F");
	}

	/**
	 * Pass null as the channel argument to check self permissions.
	 * USED FOR SELF PERMISSIONS!
	 *
	 * @param callback the IReplyCallback object that `replyEmbeds` gets called on
	 * @return missing permissions?
	 */
	public static boolean handleSelfRights(final BlackGuild guild, final BlackUser author, final TextChannel channel, @Nullable IReplyCallback callback, final Permission... permissions) {
		if (channel == null) {
			return !guild.getSelfMember().hasPermission(permissions);
		} else if (!guild.getSelfMember().hasPermission(channel, permissions)) {
			(callback != null
				? callback.replyEmbeds(noRights(guild, author, permissions))
				: channel.sendMessageEmbeds(noRights(guild, author, permissions))
			).queue();
			return true;
		}
		return false;
	}

	public static MessageEmbed noRights(final BlackGuild guild, final BlackUser author, final Permission... missingPermissions) {
		return EmbedUtils.getErrorEmbed(author, guild).addField("idonthavepermissions", LanguageSystem.getTranslation("requiredpermissions", author, guild) + "\n" + getPermissionString(missingPermissions), false).build();
	}

	/**
	 * Returned format:
	 * <pre>{@code
	 *     - DO_THINGS
	 *     - DO_OTHER_THINGS
	 * }</pre>
	 */
	public static String getPermissionString(final Permission... permissions) {
		if (permissions.length == 0) return "```\n```";
		final List<Permission> sortedPermissions = Arrays.stream(permissions)
			.filter(Objects::nonNull)
			.distinct()
			.sorted(Comparator.comparing(Permission::getName))
			.toList();
		StringBuilder output = new StringBuilder("```");
		for (Permission permission : sortedPermissions) {
			output.append("\n- ").append(permission.getName());
		}
		return output + "\n```";
	}

	public static String getPermissionString(final CustomPermission... permissions) {
		if (permissions.length == 0) return "```\n```";
		final List<CustomPermission> sortedPermissions = Arrays.stream(permissions)
			.filter(Objects::nonNull)
			.distinct()
			.sorted(Comparator.comparing(CustomPermission::getName))
			.toList();
		StringBuilder output = new StringBuilder("```");
		for (CustomPermission permission : sortedPermissions) {
			output.append("\n- ").append(permission.getName());
		}
		return output + "\n```";
	}

	/**
	 * @return 1) the given String with the first letter capitalized and the rest lower case 2) null if the input is null 3) empty if the input is empty
	 */
	public static String firstLetterUppercase(final String input) {
		if (input == null || input.isEmpty()) return input;
		return input.substring(0, 1).toUpperCase(Locale.ROOT) + (input.length() > 1 ? input.substring(1).toLowerCase(Locale.ROOT) : "");
	}

	@SuppressWarnings("checkstyle:RegexpSinglelineJava")
	public static void printLogo() {
		LoggerFactory.getLogger(Main.class).info("""

			   ___  __         __   ____       _                  ___       __
			  / _ )/ /__  ____/ /__/ __ \\___  (_)__  ___   ____  / _ )___  / /_
			 / _  / / . |/ __/  '_/ /_/ / _ \\/ / _ \\/ _ \\ /___/ / _  / _ \\/ __/
			/____/_/\\_|_|\\__/_/\\_\\\\____/_//_/_/\\___/_//_/      /____/\\___/\\__/
			""");
	}

	/**
	 * Short for "get or default"
	 */
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

	public static WebhookClient makeWebhookClient(Webhook webhook) {
		final WebhookClientBuilder clientBuilder = new WebhookClientBuilder(webhook.getUrl());
		clientBuilder.setThreadFactory(job -> {
			final Thread thread = new Thread(job);
			thread.setName("WebhookClient");
			thread.setDaemon(true);
			return thread;
		});

		return clientBuilder.build();
	}

		public static Webhook getWebhook(TextChannel channel, List<Webhook> webhooks) {
		return getWebhook(channel, webhooks,  webhook -> webhook.getOwner().getIdLong() == Bot.getInstance().getSelfUserId());
	}

	static Webhook getWebhook(TextChannel channel, List<Webhook> webhooks, Predicate<Webhook> validator) {
		return webhooks.stream()
			.filter(Objects::nonNull)
			.filter(webhook -> webhook.getOwner() != null
				&& webhook.getName().equals("BlackOnion-Bot ContentModerator")
				&& validator.test(webhook))
			.findFirst()
			.orElseGet(() -> channel
				.createWebhook("BlackOnion-Bot ContentModerator")
				.setAvatar(Bot.BLACKONION_ICON)
				.submit()
				.join());
	}

	public static JSONArray optionsToJson(List<OptionData> options) {
		final JSONArray jsonArray = new JSONArray();
		if (options == null) return jsonArray;

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

	public static @NotNull String formatDuration(Duration duration) {
		StringBuilder sb = new StringBuilder();
		if (duration.toDays() > 0) {
			sb.append(duration.toDays()).append(" day");
			if (duration.toDays() > 1) {
				sb.append("s");
			}
			sb.append(" ");
			duration = duration.minusDays(duration.toDays());
		}
		if (duration.toHours() > 0) {
			sb.append(duration.toHours()).append(" hour");
			if (duration.toHours() > 1) {
				sb.append("s");
			}
			sb.append(" ");
			duration = duration.minusHours(duration.toHours());
		}
		if (duration.toMinutes() > 0) {
			sb.append(duration.toMinutes()).append(" minute");
			if (duration.toMinutes() > 1) {
				sb.append("s");
			}
			sb.append(" ");
			duration = duration.minusMinutes(duration.toMinutes());
		}
		if (duration.toSeconds() > 0) {
			sb.append(duration.toSeconds()).append(" second");
			if (duration.toSeconds() > 1) {
				sb.append("s");
			}
		}
		return sb.toString().trim();
	}

	private static final int MAX_TIMEOUT_DURATION_MIN = 28 * 24 * 60; // 28 days
	private static final int MAX_TIMEOUT_DURATION_HOUR = 672; // 28 days
	private static final int MAX_TIMEOUT_DURATION_DAY = 28; // 28 days
	private static final int MAX_TIMEOUT_DURATION_WEEK = 4; // 28 days

	// Units
	public static final String MINUTES = "minutes";
	public static final String HOURS = "hours";
	public static final String DAYS = "days";
	public static final String WEEKS = "weeks";

	public static OptionData[] getDurationOptions(String message) {
		String description = "The time of the " + message + " in ";
		return new OptionData[] {
			new OptionData(OptionType.INTEGER, MINUTES, description + MINUTES, false).setRequiredRange(1, MAX_TIMEOUT_DURATION_MIN),
			new OptionData(OptionType.INTEGER, HOURS, description + HOURS, false).setRequiredRange(1, MAX_TIMEOUT_DURATION_HOUR),
			new OptionData(OptionType.INTEGER, DAYS, description + DAYS, false).setRequiredRange(1, MAX_TIMEOUT_DURATION_DAY),
			new OptionData(OptionType.INTEGER, WEEKS, description + WEEKS, false).setRequiredRange(1, MAX_TIMEOUT_DURATION_WEEK)
		};
	}

	/**
	 * @return the value of the supplier or null if the supplier throws an exception
	 */
	@Nullable
	public static <T> T tryGet(Supplier<T> getter) {
		try {
			return getter.get();
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T parseToT(String value, Class<T> clazz) {
		return parseToT(value, clazz, false);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T parseToT(String value, Class<T> clazz, boolean recursed) {
		if (clazz.equals(Object.class)) {
			return (T) value;
		} else if (clazz.equals(String.class)) {
			return (T) value;
		} else if (clazz.equals(Integer.class)) {
			return (T) Integer.valueOf(value);
		} else if (clazz.equals(Long.class)) {
			return (T) Long.valueOf(value);
		} else if (clazz.equals(Boolean.class)) {
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
				return (T) Boolean.valueOf(value);
			} else {
				throw new IllegalArgumentException("Invalid boolean value: " + value);
			}
		} else if (clazz.equals(Double.class)) {
			return (T) Double.valueOf(value);
		} else if (clazz.equals(Float.class)) {
			return (T) Float.valueOf(value);
		} else if (clazz.isEnum()) {
			return (T) Enum.valueOf((Class<Enum>) clazz, value); // NOSONAR
		} else {
			if (recursed) return null;
			return parseToT(value, Primitives.wrap(clazz), true);
		}
	}

	public static <T extends List<?>> T jsonArrayToList(JSONArray jsonArray) {
		//noinspection unchecked
		return (T) jsonArray.toList();
	}

	public static String serializeEmoji(Emoji emoji) {
		return emoji.getType() == Emoji.Type.CUSTOM ? ((CustomEmoji) emoji).getId() : emoji.getName();
	}

	public static ErrorHandler getCantSendHandler(AwaitDone<InteractionHook> await, String message, SlashCommandEvent event) {
		return getCantSendHandler(await, message, event.getLanguage());
	}

	public static ErrorHandler getCantSendHandler(AwaitDone<InteractionHook> await, String message, Language lang) {
		return new ErrorHandler()
			.handle(ErrorResponse.CANNOT_SEND_TO_USER, err ->
				await.setOnDone(hook -> hook.editOriginal(message + "\n" + lang.getTranslation("usernotnotified")).queue()));
	}

	public static List<Page> getPages(TranslatedEmbedBuilder baseEmbed, List<MessageEmbed.Field> fields, int perPage) {
		return Lists.partition(fields, perPage).stream()
			// DON'T switch to method references or only one copy will get created!
			.map(t -> new TranslatedEmbedBuilder(baseEmbed).addFields(t))
			.map(EmbedBuilder::build)
			.map(InteractPage::new)
			.map(Page.class::cast)
			.toList();
	}

	/**
	 * If the {@code supplier} throws an exception, the exception is caught.<br>
	 * If this exception is of type {@code toReplace}, it instead throws {@code replacement}<br>
	 * Else, it'll just throw the existing exception.<br>
	 * If no exception is thrown, the result of the supplier is returned.
	 */
	public static <T> T replaceException(ThrowableSupplier<T> supplier, @Nullable Class<? extends Exception> toReplace, Class<? extends RuntimeException> replacement) {
		requireNonNull(supplier, "supplier");
		requireNonNull(toReplace, "toReplace");
		requireNonNull(replacement, "replacement");
		try {
			return supplier.get();
		} catch (Throwable e) {
			if (toReplace != replacement && e.getClass() == toReplace) {
				try {
					throw replacement.getDeclaredConstructor(String.class).newInstance(e.getMessage());
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
					throw new RuntimeException(ex);
				}
			}
			throw e instanceof RuntimeException ex ? ex : new RuntimeException(e);
		}
	}

	public static <T> T uncheckedSupplier(ThrowableSupplier<T> supplier) {
		requireNonNull(supplier, "supplier");
		try {
			return supplier.get();
		} catch (Throwable e) {
			throw e instanceof RuntimeException ex ? ex : new RuntimeException(e);
		}
	}

	public static void uncheckedSupplier(ThrowableVoidSupplier supplier) {
		requireNonNull(supplier, "supplier");
		try {
			supplier.get();
		} catch (Throwable e) {
			throw e instanceof RuntimeException ex ? ex : new RuntimeException(e);
		}
	}

	public static String stackTraceToString(StackTraceElement[] stackTraceElements) {
		return Arrays.stream(stackTraceElements)
			.map(StackTraceElement::toString)
			.collect(Collectors.joining("\n"));
	}
}
