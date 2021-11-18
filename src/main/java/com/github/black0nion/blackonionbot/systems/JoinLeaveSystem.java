package com.github.black0nion.blackonionbot.systems;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.misc.DrawType;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinLeaveSystem extends ListenerAdapter {

    private static BufferedImage defaultBackGround;

    public JoinLeaveSystem() {
	try {
	    defaultBackGround = ImageIO.read(this.getClass().getResource("/background.png"));
	    return;
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	defaultBackGround = null;
    }

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
	try {
	    final BlackGuild guild = BlackGuild.from(event.getGuild());
	    final BlackUser author = BlackUser.from(event.getUser());
	    final long id = guild.getJoinChannel();
	    if (id == -1) return;
	    final TextChannel channel = guild.getTextChannelById(id);
	    if (channel == null) return;
	    final File file = generateImage(Color.BLACK, author, guild, DrawType.JOIN);
	    channel.sendMessage(guild.getJoinMessage().replace("%user%", author.getAsMention()).replace("%guild%", guild.getName())).addFile(file, "welcome.png").queue();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void onGuildMemberRemove(final GuildMemberRemoveEvent event) {
	try {
	    final BlackGuild guild = BlackGuild.from(event.getGuild());
	    final BlackUser author = BlackUser.from(event.getUser());
	    final long id = guild.getLeaveChannel();
	    if (id == -1) return;
	    final TextChannel channel = guild.getTextChannelById(id);
	    if (channel == null) return;
	    final File file = generateImage(Color.BLACK, author, guild, DrawType.LEAVE);
	    channel.sendMessage(guild.getLeaveMessage().replace("%user%", author.getAsMention()).replace("%guild%", guild.getName())).addFile(file, "goodbye.png").queue();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Called when the bot gets added to a new guild
     */
    @Override
    public void onGuildJoin(final GuildJoinEvent event) {
	Bot.executor.submit(() -> {
	    final BlackGuild guild = BlackGuild.from(event.getGuild());
	    final String prefix = guild.getPrefix();

	    guild.retrieveOwner().queue(user -> {
		final BlackUser author = BlackUser.from(user.getUser());

		Logger.logInfo("I got added to the guild " + guild.getName() + "(G:" + guild.getId() + ") with owner " + author.getName() + "(U:" + author.getId() + ")");

		try {
		    final Guild guildById = event.getJDA().getGuildById(BotInformation.supportServer);
		    guildById.getTextChannelById(BotInformation.botLogsChannel).sendMessageEmbeds(EmbedUtils.getSuccessEmbed().addField("addedtoguild", LanguageSystem.getDefaultLanguage().getTranslation("guildstatsjoin", new Placeholder("name", guild.getName() + "(G:" + guild.getId() + ")"), new Placeholder("usercount", guild.getMemberCount()), new Placeholder("owner", author.getName() + "(U:" + author.getId() + ")")), false).build()).queue();
		} catch (final Exception e) {
		    e.printStackTrace();
		}

		if (Bot.runMode == RunMode.BETA && !guild.getGuildType().higherThanOrEqual(GuildType.BETA)) {
		    guild.leave().queue();
		    author.openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("notbeta", "betatutorial", false).build()).queue());
		    Logger.logError(guild.getName() + "(G:" + guild.getId() + ") is not a beta guild!");
		    return;
		}

		author.openPrivateChannel().queue(channel -> {
		    channel.sendMessageEmbeds(EmbedUtils.getSuccessEmbed(author, guild).setTitle("thankyouforadding").addField(LanguageSystem.getTranslation("commandtohelp", author, guild).replace("%command%", prefix + "help"), LanguageSystem.getTranslation("changelanguage", author, guild).replace("%usercmd%", prefix + "lang").replace("%guildcmd%", prefix + "guildlang"), false).build()).queue();
		});
	    });
	});
    }

    @Override
    public void onGuildLeave(final GuildLeaveEvent event) {
	Bot.executor.submit(() -> {
	    final Guild guild = event.getGuild();
	    Logger.logInfo("I got removed from the guild " + guild.getName() + "(G:" + guild.getId() + ")");

	    try {
		final Guild guildById = event.getJDA().getGuildById(BotInformation.supportServer);
		guildById.getTextChannelById(BotInformation.botLogsChannel).sendMessageEmbeds(EmbedUtils.getErrorEmbed().addField("removedfromguild", LanguageSystem.getDefaultLanguage().getTranslation("guildstatsleave", new Placeholder("name", guild.getName() + "(G:" + guild.getId() + ")"), new Placeholder("usercount", guild.getMemberCount())), false).build()).queue();
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	});
    }

    @NotNull
    public static File generateImage(@NotNull final Color textColor, final @NotNull BlackUser user, final @NotNull BlackGuild guild, final DrawType drawType) throws Exception {
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
	final String message = drawType == DrawType.JOIN ? LanguageSystem.getTranslation("welcome", user, guild) : LanguageSystem.getTranslation("goodbye", user, guild);
	newGraphics.drawString(message, 205, 75);
	newGraphics.setColor(textColor);
	final String userName = user.getName();
	newGraphics.setFont(new Font("Arial Unicode MS", Font.BOLD, 68 - userName.length()));
	final int width = newGraphics.getFontMetrics().stringWidth(userName);
	newGraphics.drawString(userName, 190 + (619 - width) / 2, 145);

	final File file = new File("tmp/joinleave/" + user.getId() + ".png");

	if (file.getParentFile() != null) {
	    file.getParentFile().mkdirs();
	}

	ImageIO.write(bufferedImage, "png", file);

	return file;
    }

    public @NotNull static BufferedImage makeRoundedCorner(final @NotNull BufferedImage image) {
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