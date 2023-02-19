package com.github.black0nion.blackonionbot.commands.slash.impl.bot;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.api.container.SettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettingsRepo;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.LanguageSetting;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepo;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.utils.NotImplementedException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import static java.util.Objects.requireNonNull;

public class ConfigCommand extends SlashCommand {

	private final UserSettingsRepo userSettingsRepo;
	private final GuildSettingsRepo guildSettingsRepo;

	public ConfigCommand(UserSettingsRepo userSettingsRepo, GuildSettingsRepo guildSettingsRepo) {
		super(builder(Commands.slash("config", "Configure various settings.")
			.addSubcommandGroups(
				new SubcommandGroupData("user", "Configure user settings.")
					.addSubcommands(
						new SubcommandData("list", "List all settings."),
						new SubcommandData("get", "Get a setting.")
							.addOption(OptionType.STRING, "setting", "The setting to get.", true, true),
						new SubcommandData("set", "Set a setting.")
							.addOption(OptionType.STRING, "setting", "The setting to set.", true, true)
							.addOption(OptionType.STRING, "value", "The value to set the setting to.", false)
					),
				new SubcommandGroupData("guild", "Configure guild settings.")
					.addSubcommands(
						new SubcommandData("list", "List all settings."),
						new SubcommandData("get", "Get a setting.")
							.addOption(OptionType.STRING, "setting", "The setting to get.", true, true),
						new SubcommandData("set", "Set a setting.")
							.addOption(OptionType.STRING, "setting", "The setting to set.", true, true)
							.addOption(OptionType.STRING, "value", "The value to set the setting to.", false)
					)
			)
		));
		this.userSettingsRepo = userSettingsRepo;
		this.guildSettingsRepo = guildSettingsRepo;
	}

	@Override
	public void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
		String optionName = event.getFocusedOption().getName();
		String optionValue = event.getFocusedOption().getValue();
		if (optionName.equals("setting")) {
			SettingsContainer settingsContainer = getSettingsContainer(requireNonNull(event.getSubcommandGroup()), userSettingsRepo, event.getUser().getIdLong(), guildSettingsRepo, event.getGuild().getIdLong());

			event.replyChoices(settingsContainer.getSettings().stream()
				.map(Setting::getName)
				.filter(name -> name.contains(optionValue))
				.map(name -> new Command.Choice(name, name))
				.toList()).queue();
			return;
		}
		super.handleAutoComplete(event);
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		SettingsContainer settingsContainer = getSettingsContainer(cmde.getSubcommandGroup(), userSettings, guildSettings);

		switch (cmde.getSubcommandName()) {
			case "list" -> listCommand(cmde, settingsContainer);
			case "get" -> getCommand(cmde, settingsContainer);
			case "set" -> setCommand(cmde, settingsContainer, userSettings);
			default -> throw new NotImplementedException();
		}
	}

	private static void listCommand(SlashCommandEvent cmde, SettingsContainer settingsContainer) {
		TranslatedEmbedBuilder builder = cmde.getDefaultSuccessEmbed();
		for (Setting<?> setting : settingsContainer.getSettings()) {
			builder.addFieldUntranslated(setting.getPrettyName(), setting.getAsMention(), true);
		}
		cmde.reply(builder);
	}

	private static void getCommand(SlashCommandEvent cmde, SettingsContainer settingsContainer) {
		Setting<?> setting = settingsContainer.getSetting(cmde.getOption("setting", OptionMapping::getAsString));

		if (setting == null) {
			cmde.send("settingnotfound");
			return;
		}

		cmde.reply(cmde.getDefaultSuccessEmbed()
			.addFieldUntranslated(setting.getPrettyName(), setting.getAsMention(), true));
	}

	private static void setCommand(SlashCommandEvent cmde, SettingsContainer settingsContainer, UserSettings userSettings) {
		Setting<?> setting = settingsContainer.getSetting(cmde.getOption("setting", OptionMapping::getAsString));

		if (setting == null) {
			cmde.send("settingnotfound");
			return;
		}

		if (setting.getRequiredPermissions() != null && !cmde.getMember().hasPermission(setting.getRequiredPermissions())) {
			cmde.send("missingpermissions", new Placeholder("perms", Utils.getPermissionString(setting.getRequiredPermissions())));
			return;
		} else if (setting.getRequiredCustomPermissions() != null && !CustomPermission.hasRights(userSettings.getPermissions().getValue(), setting.getRequiredCustomPermissions())) {
			cmde.send("missingpermissions", new Placeholder("perms", Utils.getCustomPermissionString(setting.getRequiredCustomPermissions())));
			return;
		}

		String value = cmde.getOptionNullable("value", OptionMapping::getAsString);

		if (value == null) {
			if (setting.isNullable()) setting.setValue(null);
			else setting.reset();
		} else {
			setting.setParsedValue(value);
		}

		// fix goofy looking wrong language after literally changing it
		if (setting instanceof LanguageSetting languageSetting) {
			cmde.setLanguage(languageSetting.getValue());
		}

		cmde.reply(cmde.getDefaultSuccessEmbed()
			.setTitle("configvalueset")
			.addFieldUntranslated(setting.getPrettyName(), setting.getAsMention(), true));
	}


	private static SettingsContainer getSettingsContainer(String containerName, UserSettings userSettings, GuildSettings guildSettings) {
		return switch (containerName) {
			case "user" -> userSettings;
			case "guild" -> guildSettings;
			default ->
				throw new NotImplementedException("Subcommand group " + containerName + " not implemented.");
		};
	}

	private static SettingsContainer getSettingsContainer(String containerName, UserSettingsRepo userSettingsRepo, long userId, GuildSettingsRepo guildSettingsRepo, long guildId) {
		return switch (containerName) {
			case "user" -> userSettingsRepo.getSettings(userId);
			case "guild" -> guildSettingsRepo.getSettings(guildId);
			default ->
				throw new NotImplementedException("Subcommand group " + containerName + " not implemented.");
		};
	}
}
