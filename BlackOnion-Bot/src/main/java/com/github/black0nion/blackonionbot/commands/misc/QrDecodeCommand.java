package com.github.black0nion.blackonionbot.commands.misc;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class QrDecodeCommand extends Command {
	
	public QrDecodeCommand() {
		this.setCommand("qrdecode", "qrd")
			.setSyntax("<attach file / url to a public image");
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final List<Attachment> attachments = message.getAttachments();
		if (attachments.size() == 0) {
			if (args.length >= 2) {
				final String url = args[1];
				if (url.endsWith(".png") || url.endsWith(".jpg")) {
					readQR(cmde, url, channel, author, guild, message);
				} else {
					cmde.sendPleaseUse();
				}
			} else {
				cmde.sendPleaseUse();
			}
		} else {
			final Attachment path = attachments.get(0);
			if (path.isImage()) {
				readQR(cmde, path, channel, author, guild, message);
			} else {
				cmde.sendPleaseUse();
			}
		}
	}
	
	private void send(CommandEvent cmde, Result result, TextChannel channel, BlackUser author, BlackGuild guild, BlackMessage message, String imageUrl) {
		if (result == null || result.getBarcodeFormat() == null) {
			cmde.sendPleaseUse();
		} else {
			cmde.reply(cmde.success().setTitle("qrcode", "https://zxing.github.io/zxing")
					.setThumbnail(imageUrl)
					.addField("qrresult", result.getText(), false));
		}
	}
	
	private void readQR(CommandEvent cmde, String url, TextChannel channel, BlackUser author, BlackGuild guild, BlackMessage msg) {
		try {
			Map<EncodeHintType, ErrorCorrectionLevel> map
	        = new HashMap<EncodeHintType,
	                      ErrorCorrectionLevel>();
			map.put(EncodeHintType.ERROR_CORRECTION,
	                ErrorCorrectionLevel.L);
			
			BinaryBitmap binaryBitmap
            = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                    ImageIO.read(
                        new URL(url).openStream()))));
	
			Result result = new MultiFormatReader().decode(binaryBitmap);
			send(cmde, result, channel, author, guild, msg, url);
			return;
		} catch (Exception e) {
			if (e instanceof NotFoundException) {
				msg.reply(EmbedUtils.getErrorEmbed(author, guild).addField("qrdecodefail", "qrnotfound", false).build()).queue();
				return;
			} else {
				if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")) {
					msg.reply(EmbedUtils.getErrorEmbed(author, guild).addField("privateqr", "uploadasattachment", false).build()).queue();
					return;
				} else {					
					e.printStackTrace();
					msg.reply(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
					return;
				}
			}
		}
	}

	private void readQR(CommandEvent cmde, Attachment path, TextChannel channel, BlackUser author, BlackGuild guild, BlackMessage msg) {
		try {
			Map<EncodeHintType, ErrorCorrectionLevel> map
	        = new HashMap<EncodeHintType,
	                      ErrorCorrectionLevel>();
			map.put(EncodeHintType.ERROR_CORRECTION,
	                ErrorCorrectionLevel.L);
			
			final InputStream is = path.retrieveInputStream().join();
			BinaryBitmap binaryBitmap
            = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                    ImageIO.read(
                        is))));
			is.close();
	
			Result result = new MultiFormatReader().decode(binaryBitmap);
			send(cmde, result, channel, author, guild, msg, path.getUrl());
			return;
		} catch (Exception e) {
			if (e instanceof NotFoundException) {
				msg.reply(EmbedUtils.getErrorEmbed(msg.getBlackUser(), guild).addField("qrdecodefail", "qrnotfound", false).build()).queue();
				return;
			} else {
				if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")) {
					msg.reply(EmbedUtils.getErrorEmbed(msg.getBlackUser(), guild).addField("privateqr", "uploadasattachment", false).build()).queue();
					return;
				} else {					
					e.printStackTrace();
					msg.reply(EmbedUtils.getErrorEmbed(msg.getBlackUser(), guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
					return;
				}
			}
		}
	}
}