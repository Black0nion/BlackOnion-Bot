package com.github.black0nion.blackonionbot.commands.fun;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.oldcommands.Command;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BigbrainMemeCommand implements Command {
	
	private static BufferedImage defaultBackGround;
	
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
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		String[] messages = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).split(",");
		if (messages.length < 4) {
			channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild).addField(LanguageSystem.getTranslatedString("wrongargumentcount", author, guild), LanguageSystem.getTranslatedString("pleaseuse", author, guild) + " " + BotInformation.prefix + getCommand()[0] + " " + getSyntax(), false).build()).queue();
			return;
		}
		channel.sendMessage("bigbrain lol").addFile(generateImage(messages), "bigbrain.png").queue();
	}
	
	@Override
	public String getSyntax() {
		return "<arg1>,<arg2>,<arg3>,<arg4>";
	}
	
	@NotNull
	public static File generateImage(String[] args) {
		try {
        	BufferedImage bufferedImage = Utils.deepCopy(defaultBackGround);
	
	        final Graphics2D newGraphics = bufferedImage.createGraphics();
	        newGraphics.setRenderingHint(
	                RenderingHints.KEY_TEXT_ANTIALIASING,
	                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        newGraphics.setFont(new Font("Arial", Font.PLAIN, 40));
	        newGraphics.setColor(Color.DARK_GRAY);
	        
	        // lil brain
	        newGraphics.drawString(args[0], 10, 50);
	        
	        // medium brain
	        newGraphics.drawString(args[1], 10, 275);
	        
	        // big brain
	        newGraphics.drawString(args[2], 10, 520);
	        
	        // giga brain
	        newGraphics.drawString(args[3], 10, 750);
	
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
