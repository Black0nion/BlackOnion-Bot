/**
 *
 */
package com.github.black0nion.blackonionbot.systems.logging;

import java.util.HashMap;

import com.github.black0nion.blackonionbot.utils.BlackIncrementor;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author _SIM_
 */
public class EventEndpoint extends ListenerAdapter {

    public static final HashMap<Long, BlackIncrementor> messagesPerGuild = new HashMap<>();

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
	final BlackIncrementor incrementor = messagesPerGuild.get(event.getGuild().getIdLong());
	if (incrementor != null) {
	    incrementor.increment();
	} else {
	    messagesPerGuild.put(event.getGuild().getIdLong(), new BlackIncrementor());
	}
    }
}