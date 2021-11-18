package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class KickCommand extends Command {

    public KickCommand() {
	this.setCommand("kick", "yeet").setSyntax("<@User> [reason]").setRequiredArgumentCount(1).setRequiredPermissions(Permission.KICK_MEMBERS).setRequiredBotPermissions(Permission.KICK_MEMBERS);
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final List<Member> mentionedMembers = message.getMentionedMembers();
	if (mentionedMembers.size() == 0) {
	    cmde.error("wrongargument", "tagornameuser");
	    return;
	} else {
	    final BlackMember userToKick = BlackMember.from(mentionedMembers.get(0));

	    if (member.canInteract(userToKick)) {
		guild.kick(userToKick).queue();
		final String kickMessage = args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : cmde.getTranslation("yougotkicked");
		cmde.success("kick", "usergotkicked", "message", new Placeholder("msg", kickMessage));
		userToKick.getBlackUser().openPrivateChannel().queue(c -> {
		    cmde.error("kick", "yougotkicked", "message", new Placeholder("msg", kickMessage));
		});
	    } else {
		cmde.error("usertoopowerful", "loweruserthanu");
		return;
	    }
	}
    }
}