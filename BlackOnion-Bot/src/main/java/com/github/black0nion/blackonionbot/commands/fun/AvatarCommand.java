package com.github.black0nion.blackonionbot.commands.fun;

import java.time.Instant;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandExecutedEvent;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class AvatarCommand extends SlashCommand {

    public AvatarCommand() {
	this.setData(new CommandData("avatar", "Gives you the profile picture of a user").addOption(OptionType.USER, "user", "The user to get the avatar of", true));
    }

    @Override
    public void execute(final SlashCommandExecutedEvent cmde, final SlashCommandEvent e, final BlackMember member, final com.github.black0nion.blackonionbot.blackobjects.BlackUser author, final BlackGuild guild, final TextChannel channel) {
	final User mentionedUser = e.getOptionsByType(OptionType.USER).get(0).getAsUser();
	cmde.reply(cmde.success().setTitle(LanguageSystem.getTranslation("pfpof", author, guild) + " " + Utils.removeMarkdown(mentionedUser.getName()) + "#" + mentionedUser.getDiscriminator(), mentionedUser.getEffectiveAvatarUrl()).setImage(mentionedUser.getEffectiveAvatarUrl() + "?size=2048").setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl()).setTimestamp(Instant.now()));
    }
}