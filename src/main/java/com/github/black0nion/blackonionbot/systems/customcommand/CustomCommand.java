package com.github.black0nion.blackonionbot.systems.customcommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CustomCommand {

	private final long guildId;
	private final String command;
	private final CustomCommandResponse response;
	private boolean reply;

	public CustomCommand(final long guildId, final String command, final CustomCommandResponse response) {
		this.guildId = guildId;
		this.command = command.toLowerCase();
		this.response = response;
	}

	public CustomCommand setReply(final boolean reply) {
		this.reply = reply;
		return this;
	}

	public String getCommand() {
		return this.command;
	}

	public long getGuildId() {
		return guildId;
	}

	public void handle(final MessageReceivedEvent event) {
		this.response.handle(event, reply);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || this.getClass() != obj.getClass()) return false;

		final CustomCommand cmd = (CustomCommand) obj;
		return cmd.getCommand().equalsIgnoreCase(this.getCommand()) && cmd.getGuildId() == this.getGuildId();
	}
}
