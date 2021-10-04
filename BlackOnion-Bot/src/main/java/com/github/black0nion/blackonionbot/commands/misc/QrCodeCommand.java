package com.github.black0nion.blackonionbot.commands.misc;

import java.io.File;
import java.io.FileInputStream;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class QrCodeCommand extends SlashCommand {

    public QrCodeCommand() {
        this.setData(new CommandData("qrencode", "Convert Text to a QR Code")
                .addOption(OptionType.STRING, "input", "The text to convert", true)
                .addOption(OptionType.BOOLEAN, "private", "Should the QR Code be sent privately?", false));
    }

    @Override
    public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        final String filePath = "tmp/qr" + System.currentTimeMillis() + ".png";
        final File file = new File(filePath);
        file.getParentFile().mkdirs();

        createQR(e.getOptionsByType(OptionType.STRING).get(0).getAsString(), filePath, "UTF-8", 420, 420);

        final EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild).setTitle("qrcode", "https://zxing.github.io/zxing").setImage("attachment://qr.png");

        try {
            e.getHook().sendFile(new FileInputStream(file), "qr.png").addEmbeds(builder.build())
                .setEphemeral(!e.getOptionsByType(OptionType.BOOLEAN).isEmpty() && e.getOptionsByType(OptionType.BOOLEAN).get(0).getAsBoolean())
            .queue(msg -> {
                if (!file.delete()) {
                    file.deleteOnExit();
                }
            }, error -> {
                if (!file.delete()) {
                    file.deleteOnExit();
                }
            });
        } catch (final Exception ex) {
            if (!file.delete()) {
                file.deleteOnExit();
            }
            ex.printStackTrace();
            cmde.exception();
        }
    }

    public static void createQR(final String data, final String path, final String charset, final int height, final int width) {
        try {
            final BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, width, height);

            MatrixToImageWriter.writeToPath(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path).toPath());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}