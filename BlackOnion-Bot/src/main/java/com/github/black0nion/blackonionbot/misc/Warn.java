/**
 *
 */
package com.github.black0nion.blackonionbot.misc;

import javax.annotation.Nullable;

/**
 * @author _SIM_
 */
public class Warn {

    private final long issuer;
    private final long user;
    private final long guild;
    private final long date;
    @Nullable
    private final String reason;

    public Warn(final long guild, final long issuer, final long warned, final long date) {
	this.guild = guild;
	this.issuer = issuer;
	this.user = warned;
	this.date = date;
	this.reason = null;
    }

    public Warn(final long guild, final long issuer, final long warned, final long date, final String reason) {
	this.guild = guild;
	this.issuer = issuer;
	this.user = warned;
	this.date = date;
	this.reason = reason;
    }

    /**
     * @return the issuer user id
     */
    public long getIssuer() {
	return issuer;
    }

    /**
     * @return the reason the user got warned for
     */
    public String getReason() {
	return reason != null ? reason : "NONE";
    }

    /**
     * @return the reason, null if undefined
     */
    public String getReasonNullable() {
	return reason;
    }

    /**
     * @return the warned user id
     */
    public long getWarned() {
	return user;
    }

    /**
     * @return the guild
     */
    public long getGuild() {
	return guild;
    }

    /**
     * @return the date the warn was issued
     */
    public long getDate() {
	return date;
    }
}