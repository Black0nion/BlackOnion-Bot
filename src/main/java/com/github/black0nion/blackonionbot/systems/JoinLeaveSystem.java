package com.github.black0nion.blackonionbot.systems;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettingsRepo;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepo;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.config.mutable.api.Settings;
import com.github.black0nion.blackonionbot.misc.enums.DrawType;
import com.github.black0nion.blackonionbot.misc.enums.GuildType;
import com.github.black0nion.blackonionbot.misc.enums.RunMode;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageUtils;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

@SuppressWarnings("all")
// TODO: improve
public class JoinLeaveSystem extends ListenerAdapter {

	private static BufferedImage defaultBackGround;
	private static final Logger logger = LoggerFactory.getLogger(JoinLeaveSystem.class);

	private final Config config;
	private final Settings settings;
	private final LanguageSystem languageSystem;
	private final EmbedUtils embedUtils;
	private final GuildSettingsRepo guildSettingsRepo;
	private final UserSettingsRepo userSettingsRepo;

	public JoinLeaveSystem(Config config, Settings settings, LanguageSystem languageSystem, EmbedUtils embedUtils, GuildSettingsRepo guildSettingsRepo, UserSettingsRepo userSettingsRepo) {
		this.config = config;
		this.settings = settings;
		this.languageSystem = languageSystem;
		this.embedUtils = embedUtils;
		this.guildSettingsRepo = guildSettingsRepo;
		this.userSettingsRepo = userSettingsRepo;
		try {
			defaultBackGround = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/background.png")));
		} catch (final Exception e) {
			e.printStackTrace();
			defaultBackGround = null;
		}
	}

	@Override
	public void onGuildMemberJoin(final @NotNull GuildMemberJoinEvent event) {
		try {
			final User author = event.getUser();
			final Guild guild = event.getGuild();

			final UserSettings userSettings = userSettingsRepo.getSettings(author);
			final GuildSettings guildSettings = guildSettingsRepo.getSettings(guild);

			final TextChannel channel = guildSettings.getJoinChannel().getValue();

			final byte[] bytes = generateImage(Color.BLACK, author, guild, userSettings, guildSettings, DrawType.JOIN);
			FileUpload fileUpload = FileUpload.fromData(bytes, "welcome.png");

			var embed = new TranslatedEmbedBuilder(languageSystem.getLanguage(userSettings, guildSettings))
				.setColor(Color.BLACK)
				.setDescription(guildSettings.getJoinMessage().getValue(new Placeholder("user", author.getAsTag()), new Placeholder("guild", Utils.escapeMarkdown(guild.getName()))))
				.setImage("attachment://welcome.png")
				.setFooter("definedbyguild", author.getEffectiveAvatarUrl());

			channel.sendMessageEmbeds(embed.build())
				.addFiles(fileUpload)
				.queue();
		} catch (final Exception e) {
			logger.error("Error while sending join image", e);
		}
	}

	@Override
	public void onGuildMemberRemove(final @NotNull GuildMemberRemoveEvent event) {
		try {
			final User author = event.getUser();
			final Guild guild = event.getGuild();

			final UserSettings userSettings = userSettingsRepo.getSettings(author);
			final GuildSettings guildSettings = guildSettingsRepo.getSettings(guild);

			final TextChannel channel = guildSettings.getLeaveChannel().getValue();

			final byte[] bytes = generateImage(Color.BLACK, author, guild, userSettings, guildSettings, DrawType.LEAVE);
			FileUpload fileUpload = FileUpload.fromData(bytes, "goodbye.png");

			EmbedBuilder embed = new TranslatedEmbedBuilder(languageSystem.getLanguage(userSettings, guildSettings))
				.setColor(Color.BLACK)
				.setDescription(guildSettings.getLeaveMessage().getValue(new Placeholder("user", author.getAsTag()), new Placeholder("guild", Utils.escapeMarkdown(guild.getName()))))
				.setImage("attachment://goodbye.png")
				.setFooter("definedbyguild", author.getEffectiveAvatarUrl());

			channel.sendMessageEmbeds(embed.build())
				.addFiles(fileUpload)
				.queue();
		} catch (final Exception e) {
			logger.error("Error while sending leave image", e);
		}
	}

	/**
	 * Called when the bot gets added to a new guild
	 */
	@Override
	public void onGuildJoin(final @NotNull GuildJoinEvent event) {
		Bot.getInstance().getExecutor().submit(() -> {
			final Guild guild = event.getGuild();

			final GuildSettings guildSettings = guildSettingsRepo.getSettings(guild);

			guild.retrieveAuditLogs()
				.type(ActionType.BOT_ADD)
				.limit(5)
				.queue(entries -> {
					User tmpAddUser = null;
					UserSettings tmpUserSettings = null;
					for (final AuditLogEntry entry : entries) {
						if (entry.getTargetId().equals(event.getJDA().getSelfUser().getId())) {
							tmpAddUser = entry.getUser();
							tmpUserSettings = userSettingsRepo.getSettings(tmpAddUser);

							break;
						}
					}

					@Nullable
					User addUser = tmpAddUser;
					@Nullable
					UserSettings userSettings = tmpUserSettings;

					if (addUser != null)
						logger.info("I got added to the guild {} (G:{}) by {} (U:{})", guild.getName(), guild.getId(), addUser.getName(), addUser.getId());
					else
						logger.info("I got added to the guild {} (G:{})", guild.getName(), guild.getId());

					if (config.getRunMode() == RunMode.BETA && !guildSettingsRepo.getSettings(guild).getGuildType().getValue().higherThanOrEqual(GuildType.BETA)) {
						guild.leave().queue();
						addUser.openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(embedUtils.getErrorEmbed(addUser, userSettings, guildSettings).addField("notbeta", "betatutorial", false).build()).queue());
						logger.error("{} (G: {}) added me but is not a beta guildid!", guild.getName(), guild.getId());
						return;
					}

					if (addUser != null) {
						addUser.openPrivateChannel().queue(channel ->
							channel.sendMessageEmbeds(embedUtils.getSuccessEmbed(addUser, userSettings, guildSettings)
								.setTitle("thankyouforadding")
								.addField(
									languageSystem.getTranslation("commandtohelp", userSettings, guildSettings).replace("%command%", "/help"),
									languageSystem.getTranslation("changelanguage", userSettings, guildSettings).replace("%usercmd%", "/language user").replace("%guildcmd%", "/language guild"), false)
								.build()
							).queue());
					}

					if (settings.getLogsChannel() == -1) return;
					try {
						final Guild devGuild = event.getJDA().getGuildById(config.getDevGuild());
						guild.retrieveOwner().flatMap(owner ->
							devGuild.getTextChannelById(settings.getLogsChannel()).sendMessageEmbeds(embedUtils.getSuccessEmbed()
								.addField("addedtoguild",
									languageSystem.getDefaultLanguage()
										.getTranslation("guildstatsjoin",
											new Placeholder("name", Utils.escapeMarkdown(guild.getName()) + " (G: " + guild.getId() + ")"),
											new Placeholder("usercount", guild.getMemberCount()),
											new Placeholder("adduser", addUser != null ? (Utils.escapeMarkdown(addUser.getName()) + " (U: " + addUser.getId() + ")") : "empty"),
											new Placeholder("owner", owner != null ? (Utils.escapeMarkdown(owner.getUser().getName()) + " (U: " + owner.getUser().getId() + ")") : "empty")
										),
									false)
								.build()
							)
						).queue();
					} catch (final Exception e) {
						logger.error("Error while sending join message", e);
					}
				});
		});
	}

	@Override
	public void onGuildLeave(final GuildLeaveEvent event) {
		Bot.getInstance().getExecutor().submit(() -> {
			final Guild guild = event.getGuild();
			logger.info("I got removed from the guild {} (G:{})", guild.getName(), guild.getId());

			if (settings.getLogsChannel() == -1) return;
			try {
				final Guild devGuild = event.getJDA().getGuildById(config.getDevGuild());
				devGuild.getTextChannelById(settings.getLogsChannel()).sendMessageEmbeds(embedUtils.getErrorEmbed()
					.setTitle(null)
					.addField("removedfromguild", languageSystem.getDefaultLanguage().getTranslation("guildstatsleave",
						new Placeholder("name", guild.getName() + " (G: " + guild.getId() + ")"),
						new Placeholder("usercount", guild.getMemberCount())
					), false).build()).queue();
			} catch (final Exception e) {
				logger.error("Error while sending leave message", e);
			}
		});
	}

	@NotNull
	public byte[] generateImage(@NotNull final Color textColor, final @NotNull User user, final @NotNull Guild guild, final UserSettings userSettings, final GuildSettings guildSettings, final DrawType drawType) throws Exception {
		final double separatorTransparency = 1;

		final BufferedImage bufferedImage = Utils.deepCopy(defaultBackGround);

		final Graphics2D innerBox = bufferedImage.createGraphics();
		try {
			innerBox.setComposite(AlphaComposite.Src);
			innerBox.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			innerBox.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) separatorTransparency));
			innerBox.fill(new RoundRectangle2D.Float(10, 10, 799, 180, 10, 10));
			innerBox.setComposite(AlphaComposite.SrcAtop);
		} finally {
			innerBox.dispose();
		}

		if (user.getEffectiveAvatarUrl() != null) {
			final Graphics2D avatar = bufferedImage.createGraphics();
			final URL url = new URL(user.getEffectiveAvatarUrl() + "?size=256");
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
			try {
				avatar.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				avatar.drawImage(makeRoundedCorner(ImageIO.read(connection.getInputStream())), 10, 10, 180, 180, null);
			} finally {
				avatar.dispose();
				connection.disconnect();
			}
		}

		final Graphics2D newGraphics = bufferedImage.createGraphics();
		newGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		newGraphics.setFont(new Font("Arial", Font.PLAIN, 60));
		newGraphics.setColor(Color.BLACK);

		final Language language = LanguageUtils.getLanguage(userSettings, guildSettings, languageSystem.getDefaultLanguage());
		final String message = drawType == DrawType.JOIN
			? language.getTranslation("defaultjoinmessage", new Placeholder("guild", guild.getName()), new Placeholder("user", user.getName()))
			: language.getTranslation("defaultleavemessage", new Placeholder("guild", guild.getName()), new Placeholder("user", user.getName()));
		newGraphics.drawString(message, 205, 75);
		newGraphics.setColor(textColor);

		final String userName = user.getName();
		newGraphics.setFont(new Font("Arial Unicode MS", Font.BOLD, 68 - userName.length()));
		final int width = newGraphics.getFontMetrics().stringWidth(userName);
		newGraphics.drawString(userName, 190 + (619 - width) / 2, 145);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", outputStream);

		return outputStream.toByteArray();
	}

	public @NotNull
	static BufferedImage makeRoundedCorner(final @NotNull BufferedImage image) {
		final int w = image.getWidth();
		final int h = image.getHeight();
		final BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		final Graphics2D g2 = output.createGraphics();

		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new RoundRectangle2D.Float(0, 0, w + 100, h, 10, 10));

		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);

		g2.dispose();

		return output;
	}
}
