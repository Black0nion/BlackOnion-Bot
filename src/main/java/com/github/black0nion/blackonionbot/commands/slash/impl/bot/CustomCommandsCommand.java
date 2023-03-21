package com.github.black0nion.blackonionbot.commands.slash.impl.bot;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.customcommand.CustomCommand;
import com.github.black0nion.blackonionbot.systems.customcommand.CustomCommandRepository;
import com.github.black0nion.blackonionbot.systems.customcommand.CustomCommandResponsePlaintext;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.StartsWithLinkedList;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO: remake with modals
public class CustomCommandsCommand extends SlashCommand {
	private static final String OPTION = "option";
	private static final String COMMAND_NAME = "command_name";
	private static final String LIST = "list";
	private static final String CREATE = "create";
	private static final String DELETE = "delete";
	private final SlashCommandBase slashCommandBase;
	private final LanguageSystem languageSystem;
	private final CustomCommandRepository customCommandRepository;

	public CustomCommandsCommand(SlashCommandBase slashCommandBase, LanguageSystem languageSystem, CustomCommandRepository customCommandRepository) {
		super(builder(Commands.slash("customcommand", "Used to manage custom commands.")
			.addSubcommands(
				new SubcommandData(LIST, "List all custom commands"),
				new SubcommandData(CREATE, "Start the creation wizard")
					.addOption(OptionType.STRING, COMMAND_NAME, "The command to create"),
				new SubcommandData(DELETE, "Delete a custom command")
					.addOption(OptionType.STRING, COMMAND_NAME, "The command to delete", true, true)
			)));
		this.slashCommandBase = slashCommandBase;
		this.languageSystem = languageSystem;
		this.customCommandRepository = customCommandRepository;
	}

	@Override
	// TODO: test
	public void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
		Guild guild = Objects.requireNonNull(event.getGuild());
		if (event.getSubcommandName().equals(DELETE) && event.getFocusedOption().getName().equals(COMMAND_NAME)) {
			List<String> elements = new StartsWithLinkedList(customCommandRepository.getCustomCommands(guild).keySet())
				.getElementsStartingWith(event.getFocusedOption().getValue());

			event.replyChoices(elements.stream().map(e -> new Command.Choice(e, e)).toList()).queue();
		}
	}

	private void askForRaw(final @NotNull String command, final @NotNull SlashCommandEvent cmde, final SlashCommandInteractionEvent slashCommandInteractionEvent, final Guild guild, final Member member, final User user, final UserSettings userSettings, final GuildSettings guildSettings) {
		slashCommandInteractionEvent.replyEmbeds(cmde.success()
				.addField("messagetosend", "inputmessage", false)
				.setDescription(cmde.getTranslation("leavetutorial"))
				.setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), slashCommandInteractionEvent.getJDA().getSelfUser().getAvatarUrl()).build())
			.queue(msg -> Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class,
				e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(),
				e -> {
					final String contentRaw = e.getMessage().getContentRaw();
					if (contentRaw.startsWith("/") || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
						cmde.send("aborting");
						return;
					}

					final CustomCommand customCommand = new CustomCommand(cmde.getGuild().getIdLong(), command, new CustomCommandResponsePlaintext(contentRaw));
					askForReply(command, new SlashCommandEvent(this, slashCommandInteractionEvent, guild, member, user, languageSystem.getDefaultLanguage(), userSettings, guildSettings), customCommand, slashCommandInteractionEvent, guild, member, user, userSettings, guildSettings);
				}));
	}

	private void askForReply(final String command, final @NotNull SlashCommandEvent cmde, final @NotNull CustomCommand customCommand, final SlashCommandInteractionEvent slashCommandInteractionEvent, final Guild guild, final Member member, final User user, final UserSettings userSettings, GuildSettings guildSettings) {
		slashCommandInteractionEvent.replyEmbeds(cmde.success().addField("shouldreply", "shouldanswer", false).setDescription(cmde.getTranslation("leavetutorial")).setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), slashCommandInteractionEvent.getJDA().getSelfUser().getAvatarUrl()).build()).queue(msg -> Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
			final String contentRaw = e.getMessage().getContentRaw();

			if (contentRaw.startsWith("/") || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
				cmde.send("aborting");
				return;
			}

			boolean reply;
			if (contentRaw.equalsIgnoreCase("true")) {
				reply = true;
			} else if (contentRaw.equalsIgnoreCase("false")) {
				reply = false;
			} else {
				askForReply(command, new SlashCommandEvent(this, slashCommandInteractionEvent, guild, member, user, languageSystem.getDefaultLanguage(), userSettings, guildSettings), customCommand, slashCommandInteractionEvent, guild, member, user, userSettings, guildSettings);
				return;
			}

			customCommand.setReply(reply);
			// TODO: add custom command to guild
			// cmde.getGuild().addCustomCommand(customCommand);
			cmde.success("commandadded", "executetutorial", new Placeholder("%cmd%", "/" + customCommand.getCommand()));
		}));
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, Guild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		var option = cmde.getOption(OPTION, OptionMapping::getAsString);
		Map<String, CustomCommand> customCommands = customCommandRepository.getCustomCommands(guild);

		switch (Objects.requireNonNull(option)) {
			case LIST -> cmde.success("customcommandslist", customCommands.values().stream().map(val -> "- `" + val.getCommand() + "`").collect(Collectors.joining("\n")));
			case CREATE -> {
				final String command = cmde.getOption(COMMAND_NAME, OptionMapping::getAsString);
				final int maxCount = guildSettings.getGuildType().getValue().getMaxCustomCommands();
				if (customCommands.size() >= maxCount) {
					cmde.error("toomanycustomcommands", "maxcustomcommands", new Placeholder("count", maxCount));
					return;
				}

				if (slashCommandBase.getCommand(command) != null || customCommands.containsKey(command)) {
					cmde.error("alreadyexisting", "commandexisting");
					return;
				}

				this.askForType(command, cmde, e, guild, member, author, userSettings, guildSettings);
			}
			case DELETE -> {
				final String command = cmde.getOption(COMMAND_NAME, OptionMapping::getAsString);
				if (customCommands.containsKey(command)) {
					this.askForType(command, cmde, e, guild, member, author, userSettings, guildSettings);
				} else {
					cmde.error("notfound", "commandnotfound");
				}
			}
			default -> cmde.sendPleaseUse();
		}
	}

	private void askForDelete(final String command, final @NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent slashCommandInteractionEvent) {
		slashCommandInteractionEvent.replyEmbeds(cmde.success().addField("areyousure", "@blaumeise was soll hier stehen?", false).build()).queue(msg -> Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, e -> e.getChannelType() == ChannelType.TEXT && e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
			final String contentRaw = e.getMessage().getContentRaw();

			if (contentRaw.equalsIgnoreCase("true")) {
				// TODO: remove custom command
				// cmde.getGuild().deleteCustomCommand(command);
				cmde.success("entrydeleted", "commanddeleted", new Placeholder("cmd", command));
			} else {
				cmde.error("abort", "nothingdeleted");
			}
		}));
	}

	private void askForType(final @NotNull String command, final @NotNull SlashCommandEvent cmde, final SlashCommandInteractionEvent slashCommandInteractionEvent, final Guild guild, final Member member, final User user, final UserSettings userSettings, GuildSettings guildSettings) {
		slashCommandInteractionEvent.replyEmbeds(cmde.success().addField("inputtype", "validtypes", false).setDescription(cmde.getTranslation("leavetutorial")).setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), slashCommandInteractionEvent.getJDA().getSelfUser().getAvatarUrl()).build()).queue(msg -> Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, e -> e.getChannelType() == ChannelType.TEXT && e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
			final String contentRaw = e.getMessage().getContentRaw();
			if (contentRaw.startsWith("/") || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
				cmde.error("aborting", "byeeee");
				return;
			}

			if (contentRaw.equalsIgnoreCase("raw") || contentRaw.equalsIgnoreCase("message")) {
				askForRaw(command, new SlashCommandEvent(this, slashCommandInteractionEvent, guild, member, user, languageSystem.getDefaultLanguage(), userSettings, guildSettings), slashCommandInteractionEvent, guild, member, user, userSettings, guildSettings);
			} else if (contentRaw.equalsIgnoreCase("embed")) {
				// TODO: add embed
			} else {
				this.askForType(command, new SlashCommandEvent(this, slashCommandInteractionEvent, guild, member, user, languageSystem.getDefaultLanguage(), userSettings, guildSettings), slashCommandInteractionEvent, guild, member, user, userSettings, guildSettings);
			}
		}));
	}
}
