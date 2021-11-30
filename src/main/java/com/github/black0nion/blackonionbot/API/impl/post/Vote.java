package com.github.black0nion.blackonionbot.API.impl.post;

import java.time.Instant;
import java.util.HashMap;

import com.github.black0nion.blackonionbot.utils.config.Config;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.API.BlackSession;
import com.github.black0nion.blackonionbot.API.PostRequest;
import com.github.black0nion.blackonionbot.blackobjects.BlackEmbed;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import spark.Request;
import spark.Response;

public class Vote extends PostRequest {

    @Override
    public String url() {
	return "vote";
    }

    @Override
    public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession u) {
	final String ip = request.headers("X-Real-IP") != null ? request.headers("X-Real-IP") : request.ip();
	if (!ip.equals("159.203.105.187") || !headers.containsKey("authorization") || !headers
		.get("authorization")
		.equals(Config.other.TOPGG_AUTH)) {
	    response.status(401);
	    return "get outta here";
	}

	final Long channelid = Config.discord.VOTE_CHANNEL;
	if (channelid != null && channelid != -1) {
	    final TextChannel channel = Bot.jda.getTextChannelById(channelid);
	    if (channel != null) {
		final String userid = body.getString("user");
		Bot.jda.getGuildById(781214445778894898L).retrieveMemberById(userid).queue(member -> {
		    final User user = member.getUser();
		    channel
			    .sendMessageEmbeds(new BlackEmbed()
				    .setColor(EmbedUtils.blackOnionColor)
				    .setDescription("**" + Utils.removeMarkdown(user.getName()) + "#" + user.getDiscriminator() + "** (" + user
					    .getId() + ") just voted for me on [top.gg](https://top.gg/bot/795225954355249180)!")
				    .setFooter("Thanks for voting!", user.getEffectiveAvatarUrl())
				    .setTimestamp(Instant.now())
				    .build())
			    .queue();
		}, bruh -> {
		    Bot.jda.retrieveUserById(userid).queue(user -> {
			channel
				.sendMessageEmbeds(new BlackEmbed()
					.setColor(EmbedUtils.blackOnionColor)
					.setDescription("**" + Utils.removeMarkdown(user.getName()) + "#" + user.getDiscriminator() + "** (" + user
						.getId() + ") just voted for me on [top.gg](https://top.gg/bot/795225954355249180)!")
					.setFooter("Thanks for voting!", user.getEffectiveAvatarUrl())
					.setTimestamp(Instant.now())
					.build())
				.queue();
		    });
		});
	    }
	}
	return "";
    }

    @Override
    public boolean requiresLogin() {
	return false;
    }

    @Override
    public String[] requiredBodyParameters() {
	return new String[] { "user" };
    }

    @Override
    public String[] requiredParameters() {
	return new String[] { "authorization" };
    }
}