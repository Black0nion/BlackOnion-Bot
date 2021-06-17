package com.github.black0nion.blackonionbot.blackobjects;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.Bag;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.mongodb.MongoDB;
import com.github.black0nion.blackonionbot.mongodb.MongoManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.MessageSticker;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;

public class BlackMessage extends BlackObject implements Message {

    private static final MongoCollection<Document> configs = MongoManager.getCollection("messages", MongoDB.botDatabase);

    private final Message message;
    private final BlackMember blackMember;
    private final BlackUser blackUser;
    private final BlackGuild blackGuild;
    private final TextChannel channel;

    private static final LoadingCache<Message, BlackMessage> messages = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<Message, BlackMessage>() {
	@Override
	public BlackMessage load(final Message message) {
	    return new BlackMessage(message);
	}
    });

    public static BlackMessage from(@NotNull final Message message) {
	try {
	    return messages.get(message);
	} catch (final Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    @Deprecated
    /**
     * Deprecated as a warning
     *
     * @param guild
     * @return
     */
    @Reloadable("messagecache")
    public static final void clearCache() {
	messages.invalidateAll();
    }

    private BlackMessage(@NotNull final Message message) {
	this.message = message;
	this.blackMember = BlackMember.from(message.getMember());
	this.blackUser = BlackUser.from(this.blackMember);
	this.blackGuild = BlackGuild.from(message.getGuild());
	this.channel = message.getTextChannel();
    }

    // override methods
    @Override
    public Document getIdentifier() {
	return new Document().append("guildid", this.blackGuild.getIdLong()).append("channelid", this.channel.getIdLong()).append("messageid", this.message.getIdLong());
    }

    @Override
    MongoCollection<Document> getCollection() {
	return configs;
    }

    // built in methods
    @Override
    public long getIdLong() {
	return this.message.getIdLong();
    }

    @Override
    public void formatTo(final Formatter arg0, final int arg1, final int arg2, final int arg3) {
	this.message.formatTo(arg0, arg1, arg2, arg3);
    }

    @Override
    public Message getReferencedMessage() {
	return this.message.getReferencedMessage();
    }

    @Override
    @Deprecated
    public List<User> getMentionedUsers() {
	return this.message.getMentionedUsers();
    }

    public List<BlackUser> getMentionedBlackUsers() {
	return BlackUser.from(this.message.getMentionedUsers());
    }

    @Override
    public Bag<User> getMentionedUsersBag() {
	return this.message.getMentionedUsersBag();
    }

    @Override
    public List<TextChannel> getMentionedChannels() {
	return this.message.getMentionedChannels();
    }

    @Override
    public Bag<TextChannel> getMentionedChannelsBag() {
	return this.message.getMentionedChannelsBag();
    }

    @Override
    public List<Role> getMentionedRoles() {
	return this.message.getMentionedRoles();
    }

    @Override
    public Bag<Role> getMentionedRolesBag() {
	return this.message.getMentionedRolesBag();
    }

    @Override
    @Deprecated
    public List<Member> getMentionedMembers(final Guild guild) {
	return this.message.getMentionedMembers(guild);
    }

    public List<BlackMember> getMentionedBlackMembers(final Guild guild) {
	return BlackMember.from(this.message.getMentionedMembers(guild));
    }

    @Override
    @Deprecated
    public List<Member> getMentionedMembers() {
	return this.message.getMentionedMembers();
    }

    public List<BlackMember> getMentionedBlackMembers() {
	return BlackMember.from(this.message.getMentionedMembers());
    }

    @Override
    public List<IMentionable> getMentions(final MentionType... types) {
	return this.message.getMentions(types);
    }

    @Override
    public boolean isMentioned(final IMentionable mentionable, final MentionType... types) {
	return this.message.isMentioned(mentionable, types);
    }

    @Override
    public boolean mentionsEveryone() {
	return this.message.mentionsEveryone();
    }

    @Override
    public boolean isEdited() {
	return this.message.isEdited();
    }

    @Override
    public OffsetDateTime getTimeEdited() {
	return this.message.getTimeEdited();
    }

    @Override
    @Deprecated
    public User getAuthor() {
	return this.message.getAuthor();
    }

    public BlackUser getBlackUser() {
	return this.blackUser;
    }

    @Override
    @Deprecated
    public Member getMember() {
	return this.message.getMember();
    }

    public BlackMember getBlackMember() {
	return this.blackMember;
    }

    @Override
    public String getJumpUrl() {
	return this.message.getJumpUrl();
    }

    @Override
    public String getContentDisplay() {
	return this.message.getContentDisplay();
    }

    @Override
    public String getContentRaw() {
	return this.message.getContentRaw();
    }

    @Override
    public String getContentStripped() {
	return this.message.getContentStripped();
    }

    @Override
    public List<String> getInvites() {
	return this.message.getInvites();
    }

    @Override
    public String getNonce() {
	return this.message.getNonce();
    }

    @Override
    public boolean isFromType(final ChannelType type) {
	return this.message.isFromType(type);
    }

    @Override
    public ChannelType getChannelType() {
	return this.message.getChannelType();
    }

    @Override
    public boolean isWebhookMessage() {
	return this.message.isWebhookMessage();
    }

    @Override
    public MessageChannel getChannel() {
	return this.message.getChannel();
    }

    @Override
    public PrivateChannel getPrivateChannel() {
	return this.message.getPrivateChannel();
    }

    @Override
    public TextChannel getTextChannel() {
	return this.message.getTextChannel();
    }

    @Override
    public Category getCategory() {
	return this.message.getCategory();
    }

    @Override
    @Deprecated
    public Guild getGuild() {
	return this.message.getGuild();
    }

    public BlackGuild getBlackGuild() {
	return this.blackGuild;
    }

    @Override
    public List<Attachment> getAttachments() {
	return this.message.getAttachments();
    }

    @Override
    public List<MessageEmbed> getEmbeds() {
	return this.message.getEmbeds();
    }

    @Override
    public List<Emote> getEmotes() {
	return this.message.getEmotes();
    }

    @Override
    public Bag<Emote> getEmotesBag() {
	return this.message.getEmotesBag();
    }

    @Override
    public List<MessageReaction> getReactions() {
	return this.message.getReactions();
    }

    @Override
    public List<MessageSticker> getStickers() {
	return this.message.getStickers();
    }

    @Override
    public boolean isTTS() {
	return this.message.isTTS();
    }

    @Override
    public MessageActivity getActivity() {
	return this.message.getActivity();
    }

    @Override
    public MessageAction editMessage(final CharSequence newContent) {
	return this.message.editMessage(newContent);
    }

    @Override
    @Deprecated
    public MessageAction editMessage(final MessageEmbed newContent) {
	return this.message.editMessage(newContent);
    }

    @Override
    public MessageAction editMessageFormat(final String format, final Object... args) {
	return this.message.editMessageFormat(format, args);
    }

    @Override
    public MessageAction editMessage(final Message newContent) {
	return this.message.editMessage(newContent);
    }

    @Override
    public AuditableRestAction<Void> delete() {
	return this.message.delete();
    }

    @Override
    public JDA getJDA() {
	return this.message.getJDA();
    }

    @Override
    public boolean isPinned() {
	return this.message.isPinned();
    }

    @Override
    public RestAction<Void> pin() {
	return this.message.pin();
    }

    @Override
    public RestAction<Void> unpin() {
	return this.message.unpin();
    }

    @Override
    public RestAction<Void> addReaction(final Emote emote) {
	return this.message.addReaction(emote);
    }

    @Override
    public RestAction<Void> addReaction(final String unicode) {
	return this.message.addReaction(unicode);
    }

    @Override
    public RestAction<Void> clearReactions() {
	return this.message.clearReactions();
    }

    @Override
    public RestAction<Void> clearReactions(final String unicode) {
	return this.message.clearReactions(unicode);
    }

    @Override
    public RestAction<Void> clearReactions(final Emote emote) {
	return this.message.clearReactions(emote);
    }

    @Override
    public RestAction<Void> removeReaction(final Emote emote) {
	return this.message.removeReaction(emote);
    }

    @Override
    public RestAction<Void> removeReaction(final Emote emote, final User user) {
	return this.message.removeReaction(emote, user);
    }

    @Override
    public RestAction<Void> removeReaction(final String unicode) {
	return this.message.removeReaction(unicode);
    }

    @Override
    public RestAction<Void> removeReaction(final String unicode, final User user) {
	return this.message.removeReaction(unicode, user);
    }

    @Override
    public ReactionPaginationAction retrieveReactionUsers(final Emote emote) {
	return this.message.retrieveReactionUsers(emote);
    }

    @Override
    public ReactionPaginationAction retrieveReactionUsers(final String unicode) {
	return this.message.retrieveReactionUsers(unicode);
    }

    @Override
    public ReactionEmote getReactionByUnicode(final String unicode) {
	return this.message.getReactionByUnicode(unicode);
    }

    @Override
    public ReactionEmote getReactionById(final String id) {
	return this.message.getReactionById(id);
    }

    @Override
    public ReactionEmote getReactionById(final long id) {
	return this.message.getReactionById(id);
    }

    @Override
    public AuditableRestAction<Void> suppressEmbeds(final boolean suppressed) {
	return this.message.suppressEmbeds(suppressed);
    }

    @Override
    public RestAction<Message> crosspost() {
	return this.message.crosspost();
    }

    @Override
    public boolean isSuppressedEmbeds() {
	return this.message.isSuppressedEmbeds();
    }

    @Override
    public EnumSet<MessageFlag> getFlags() {
	return this.message.getFlags();
    }

    @Override
    public MessageType getType() {
	return this.message.getType();
    }

    @Override
    public List<ActionRow> getActionRows() {
	return this.message.getActionRows();
    }

    @Override
    public MessageAction editMessageEmbeds(final Collection<? extends MessageEmbed> embeds) {
	return this.message.editMessageEmbeds(embeds);
    }
}