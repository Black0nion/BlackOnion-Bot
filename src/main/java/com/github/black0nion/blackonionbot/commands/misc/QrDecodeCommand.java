package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class QrDecodeCommand extends SlashCommand {
	private static final String IMAGE = "image";
    private static final String QR_CODE_IMAGE = "qr_code_image";
	private static final String URL = "url";
    private static final String QR_CODE_LINK = "qr_code_link";
    private static final MultiFormatReader multiFormatReader = new MultiFormatReader();

    public QrDecodeCommand() {
        super(builder(Commands.slash("qr", "Used to decode a QR code from an image")
                .addSubcommands(
						new SubcommandData(IMAGE, "Decodes a QR code from an image")
                                .addOption(OptionType.ATTACHMENT, QR_CODE_IMAGE, "The image to decode", true),
                        new SubcommandData(URL, "Decodes a QR code from a link")
                                .addOption(OptionType.STRING, QR_CODE_LINK, "The link to decode", true))));
    }

    @Override
    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		switch (e.getSubcommandName()) {
			case IMAGE -> decodeImage(cmde, e, member, author, guild, channel);
			case URL -> decodeUrl(cmde, e, member, author, guild, channel);
			default -> cmde.sendPleaseUse();
		}
    }

	private void decodeImage(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var qrCodeImage = e.getOption(QR_CODE_IMAGE, OptionMapping::getAsAttachment);
		this.readQR(cmde, qrCodeImage, guild, member);
	}

    private void decodeUrl(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var qrCodeLink = e.getOption(QR_CODE_LINK, OptionMapping::getAsString);
        if (qrCodeLink.endsWith(".png") || qrCodeLink.endsWith(".jpg")) {
            this.readQR(cmde, qrCodeLink, author, guild);
        } else {
            cmde.sendPleaseUse();
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

    private void readQR(final SlashCommandEvent cmde, final String url, final BlackUser author, final BlackGuild guild) {
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
                cmde.reply(EmbedUtils.getErrorEmbed(author, guild).addField("qrdecodefail", "qrnotfound", false));
            } else if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")) {
                cmde.reply(EmbedUtils.getErrorEmbed(author, guild).addField("privateqr", "uploadasattachment", false));
            } else {
                e.printStackTrace();
                cmde.reply(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false));
            }
        }
    }

    private void readQR(final SlashCommandEvent cmde, final Attachment path, final BlackGuild guild, final BlackMember member) {
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
            assert member != null;
            if (e instanceof NotFoundException) {
                cmde.reply(EmbedUtils.getErrorEmbed(BlackUser.from(member), guild).addField("qrdecodefail", "qrnotfound", false));
            } else if (e.getMessage().startsWith("Server returned HTTP response code: 403 for URL:")) {
                cmde.reply(EmbedUtils.getErrorEmbed(BlackUser.from(member), guild).addField("privateqr", "uploadasattachment", false));
            } else {
                e.printStackTrace();
                cmde.reply(EmbedUtils.getErrorEmbed(BlackUser.from(member), guild).addField("errorhappened", "somethingwentwrong", false));
            }
        }
    }
}