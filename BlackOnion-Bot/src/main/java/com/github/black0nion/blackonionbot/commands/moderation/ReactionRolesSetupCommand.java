package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.bson.Document;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.ReactionRoleSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ReactionRolesSetupCommand extends SlashCommand {

    public ReactionRolesSetupCommand() {
        OptionData[] options = {
            new OptionData(OptionType.CHANNEL, "channel", "The channel the message is in", true),
            new OptionData(OptionType.STRING, "messageid", "The ID of the message", true),
            new OptionData(OptionType.STRING, "emote", "The emote the reaction should be triggered with", true),
            new OptionData(OptionType.ROLE, "role", "The affected role", true)
        };
        this.setData(new CommandData("reactionrole", "Manage Reaction Roles")
                .addSubcommands(
                        new SubcommandData("create", "Create a new reaction role").addOptions(options),
                        new SubcommandData("delete", "Delete a existing reaction role").addOptions(options),
                        new SubcommandData("list", "List existing reaction roles")))
        .setRequiredPermissions(Permission.MANAGE_ROLES).setRequiredBotPermissions(Permission.MANAGE_ROLES, Permission.MESSAGE_ADD_REACTION);
    }

    @Override
    public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        String type = e.getSubcommandName();
        if (type.equalsIgnoreCase("list")) {
            // TODO: show list
        } else {
            final MessageChannel tc = e.getOptionsByType(OptionType.CHANNEL).get(0).getAsMessageChannel();
            final Role role = e.getOptionsByType(OptionType.ROLE).get(0).getAsRole();

            final String messageIDString = e.getOption("messageid").getAsString();

            try {
                final long messageID = Long.parseLong(messageIDString);
                tc.retrieveMessageById(messageID).queue(success -> {
                    final String emoteName = e.getOption("emote").getAsString();
                    guild.retrieveEmotes().queue(emoteList -> {
                        String emote = null;

                        emoteList = emoteList.stream().filter(entry -> entry.getName().equals(emoteName.replace(":", ""))).collect(Collectors.toList());

                        if (emoteList.size() != 0) {
                            tc.addReactionById(messageID, emoteList.get(0)).queue();
                            emote = emoteList.get(0).getAsMention();
                        } else {
                            emote = emoteName;
                            tc.addReactionById(messageID, emote).queue(null, fail -> {
                                cmde.error("wrongargument", "emotenotfound");
                                return;
                            });
                        }

                        final Document doc = new Document().append("guildid", e.getGuild().getIdLong()).append("channelid", tc.getIdLong()).append("messageid", messageID).append("emote", emote).append("roleid", role.getIdLong());

                        if (type.equalsIgnoreCase("create")) {
                            if (ReactionRoleSystem.collection.find(doc).first() != null) {
                                e.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField("alreadyexisting", "thisalreadyexisting", false).build()).queue();
                                return;
                            }

                            ReactionRoleSystem.collection.insertOne(doc);

                            cmde.success("reactionrolecreated", "reactionrolecreatedinfo", new Placeholder("emote", emote), new Placeholder("role", role.getAsMention()));
                            return;
                        } else if (type.equalsIgnoreCase("remove")) {
                            if (ReactionRoleSystem.collection.find(doc).first() != null) {
                                ReactionRoleSystem.collection.deleteOne(doc);

                                final String finalEmote = emote;
                                tc.retrieveMessageById(messageID).queue(msg -> {
                                    guild.retrieveEmoteById(finalEmote.split(":")[2].replace(">", "")).queue(customEmote -> {
                                        if (customEmote != null) {
                                            msg.clearReactions(customEmote).queue();
                                        } else {
                                            msg.clearReactions(finalEmote).queue();
                                        }
                                        cmde.success("entrydeleted", "reactionroledeleted");
                                    });
                                });
                                return;
                            } else {
                                cmde.error("errorhappened", "thisnotfound");
                                return;
                            }
                        } else {
                            cmde.sendPleaseUse();
                            return;
                        }
                    }, fail -> {
                        cmde.exception();
                        return;
                    });
                }, fail -> {
                    cmde.error("messagenotfound", "messagecouldntbefound");
                    return;
                });
            } catch (final NumberFormatException ex) {
                cmde.sendPleaseUse();
            }
        }
    }
}