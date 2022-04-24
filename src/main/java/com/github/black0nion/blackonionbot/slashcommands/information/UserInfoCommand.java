package com.github.black0nion.blackonionbot.slashcommands.information;

import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.wrappers.TranslatedEmbed;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.User.UserFlag;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.time.format.DateTimeFormatter;

public class UserInfoCommand extends SlashCommand {
    private static final String USER = "user";
    private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public UserInfoCommand() {

        super(builder(Commands.slash("userinfo", "Used to get information about a member.")
                .addOption(OptionType.USER,USER, "The member to get information about.", false)));
    }

    static @NotNull EmbedBuilder getUserInfo(final @NotNull SlashCommandEvent cmde, final @NotNull User user, final @Nullable Member member) {
        var statsUser = BlackUser.from(user);
        var statsMember = member != null ? BlackMember.from(member) : null;
        final String[] flags = statsUser.getFlags().stream().map(UserFlag::getName).toArray(String[]::new);

        final TranslatedEmbed builder = cmde.success();
        builder.setTitle("userinfo");
        final String avatarUrl = statsUser.getAvatarUrl();
        builder.setThumbnail(avatarUrl != null ? avatarUrl : statsUser.getDefaultAvatarUrl());
        builder.addField("name", statsUser.getEscapedName(), true);
        builder.addField("discriminator", statsUser.getDiscriminator(), true);
        builder.addField("userid", statsUser.getId(), true);
        builder.addField("badges", (flags.length != 0 ? String.join("\n", flags) : "empty"), false);
        builder.addField("language", statsUser.getLanguage() != null ? statsUser.getLanguage().getName() + " (" + statsUser.getLanguage().getLanguageCode() + ")" : "nopreference", true);
        builder.addField("created", statsUser.getTimeCreated().format(pattern), true);
        if (statsMember != null) {
            builder.addField("joined", statsMember.getTimeJoined().format(pattern), true);
            if (statsMember.getTimeBoosted() != null)
                builder.addField("boosted", statsMember.getTimeBoosted().format(pattern), true);
        }
        return builder;
    }

    @Override
    public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var givenUser = e.getOption(USER, OptionMapping::getAsUser);

        if (givenUser == null) {
            cmde.reply(getUserInfo(cmde, author, member));
        } else {
            guild.retrieveMember(givenUser).queue(loadedMember -> cmde.reply(getUserInfo(cmde, givenUser, loadedMember)), error -> cmde.reply(getUserInfo(cmde, givenUser, null)));
        }
    }
}