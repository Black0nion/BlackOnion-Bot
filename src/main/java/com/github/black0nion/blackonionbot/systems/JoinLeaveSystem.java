package com.github.black0nion.blackonionbot.systems;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.config.mutable.api.Settings;
import com.github.black0nion.blackonionbot.misc.enums.DrawType;
import com.github.black0nion.blackonionbot.misc.enums.GuildType;
import com.github.black0nion.blackonionbot.misc.enums.RunMode;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
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

	public JoinLeaveSystem(Config config, Settings settings, LanguageSystem languageSystem, EmbedUtils embedUtils) {
		this.config = config;
		this.settings = settings;
		this.languageSystem = languageSystem;
		this.embedUtils = embedUtils;
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
			final BlackGuild guild = BlackGuild.from(event.getGuild());
			final BlackUser author = BlackUser.from(event.getUser());
			final long id = guild.getJoinChannel();
			if (id == -1) return;
			final TextChannel channel = guild.getTextChannelById(id);
			if (channel == null) return;

			final byte[] bytes = generateImage(Color.BLACK, author, guild, DrawType.JOIN);
			FileUpload fileUpload = FileUpload.fromData(bytes, "welcome.png");

			var embed = new TranslatedEmbedBuilder(languageSystem.getLanguage(author, guild))
				.setColor(Color.BLACK)
				.setDescription(guild.getJoinMessage().replace("%user%", author.getAsMention()).replace("%guild%", guild.getEscapedName()))
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
			final BlackGuild guild = BlackGuild.from(event.getGuild());
			final BlackUser author = BlackUser.from(event.getUser());
			final long id = guild.getLeaveChannel();
			if (id == -1) return;
			final TextChannel channel = guild.getTextChannelById(id);
			if (channel == null) return;

			final byte[] bytes = generateImage(Color.BLACK, author, guild, DrawType.LEAVE);
			FileUpload fileUpload = FileUpload.fromData(bytes, "goodbye.png");

			var embed = new TranslatedEmbedBuilder(languageSystem.getLanguage(author, guild))
				.setColor(Color.BLACK)
				.setDescription(guild.getLeaveMessage().replace("%user%", author.getAsMention()).replace("%guild%", guild.getEscapedName()))
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
	 * Called when the bot gets added to a new guildid
	 */
	@Override
	public void onGuildJoin(final @NotNull GuildJoinEvent event) {
		Bot.getInstance().getExecutor().submit(() -> {
			final BlackGuild guild = BlackGuild.from(event.getGuild());

			guild.retrieveOwner().queue(user -> {
				final BlackUser author = BlackUser.from(user.getUser());

				logger.info("I got added to the guildid {} (G: {}) with owner {} (U: {})", guild.getName(), guild.getId(), author.getName(), author.getId());

				try {
					final Guild guildById = event.getJDA().getGuildById(config.getDevGuild());
					guildById.getTextChannelById(settings.getLogsChannel()).sendMessageEmbeds(embedUtils.getSuccessEmbed().addField("addedtoguild", languageSystem.getDefaultLanguage().getTranslation("guildstatsjoin", new Placeholder("name", guild.getEscapedName() + "(G:" + guild.getId() + ")"), new Placeholder("usercount", guild.getMemberCount()), new Placeholder("owner", author.getEscapedName() + "(U:" + author.getId() + ")")), false).build()).queue();
				} catch (final Exception e) {
					e.printStackTrace();
				}

				if (config.getRunMode() == RunMode.BETA && !guild.getGuildType().higherThanOrEqual(GuildType.BETA)) {
					guild.leave().queue();
					author.openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(embedUtils.getErrorEmbed(author, guild).addField("notbeta", "betatutorial", false).build()).queue());
					logger.error("{} (G: {}) added me but is not a beta guildid!", guild.getName(), guild.getId());
					return;
				}

				author.openPrivateChannel().queue(channel ->
					channel.sendMessageEmbeds(embedUtils.getSuccessEmbed(author, guild)
						.setTitle("thankyouforadding")
						.addField(
							languageSystem.getTranslation("commandtohelp", author, guild).replace("%command%", "/help"),
							languageSystem.getTranslation("changelanguage", author, guild).replace("%usercmd%", "/language user").replace("%guildcmd%", "/language guild"), false)
						.build()
					).queue());
			});
		});
	}

	@Override
	public void onGuildLeave(final GuildLeaveEvent event) {
		Bot.getInstance().getExecutor().submit(() -> {
			final Guild guild = event.getGuild();
			logger.info("I got removed from the guildid {} (G: {})", guild.getName(), guild.getId());

			try {
				final Guild guildById = event.getJDA().getGuildById(config.getDevGuild());
				guildById.getTextChannelById(settings.getLogsChannel()).sendMessageEmbeds(embedUtils.getErrorEmbed().addField("removedfromguild", languageSystem.getDefaultLanguage().getTranslation("guildstatsleave", new Placeholder("name", guild.getName() + "(G:" + guild.getId() + ")"), new Placeholder("usercount", guild.getMemberCount())), false).build()).queue();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
	}

	@NotNull
	public byte[] generateImage(@NotNull final Color textColor, final @NotNull BlackUser user, final @NotNull BlackGuild guild, final DrawType drawType) throws Exception {
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
		final String message = drawType == DrawType.JOIN ? languageSystem.getTranslation("welcome", user, guild) : languageSystem.getTranslation("goodbye", user, guild);
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
