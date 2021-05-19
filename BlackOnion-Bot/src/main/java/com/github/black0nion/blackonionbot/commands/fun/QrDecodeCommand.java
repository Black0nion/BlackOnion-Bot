package com.github.black0nion.blackonionbot.commands.fun;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class QrDecodeCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "qrdecode", "qrd" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		final List<Attachment> attachments = message.getAttachments();
		if (attachments.size() == 0) {
			channel.sendMessage(Utils.getWrongArgument(author, guild, this)).queue();
			return;
		} else {
			Result result = readQR(attachments.get(0));
			if (result == null) {
				channel.sendMessage(Utils.getWrongArgument(author, guild, this)).queue();
				return;
			} else {
				channel.sendMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle("qrcode", "https://zxing.github.io/zxing")
						.setThumbnail(attachments.get(0).getUrl())
						.addField("result", result.getText(), false).build()).queue();
			}
		}
	}

	@Override
	public Category getCategory() {
		return Category.FUN;
	}

	@Override
	public String getSyntax() {
		return "<attach files>";
	}

	private static Result readQR(Attachment path) {
		try {
			File file = path.downloadToFile().join();
			
			Map<EncodeHintType, ErrorCorrectionLevel> map
	        = new HashMap<EncodeHintType,
	                      ErrorCorrectionLevel>();
			map.put(EncodeHintType.ERROR_CORRECTION,
	                ErrorCorrectionLevel.L);
			
			BinaryBitmap binaryBitmap
            = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                    ImageIO.read(
                        new FileInputStream(file)))));
	
			Result result = new MultiFormatReader().decode(binaryBitmap);
			file.delete();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}