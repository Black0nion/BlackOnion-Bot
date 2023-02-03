package com.github.black0nion.blackonionbot.commands.slash.impl.misc;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasteCommand extends SlashCommand {
	private static final String TEXT = "text";

	public PasteCommand() {
		super(builder(Commands.slash("paste", "pastes a message to a past website")
			.addOption(OptionType.STRING, TEXT, "the text to paste (code-block with language specification if wanted)", true)));
	}

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel, UserSettings userSettings) {
		var bodyRaw = e.getOption(TEXT, OptionMapping::getAsString);
		final Matcher m = Pattern.compile("\\s*```([a-z]+\\n)?\\s*([\\s\\S]*?)\\s*```\\s*").matcher(bodyRaw);
		String body = null;
		String language = null;

		if (m.find()) {
			try {
				language = m.group(1);
			} catch (final Exception ignored) {}
			try {
				body = m.group(2);
			} catch (final Exception ignored) {}
		}

		final String finalLanguage = language;
		final String finalBody = body != null ? body : bodyRaw;
		try {
			HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create("https://paste.sv-studios.net/documents"))
				.POST(HttpRequest.BodyPublishers.ofString(finalBody))
				.header("Content-Type", "text/plain");

			if (finalLanguage != null) {
				requestBuilder.setHeader("language", finalLanguage);
			}

			final HttpResponse<String> response = Bot.getInstance().getHttpClient().send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

			final JSONObject obj = new JSONObject(response.body());

			final EmbedBuilder builder = cmde.success().setTitle("pastecreated", "https://paste.sv-studios.net/" + obj.getString("key")).setDescription("```" + (finalLanguage != null ? finalLanguage : "")).appendDescription("\n").appendDescription(finalBody).appendDescription("```");

			cmde.reply(builder);

			author.openPrivateChannel().queue(ch ->
				ch.sendMessageEmbeds(builder
					.appendDescription("\n" + cmde.getTranslation("yourcode", new Placeholder("code", obj.getString("deleteSecret"))))
					.build()
				).queue());
		} catch (final InterruptedException ex) {
			cmde.exception();
			// sonarlint told me to do this
			Thread.currentThread().interrupt();
		} catch (final Exception ex) {
			cmde.exception(ex);
		}
	}
}
