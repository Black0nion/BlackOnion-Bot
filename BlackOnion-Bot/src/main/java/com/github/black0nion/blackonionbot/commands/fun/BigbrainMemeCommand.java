package com.github.black0nion.blackonionbot.commands.fun;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BigbrainMemeCommand implements Command {
	
	private static BufferedImage defaultBackGround;
	
	final static int maxWidth = 420;
	
	public BigbrainMemeCommand() {
		try {
			InputStream inputstream = new FileInputStream(new File("resources/bigbrain-meme.jpg"));

    		defaultBackGround = ImageIO.read(inputstream);
    		return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		defaultBackGround = null;
	}

	@Override
	public String[] getCommand() {
		return new String[] { "bigbrain" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		String[] messages = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).split(",");
		if (messages.length < 4) {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargumentcount", Utils.getPleaseUse(guild, author, this), false).build()).queue();
			return;
		}
		message.reply("Generating your image...").queue(m -> {
			final @NotNull File file = generateImage(messages);
			message.reply("bigbrian").addFile(file, "bigbrain.png").queue(msg -> {				
				file.delete();
				m.delete().queue();
			});
		});
	}
	
	@Override
	public String getSyntax() {
		return "<smul brain text>,<medium brain text>,<big brain text>,<thicc brain text>";
	}
	
	@NotNull
	public static File generateImage(String[] args) {
		try {
			for (int i = 0; i < args.length; i++) {
				args[i] = args[i].trim();
			}
			
        	BufferedImage bufferedImage = Utils.deepCopy(defaultBackGround);
	
	        final Graphics2D newGraphics = bufferedImage.createGraphics();
	        newGraphics.setRenderingHint(
	                RenderingHints.KEY_TEXT_ANTIALIASING,
	                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        newGraphics.setFont(new Font("Arial", Font.PLAIN, 40));
	        newGraphics.setColor(Color.DARK_GRAY);
	        
	        final FontMetrics fontMetrics = newGraphics.getFontMetrics();
	        
	        List<String> lines = new ArrayList<>();
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
	        	if (width > maxWidth || i == input.length-1) {
	        		if (i == input.length-1) current += s;
	        		lines.add(current);
	        		current = s + " ";
	        		width = 0;
	        	} else current += s + " ";
	        }
	        for (int i = 0; i < lines.size(); i++) newGraphics.drawString(lines.get(i), 10, i * 50 + 50);
        	
	        // medium brain
	        lines.clear();
	        width = 0;
	        current = "";
	        input = args[1].split(" ");
			for (int i = 0; i < input.length; i++) {
				final String s = input[i];
	        	width += fontMetrics.stringWidth(s);
	        	if (width > maxWidth || i == input.length-1) {
	        		if (i == input.length-1) current += s;
	        		lines.add(current);
	        		current = s + " ";
	        		width = 0;
	        	} else current += s + " ";
	        }
	        for (int i = 0; i < lines.size(); i++) newGraphics.drawString(lines.get(i), 10, i * 50 + 275);
        	
	        
	        // big brain
	        lines.clear();
	        width = 0;
	        current = "";
	        input = args[2].split(" ");
			for (int i = 0; i < input.length; i++) {
				final String s = input[i];
	        	width += fontMetrics.stringWidth(s);
	        	if (width > maxWidth || i == input.length-1) {
	        		if (i == input.length-1) current += s;
	        		lines.add(current);
	        		current = s + " ";
	        		width = 0;
	        	} else current += s + " ";
	        }
	        for (int i = 0; i < lines.size(); i++) newGraphics.drawString(lines.get(i), 10, i * 50 + 520);
	        
	        // giga brain
	        lines.clear();
	        width = 0;
	        current = "";
	        input = args[3].split(" ");
			for (int i = 0; i < input.length; i++) {
				final String s = input[i];
	        	width += fontMetrics.stringWidth(s);
	        	if (width > maxWidth || i == input.length-1) {
	        		if (i == input.length-1) current += s;
	        		lines.add(current);
	        		current = s + " ";
	        		width = 0;
	        	} else current += s + " ";
	        }
	        for (int i = 0; i < lines.size(); i++) newGraphics.drawString(lines.get(i), 10, i * 50 + 750);
	
	        final File file = new File("tmp/bigbrain/" + System.currentTimeMillis() + ".png");
	
	        if (file.getParentFile() != null) {
	            file.getParentFile().mkdirs();
	        }
	
	        ImageIO.write(bufferedImage, "png", file);
	
	        return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
}