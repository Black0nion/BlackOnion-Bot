package com.github.black0nion.blackonionbot.commands.misc;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class IpLocationCommand extends SlashCommand {

    public IpLocationCommand() {
	this.setData(new CommandData("iplocation", "Displays information about a IPv4 or IPv6").addOption(OptionType.STRING, "ip", "The IP in IPv4 or IPv6 format", true));
    }

    private static final InetAddressValidator validator = InetAddressValidator.getInstance();

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final String ip = e.getOptionsByType(OptionType.STRING).get(0).getAsString().trim();
	if (!validator.isValid(ip)) {
	    cmde.error("notavalidip", "pleaseentervalidip");
	    return;
	}
	e.replyEmbeds(cmde.loading().build()).flatMap(InteractionHook::retrieveOriginal).queue(msg -> {
	    try {
		Unirest.setTimeouts(0, 0);
		final HttpResponse<String> response = Unirest.get("https://ipapi.co/" + ip + "/json/").asString();
		final JSONObject object = new JSONObject(response.getBody());
		if (object.has("error") && object.getBoolean("error")) if (object.getBoolean("reserved")) {
		    cmde.error("ipnotpublic", "pleaseentervalidip");
		    return;
		} else {
		    cmde.sendPleaseUse();
		    return;
		}
		msg.editMessageEmbeds(cmde.success().setTitle("IP Geolocation", "https://ipapi.co").addField(object.getString("city") + ", " + object.getString("region") + " (" + object.getString("region_code") + ", " + object.getString("country") + ")", object.getString("timezone"), false).build()).queue();
	    } catch (final Exception ex) {
		ex.printStackTrace();
		cmde.exception();
	    }
	});
    }
}