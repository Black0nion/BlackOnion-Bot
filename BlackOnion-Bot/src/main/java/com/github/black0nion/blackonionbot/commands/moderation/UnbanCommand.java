package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class UnbanCommand extends SlashCommand {

    public UnbanCommand() {
	this.setData(new CommandData("unban", "Unban a User")
		.addOption(OptionType.USER, "user", "The user to unban", true))
	.setRequiredPermissions(Permission.BAN_MEMBERS).setRequiredBotPermissions(Permission.BAN_MEMBERS);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final User bannedUser = e.getOptionsByType(OptionType.USER).get(0).getAsUser();
	guild.retrieveBan(bannedUser).queue(ban -> {
	    cmde.success("unban", "userunbanned", "bannedfor", new Placeholder("reason", "`" + (ban.getReason() != null ? ban.getReason().replace("`", "\\`") : "unknown") + "`"));
	    guild.unban(bannedUser).queue();
	}, err -> {
	    cmde.error("bannotfound", "usernotbanned");
	});
    }
}