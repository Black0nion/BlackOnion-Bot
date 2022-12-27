package com.github.black0nion.blackonionbot.systems.customcommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CustomCommandResponsePlaintext implements CustomCommandResponse {

	private final String response;

	public CustomCommandResponsePlaintext(String response) {
		this.response = response;
	}

	@Override
	public void handle(MessageReceivedEvent event, boolean reply) {
		if (reply) {
			event.getMessage().reply(this.response).queue();
		} else {
			event.getChannel().sendMessage(this.response).queue();
		}
	}
}
