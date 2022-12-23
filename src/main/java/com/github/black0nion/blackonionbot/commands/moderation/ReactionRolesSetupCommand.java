package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.exception.CauseWrapperException;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.regex.Matcher;

import static java.util.Objects.requireNonNull;

public class ReactionRolesSetupCommand extends SlashCommand {

	private static final String CREATE_REACTION = "create";
	private static final String CREATE_REACTION_CHANNEL = "channel";
	private static final String CREATE_REACTION_MESSAGE_ID = "message_id";
	private static final String CREATE_REACTION_EMOTE = "emoji";
	private static final String CREATE_REACTION_ROLE = "role";
	private static final String REMOVE_REACTION = "remove";
	private static final String REMOVE_REACTION_CHANNEL = "channel";
	private static final String REMOVE_REACTION_MESSAGE_ID = "message_id";
	private static final String REMOVE_REACTION_EMOTE = "emoji";
	private static final String REMOVE_REACTION_ROLE = "role";

	private final SQLHelperFactory sql;

	public ReactionRolesSetupCommand(SQLHelperFactory sqlHelperFactory) {
		super(builder(
			Commands.slash("reactionroles", "Used to set up a reaction role.").addSubcommands(
				new SubcommandData(CREATE_REACTION, "Used to create a reaction role.")
					.addOption(OptionType.CHANNEL, CREATE_REACTION_CHANNEL, "The channel to create the reaction role in.", true)
					.addOption(OptionType.STRING, CREATE_REACTION_MESSAGE_ID, "The message to create the reaction role on.", true)
					.addOption(OptionType.STRING, CREATE_REACTION_EMOTE, "The emoji id to create the reaction role with.", true)
					.addOption(OptionType.ROLE, CREATE_REACTION_ROLE, "The role to give to the userid when they react.", true),
				new SubcommandData(REMOVE_REACTION, "Used to remove a reaction role.")
					.addOption(OptionType.CHANNEL, REMOVE_REACTION_CHANNEL, "The channel to remove the reaction role from.", true)
					.addOption(OptionType.NUMBER, REMOVE_REACTION_MESSAGE_ID, "The message to remove the reaction role from.", true)
					.addOption(OptionType.STRING, REMOVE_REACTION_EMOTE, "The emoji id to remove the reaction role with.", true)
					.addOption(OptionType.ROLE, REMOVE_REACTION_ROLE, "The role to remove from the userid when they react.", true)))
			.setRequiredPermissions(Permission.MANAGE_ROLES)
			.setRequiredBotPermissions(Permission.MANAGE_ROLES, Permission.MESSAGE_ADD_REACTION));
		this.sql = sqlHelperFactory;
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) throws SQLException {
		ChannelType channelType = cmde.getOption(REMOVE_REACTION_CHANNEL, OptionMapping::getChannelType);

		if (!requireNonNull(channelType).isMessage()) {
			cmde.send("nottextchannel");
			return;
		}

		var messageId = cmde.getOption(REMOVE_REACTION_MESSAGE_ID, OptionMapping::getAsLong);

		String emote = cmde.getOption(CREATE_REACTION_EMOTE, OptionMapping::getAsString);
		// TODO: test if this works
		Matcher emojiMatcher = Message.MentionType.EMOJI.getPattern().matcher(emote);
		Emoji emoji = emojiMatcher.matches()
			? Emoji.fromCustom(emojiMatcher.group(1), Long.parseLong(emojiMatcher.group(2)), emote.startsWith("<a:"))
			: Emoji.fromUnicode(emote);

		var role = cmde.getOption(CREATE_REACTION_ROLE, OptionMapping::getAsRole);

		var textChannel = cmde.getOption(CREATE_REACTION_CHANNEL, OptionMapping::getAsChannel).asTextChannel();
		try {
			textChannel.retrieveMessageById(messageId).queue(
				success -> {
					try {
						String emojiStr = Utils.serializeEmoji(emoji);
						switch (cmde.getSubcommandName()) {
							case CREATE_REACTION -> createReactionRole(cmde, textChannel, messageId, emoji, emojiStr, role);
							case REMOVE_REACTION -> removeReactionRole(cmde, textChannel, messageId, emoji, emojiStr, role);
							default -> cmde.send("invalidsubcommand");
						}
					} catch (SQLException ex) {
						throw new CauseWrapperException(ex);
					}
				}, error -> cmde.send("messagecouldntbefound")
			);
		} catch (CauseWrapperException ex) {
			throw (SQLException) ex.getCause();
		}
	}

	public void createReactionRole(@NotNull SlashCommandEvent cmde, TextChannel textChannel, long messageId, Emoji emoji, String emojiStr, Role role) throws SQLException {
		textChannel.addReactionById(messageId, emoji).queue();

		if (entryExists(cmde.getGuild(), messageId, emoji.getAsReactionCode(), role, textChannel)) {
			cmde.error("alreadyexisting", "thisalreadyexisting");
			return;
		}

		if (sql.run("INSERT INTO reaction_roles (guild_id, role_id, channel_id, message_id, emoji) VALUES (?, ?, ?, ?, ?);",
				cmde.getGuild().getIdLong(), role.getIdLong(), textChannel.getIdLong(), messageId, emojiStr)) {
			cmde.success("reactionrolecreated", "reactionrolecreatedinfo",
				new Placeholder("emote", emoji.getFormatted()),
				new Placeholder("role", role.getAsMention()));
		} else {
			cmde.send("erroroccurred");
			logDebugMessage(cmde, textChannel, messageId, emoji, role);
		}
	}

	public void removeReactionRole(@NotNull SlashCommandEvent cmde, @NotNull TextChannel textChannel, long messageId, Emoji emoji, String emojiStr, Role role) throws SQLException {
		textChannel.retrieveMessageById(messageId).queue(
			success -> success.clearReactions(emoji).queue(),
			fail -> cmde.error("wrongargument", "emotenotfound"));

		if (!entryExists(cmde.getGuild(), messageId, emoji.getAsReactionCode(), role, textChannel)) {
			cmde.send("entrynotfound");
			return;
		}

		try (SQLHelper sqlHelper = sql.create("DELETE FROM reaction_roles WHERE guild_id = ? AND channel_id = ? AND message_id = ? AND emoji_id = ? AND role_id = ?",
				cmde.getGuild().getIdLong(),
				textChannel.getIdLong(),
				messageId,
				emojiStr,
				role.getIdLong())) {
			if (sqlHelper.run()) {
				cmde.success("entrydeleted", "reactionroledeleted");
			} else {
				cmde.send("erroroccurred");
				logDebugMessage(cmde, textChannel, messageId, emoji, role);
			}
		}
	}

	private void logDebugMessage(SlashCommandEvent cmde, TextChannel textChannel, long messageId, Emoji emoji, Role role) {
		LoggerFactory.getLogger(ReactionRolesSetupCommand.class).error("Failed to delete entry from database!" +
				"Guild: {}, Channel: {}, Message: {}, Emoji: {}, Role: {}",
			cmde.getGuild().getDebugMessage(), textChannel.getName() + "(C:" + textChannel.getIdLong() + ")", messageId, emoji.getFormatted(), role.getName() + "(R:" + role.getIdLong() + ")");
	}

	private boolean entryExists(@NotNull Guild guild, Long messageId, String emoji, Role role, TextChannel textChannel) throws SQLException {
		return sql.anyMatch("SELECT * FROM reaction_roles WHERE guild_id = ? AND channel_id = ? AND message_id = ? AND emoji = ? AND role_id = ?",
				guild.getIdLong(), textChannel.getIdLong(), messageId, emoji, role.getIdLong());
	}
}
