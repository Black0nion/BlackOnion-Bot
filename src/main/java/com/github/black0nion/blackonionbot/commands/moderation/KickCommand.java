package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.AwaitDone;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class KickCommand extends SlashCommand {
	private static final String USER = "user";
	private static final String REASON = "reason";

	private static final Placeholder ACTION = new Placeholder("action", "kicked");

	public KickCommand() {
		super(builder(Commands.slash("kick", "Used to kick an user from a server")
			.addOption(OptionType.USER, USER, "The user to kick", true)
			.addOption(OptionType.STRING, REASON, "The reason for the kick", false))
			.setRequiredPermissions(Permission.KICK_MEMBERS)
			.setRequiredBotPermissions(Permission.KICK_MEMBERS));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		var memberToKick = e.getOption(USER, OptionMapping::getAsMember);
		var reason = e.getOption(REASON, OptionMapping::getAsString);

		if (memberToKick == null) {
			cmde.send("notamember");
			return;
		}

		if (reason != null && reason.length() > 512) {
			cmde.send("reasonoption");
			return;
		}

		if (member.canInteract(memberToKick) && member.getIdLong() != memberToKick.getIdLong()) {
			kickMember(cmde, memberToKick, reason, e.getJDA(), guild, e);
		} else {
			cmde.send("loweruserthanu");
		}
	}

	private static void kickMember(SlashCommandEvent cmde, @NotNull Member memberToKick, String reason, @NotNull JDA jda, @NotNull Guild guild, @NotNull SlashCommandInteractionEvent event) {
		String message = cmde.getTranslation(reason != null ? "idid" : "ididnoreason", new Placeholder("user", memberToKick.getUser().getAsMention()), new Placeholder("reason", reason), ACTION);
		AwaitDone<InteractionHook> await = new AwaitDone<>();
		guild.kick(memberToKick).reason("[" + cmde.getUser().getId() + "]" + (reason != null ? " " + reason : ""))
				.queue(success -> {
					event.reply(message).queue(await::done);
					jda.openPrivateChannelById(memberToKick.getUser().getIdLong())
						.flatMap(channel -> channel.sendMessage(cmde.getTranslation(reason != null ? "yougot" : "yougotnoreason", new Placeholder("reason", reason), new Placeholder("guild", guild.getName()), ACTION)))
						.queue(null, err -> Utils.getCantSendHandler(await, message, cmde));
				}, cmde::exception);
	}
}
