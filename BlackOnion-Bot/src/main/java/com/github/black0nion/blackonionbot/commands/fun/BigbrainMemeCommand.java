package com.github.black0nion.blackonionbot.commands.fun;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class BigbrainMemeCommand extends SlashCommand {

    private static BufferedImage defaultBackGround;

    final static int maxWidth = 420;

    public BigbrainMemeCommand() {
	this.setData(new CommandData("bigbrain", "Creates a BigBrain-Meme")
		.addOption(OptionType.STRING, "smallbrain", "The text next to the small brain", true)
		.addOption(OptionType.STRING, "mediumbrain", "The text next to the medium brain", true)
		.addOption(OptionType.STRING, "bigbrain", "The text next to the big brain", true)
		.addOption(OptionType.STRING, "ultrabigbrain", "The text next to the ultra big brain", true));
	try {
	    defaultBackGround = ImageIO.read(this.getClass().getResource("/bigbrain-meme.jpg"));
	    return;
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	defaultBackGround = null;
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String[] messages = e.getOptionsByType(OptionType.STRING).stream().map(OptionMapping::getAsString).toArray(String[]::new);
	if (messages.length < 4) {
	    cmde.sendPleaseUse();
	    return;
	}
	e.reply("Generating your image...").queue(m -> {
	    final @NotNull File file = generateImage(messages);
	    m.editOriginal("Here is your result.").addFile(file).queue(ignored -> file.delete(), ignored -> file.delete());
	});
    }

    @NotNull
    public static File generateImage(final String[] args) {
	try {
	    for (int i = 0; i < args.length; i++) {
		args[i] = args[i].trim();
	    }

	    final BufferedImage bufferedImage = Utils.deepCopy(defaultBackGround);

	    final Graphics2D newGraphics = bufferedImage.createGraphics();
	    newGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    newGraphics.setFont(new Font("Arial", Font.PLAIN, 40));
	    newGraphics.setColor(Color.DARK_GRAY);

	    final FontMetrics fontMetrics = newGraphics.getFontMetrics();

	    final List<String> lines = new ArrayList<>();
	    int width;
	    String current;
	    String[] input;

	    // lil brain
	    lines.clear();
	    width = 0;
	    current = "";
	    input = args[0].split(" ");
	    for (int i = 0; i < input.length; i++) {
		final String s = input[i];
		width += fontMetrics.stringWidth(s);
		if (width > maxWidth || i == input.length - 1) {
		    if (i == input.length - 1) {
			current += s;
		    }
		    lines.add(current);
		    current = s + " ";
		    width = 0;
		} else {
		    current += s + " ";
		}
	    }
	    for (int i = 0; i < lines.size(); i++) {
		newGraphics.drawString(lines.get(i), 10, i * 50 + 50);
	    }

	    // medium brain
	    lines.clear();
	    width = 0;
	    current = "";
	    input = args[1].split(" ");
	    for (int i = 0; i < input.length; i++) {
		final String s = input[i];
		width += fontMetrics.stringWidth(s);
		if (width > maxWidth || i == input.length - 1) {
		    if (i == input.length - 1) {
			current += s;
		    }
		    lines.add(current);
		    current = s + " ";
		    width = 0;
		} else {
		    current += s + " ";
		}
	    }
	    for (int i = 0; i < lines.size(); i++) {
		newGraphics.drawString(lines.get(i), 10, i * 50 + 275);
	    }

	    // big brain
	    lines.clear();
	    width = 0;
	    current = "";
	    input = args[2].split(" ");
	    for (int i = 0; i < input.length; i++) {
		final String s = input[i];
		width += fontMetrics.stringWidth(s);
		if (width > maxWidth || i == input.length - 1) {
		    if (i == input.length - 1) {
			current += s;
		    }
		    lines.add(current);
		    current = s + " ";
		    width = 0;
		} else {
		    current += s + " ";
		}
	    }
	    for (int i = 0; i < lines.size(); i++) {
		newGraphics.drawString(lines.get(i), 10, i * 50 + 520);
	    }

	    // giga brain
	    lines.clear();
	    width = 0;
	    current = "";
	    input = args[3].split(" ");
	    for (int i = 0; i < input.length; i++) {
		final String s = input[i];
		width += fontMetrics.stringWidth(s);
		if (width > maxWidth || i == input.length - 1) {
		    if (i == input.length - 1) {
			current += s;
		    }
		    lines.add(current);
		    current = s + " ";
		    width = 0;
		} else {
		    current += s + " ";
		}
	    }
	    for (int i = 0; i < lines.size(); i++) {
		newGraphics.drawString(lines.get(i), 10, i * 50 + 750);
	    }

	    final File file = new File("tmp/bigbrain/" + System.currentTimeMillis() + ".png");

	    if (file.getParentFile() != null) {
		file.getParentFile().mkdirs();
	    }

	    ImageIO.write(bufferedImage, "png", file);

	    return file;
	} catch (final Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }
}