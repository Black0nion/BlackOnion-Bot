package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class QrDecodeCommand extends SlashCommand {

	private static final MultiFormatReader multiFormatReader = new MultiFormatReader();

	public QrDecodeCommand() {
		this.setCommand("qrdecode", "qrd")
			.setSyntax("<attach file / url to a public image>");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final List<Attachment> attachments = message.getAttachments();
		if (attachments.size() == 0) {
			if (args.length >= 2) {
				final String url = args[1];
				if (url.endsWith(".png") || url.endsWith(".jpg")) {
					this.readQR(cmde, url, author, guild, message);
				} else {
					cmde.sendPleaseUse();
				}
			} else {
				cmde.sendPleaseUse();
			}
		} else {
			final Attachment path = attachments.get(0);
			if (path.isImage()) {
				this.readQR(cmde, path, guild, message);
			} else {
				cmde.sendPleaseUse();
			}
		}
	}

	private void send(final CommandEvent cmde, final Result result, final String imageUrl) {
		if (result == null || result.getBarcodeFormat() == null) {
			cmde.sendPleaseUse();
		} else {
			cmde.reply(cmde.success().setTitle("qrcode", "https://zxing.github.io/zxing")
					.setThumbnail(imageUrl)
					.addField("qrresult", result.getText(), false));
		}
	}

	private void readQR(final CommandEvent cmde, final String url, final BlackUser author, final BlackGuild guild, final Message msg) {
		try {
			final BinaryBitmap binaryBitmap
					= new BinaryBitmap(new HybridBinarizer(
					new BufferedImageLuminanceSource(
							ImageIO.read(
									new URL(url).openStream()))));

			final Result result = multiFormatReader.decode(binaryBitmap);
			this.send(cmde, result, url);
		} catch (final Exception e) {
			if (e instanceof NotFoundException) {
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("qrdecodefail", "qrnotfound", false).build()).queue();
			} else if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")) {
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("privateqr", "uploadasattachment", false).build()).queue();
			} else {
				e.printStackTrace();
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
			}
		}
	}

	private void readQR(final CommandEvent cmde, final Attachment path, final BlackGuild guild, final Message msg) {
		try {
			final InputStream is = path.retrieveInputStream().join();
			final BinaryBitmap binaryBitmap
					= new BinaryBitmap(new HybridBinarizer(
					new BufferedImageLuminanceSource(
							ImageIO.read(
									is))));
			is.close();

			final Result result = multiFormatReader.decode(binaryBitmap);
			this.send(cmde, result, path.getUrl());
		} catch (final Exception e) {
			assert msg.getMember() != null;
			if (e instanceof NotFoundException) {
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(BlackUser.from(msg.getMember()), guild).addField("qrdecodefail", "qrnotfound", false).build()).queue();
			} else if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")) {
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(BlackUser.from(msg.getMember()), guild).addField("privateqr", "uploadasattachment", false).build()).queue();
			} else {
				e.printStackTrace();
				msg.replyEmbeds(EmbedUtils.getErrorEmbed(BlackUser.from(msg.getMember()), guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
			}
		}
	}
}