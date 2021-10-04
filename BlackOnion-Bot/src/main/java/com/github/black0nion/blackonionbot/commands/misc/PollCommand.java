package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import net.dv8tion.jda.api.entities.Message;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class PollCommand extends SlashCommand {

    public PollCommand() {
        this.setData(new CommandData("poll", "Creates a poll").setDefaultEnabled(false).addOptions(
                new OptionData(OptionType.STRING, "type", "The type of the poll", true).addChoice("Yes / No", "yesno").addChoice("Rate", "rate"),
                new OptionData(OptionType.STRING, "question", "The question to ask", true)));
    }

    @Override
    public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        e.deferReply().queue();
        final String type = e.getOption("type").getAsString();
        if (type.equalsIgnoreCase("yesno")) {
            e.getHook().sendMessageEmbeds(cmde.success().setTitle("poll").addField(e.getOption("question").getAsString(), "votewithemotes", false).build()).queue(msg -> {
                msg.addReaction("\u2705").queue();
                msg.addReaction("\u274C").queue();
            });
        } else if (type.equalsIgnoreCase("rate")) {
            e.getHook().sendMessageEmbeds(cmde.success().setTitle("poll").addField(e.getOption("question").getAsString(), "votewithemotes", false).build()).queue(msg -> {
                for (int i = 1; i <= 5; i++) {
                    msg.addReaction(Utils.numbersUnicode.get(i)).queue();
                }
            });
        } else {
            System.err.println("Illegal Argument in PollCommand.");
        }
    }
}