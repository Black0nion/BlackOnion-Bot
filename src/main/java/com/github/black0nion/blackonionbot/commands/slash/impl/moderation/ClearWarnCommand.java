package com.github.black0nion.blackonionbot.commands.slash.impl.moderation;

import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClearWarnCommand extends SlashCommand {
	private static final String USER = "user";
	private static final String WARN_ID = "warnid";

	public ClearWarnCommand() {
		super(builder(Commands.slash("clearwarn", "Used to clear a warn.")
			.addOption(OptionType.USER, USER, "The user to clear the warn of.", true)
			.addOption(OptionType.INTEGER, WARN_ID, "The id of the warn to clear (will clear all if none is specified).", false))
			.setRequiredPermissions(Permission.KICK_MEMBERS));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		Member memb = e.getOption(USER, OptionMapping::getAsMember);
		if (memb == null) {
			cmde.send("notamember");
			return;
		}

		var warnMember = BlackMember.from(memb);
		if (warnMember == null)
			throw new NullPointerException("warnMember is null");

		var warnId = e.getOption(WARN_ID, OptionMapping::getAsLong);

		if (warnId == null) {
			// delete all warns
			warnMember.clearWarns();
			cmde.send("warnsdeleted", new Placeholder("user", warnMember.getUser().getAsMention()));
			return;
		}

		final List<Warn> memberWarns = warnMember.getWarns();
		for (final Warn warn : memberWarns) {
			if (warn.id() == warnId) {
				warnMember.deleteWarn(warn);
				cmde.send("warndeleted");
				return;
			}
		}
		cmde.send("warnnotfound");
	}
}
