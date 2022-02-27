package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasteCommand extends Command {

	public PasteCommand() {
		this.setCommand("paste", "uploadtext").setSyntax("<text (codeblock with language specification if wanted)>").setRequiredArgumentCount(1);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final String bodyRaw = String.join(" ", Utils.removeFirstArg(args)).trim();
		// broken lol
		final Matcher m = Pattern.compile("\\s*```([a-z]+\\n)?\\s*([\\s\\S]*?)\\s*```\\s*").matcher(bodyRaw);
		String body, language = null;

		m.find();
		try {
			language = m.group(1);
		} catch (final Exception ignored) {}

		try {
			body = m.group(2);
		} catch (final Exception ignored) {
			body = bodyRaw;
		}

		final String finalLanguage = language;
		final String finalBody = body;

		cmde.loading(msg -> {
			try {
				Unirest.setTimeouts(0, 0);
				final HttpRequestWithBody headers = Unirest.post("https://paste.sv-studios.net/documents").header("Content-Type", "text/plain");

				if (finalLanguage != null) {
					headers.header("language", finalLanguage);
				}

				final HttpResponse<String> response = headers.body(finalBody).asString();

				final JSONObject obj = new JSONObject(response.getBody());

				final EmbedBuilder builder = cmde.success().setTitle("pastecreated", "https://paste.sv-studios.net/" + obj.getString("key")).setDescription("```" + (finalLanguage != null ? finalLanguage : "")).appendDescription("\n").appendDescription(finalBody).appendDescription("```");

				msg.editMessageEmbeds(builder.build()).queue();

				author.openPrivateChannel().queue(ch ->
					ch.sendMessageEmbeds(builder.appendDescription("\n" + cmde.getTranslation("yourcode").replace("%code%", obj.getString("deleteSecret"))).build()).queue());
			} catch (final Exception ex) {
				cmde.exception();
			}
		});
	}
}