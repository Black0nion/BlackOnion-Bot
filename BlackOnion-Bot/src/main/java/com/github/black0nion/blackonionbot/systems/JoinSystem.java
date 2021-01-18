package com.github.black0nion.blackonionbot.systems;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.enums.DrawType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinSystem extends ListenerAdapter {

    private static BufferedImage defaultBackGround;
    
    public JoinSystem() {
    	try {
    		InputStream inputstream = new FileInputStream(new File("files/background.png"));

        	defaultBackGround = ImageIO.read(inputstream);
        	return;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	defaultBackGround = null;
	}
    
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		try {
			final File file = generateImage(Color.BLACK, event.getMember(), event.getGuild(), DrawType.JOIN);
			event.getGuild().getTextChannelById("800032895803719751").sendMessage("Welcome, " + event.getMember().getAsMention() + "! :)").addFile(file, "welcome.png").queue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		try {
			final File file = generateImage(Color.BLACK, event.getMember(), event.getGuild(), DrawType.LEAVE);
			event.getGuild().getTextChannelById("800032895803719751").sendMessage("Goodbye, " + event.getMember().getAsMention() + "! :(").addFile(file, "goodbye.png").queue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage("Thank you for adding the **BlackOnion-Bot**! To get help, use ``" + BotInformation.prefix + "help``!").queue();
	}
	
    @NotNull
	public static File generateImage(@NotNull final Color textColor, final @NotNull Member member, final @NotNull Guild guild, final DrawType drawType) throws Exception {
        final double separatorTransparency = 1;

        BufferedImage bufferedImage = Utils.deepCopy(defaultBackGround);

        final Graphics2D innerBox = bufferedImage.createGraphics();
        try {
            innerBox.setComposite(AlphaComposite.Src);
            innerBox.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            innerBox.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) separatorTransparency));
            innerBox.fill(new RoundRectangle2D.Float(20, 10, 670, 180, 10, 10));
            innerBox.setComposite(AlphaComposite.SrcAtop);
        } finally {
            innerBox.dispose();
        }
        
        if (member.getUser().getAvatarUrl() != null) {
            final Graphics2D avatar = bufferedImage.createGraphics();
            final URL url = new URL(member.getUser().getAvatarUrl());
            final HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
            try {
            	avatar.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                avatar.drawImage(makeRoundedCorner(ImageIO.read(connection.getInputStream())), 10, 10, 180, 180, null);
            } finally {
                avatar.dispose();
                connection.disconnect();
            }
        }

        final Graphics2D newGraphics = bufferedImage.createGraphics();
        newGraphics.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        newGraphics.setFont(new Font("Arial", Font.PLAIN, 40));
        newGraphics.setColor(Color.DARK_GRAY);
        String message = drawType == DrawType.JOIN ? LanguageSystem.getTranslatedString("welcome", member.getUser(), guild) : LanguageSystem.getTranslatedString("goodbye", member.getUser(), guild);
        newGraphics.drawString(message, 205, 55);
        newGraphics.setFont(new Font("Arial", Font.BOLD, 80));
        newGraphics.setColor(textColor);
        String user = member.getUser().getName();
        int width = newGraphics.getFontMetrics().stringWidth(user);
        newGraphics.drawString(user, 190 + (500 - width) / 2, 145);

        final File file = new File("tmp/joinleave/" + member.getId() + ".png");

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        ImageIO.write(bufferedImage, "png", file);

        return file;
    }
    
    private @NotNull static BufferedImage makeRoundedCorner(final @NotNull BufferedImage image) {
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
