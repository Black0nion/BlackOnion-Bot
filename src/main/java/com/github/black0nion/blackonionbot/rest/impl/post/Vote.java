package com.github.black0nion.blackonionbot.rest.impl.post;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.rest.api.IPostRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Objects;

// TODO: this is absolutely terrible
public class Vote implements IPostRoute {

	private final Config config;

	public Vote(Config config) {
		this.config = config;
	}

	@Override
	public @Nonnull String url() {
		return "vote";
	}

	@Override
	public Object handle(Context ctx, JSONObject body, @Nullable RestSession session, DiscordUser dcUser) throws Exception {
		final String ip = ctx.header("X-Real-IP") != null ? ctx.header("X-Real-IP") : ctx.ip();
		assert ip != null;
		if (!ip.equals("159.203.105.187") || !config.getTopggAuth().equals(ctx.header("authorization"))) {
			throw new UnauthorizedResponse();
		}

		final long channelId = config.getVoteChannel();
		if (channelId != -1) {
			final TextChannel channel = Bot.getInstance().getJDA().getTextChannelById(channelId);
			if (channel != null) {
				final String userid = body.getString("user");
				Objects.requireNonNull(Bot.getInstance().getJDA().getGuildById(config.getDevGuild())).retrieveMemberById(userid).queue(member -> {
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
				}, bruh -> Bot.getInstance().getJDA().retrieveUserById(userid).queue(user -> channel
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
	public String[] requiredHeaders() {
		return new String[] { "authorization" };
	}

	@Override
	public boolean isJson() {
		return false;
	}
}
