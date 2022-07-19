package com.github.black0nion.blackonionbot.systems.antispoiler;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.github.black0nion.blackonionbot.systems.antispoiler.AntiSpoilerSystem.AntiSpoilerType.*;

public class AntiSpoilerSystem extends ListenerAdapter {
    /**
     * @return if the message contained a spoiler
     */
    public static boolean removeSpoilers(@Nullable MessageReceivedEvent event, @Nullable MessageUpdateEvent event1) {
        final Message msg = event != null ? event.getMessage() : event1.getMessage();
        final String message = msg.getContentRaw();
        final BlackUser author = BlackUser.from(event != null ? event.getAuthor() : event1.getAuthor());
        final TextChannel channel = event != null ? event.getChannel().asTextChannel() : event1.getChannel().asTextChannel();
        final BlackGuild guild = BlackGuild.from(event != null ? event.getGuild() : event1.getGuild());
        String newMessage = message;
        final AntiSpoilerType type = guild.getAntiSpoilerType();
        return handleSystem(guild, msg, message, author, channel, newMessage, type);
    }

    public static boolean handleSystem(BlackGuild guild, Message msg, String message, BlackUser author, TextChannel channel, String newMessage, AntiSpoilerType type) {
        if (type != OFF) {
            long count = message.chars().filter(c -> c == '|').count();
            if (count < 4) return false;

            if (Utils.handleRights(guild, author, channel, null, Permission.MESSAGE_MANAGE)) return false;

            msg.delete().queue();
            if (type == DELETE) return true;

            if (Utils.handleRights(guild, author, channel, null, Permission.MANAGE_WEBHOOKS)) return true;

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
                channel.sendMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "somethingwentwrong", false).build()).queue();
            }
        }
        return false;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (removeSpoilers(event, null)) event.getMessage().delete().queue();
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        if (removeSpoilers(null, event)) event.getMessage().delete().queue();
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