package com.github.black0nion.blackonionbot.slashcommands.bot;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.CustomCommand;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Collectors;

public class CustomCommandsCommand extends SlashCommand {
    private static final String OPTION = "option";
    private static final String COMMAND_NAME = "command_name";
    private static final String LIST = "list";
    private static final String CREATE = "create";
    private static final String DELETE = "delete";
    private static final String CLEAR = "clear";

    public CustomCommandsCommand() {
        super(builder(Commands.slash("customcommand", "Used to create a custom command.")
                .addOptions(new OptionData(OptionType.STRING, OPTION, "The name of the command to set/get/clear.", true)
                        .addChoice("List", LIST)
                        .addChoice("Create", CREATE)
                        .addChoice("Delete", DELETE))
                .addOption(OptionType.STRING, COMMAND_NAME, "[command (required for create and delete)]")));
    }

    private void askForRaw(final @NotNull String command, final @NotNull SlashCommandEvent cmde, final SlashCommandInteractionEvent slashCommandInteractionEvent, final BlackGuild guild, final BlackMember member, final BlackUser user) {
        slashCommandInteractionEvent.replyEmbeds(cmde.success()
                        .addField("messagetosend", "inputmessage", false)
                        .setDescription(cmde.getTranslation("leavetutorial"))
                        .setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), slashCommandInteractionEvent.getJDA().getSelfUser().getAvatarUrl()).build())
                .queue(msg -> Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class,
                        e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(),
                        e -> {
                            final String contentRaw = e.getMessage().getContentRaw();
                            if (contentRaw.startsWith(cmde.getGuild().getPrefix()) || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
                                cmde.error("aborting", "byeeee");
                                return;
                            }

                            final CustomCommand customCommand = new CustomCommand(cmde.getGuild(), command, contentRaw);
                            askForReply(command, new SlashCommandEvent(this, slashCommandInteractionEvent, guild, member, user), customCommand, slashCommandInteractionEvent, guild, member, user);
                        }));
    }

    private void askForReply(final String command, final @NotNull SlashCommandEvent cmde, final @NotNull CustomCommand customCommand, final SlashCommandInteractionEvent slashCommandInteractionEvent, final BlackGuild guild, final BlackMember member, final BlackUser user) {
        slashCommandInteractionEvent.replyEmbeds(cmde.success().addField("shouldreply", "shouldanswer", false).setDescription(cmde.getTranslation("leavetutorial")).setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), slashCommandInteractionEvent.getJDA().getSelfUser().getAvatarUrl()).build()).queue(msg -> Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, e -> e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
            final String contentRaw = e.getMessage().getContentRaw();

            if (contentRaw.startsWith(cmde.getGuild().getPrefix()) || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
                cmde.error("aborting", "cya");
                return;
            }

            boolean reply;
            if (contentRaw.equalsIgnoreCase("true")) {
                reply = true;
            } else if (contentRaw.equalsIgnoreCase("false")) {
                reply = false;
            } else {
                askForReply(command, new SlashCommandEvent(this, slashCommandInteractionEvent, guild, member, user), customCommand, slashCommandInteractionEvent, guild, member, user);
                return;
            }

            customCommand.setReply(reply);
            cmde.getGuild().addCustomCommand(customCommand);
            cmde.success("commandadded", "executetutorial", new Placeholder("%cmd%", cmde.getGuild().getPrefix() + customCommand.getCommand()));
        }));
    }

    @Override
    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
        var option = e.getOption(OPTION, OptionMapping::getAsString);
        switch (Objects.requireNonNull(option)) {
            case LIST ->
                    cmde.success("customcommandslist", guild.getCustomCommands().values().stream().map(val -> "- `" + val.getCommand() + "`").collect(Collectors.joining("\n")));
            case CREATE -> {
                final String command = e.getOption(COMMAND_NAME, OptionMapping::getAsString);
                final int maxCount = guild.getGuildType().getMaxCustomCommands();
                if (guild.getCustomCommands().size() >= maxCount) {
                    cmde.error("toomanycustomcommands", "maxcustomcommands", new Placeholder("count", maxCount));
                    return;
                }

                if (SlashCommandBase.commands.containsKey(command) || guild.getCustomCommands().containsKey(command)) {
                    cmde.error("alreadyexisting", "commandexisting");
                    return;
                }

                this.askForType(command, cmde, e, guild, member, author);
            }
            case DELETE -> {
                final String command = e.getOption(COMMAND_NAME, OptionMapping::getAsString);
                if (guild.getCustomCommands().containsKey(command)) {
                    this.askForType(command, cmde, e, guild, member, author);
                } else {
                    cmde.error("notfound", "commandnotfound");
                }
            }
            default -> cmde.sendPleaseUse();
        }
    }

    private void askForDelete(final String command, final @NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent slashCommandInteractionEvent) {
        slashCommandInteractionEvent.replyEmbeds(cmde.success().addField("areyousure", "@blaumeise was soll hier stehen?", false).build()).queue(msg -> Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, e -> e.getChannelType() == ChannelType.TEXT && e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
            final String contentRaw = e.getMessage().getContentRaw();

            if (contentRaw.equalsIgnoreCase("true")) {
                cmde.getGuild().deleteCustomCommand(command);
                cmde.success("entrydeleted", "commanddeleted", new Placeholder("cmd", command));
            } else {
                cmde.error("abort", "nothingdeleted");
            }
        }));
    }

    private void askForType(final @NotNull String command, final @NotNull SlashCommandEvent cmde, final SlashCommandInteractionEvent slashCommandInteractionEvent, final BlackGuild guild, final BlackMember member, final BlackUser user) {
        slashCommandInteractionEvent.replyEmbeds(cmde.success().addField("inputtype", "validtypes", false).setDescription(cmde.getTranslation("leavetutorial")).setAuthor(cmde.getTranslation("customcommandsetup", new Placeholder("cmd", command)), slashCommandInteractionEvent.getJDA().getSelfUser().getAvatarUrl()).build()).queue(msg -> Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, e -> e.getChannelType() == ChannelType.TEXT && e.getChannel().getIdLong() == cmde.getChannel().getIdLong() && e.getAuthor().getIdLong() == cmde.getUser().getIdLong(), e -> {
            final String contentRaw = e.getMessage().getContentRaw();
            if (contentRaw.startsWith(cmde.getGuild().getPrefix()) || Utils.equalsOneIgnoreCase(contentRaw, "exit", "leave", "cancel")) {
                cmde.error("aborting", "byeeee");
                return;
            }

            if (contentRaw.equalsIgnoreCase("raw") || contentRaw.equalsIgnoreCase("message")) {
                askForRaw(command, new SlashCommandEvent(this, slashCommandInteractionEvent, guild, member, user), slashCommandInteractionEvent, guild, member, user);
            } else if (contentRaw.equalsIgnoreCase("embed")) {
                // TODO: add embed
            } else {
                this.askForType(command, new SlashCommandEvent(this, slashCommandInteractionEvent, guild, member, user), slashCommandInteractionEvent, guild, member, user);
            }
        }));
    }
}