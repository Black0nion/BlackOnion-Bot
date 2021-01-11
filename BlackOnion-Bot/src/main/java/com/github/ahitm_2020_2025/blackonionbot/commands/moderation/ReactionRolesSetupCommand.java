package com.github.ahitm_2020_2025.blackonionbot.commands.moderation;

import java.util.List;

import com.github.ahitm_2020_2025.blackonionbot.SQL.LiteSQL;
import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.enums.CommandVisibility;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ReactionRolesSetupCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		args = message.getContentDisplay().split(" ");
		
		if (args.length >= 5) {
			List<TextChannel> channels = message.getMentionedChannels();
			List<Role> roles = message.getMentionedRoles();
			
			if (!channels.isEmpty()) {
				TextChannel tc = channels.get(0);
				Role role = roles.get(0);
				String messageIDString = args[2];
				
				try {
					long messageID = Long.parseLong(messageIDString);
					
					String emote = args[3];
					tc.addReactionById(messageID, emote).queue();
					
					LiteSQL.onUpdate("INSERT INTO reactroles(guildid, channelid, messageid, emote, roleid) VALUES(" + 
					e.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + messageID + ", '" + emote + "', " + role.getIdLong() + ")");
				} catch (NumberFormatException ex) {
					channel.sendMessage("Bitte benutze " + getSyntax()).queue();
				}
			}
		}
	}
	
	@Override
	public String getSyntax() {
		return "<#channel> <message id> <emote> <role mentioned>";
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}
	
	@Override
	public CommandVisibility getVisisbility() {
		return CommandVisibility.HIDDEN;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"reactionrole", "rr"};
	}

}
