package com.github.black0nion.blackonionbot.slashcommands.fun;

import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class AvatarCommand extends SlashCommand {
    private static final String USER = "user";

    public AvatarCommand() {
        super(builder(Commands.slash("avatar", "Used to get the avatar of a user.")
                .addOption(OptionType.USER, USER, "The user to get the avatar of.")));
    }

    @Override
    public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var user = e.getOption(USER, OptionMapping::getAsMember);
        var selfBlackMember = BlackMember.from(e.getMember());

        if (user == null) {
            cmde.reply(cmde.success()
                    .setTitle(cmde.getTranslation("pfpof") + " " + selfBlackMember.getEffectiveName(), selfBlackMember.getEffectiveAvatarUrl())
                    .setImage(selfBlackMember.getEffectiveAvatarUrl() + "?size=2048")
                    .setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now()));
        } else {
            cmde.reply(cmde.success()
                    .setTitle(cmde.getTranslation("pfpof") + " " + user.getEffectiveName(), user.getEffectiveAvatarUrl())
                    .setImage(user.getEffectiveAvatarUrl() + "?size=2048")
                    .setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now()));
        }
    }
}