package com.github.black0nion.blackonionbot.api.impl.get;

import com.github.black0nion.blackonionbot.api.BlackSession;
import com.github.black0nion.blackonionbot.api.routes.IGetRoute;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.Map;

public class WhoAmI implements IGetRoute {

  @Override
  public Object handle(Context ctx, JSONObject body, Map<String, String> headers,
      @Nullable BlackSession session, DiscordUser user) throws Exception {
    return user.getUserAsJson();
  }

  @Override
  public @Nonnull String url() {
    return "whoami";
  }

  @Override
  public boolean requiresLogin() {
    return true;
  }
}
