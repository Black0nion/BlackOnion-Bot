/**
 *
 */
package com.github.black0nion.blackonionbot.systems.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.utils.DiscordUser;

/**
 * @author _SIM_
 */
public class DiscordLogin {

    private static final List<String> requiredKeyNames = Arrays.asList("id", "username", "avatar", "discriminator", "locale", "mfa_enabled");

    private final @Nullable DiscordUser user;
    private final @Nullable SessionError error;

    private static final HashMap<SessionError, DiscordLogin> errors = new HashMap<>();

    private DiscordLogin(final DiscordUser user) {
	this.user = user;
	this.error = null;
    }

    public static DiscordLogin success(final JSONObject doc) {
	if (!doc.keySet().containsAll(requiredKeyNames)) return error(SessionError.INVALID_USER);
	return new DiscordLogin(new DiscordUser(doc.getLong("id"), doc.getString("username"), doc.getString("avatar"), doc.getString("discriminator"), doc.getString("locale"), doc.getBoolean("mfa_enabled")));
    }

    private DiscordLogin(final SessionError error) {
	this.user = null;
	this.error = error;
    }

    public static DiscordLogin error(final SessionError error) {
	if (errors.containsKey(error)) return errors.get(error);
	else {
	    final DiscordLogin login = new DiscordLogin(error);
	    errors.put(error, login);
	    return login;
	}
    }

    /**
     * @return the error
     */
    public SessionError getError() {
	return error;
    }

    /**
     * @return the user
     */
    public DiscordUser getUser() {
	return user;
    }

    public boolean success() {
	return error == null;
    }

    /**
     * @author _SIM_
     */
    public static enum SessionError {

	TIMED_OUT(4408, "Mach dich aus meiner Leitung raus, du Birne!"), SESSION_NOT_CREATED(4410, "The session couldn't be created."), DISCORD_ERROR(4411, "Contacting Discord failed!"), INVALID_DISCORD_CODE(4412, "Invalid Code."), INVALID_SCOPES(4413), INVALID_USER(4414), EXCEPTION(4450);

	private final int code;
	private final @Nullable String description;

	private SessionError(final int code) {
	    this.code = code;
	    this.description = null;
	}

	private SessionError(final int code, final String description) {
	    this.code = code;
	    this.description = description;
	}

	/**
	 * @return the assigned code
	 */
	public int getCode() {
	    return code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
	    return description;
	}
    }
}