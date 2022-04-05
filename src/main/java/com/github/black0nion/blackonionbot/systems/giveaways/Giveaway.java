package com.github.black0nion.blackonionbot.systems.giveaways;

import java.util.Date;

public record Giveaway(Date endDate, long messageId, long channelId, long createrId, long guildId,
    String item, int winners) {


  @Override
  public String toString() {
    return "Giveaway [endDate=" + endDate + ", messageId=" + messageId + ", channelId=" + channelId
        + ", guildId=" + guildId + ", item=" + item + ", winners=" + winners + "]";
  }
}
