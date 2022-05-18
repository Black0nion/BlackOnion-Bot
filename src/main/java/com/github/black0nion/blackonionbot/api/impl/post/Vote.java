package com.github.black0nion.blackonionbot.api.impl.post;

import com.github.black0nion.blackonionbot.api.routes.IPostRoute;
import com.github.black0nion.blackonionbot.api.sessions.RestSession;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

// TODO: this is absolutely terrible
public class Vote implements IPostRoute {

	@Override
	public @Nonnull String url() {
		return "vote";
	}

	@Override
	public Object handle(Context ctx, JSONObject body, Map<String, String> headers, @Nullable RestSession session, DiscordUser dcUser) throws Exception {
		final String ip = ctx.header("X-Real-IP") != null ? ctx.header("X-Real-IP") : ctx.ip();
		assert ip != null;
		if (!ip.equals("159.203.105.187") || !headers.get("authorization").equals(Config.getInstance().getTopggAuth())) {
			throw new UnauthorizedResponse();
		}

		final long channelId = Config.getInstance().getVoteChannel();
		if (channelId != -1) {
			final TextChannel channel = Bot.getInstance().getJda().getTextChannelById(channelId);
			if (channel != null) {
				final String userid = body.getString("user");
				Objects.requireNonNull(Bot.getInstance().getJda().getGuildById(BotInformation.SUPPORT_SERVER)).retrieveMemberById(userid).queue(member -> {
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
	public String[] requiredHeaders() {
		return new String[] { "authorization" };
	}

	@Override
	public boolean isJson() {
		return false;
	}
}