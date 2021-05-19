package com.github.black0nion.blackonionbot.commands.fun;

import java.io.File;
import java.io.FileInputStream;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class QrCodeCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "qrcode", "qr" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		final String filePath = String.valueOf("tmp/" + System.currentTimeMillis()) + ".png";
		final File file = new File(filePath);
		file.getParentFile().mkdirs();
		
		createQR(String.join(" ", Utils.removeFirstArg(args)), filePath, "UTF-8", 420, 420);
		
		EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild)
				.setTitle("qrcode", "https://zxing.github.io/zxing")
				.setImage("attachment://qr.png");
		
		try {
			channel.sendFile(new FileInputStream(file), "qr.png").embed(builder.build()).queue(msg -> {
				if (!file.delete())
					file.deleteOnExit();
			}, error -> {
				if (!file.delete())
					file.deleteOnExit();
			});
		} catch (Exception ex) {
			if (!file.delete())
				file.deleteOnExit();
			ex.printStackTrace();
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
		}
	}

	@Override
	public Category getCategory() {
		return Category.FUN;
	}

	@Override
	public String getSyntax() {
		return "<input>";
	}

	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
	
	public static void createQR(String data, String path, String charset, int height, int width) {
		try {
			BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset),
					BarcodeFormat.QR_CODE, width, height);
	
			MatrixToImageWriter.writeToPath(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path).toPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}