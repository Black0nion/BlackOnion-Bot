package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class KickCommand extends SlashCommand {

    public KickCommand() {
	this.setData(new CommandData("kick", "Kick a User")
		.addOption(OptionType.USER, "user", "The user to kick", true)
		.addOption(OptionType.STRING, "reason", "The reason to kick the user with", false))
	.setRequiredPermissions(Permission.KICK_MEMBERS)
	.setRequiredBotPermissions(Permission.KICK_MEMBERS);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final Member userToKick = e.getOptionsByType(OptionType.USER).get(0).getAsMember();
	if (userToKick == null) {
	    cmde.error("notamember", "cantkicknotmember");
	    return;
	}

	if (member.canInteract(userToKick)) {
	    guild.kick(userToKick).queue();
	    final String kickMessage = e.getOption("reason") != null ? e.getOption("reason").getAsString() : cmde.getTranslation("yougotkicked");
	    cmde.success("kick", "usergotkicked", "message", new Placeholder("msg", kickMessage));
	    userToKick.getUser().openPrivateChannel().queue(c -> {
		cmde.error("kick", "yougotkicked", "message", new Placeholder("msg", kickMessage));
	    });
	} else {
	    cmde.error("usertoopowerful", "loweruserthanu");
	    return;
	}
    }
}