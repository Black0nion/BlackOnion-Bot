package com.github.black0nion.blackonionbot.systems.customcommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface CustomCommandResponse {

	void handle(MessageReceivedEvent event, boolean reply);
}
