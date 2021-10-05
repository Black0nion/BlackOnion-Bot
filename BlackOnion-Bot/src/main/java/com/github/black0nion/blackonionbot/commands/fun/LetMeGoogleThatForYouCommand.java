package com.github.black0nion.blackonionbot.commands.fun;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class LetMeGoogleThatForYouCommand extends SlashCommand {
    
    public LetMeGoogleThatForYouCommand() {
        this.setData(new CommandData("letmegooglethatforyou", "Send a user a link to letmegooglethatforyou.com to let him know he should google first!")
                .addOption(OptionType.STRING, "query", "The query to search for", true)
                .addOption(OptionType.USER, "user", "The user to send the link to (hidden!)", false)
        );
    }
    
    @Override
    public void execute(SlashCommandExecutedEvent cmde, SlashCommandEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        String query = e.getOptionsByType(OptionType.STRING).get(0).getAsString().replace(" ", "+");
        if (e.getOption("user") != null) {
            Member meme = e.getOption("user").getAsMember();
            if (meme == null) {
                cmde.errorPrivate("notamember", "cantlmgtfynotmembers");
            } else {
                e.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription(cmde.getTranslation("lmgtfy",
                    new Placeholder("user", member.getAsMention()),
                    new Placeholder("link", "https://letmegooglethat.com/?q=" + query)))
                .build()).content(meme.getAsMention()).queue();
                e.reply(cmde.getTranslation("sentlmgtfy")).setEphemeral(true).queue();
            }
        } else {
            e.reply(cmde.getTranslation("urlmgtfy", new Placeholder("link", "https://letmegooglethat.com/?q=" + query))).setEphemeral(true).queue();
        }
    }
}