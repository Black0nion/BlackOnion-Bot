package com.github.black0nion.blackonionbot.commands.slash.impl.moderation;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.utils.AwaitDone;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BanCommand extends SlashCommand {
	private static final String USER = "user";
	private static final String REASON = "reason";
	private static final String DEL_DAYS = "del_days";

	private static final Placeholder ACTION = new Placeholder("action", "banned");

	public BanCommand() {
		super(builder(Commands.slash("ban", "Used to ban an user from the server.")
				.addOption(OptionType.USER, USER, "The user to ban.", true)
				.addOption(OptionType.STRING, REASON, "The reason for the ban.", false)
				.addOptions(new OptionData(OptionType.INTEGER, DEL_DAYS, "The amount of days to delete messages of the user.", false)
					.addChoices(DELETION_DAYS))
				.addOptions(Utils.getDurationOptions("ban"))
			)
			.setRequiredPermissions(Permission.BAN_MEMBERS)
			.setRequiredBotPermissions(Permission.BAN_MEMBERS));
	}

	//region Deletion Days
	private static final Command.Choice[] DELETION_DAYS = {
		new Command.Choice("None", 0),
		new Command.Choice("One Day", 1),
		new Command.Choice("Two Days", 2),
		new Command.Choice("Three Days", 3),
		new Command.Choice("Four Days", 4),
		new Command.Choice("Five Days", 5),
		new Command.Choice("Six Days", 6),
		new Command.Choice("Seven Days", 7)
	};
	//endregion

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var userOptionMapping = e.getOption(USER);
		var banUser = Objects.requireNonNull(userOptionMapping, "The user is null").getAsUser();
		var banMember = userOptionMapping.getAsMember();
		@Nullable var reason = e.getOption(REASON, OptionMapping::getAsString);
		@Nullable var delDays = e.getOption(DEL_DAYS, OptionMapping::getAsInt);

		if (reason != null && reason.length() > 512) {
			cmde.send("reasonoption");
			return;
		}

		guild.retrieveBan(banUser).queue(
			exists -> cmde.error("alreadybanned", "bannedfor", new Placeholder("user", banUser.getAsTag()), new Placeholder("reason", exists.getReason())),
			doesntExist -> guild.ban(banMember != null ? banMember : banUser, delDays != null ? delDays : 0, TimeUnit.DAYS).reason("[" + author.getId() + "]" + (reason != null ? " " + reason : ""))
				.queue(success -> {
					AwaitDone<InteractionHook> await = new AwaitDone<>();
					cmde.send(reason != null ? "idid" : "ididnoreason", await::done, new Placeholder("user", banUser.getAsMention()), new Placeholder("reason", reason), ACTION);
					// prevent messaging random users
					if (banMember != null) {
						banUser.openPrivateChannel().queue(
							privateChannel -> {
								String message = cmde.getTranslation(reason != null ? "yougot" : "yougotnoreason", new Placeholder("guild", guild.getName()), new Placeholder("reason", reason), ACTION);
								privateChannel.sendMessage(message).queue(null, err -> Utils.getCantSendHandler(await, message, cmde));
							}
						);
					}
				})
		);
	}
}
