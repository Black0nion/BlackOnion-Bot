package com.github.black0nion.blackonionbot.commands.misc;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class IpLocationCommand extends Command {
	
	public IpLocationCommand() {
		this.setCommand("iplocation", "ip")
			.setSyntax("<public ipv4 / ipv6>")
			.setRequiredArgumentCount(2);
	}
	
	private static final InetAddressValidator validator = InetAddressValidator.getInstance();

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
			if (!validator.isValid(args[1])) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("notavalidip", "pleaseentervalidip", false).build()).queue();
				return;
			}
			MessageAction action = message.reply(cmde.loading().build());
			action.queue(msg -> {				
				try {
					Unirest.setTimeouts(0, 0);
					HttpResponse<String> response = Unirest.get("https://ipapi.co/" + args[1] + "/json/").asString();
					JSONObject object = new JSONObject(response.getBody());
					if (object.has("error") && object.getBoolean("error")) {
						if (object.getBoolean("reserved")) {
							cmde.error("ipnotpublic", "pleaseentervalidip");
							return;
						} else {					
							cmde.sendPleaseUse();
							return;
						}
					}
					msg.editMessage(cmde.success().setTitle("IP Geolocation", "https://ipapi.co").addField(object.getString("city") + ", " + object.getString("region") + " (" + object.getString("region_code") + ", " + object.getString("country") + ")", object.getString("timezone"), false).build()).queue();
				} catch (Exception ex) {
					ex.printStackTrace();
					cmde.exception();
				}
			});
	}
}