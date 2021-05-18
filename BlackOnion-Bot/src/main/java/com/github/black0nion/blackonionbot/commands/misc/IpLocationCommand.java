package com.github.black0nion.blackonionbot.commands.misc;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class IpLocationCommand implements Command {
	private static final InetAddressValidator validator = InetAddressValidator.getInstance();
	
	@Override
	public String[] getCommand() {
		return new String[] { "iplocation", "ip" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
			if (!validator.isValid(args[1])) {
				channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("notavalidip", "pleaseentervalidip", false).build()).queue();
				return;
			}
			MessageAction action = channel.sendMessage(EmbedUtils.getLoadingEmbed(author, guild).build());
			action.queue(msg -> {				
				try {
					Unirest.setTimeouts(0, 0);
					HttpResponse<String> response = Unirest.get("https://ipapi.co/" + args[1] + "/json/").asString();
					JSONObject object = new JSONObject(response.getBody());
					if (object.has("error") && object.getBoolean("error")) {
						if (object.getBoolean("reserved")) {
							channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("ipnotpublic", "pleaseentervalidip", false).build()).queue();
							return;
						} else {					
							channel.sendMessage(Utils.getWrongArgument(author, guild, this)).queue();
							return;
						}
					}
					msg.editMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle("IP Geolocation", "https://ipapi.co").addField(object.getString("city") + ", " + object.getString("region") + " (" + object.getString("region_code") + ", " + object.getString("country") + ")", object.getString("timezone"), false).build()).queue();
				} catch (Exception ex) {
					ex.printStackTrace();
					msg.editMessage(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
				}
			});
	}

	@Override
	public Category getCategory() {
		return Category.MISC;
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}

	@Override
	public String getSyntax() {
		return "<public ipv4 / ipv6>";
	}
}