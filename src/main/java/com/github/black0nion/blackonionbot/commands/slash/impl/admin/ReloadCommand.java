package com.github.black0nion.blackonionbot.commands.slash.impl.admin;

import com.github.black0nion.blackonionbot.commands.common.utils.UserRespondUtilsImpl;
import com.github.black0nion.blackonionbot.commands.common.utils.event.UserRespondUtils;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettingsRepo;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepo;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.reload.ReloadSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadCommand extends SlashCommand {

	private final ReloadSystem reloadSystem;
	private final LanguageSystem languageSystem;
	private final GuildSettingsRepo guildSettingsRepo;
	private final UserSettingsRepo userSettingsRepo;

	public ReloadCommand(ReloadSystem reloadSystem, LanguageSystem languageSystem, GuildSettingsRepo guildSettingsRepo, UserSettingsRepo userSettingsRepo) {
		super(builder("reload", "Reloads the bot")
			.setAdminGuild());
		this.reloadSystem = reloadSystem;
		this.languageSystem = languageSystem;
		this.guildSettingsRepo = guildSettingsRepo;
		this.userSettingsRepo = userSettingsRepo;
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, Guild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		e.replyComponents(
				ActionRow.of(StringSelectMenu.create(enrichId("reloadablemethods"))
					.addOptions(reloadSystem.getReloadables().stream()
						.map(l -> SelectOption.of(l.getReloadName(), l.getReloadName()))
						.toArray(SelectOption[]::new))
					.setMaxValues(SelectMenu.OPTIONS_MAX_AMOUNT)
					.build()),
				ActionRow.of(Button.primary(enrichId("all"), "All"), getCancelButton()))
			.setEphemeral(true)
			.queue();
	}

	@Override
	public void handleSelectMenuInteraction(GenericSelectMenuInteractionEvent<?, ?> e) {
		if (!(e instanceof StringSelectInteractionEvent event)) return;

		e.deferReply().queue();

		UserRespondUtils cmde = createRespondUtils(event);

		List<String> options = event.getValues();
		for (String option : options) {
			reloadSystem.reload(option);
		}
		// DON'T use cmde.send() here because that'll invoke "reply" which will error because of the deferred reply (for some reason)
		// TODO: setEphemeral() doesn't work here
		e.getHook().sendMessage(cmde.getTranslation("configreloaded", new Placeholder("config", String.join(", ", options.toArray(String[]::new)))))
			.setEphemeral(true)
			.queue();
	}

	@Override
	public void handleButtonPress(ButtonInteractionEvent event) {
		UserRespondUtils cmde = createRespondUtils(event);
		reloadSystem.reloadAll();
		cmde.send("configsreload");
	}

	@NotNull
	private UserRespondUtilsImpl createRespondUtils(GenericComponentInteractionCreateEvent event) {
		return new UserRespondUtilsImpl(event, guildSettingsRepo.getSettings(event.getGuild()), event.getUser(), userSettingsRepo.getSettings(event.getUser()), languageSystem.getDefaultLanguage()) {
			@Override
			public boolean isEphemeral() {
				return true;
			}
		};
	}
}
