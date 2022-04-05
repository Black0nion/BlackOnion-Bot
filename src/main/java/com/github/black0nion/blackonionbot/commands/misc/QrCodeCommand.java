package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.FileInputStream;

public class QrCodeCommand extends TextCommand {

  public QrCodeCommand() {
    this.setCommand("qrcode", "qr").setRequiredArgumentCount(1).setSyntax("<input>");
  }

  @Override
  public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
      final Message message, final BlackMember member, final BlackUser author,
      final BlackGuild guild, final TextChannel channel) {
    final String filePath = "tmp/" + System.currentTimeMillis() + ".png";
    final File file = new File(filePath);
    file.getParentFile().mkdirs();

    createQR(String.join(" ", Utils.removeFirstArg(args)), filePath, "UTF-8", 420, 420);

    final EmbedBuilder builder = EmbedUtils.getSuccessEmbed(author, guild)
        .setTitle("qrcode", "https://zxing.github.io/zxing").setImage("attachment://qr.png");

    try {
      channel.sendFile(new FileInputStream(file), "qr.png").reference(message)
          .setEmbeds(builder.build()).queue(msg -> {
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
      message.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild)
          .addField("errorhappened", "somethingwentwrong", false).build()).queue();
    }
  }

  private static final MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

  public static void createQR(final String data, final String path, final String charset,
      final int height, final int width) {
    try {
      final BitMatrix matrix = multiFormatWriter.encode(new String(data.getBytes(charset), charset),
          BarcodeFormat.QR_CODE, width, height);

      MatrixToImageWriter.writeToPath(matrix, path.substring(path.lastIndexOf('.') + 1),
          new File(path).toPath());
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
