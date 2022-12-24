package com.github.black0nion.blackonionbot.commands.slash.impl.moderation;

import com.github.black0nion.blackonionbot.commands.common.utils.UserRespondUtilsImpl;
import com.github.black0nion.blackonionbot.commands.common.utils.event.UserRespondUtils;
import com.github.black0nion.blackonionbot.commands.message.MessageCommand;
import com.github.black0nion.blackonionbot.commands.message.MessageCommandEvent;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.regex.Matcher;

import static com.github.black0nion.blackonionbot.systems.language.LanguageSystem.getLanguage;
import static java.util.Objects.requireNonNull;

public class ReactionRolesSetupCommand extends MessageCommand {

	private static final String CREATE_REACTION = "create";
	private static final String REMOVE_REACTION = "remove";


	private final SQLHelperFactory sql;

	public ReactionRolesSetupCommand(SQLHelperFactory sqlHelperFactory) {
		super(builder(Commands.message("Reaction Roles"))
			.setRequiredPermissions(Permission.MANAGE_ROLES)
			.setRequiredBotPermissions(Permission.MANAGE_ROLES, Permission.MESSAGE_ADD_REACTION)
		);
		this.sql = sqlHelperFactory;
	}

	@Override
	public void execute(MessageCommandEvent cmde, MessageContextInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel, Message message) throws SQLException {
		String messageId = message.getId();

		e.replyComponents(ActionRow.of(
			Button.of(ButtonStyle.SUCCESS, enrichId(CREATE_REACTION, messageId), Emoji.fromUnicode("U+2795")),
			Button.of(ButtonStyle.DANGER, enrichId(REMOVE_REACTION, messageId), Emoji.fromUnicode("U+D83DU+DEAE")),
			getCancelButton()
		)).queue();
	}

	@Override
	public void handleButtonPress(ButtonInteractionEvent event) {
		// format: command|action:messageId
		String[] split = getIdParts(event.getComponentId());

		Language language = getLanguage(event.getUser(), event.getGuild());
		(
		switch (split[0]) {
			case CREATE_REACTION -> event.editComponents(ActionRow.of(EntitySelectMenu.create(event.getComponentId(), EntitySelectMenu.SelectTarget.ROLE).build()));
			case REMOVE_REACTION -> event.replyModal(Modal.create(enrichId(REMOVE_REACTION, split[1]), language.getTranslationNonNull("delete"))
				.addActionRow(TextInput.create("emoji", "Type emoji here", TextInputStyle.SHORT).build())
				.build());
			default -> throw new IllegalStateException("Unexpected value: " + split[0]);
		}
		).queue();
	}

	@Override
	public void handleSelectMenuInteraction(GenericSelectMenuInteractionEvent<?, ?> event) {
		if (!(event instanceof EntitySelectInteractionEvent e)) return;

		// format: command|action:messageId
		String id = requireNonNull(event.getComponent().getId());
		String[] split = getIdParts(id);

		Language language = getLanguage(event.getUser(), event.getGuild());
		event.getInteraction().replyModal(Modal
			.create(enrichId(CREATE_REACTION, split[1], e.getValues().get(0).getId()), language.getTranslationNonNull("create"))
				.addActionRow(TextInput.create("emoji", "Type emoji here", TextInputStyle.SHORT).build())
				.build()
		).queue();
	}

	@Override
	public void handleModalInteraction(ModalInteractionEvent event) {
		// format: command|action:message_id:role_id
		String[] split = getIdParts(event.getModalId());

		String emojiRaw = requireNonNull(event.getValue("emoji")).getAsString();
		Matcher emojiMatcher = Message.MentionType.EMOJI.getPattern().matcher(emojiRaw);
		Emoji emoji = emojiMatcher.matches()
			? Emoji.fromCustom(emojiMatcher.group(1), Long.parseLong(emojiMatcher.group(2)), emojiRaw.startsWith("<a:"))
			: Emoji.fromUnicode(emojiRaw);

		try {
			switch (split[0]) {
				case CREATE_REACTION -> createReactionRole(
						new UserRespondUtilsImpl(event,
							BlackGuild.from(event.getGuild()),
							BlackUser.from(event.getUser())),
						event.getGuild(),
						event.getChannel().asTextChannel(),
						Long.parseLong(split[1]),
						emoji,
						Utils.serializeEmoji(emoji),
						requireNonNull(event.getGuild()).getRoleById(split[2])
					);
				case REMOVE_REACTION -> removeReactionRole(
						new UserRespondUtilsImpl(event,
							BlackGuild.from(event.getGuild()),
							BlackUser.from(event.getUser())),
						event.getGuild(),
						event.getChannel().asTextChannel(),
						Long.parseLong(split[1]),
						emoji,
						Utils.serializeEmoji(emoji)
					);
				default -> throw new IllegalStateException("Unexpected value: " + split[0]);
			}
		} catch (SQLException e) {
			logger.error("Failed to create reaction role", e);
		}
	}

	public void createReactionRole(UserRespondUtils cmde, Guild guild, TextChannel textChannel, long messageId, Emoji emoji, String emojiStr, Role role) throws SQLException {
		textChannel.addReactionById(messageId, emoji).queue();

		if (entryExists(guild, messageId, emoji.getAsReactionCode(), role, textChannel)) {
			cmde.error("alreadyexisting", "thisalreadyexisting");
			return;
		}

		if (sql.run("INSERT INTO reaction_roles (guild_id, role_id, channel_id, message_id, emoji) VALUES (?, ?, ?, ?, ?);",
				guild.getIdLong(), role.getIdLong(), textChannel.getIdLong(), messageId, emojiStr)) {
			cmde.success("reactionrolecreated", "reactionrolecreatedinfo",
				new Placeholder("emote", emoji.getFormatted()),
				new Placeholder("role", role.getAsMention()));
		} else {
			cmde.send("erroroccurred");
			logDebugMessage(textChannel.getGuild(), textChannel, messageId, emoji, role);
		}
	}

	public void removeReactionRole(@NotNull UserRespondUtils cmde, Guild guild, @NotNull TextChannel textChannel, long messageId, Emoji emoji, String emojiStr) throws SQLException {
		textChannel.retrieveMessageById(messageId).queue(
			success -> success.clearReactions(emoji).queue(),
			fail -> cmde.error("wrongargument", "emotenotfound"));

		String roleStr = sql.runQuery("SELECT role_id FROM reaction_roles WHERE guild_id = ? AND message_id = ? AND emoji = ?;",
			guild.getIdLong(), messageId, emojiStr);

		if (roleStr == null) {
			cmde.send("noentryfound");
			return;
		}

		Role role = guild.getRoleById(roleStr);
		if (role == null || !entryExists(guild, messageId, emoji.getAsReactionCode(), role, textChannel)) {
			cmde.send("entrynotfound");
			return;
		}

		try (SQLHelper sqlHelper = sql.create("DELETE FROM reaction_roles WHERE guild_id = ? AND channel_id = ? AND message_id = ? AND emoji_id = ? AND role_id = ?",
				guild.getIdLong(), textChannel.getIdLong(), messageId, emojiStr, role.getIdLong())) {
			if (sqlHelper.run()) {
				cmde.success("entrydeleted", "reactionroledeleted");
			} else {
				cmde.send("erroroccurred");
				logDebugMessage(textChannel.getGuild(), textChannel, messageId, emoji, role);
			}
		} catch (SQLException e) {
			logger.error("Failed to remove reaction role", e);
			cmde.send("erroroccurred");
			logDebugMessage(textChannel.getGuild(), textChannel, messageId, emoji, role);
		}
	}

	private void logDebugMessage(Guild guild, TextChannel textChannel, long messageId, Emoji emoji, Role role) {
		LoggerFactory.getLogger(ReactionRolesSetupCommand.class).error("Failed to delete entry from database! " +
				"Guild: {}, Channel: {}, Message: {}, Emoji: {}, Role: {}",
			guild, textChannel.getName() + "(C:" + textChannel.getIdLong() + ")", messageId, emoji.getFormatted(), role.getName() + "(R:" + role.getIdLong() + ")");
	}

	private boolean entryExists(Guild guild, Long messageId, String emoji, Role role, TextChannel textChannel) throws SQLException {
		return entryExists(guild, messageId, emoji, role.getId(), textChannel);
	}

	private boolean entryExists(@NotNull Guild guild, Long messageId, String emoji, String role, TextChannel textChannel) throws SQLException {
		return sql.anyMatch("SELECT * FROM reaction_roles WHERE guild_id = ? AND channel_id = ? AND message_id = ? AND emoji = ? AND role_id = ?",
				guild.getIdLong(), textChannel.getIdLong(), messageId, emoji, role);
	}
}
