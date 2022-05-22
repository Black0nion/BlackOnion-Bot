package com.github.black0nion.blackonionbot.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.await.AwaitDone;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.ygimenez.model.InteractPage;
import com.github.ygimenez.model.Page;
import com.google.common.collect.Lists;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Utils {
	private Utils() {}
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

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
	 * Pass null as the channel argument to check self permissions.
	 * USED FOR SELF PERMISSIONS!
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

	public static Webhook getWebhook(TextChannel channel, List<Webhook> webhooks) {
		return webhooks.stream()
			.filter(Objects::nonNull)
			.filter(webhook -> webhook.getOwner() != null)
			.filter(webhook -> webhook.getName().equals("BlackOnion-Bot ContentModerator") && webhook.getOwner().getIdLong() == Bot.getInstance().getSelfUserId())
			.findFirst()
			.orElse(channel
				.createWebhook("BlackOnion-Bot ContentModerator")
				.setAvatar(Bot.BLACKONION_ICON)
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
		}
		return sb.toString();
	}

	private static final int MAX_TIMEOUT_DURATION_MIN = 28 * 24 * 60; // 28 days
	private static final int MAX_TIMEOUT_DURATION_HOUR = 672; // 28 days
	private static final int MAX_TIMEOUT_DURATION_DAY = 28; // 28 days
	private static final int MAX_TIMEOUT_DURATION_WEEK = 4; // 28 days

	// Units
	private static final String MINUTES = "minutes";
	private static final String HOURS = "hours";
	private static final String DAYS = "days";
	private static final String WEEKS = "weeks";
	public static OptionData[] getDurationOptions(String message) {
		String description = "The time of the " + message + " in ";
		return new OptionData[] {
			new OptionData(OptionType.INTEGER, MINUTES, description + MINUTES, false).setRequiredRange(1, MAX_TIMEOUT_DURATION_MIN),
			new OptionData(OptionType.INTEGER, HOURS, description + HOURS, false).setRequiredRange(1, MAX_TIMEOUT_DURATION_HOUR),
			new OptionData(OptionType.INTEGER, DAYS, description + DAYS, false).setRequiredRange(1, MAX_TIMEOUT_DURATION_DAY),
			new OptionData(OptionType.INTEGER, WEEKS, description + WEEKS, false).setRequiredRange(1, MAX_TIMEOUT_DURATION_WEEK)
		};
	}

	public static Duration parseDuration(SlashCommandInteractionEvent e) throws TooLongException {
		var min = e.getOption(MINUTES, OptionMapping::getAsLong);
		var hour = e.getOption(HOURS, OptionMapping::getAsLong);
		var day = e.getOption(DAYS, OptionMapping::getAsLong);
		var week = e.getOption(WEEKS, OptionMapping::getAsLong);

		Duration dur = Duration.ofMinutes(
			(min != null ? min : 0) +
			(hour != null ? hour * 60 : 0) +
			(day != null ? day * 60 * 24 : 0) +
			(week != null ? week * 60 * 24 * 7 : 0)
		);
		if (dur.toMinutes() > MAX_TIMEOUT_DURATION_MIN) {
			throw TooLongException.INSTANCE;
		}
		if (dur.toMinutes() <= 0) {
			throw new IllegalArgumentException("Duration must be greater than 0");
		}
		return dur;
	}

	public static class TooLongException extends Exception {
		static final TooLongException INSTANCE = new TooLongException();

		public TooLongException() {
			super();
		}
	}

	public static ErrorHandler getCantSendHandler(AwaitDone<InteractionHook> await, String message, SlashCommandEvent event) {
		return getCantSendHandler(await, message, event.getLanguage());
	}

	public static ErrorHandler getCantSendHandler(AwaitDone<InteractionHook> await, String message, Language lang) {
		return new ErrorHandler()
			.handle(ErrorResponse.CANNOT_SEND_TO_USER, err ->
				await.setOnDone(hook -> hook.editOriginal(message + "\n" + lang.getTranslation("usernotnotified")).queue()));
	}

	public static List<Page> getPages(TranslatedEmbed baseEmbed, List<MessageEmbed.Field> fields) {
		return getPages(baseEmbed, fields, 10);
	}

	public static List<Page> getPages(TranslatedEmbed baseEmbed, List<MessageEmbed.Field> fields, int perPage) {
		return Lists.partition(fields, perPage).stream()
			// DON'T switch to method references or only one copy will get created!
			.map(t -> new TranslatedEmbed(baseEmbed).addFields(t))
			.map(EmbedBuilder::build)
			.map(InteractPage::new)
			.map(Page.class::cast)
			.toList();
	}

	public static <T> T replaceException(ThrowableSupplier<T> supplier, @Nullable Class<? extends Exception> toReplace, Class<? extends RuntimeException> replacement) {
		try {
			return supplier.get();
		} catch (Throwable e) {
			if (toReplace != null && e.getClass() == toReplace) {
				try {
					throw replacement.getDeclaredConstructor(String.class).newInstance(e.getMessage());
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
					throw new RuntimeException(ex);
				}
			}
			throw e instanceof RuntimeException ex ? ex : new RuntimeException(e);
		}
	}
}
