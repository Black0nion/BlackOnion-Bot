package com.github.black0nion.blackonionbot.api.impl.post;

import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import org.json.JSONObject;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.api.routes.IPostRoute;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import spark.Request;
import spark.Response;

// TODO: this is absolutely terrible
public class Vote implements IPostRoute {

	@Override
	public String url() {
		return "vote";
	}

	@Override
	public String handle(final Request request, final Response response, final JSONObject body, final HashMap<String, String> headers, final BlackSession u, DiscordUser discordUser) {
		final String ip = request.headers("X-Real-IP") != null ? request.headers("X-Real-IP") : request.ip();
		if (!ip.equals("159.203.105.187") || !headers.containsKey("authorization") || !headers.get("authorization").equals(Config.topgg_auth)) {
			response.status(401);
			return "get outta here";
		}

		final long channelId = Config.vote_channel;
		if (channelId != -1) {
			final TextChannel channel = Bot.getInstance().getJda().getTextChannelById(channelId);
			if (channel != null) {
				final String userid = body.getString("user");
				Objects.requireNonNull(Bot.getInstance().getJda().getGuildById(BotInformation.supportServer)).retrieveMemberById(userid).queue(member -> {
					final User user = member.getUser();
					channel
						.sendMessageEmbeds(new TranslatedEmbed()
							.setColor(EmbedUtils.BLACK_ONION_COLOR)
							.setDescription("**" + BlackUser.from(user).getEscapedEffectiveName() + "** (" + user
								.getId() + ") just voted for me on [top.gg](https://top.gg/bot/795225954355249180)!")
							.setFooter("Thanks for voting!", user.getEffectiveAvatarUrl())
							.setTimestamp(Instant.now())
							.build())
						.queue();
				}, bruh -> Bot.getInstance().getJda().retrieveUserById(userid).queue(user -> channel
					.sendMessageEmbeds(new TranslatedEmbed()
						.setColor(EmbedUtils.BLACK_ONION_COLOR)
						.setDescription("**" + BlackUser.from(user).getEscapedEffectiveName() + "** (" + user
							.getId() + ") just voted for me on [top.gg](https://top.gg/bot/795225954355249180)!")
						.setFooter("Thanks for voting!", user.getEffectiveAvatarUrl())
						.setTimestamp(Instant.now())
						.build())
					.queue()));
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