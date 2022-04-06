package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class KickCommand extends SlashCommand {
	private static final String USER = "user";
	private static final String REASON = "reason";

	public KickCommand() {
		super(builder(Commands.slash("kick", "Used to kick a user from a server")
				.addOption(OptionType.USER, USER, "The user to kick", true)
				.addOption(OptionType.STRING, REASON, "The reason for the kick", true))
						.setRequiredPermissions(Permission.KICK_MEMBERS)
						.setRequiredBotPermissions(Permission.KICK_MEMBERS));
	}

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member,
			BlackUser author, BlackGuild guild, TextChannel channel) {
		var memberToKick = e.getOption(USER, OptionMapping::getAsMember);
		var reason = e.getOption(REASON, OptionMapping::getAsString);

		if (memberToKick == null) {
			e.reply("The provided member is invalid please double check the name of the member").setEphemeral(true)
					.queue();
			return;
		}

		if (reason.length() > 512) {
			e.reply("The reason cannot be longer than 512 characters.").setEphemeral(true).queue();
			return;
		}

		if (member.canInteract(memberToKick)) {
			kickMember(member, reason, e.getJDA(), guild, e);
			guild.kick(memberToKick, reason).queue(success -> e
					.reply("The member " + memberToKick.getUser().getAsTag() + " has been kicked from the server.")
					.setEphemeral(true).queue(),
					failure -> e.reply(
							"The member " + memberToKick.getUser().getAsTag() + " could not be kicked from the server.")
							.setEphemeral(true).queue());
		} else {
			e.reply("You cannot kick a member that is higher or equal to you.").setEphemeral(true).queue();
		}
	}

	private static void kickMember(@NotNull Member member, String reason, @NotNull JDA jda, Guild guild,
			SlashCommandInteractionEvent event) {
		jda.openPrivateChannelById(member.getUser().getIdLong())
				.flatMap(channel -> channel
						.sendMessage("You have been kicked from the server for the following reason: " + reason + "\n"
								+ "If you believe this was done in error, please contact a server administrator." + "\n"
								+ "This message was sent by the bot, please do not reply to this message."))
				.mapToResult().flatMap(result -> guild.kick(member, reason))
				.flatMap(success -> event.reply("I have kicked " + member.getUser().getAsTag()
						+ " from the server for the following reason: " + reason))
				.queue();
	}
}
