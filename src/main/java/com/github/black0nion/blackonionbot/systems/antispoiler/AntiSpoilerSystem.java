package com.github.black0nion.blackonionbot.systems.antispoiler;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem.AntiSpoilerType.*;

public class AntiSpoilerSystem extends ListenerAdapter {

    private final LanguageSystem languageSystem;
    private final EmbedUtils embedUtils;

    public AntiSpoilerSystem(LanguageSystem languageSystem, EmbedUtils embedUtils) {
        this.languageSystem = languageSystem;
        this.embedUtils = embedUtils;
    }

    /**
     * @return if the message contained a spoiler
     */
    public boolean removeSpoilers(@Nullable MessageReceivedEvent event, @Nullable MessageUpdateEvent event1) {
        final Message msg = event != null ? event.getMessage() : event1.getMessage();
        final String message = msg.getContentRaw();
        final BlackUser author = BlackUser.from(event != null ? event.getAuthor() : event1.getAuthor());
        final TextChannel channel = event != null ? event.getChannel().asTextChannel() : event1.getChannel().asTextChannel();
        final BlackGuild guild = BlackGuild.from(event != null ? event.getGuild() : event1.getGuild());
        final AntiSpoilerType type = guild.getAntiSpoilerType();
        return handleSystem(guild, msg, message, author, channel, message, type);
    }

    public boolean handleSystem(BlackGuild guild, Message msg, String message, BlackUser author, TextChannel channel, String newMessage, AntiSpoilerType type) {
        if (type != OFF) {
            long count = message.chars().filter(c -> c == '|').count();
            if (count < 4) return false;

            if (Utils.handleSelfRights(languageSystem, guild, author, channel, null, Permission.MESSAGE_MANAGE)) return false;

            msg.delete().queue();
            if (type == DELETE) return true;

            while (count >= 4) {
                newMessage = newMessage.replaceFirst("\\|\\|", "");
                newMessage = newMessage.replaceFirst("\\|\\|", "");
                count -= 4;
            }

            final String finalNewMessage = newMessage;

            if (type == REPLACE) {
                try {
                    channel.retrieveWebhooks().queue(webhooks -> {
                        try {
                            WebhookClient client = Utils.makeWebhookClient(Utils.getWebhook(channel, webhooks));
                            final WebhookMessageBuilder builder = new WebhookMessageBuilder();
                            builder.setUsername(author.getEscapedEffectiveName());
                            builder.setContent(finalNewMessage);
                            builder.setAvatarUrl(author.getAvatarUrl());
                            client.send(builder.build());
                            client.close();
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    });
                    return true;
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                channel.sendMessageEmbeds(embedUtils.getErrorEmbed(author, guild).addField("erroroccurred", "somethingwentwrong", false).build()).queue();
            }
        }
        return false;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        removeSpoilers(event, null);
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        removeSpoilers(null, event);
    }

    public enum AntiSpoilerType {
        DELETE,
        REPLACE,
        OFF;

        public static AntiSpoilerType parse(final String input) {
            if (input == null || input.isEmpty()) return null;
            try {
                return valueOf(input.toUpperCase());
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}
