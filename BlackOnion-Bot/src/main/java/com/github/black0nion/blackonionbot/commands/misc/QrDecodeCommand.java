package com.github.black0nion.blackonionbot.commands.misc;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

// waiting for Discord to add files as a option
public class QrDecodeCommand extends Command {

	public QrDecodeCommand() {
		this.setCommand("qrdecode", "qrd")
			.setSyntax("<attach file / url to a public image>");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final List<Attachment> attachments = message.getAttachments();
		if (attachments.size() == 0) {
			if (args.length >= 2) {
				final String url = args[1];
				if (url.endsWith(".png") || url.endsWith(".jpg")) {
				    this.readQR(cmde, url, channel, author, guild, message);
				} else {
				    cmde.sendPleaseUse();
				}
			} else {
			    cmde.sendPleaseUse();
			}
		} else {
			final Attachment path = attachments.get(0);
			if (path.isImage()) {
			    this.readQR(cmde, path, channel, author, guild, message);
			} else {
			    cmde.sendPleaseUse();
			}
		}
	}

	private void send(final CommandEvent cmde, final Result result, final TextChannel channel, final BlackUser author, final BlackGuild guild, final Message message, final String imageUrl) {
		if (result == null || result.getBarcodeFormat() == null) {
		    cmde.sendPleaseUse();
		} else {
		    cmde.reply(cmde.success().setTitle("qrcode", "https://zxing.github.io/zxing")
		    		.setThumbnail(imageUrl)
		    		.addField("qrresult", result.getText(), false));
		}
	}

	private void readQR(final CommandEvent cmde, final String url, final TextChannel channel, final BlackUser author, final BlackGuild guild, final Message msg) {
		try {
			final Map<EncodeHintType, ErrorCorrectionLevel> map
	        = new HashMap<EncodeHintType,
	                      ErrorCorrectionLevel>();
			map.put(EncodeHintType.ERROR_CORRECTION,
	                ErrorCorrectionLevel.L);

			final BinaryBitmap binaryBitmap
            = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                    ImageIO.read(
                        new URL(url).openStream()))));

			final Result result = new MultiFormatReader().decode(binaryBitmap);
			this.send(cmde, result, channel, author, guild, msg, url);
			return;
		} catch (final Exception e) {
			if (e instanceof NotFoundException) {
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("qrdecodefail", "qrnotfound", false).build()).queue();
				return;
			} else if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")) {
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("privateqr", "uploadasattachment", false).build()).queue();
				return;
			} else {
				e.printStackTrace();
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
				return;
			}
		}
	}

	private void readQR(final CommandEvent cmde, final Attachment path, final TextChannel channel, final BlackUser author, final BlackGuild guild, final Message msg) {
		try {
			final Map<EncodeHintType, ErrorCorrectionLevel> map
	        = new HashMap<EncodeHintType,
	                      ErrorCorrectionLevel>();
			map.put(EncodeHintType.ERROR_CORRECTION,
	                ErrorCorrectionLevel.L);

			final InputStream is = path.retrieveInputStream().join();
			final BinaryBitmap binaryBitmap
            = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                    ImageIO.read(
                        is))));
			is.close();

			final Result result = new MultiFormatReader().decode(binaryBitmap);
			this.send(cmde, result, channel, author, guild, msg, path.getUrl());
			return;
		} catch (final Exception e) {
			if (e instanceof NotFoundException) {
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(BlackUser.from(msg.getMember()), guild).addField("qrdecodefail", "qrnotfound", false).build()).queue();
				return;
			} else if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")) {
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(BlackUser.from(msg.getMember()), guild).addField("privateqr", "uploadasattachment", false).build()).queue();
				return;
			} else {
				e.printStackTrace();
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(BlackUser.from(msg.getMember()), guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
				return;
			}
		}
	}
}