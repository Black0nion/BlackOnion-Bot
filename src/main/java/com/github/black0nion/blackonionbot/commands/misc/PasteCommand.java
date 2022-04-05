package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasteCommand extends TextCommand {

  public PasteCommand() {
    this.setCommand("paste", "uploadtext")
        .setSyntax("<text (codeblock with language specification if wanted)>")
        .setRequiredArgumentCount(1);
  }

  @Override
  public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e,
      final Message message, final BlackMember member, final BlackUser author,
      final BlackGuild guild, final TextChannel channel) {
    final String bodyRaw = String.join(" ", Utils.removeFirstArg(args)).trim();
    // broken lol
    final Matcher m =
        Pattern.compile("\\s*```([a-z]+\\n)?\\s*([\\s\\S]*?)\\s*```\\s*").matcher(bodyRaw);
    String body = null, language = null;

    if (m.find()) {
      try {
        language = m.group(1);
      } catch (final Exception ignored) {
      }

      try {
        body = m.group(2);
      } catch (final Exception ignored) {
      }
    }

    final String finalLanguage = language;
    final String finalBody = body != null ? body : bodyRaw;

    cmde.loading(msg -> {
      try {
        HttpRequest.Builder requestBuilder =
            HttpRequest.newBuilder(URI.create("https://paste.sv-studios.net/documents"))
                .POST(HttpRequest.BodyPublishers.ofString(finalBody))
                .header("Content-Type", "text/plain");

        if (finalLanguage != null) {
          requestBuilder.setHeader("language", finalLanguage);
        }

        final HttpResponse<String> response = Bot.getInstance().getHttpClient()
            .send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        final JSONObject obj = new JSONObject(response.body());

        final EmbedBuilder builder = cmde.success()
            .setTitle("pastecreated", "https://paste.sv-studios.net/" + obj.getString("key"))
            .setDescription("```" + (finalLanguage != null ? finalLanguage : ""))
            .appendDescription("\n").appendDescription(finalBody).appendDescription("```");

        msg.editMessageEmbeds(builder.build()).queue();

        author.openPrivateChannel()
            .queue(ch -> ch.sendMessageEmbeds(builder.appendDescription("\n"
                + cmde.getTranslation("yourcode").replace("%code%", obj.getString("deleteSecret")))
                .build()).queue());
      } catch (final Exception ex) {
        cmde.exception();
      }
    });
  }
}
