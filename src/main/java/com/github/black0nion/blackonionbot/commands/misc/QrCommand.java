package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class QrCommand extends SlashCommand {

	private static final Logger logger = LoggerFactory.getLogger(QrCommand.class);

	private static final String IMAGE = "image";
	private static final String QR_CODE_IMAGE = "qr_code_image";
	private static final String URL = "url";
	private static final String QR_CODE_LINK = "qr_code_link";

	private static final String INPUT = "input";

	public QrCommand() {
		super(builder(Commands.slash("qr", "Used to decode a QR code from an image")
			.addSubcommands(
				new SubcommandData("encode", "Used to encode a QR code from a string")
					.addOption(OptionType.STRING, INPUT, "The data to encode", true)
			)
			.addSubcommandGroups(
				new SubcommandGroupData("decode", "Used to decode a QR code from an image")
					.addSubcommands(
						new SubcommandData(IMAGE, "Decodes a QR code from an image")
							.addOption(OptionType.ATTACHMENT, QR_CODE_IMAGE, "The image to decode", true),
						new SubcommandData(URL, "Decodes a QR code from a link")
							.addOption(OptionType.STRING, QR_CODE_LINK, "The link to decode", true)))
			)
		);
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		switch (e.getSubcommandGroup()) {
			case "encode" -> encodeQR(cmde, e);
			case "decode" -> {
				switch (e.getSubcommandName()) {
					case IMAGE -> decodeImage(cmde, e);
					case URL -> decodeUrl(cmde, e);
					default -> cmde.sendPleaseUse();
				}
			}
			default -> cmde.sendPleaseUse();
		}
	}

	//region Encode
	private static final MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

	private static void encodeQR(SlashCommandEvent cmde, SlashCommandInteractionEvent e) {
		var input = e.getOption(INPUT, OptionMapping::getAsString);
		final String filePath = "tmp/" + System.currentTimeMillis() + ".png";
		final File file = new File(filePath);
		createQR(input, filePath);
		final EmbedBuilder builder = cmde.success().setTitle("qrcode", "https://zxing.github.io/zxing").setImage("attachment://qr.png");
		try {
			e.replyFile(new FileInputStream(file), "qr.png").addEmbeds(builder.build()).queue();
		} catch (FileNotFoundException ex) {
			logger.error("Could not find file", ex);
			cmde.send("somethingwentwrong");
		}
	}

	private static void createQR(final String data, final String path) {
		try {
			final BitMatrix matrix = multiFormatWriter.encode(new String(data.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, 420, 420);

			MatrixToImageWriter.writeToPath(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path).toPath());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	//endregion

	//region Decode
	private static final MultiFormatReader multiFormatReader = new MultiFormatReader();

	private void decodeImage(SlashCommandEvent cmde, SlashCommandInteractionEvent e) {
		var qrCodeImage = e.getOption(QR_CODE_IMAGE, OptionMapping::getAsAttachment);
		this.readQRFromAttachment(cmde, qrCodeImage);
	}

	private void decodeUrl(SlashCommandEvent cmde, SlashCommandInteractionEvent e) {
		var qrCodeLink = e.getOption(QR_CODE_LINK, OptionMapping::getAsString);
		if (qrCodeLink.endsWith(".png") || qrCodeLink.endsWith(".jpg")) {
			this.readQRFromURL(cmde, qrCodeLink);
		} else {
			cmde.send("qronlypngandjpg");
		}
	}

	private void send(final SlashCommandEvent cmde, final Result result, final String imageUrl) {
		if (result == null || result.getBarcodeFormat() == null) {
			cmde.sendPleaseUse();
		} else {
			cmde.reply(cmde.success().setTitle("qrcode", "https://zxing.github.io/zxing")
				.setThumbnail(imageUrl)
				.addField("qrresult", result.getText(), false));
		}
	}

	private void readQRFromURL(final SlashCommandEvent cmde, final String url) {
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
				cmde.error("qrdecodefail", "qrnotfound");
			} else if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")) {
				cmde.error("qrprivate", "uploadasattachment");
			} else {
				cmde.exception(e);
			}
		}
	}

	private void readQRFromAttachment(final SlashCommandEvent cmde, final Attachment path) {
		try {
			final InputStream is = path.retrieveInputStream().join();
			final BinaryBitmap binaryBitmap = new BinaryBitmap(
				new HybridBinarizer(
					new BufferedImageLuminanceSource(
						ImageIO.read(is)
					)
				)
			);
			is.close();

			final Result result = multiFormatReader.decode(binaryBitmap);
			this.send(cmde, result, path.getUrl());
		} catch (final Exception e) {
			if (e instanceof NotFoundException) {
				cmde.error("qrdecodefail", "qrnotfound");
			} else if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")) {
				cmde.error("qrprivate", "uploadasattachment");
			} else {
				cmde.exception(e);
			}
		}
	}
	//endregion
}