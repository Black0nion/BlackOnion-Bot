package com.github.black0nion.blackonionbot.systems.giveaways;

import java.util.Date;

public class Giveaway {
	private final Date endDate;
	private final long channelId;
	private final long messageId;
	private final long createrId;
	private final long guildId;
	private final String item;
	private final int winners;
	
	public Giveaway(final Date endDate, final long messageId, final long channelId, final long createrId, final long guildId, final String item, final int winners) {
		this.endDate = endDate;
		this.messageId = messageId;
		this.channelId = channelId;
		this.guildId = guildId;
		this.item = item;
		this.winners = winners;
		this.createrId = createrId;
	}

	public Date getEndDate() {
		return endDate;
	}

	public long getMessageId() {
		return messageId;
	}

	public long getChannelId() {
		return channelId;
	}

	public long getGuildId() {
		return guildId;
	}

	public String getItem() {
		return item;
	}

	public int getWinners() {
		return winners;
	}
	
	public long getCreaterId() {
		return createrId;
	}

	@Override
	public String toString() {
		return "Giveaway [endDate=" + endDate + ", messageId=" + messageId + ", channelId=" + channelId + ", guildId="
				+ guildId + ", item=" + item + ", winners=" + winners + "]";
	}
}
