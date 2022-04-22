package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class QrCodeCommand extends SlashCommand {
    private static final String INPUT = "input";

    public QrCodeCommand() {

        super(builder(Commands.slash("qrcode", "Use this command to create a qrcode.")
                .addOption(OptionType.STRING, INPUT, "The text you want to encode in the qrcode.", true)));
    }

    @Override
    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var input = e.getOption(INPUT, OptionMapping::getAsString);
        final String filePath = "tmp/" + System.currentTimeMillis() + ".png";
        final File file = new File(filePath);
        createQR(input, filePath, "UTF-8", 420, 420);
        final EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild).setTitle("qrcode", "https://zxing.github.io/zxing").setImage("attachment://qr.png");
        try {
            e.replyFile(new FileInputStream(file), "qr.png").addEmbeds(builder.build()).queue();
        } catch (FileNotFoundException ex) {
            cmde.send("Something went wrong while trying to send the qrcode.");
        }
    }

    private static final MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

    public static void createQR(final String data, final String path, final String charset, final int height, final int width) {
        try {
            final BitMatrix matrix = multiFormatWriter.encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, width, height);

            MatrixToImageWriter.writeToPath(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path).toPath());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}