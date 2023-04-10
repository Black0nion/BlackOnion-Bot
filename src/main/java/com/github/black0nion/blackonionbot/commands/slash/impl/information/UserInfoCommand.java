package com.github.black0nion.blackonionbot.commands.slash.impl.information;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepo;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.User.UserFlag;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.time.format.DateTimeFormatter;

public class UserInfoCommand extends SlashCommand {

	private static final String USER = "user";
	private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
	private final UserSettingsRepo userSettingsRepo;

	public UserInfoCommand(UserSettingsRepo userSettingsRepo) {
		super(builder(Commands.slash("userinfo", "Used to get information about a member.")
			.addOption(OptionType.USER, USER, "The member to get information about.", false)));
		this.userSettingsRepo = userSettingsRepo;
	}

	@NotNull EmbedBuilder getUserInfo(final @NotNull SlashCommandEvent cmde, final @NotNull User user, final @Nullable Member member) {
		UserSettings userSettings = userSettingsRepo.getSettings(user);
		Language language = null;
		if (userSettings != null)
			language = userSettings.getLanguage().getValue();

		final String avatarUrl = user.getAvatarUrl();
		final String[] flags = user.getFlags().stream().map(UserFlag::getName).toArray(String[]::new);
		final TranslatedEmbedBuilder builder = cmde.success()
			.setTitle("userinfo")
			.setThumbnail(avatarUrl != null ? avatarUrl : user.getDefaultAvatarUrl())
			.addField("name", Utils.escapeMarkdown(user.getName()), true)
			.addField("discriminator", user.getDiscriminator(), true)
			.addField("userid", user.getId(), true)
			.addField("badges", (flags.length != 0 ? String.join("\n", flags) : "empty"), false)
			.addField("language", language != null ? language.getName() + " (" + language.getLanguageCode() + ")" : "nopreference", true)
			.addField("created", user.getTimeCreated().format(TIME_PATTERN), true)
			.addField("isbot", user.isBot() ? "yes" : "no", true);

		if (member != null) {
			builder.addField("joined", member.getTimeJoined().format(TIME_PATTERN), true);
			if (member.getTimeBoosted() != null)
				builder.addField("boosted", member.getTimeBoosted().format(TIME_PATTERN), true);
		}

		return builder;
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, Member member, User author, Guild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		var givenUser = e.getOption(USER, OptionMapping::getAsUser);

		if (givenUser == null) {
			cmde.reply(getUserInfo(cmde, author, member));
		} else {
			guild.retrieveMember(givenUser).queue(loadedMember -> cmde.reply(getUserInfo(cmde, givenUser, loadedMember)), error -> cmde.reply(getUserInfo(cmde, givenUser, null)));
		}
	}
}
