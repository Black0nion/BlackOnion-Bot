package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
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
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class ClearCommand extends SlashCommand {

	private static final String MESSAGEID = "message_id";
	private static final String AMOUNT = "amount";

	public ClearCommand() {
		super(builder(Commands.slash("clear", "Clear a certain amount of messages")
			.addSubcommands(
				new SubcommandData("count", "Clear a specific amount of messages")
					.addOptions(new OptionData(OptionType.INTEGER, AMOUNT, "Amount of messages to delete", true)
						.setRequiredRange(2, 100)),
				new SubcommandData("until", "Clear all messages until a specific message")
					.addOption(OptionType.STRING, MESSAGEID, "The message id of the last message to be kept", true)
			))
			.setRequiredPermissions(Permission.MESSAGE_MANAGE).setEphemeral(true)
			.setRequiredBotPermissions(Permission.MESSAGE_MANAGE));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, @NotNull TextChannel channel) {
		System.out.println("LOL: " + e.getChannel().getLatestMessageId());
		switch (e.getSubcommandName()) {
			case "count" -> deleteCount(cmde, e, channel);
			case "until" -> deleteUntil(cmde, e, channel);
			default -> cmde.sendPleaseUse();
		}
	}

	private static void deleteCount(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel channel) {
		try {
			final Integer amount = e.getOption("amount", OptionMapping::getAsInt);

			try {
				channel.getIterableHistory().cache(false).queue(msgs -> {
					final OffsetDateTime firstValidDate = OffsetDateTime.now(ZoneOffset.UTC)
						.minusWeeks(2)
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

	private static void deleteUntil(SlashCommandEvent cmde, SlashCommandInteractionEvent e, TextChannel channel) {
		try {
			String messageIdString = e.getOption(MESSAGEID, OptionMapping::getAsString);
			if (!Utils.isLong(messageIdString)) {
				cmde.sendPleaseUse();
				return;
			}
			final long messageId = Long.parseLong(messageIdString);
			channel.retrieveMessageById(messageId).queue(msg -> {
				try {
					channel.getHistoryAfter(msg, 100).queue(msgs -> {
						final int msgsize = msgs.size();
						if (msgsize == 0 || msgsize > 100) {
							cmde.error("toomanymessages", "toomanymessagesinfo", new Placeholder("msgcount", msgsize));
							return;
						}
						final OffsetDateTime lastValidTime = OffsetDateTime.now(ZoneOffset.UTC).minusWeeks(2)
							.plusSeconds(1);
						final List<Message> messages = new ArrayList<>();
						int i = msgsize + 1;
						for (final Message m : msgs.getRetrievedHistory()) {
							if (m.getTimeCreated().isAfter(lastValidTime)) break;

							if (!m.isPinned()) {
								messages.add(m);
							}

							if (--i <= 0) {
								break;
							}
						}

						ClearCommand.deleteMessages(cmde, channel, msgsize, messages);
					});
				} catch (final Exception ex) {
					cmde.exception(ex);
				}
			}, err -> cmde.error("nomessagesfound", "pleaseinputmessage"));
		} catch (final Exception ignored) {
			cmde.sendPleaseUse();
		}
	}

	static void deleteMessages(@NotNull SlashCommandEvent cmde, @NotNull TextChannel channel, int expectedCount, @NotNull List<Message> messages) {
		channel.deleteMessages(messages).queue(success -> {
			if (messages.size() < expectedCount) {
				cmde.send("msgsgotdeletedless", new Placeholder("msgcount", messages.size()),
					new Placeholder("remaining", expectedCount - messages.size()));
			} else {
				cmde.send("msgsgotdeleted", new Placeholder("msgcount", expectedCount));
			}
		}, cmde::exception);
	}
}