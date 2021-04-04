package com.github.black0nion.blackonionbot.systems;


/**
 * @author _SIM_
 * A wrapper for the Information of a Webhook including <b>Guild ID -> Channel ID -> Webhook ID</b>
 */
public class WebhookInformation implements Comparable<WebhookInformation> {
	
	public long guildid;
	public long channelid;
	public long webhookid;
	
	public WebhookInformation(long guildid, long channelid, long webhookid) {
		this.guildid = guildid;
		this.channelid = channelid;
		this.webhookid = webhookid;
	}

	/**
	 * Will return 0 if there is a webhook existing for that channel
	 */
	@Override
	public int compareTo(WebhookInformation info) {
		return (this.guildid == info.guildid && this.channelid == info.channelid) ? 0 : -1;
	}
}
