package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class ClearCommand extends SlashCommand {

	public ClearCommand() {
		super(builder(Commands.slash("clear", "Clear a certain amount of messages")
				.addOptions(new OptionData(OptionType.INTEGER, "amount", "Amount of messages to delete", true)
						.setRequiredRange(2, 100))).setRequiredPermissions(Permission.MESSAGE_MANAGE).setEphemeral(true)
								.setRequiredBotPermissions(Permission.MESSAGE_MANAGE));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member,
			BlackUser author, BlackGuild guild, @NotNull TextChannel channel) {
		try {
			final Integer amount = e.getOption("amount", OptionMapping::getAsInt);

			try {
				channel.getIterableHistory().cache(false).queue(msgs -> {
					final OffsetDateTime firstValidDate = OffsetDateTime.now(ZoneOffset.UTC).minusWeeks(2)
							.plusSeconds(1);
					final List<Message> messages = new ArrayList<>();
					int i = amount;
					for (final Message m : msgs) {
						if (!m.isPinned() && m.getTimeCreated().isAfter(firstValidDate)) {
							messages.add(m);
						}
						if (--i <= 0) {
							break;
						}
					}

					deleteMessages(cmde, channel, amount, messages);
				});
			} catch (final Exception ex) {
				if (!(ex instanceof IllegalArgumentException)) {
					ex.printStackTrace();
					cmde.exception(ex);
				} else {
					cmde.send("messagestooold");
				}
			}
		} catch (final Exception ignored) {
			cmde.sendPleaseUse();
		}
	}

	static void deleteMessages(@NotNull SlashCommandEvent cmde, @NotNull TextChannel channel, int amount,
			@NotNull List<Message> messages) {
		channel.deleteMessages(messages).queue(success -> {
			if (messages.size() > amount) {
				cmde.send("msgsgotdeletedless", new Placeholder("msgcount", messages.size()),
						new Placeholder("remaining", amount - messages.size()));
			} else {
				cmde.send("msgsgotdeleted", new Placeholder("msgcount", amount));
			}
		}, cmde::exception);
	}
}
