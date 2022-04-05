package com.github.black0nion.blackonionbot.oauth;

import com.google.common.base.Suppliers;
import io.mokulu.discord.oauth.DiscordAPI;
import io.mokulu.discord.oauth.model.Guild;
import io.mokulu.discord.oauth.model.TokensResponse;
import io.mokulu.discord.oauth.model.User;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.github.black0nion.blackonionbot.oauth.OAuthUtils.OAUTH_HANDLER;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DiscordUser {

  private static final int EXPIRATION_TIME = 604800; // 7 days

  private String accessToken;
  private String refreshToken;
  private long expiresAt;
  private DiscordAPI api;
  private User user;

  public DiscordUser(String accessToken, String refreshToken, DiscordAPI api)
      throws IllegalArgumentException, IOException {
    if (accessToken == null || api == null) {
      throw new IllegalArgumentException("Invalid parameters!");
    } else if (!OAuthUtils.TOKEN_PATTERN.matcher(accessToken).matches()
        || (refreshToken != null && !OAuthUtils.TOKEN_PATTERN.matcher(refreshToken).matches())) {
      throw new IllegalArgumentException("Invalid tokens!");
    }
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.api = api;
    this.user = api.fetchUser();
    this.expiresAt = System.currentTimeMillis() + EXPIRATION_TIME;
  }


  public User getUser() {
    return user;
  }

  public JSONObject getUserAsJson() {
    return new JSONObject().put("id", user.getId()).put("username", user.getUsername())
        .put("avatar", user.getAvatar()).put("discriminator", user.getDiscriminator())
        .put("mfa_enabled", user.getMfaEnabled()).put("locale", user.getLocale())
        .put("verified", user.getVerified()).put("email", user.getEmail())
        .put("flags", user.getFlags()).put("premium_type", user.getPremiumType());
  }

  public DiscordAPI getAPI() {
    return api;
  }

  public DiscordUser refreshAll() throws IOException {
    refreshTokens();
    refreshUser();
    return this;
  }

  public DiscordUser refreshTokens() throws IOException {
    TokensResponse tokensResponse = OAUTH_HANDLER.refreshTokens(this.refreshToken);
    this.accessToken = tokensResponse.getAccessToken();
    this.refreshToken = tokensResponse.getRefreshToken();
    this.api = new DiscordAPI(this.accessToken);
    this.expiresAt = System.currentTimeMillis() + EXPIRATION_TIME;
    return this;
  }

  public DiscordUser refreshUser() throws IOException {
    this.user = api.fetchUser();
    return this;
  }

  private final Supplier<List<Guild>> reloadSupplier = Suppliers.memoizeWithExpiration(() -> {
    try {
      return this.api.fetchGuilds();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }, 10, TimeUnit.MINUTES);

  /**
   * @return the guilds as discord's json response or null
   * @throws IOException if the request fails - this is a UncheckedIOException because Suppliers
   *         don't allow you to throw Exceptions
   */
  @SuppressWarnings("RedundantThrows")
  @Nullable
  public List<Guild> getGuilds() throws IOException {
    return reloadSupplier.get();
  }
}
