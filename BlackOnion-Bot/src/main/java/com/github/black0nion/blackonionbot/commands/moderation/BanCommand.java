package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class BanCommand extends SlashCommand {

    public BanCommand() {
	this.setData(new CommandData("ban", "Bans a user from the server")
		.addOption(OptionType.USER, "user", "The user to ban", true)
		.addOption(OptionType.STRING, "message", "The message to ban the user with", false))
	.setRequiredPermissions(Permission.BAN_MEMBERS)
	.setRequiredBotPermissions(Permission.BAN_MEMBERS);
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final User userToBan = e.getOptionsByType(OptionType.USER).get(0).getAsUser();
	final @Nullable Member memberToBan = e.getOptionsByType(OptionType.USER).get(0).getAsMember();

	if (memberToBan == null || member.canInteract(memberToBan)) {
	    final Consumer<Object> success = done -> {
		String banMessage = author.getLanguage().getTranslationNonNull("banned");
		final List<OptionMapping> options = e.getOptionsByType(OptionType.STRING);
		if (!options.isEmpty()) {
		    banMessage = options.get(0).getAsString().replace("`", "\\`");
		    guild.ban(userToBan, 0, banMessage).queue();
		} else {
		    guild.ban(userToBan, 0).queue();
		}
		final String finalBanMessage = banMessage;
		cmde.reply(cmde.success().setTitle("Ban").addField("usergotbanned", cmde.getTranslation("message", new Placeholder("msg", banMessage)), false));
		userToBan.openPrivateChannel().queue(c -> {
		    c.sendMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild).setTitle("Ban").addField(author.getLanguage().getTranslation("yougotbanned", new Placeholder("guild", guild.getName())), author.getLanguage().getTranslation("message", new Placeholder("msg", finalBanMessage)), false).build()).queue();
		});
	    };
	    guild.unban(userToBan).queue(success, success);
	} else {
	    cmde.error("usertoopowerful", "loweruserthanu");
	    return;
	}
    }
}