package com.github.black0nion.blackonionbot.bot;

import java.util.HashMap;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.bot.ActivityCommand;
import com.github.black0nion.blackonionbot.commands.bot.AdminHelpCommand;
import com.github.black0nion.blackonionbot.commands.bot.AntiSpoilerCommand;
import com.github.black0nion.blackonionbot.commands.bot.AntiSwearCommand;
import com.github.black0nion.blackonionbot.commands.bot.BanUsageCommand;
import com.github.black0nion.blackonionbot.commands.bot.BugReportCommand;
import com.github.black0nion.blackonionbot.commands.bot.GuildLanguageCommand;
import com.github.black0nion.blackonionbot.commands.bot.HelpCommand;
import com.github.black0nion.blackonionbot.commands.bot.JoinLeaveMessageCommand;
import com.github.black0nion.blackonionbot.commands.bot.LanguageCommand;
import com.github.black0nion.blackonionbot.commands.bot.NotifyCommand;
import com.github.black0nion.blackonionbot.commands.bot.PingCommand;
import com.github.black0nion.blackonionbot.commands.bot.PrefixCommand;
import com.github.black0nion.blackonionbot.commands.bot.ReloadCommand;
import com.github.black0nion.blackonionbot.commands.bot.ShutdownDBCommand;
import com.github.black0nion.blackonionbot.commands.bot.StatsCommand;
import com.github.black0nion.blackonionbot.commands.bot.StatusCommand;
import com.github.black0nion.blackonionbot.commands.bot.SupportCommand;
import com.github.black0nion.blackonionbot.commands.bot.SwearWhitelistCommand;
import com.github.black0nion.blackonionbot.commands.fun.AvatarCommand;
import com.github.black0nion.blackonionbot.commands.fun.BigbrainMemeCommand;
import com.github.black0nion.blackonionbot.commands.fun.ConnectFourCommand;
import com.github.black0nion.blackonionbot.commands.fun.GiveawayCommand;
import com.github.black0nion.blackonionbot.commands.information.GuildInfoCommand;
import com.github.black0nion.blackonionbot.commands.information.UserInfoCommand;
import com.github.black0nion.blackonionbot.commands.information.WeatherCommand;
import com.github.black0nion.blackonionbot.commands.misc.InstagramCommand;
import com.github.black0nion.blackonionbot.commands.misc.PastebinCommand;
import com.github.black0nion.blackonionbot.commands.misc.PollCommand;
import com.github.black0nion.blackonionbot.commands.misc.TestCommand;
import com.github.black0nion.blackonionbot.commands.misc.VirusCommand;
import com.github.black0nion.blackonionbot.commands.moderation.AutoRolesCommand;
import com.github.black0nion.blackonionbot.commands.moderation.BanCommand;
import com.github.black0nion.blackonionbot.commands.moderation.ClearCommand;
import com.github.black0nion.blackonionbot.commands.moderation.KickCommand;
import com.github.black0nion.blackonionbot.commands.moderation.ReactionRolesSetupCommand;
import com.github.black0nion.blackonionbot.commands.moderation.RenameCommand;
import com.github.black0nion.blackonionbot.commands.moderation.UnbanCommand;
import com.github.black0nion.blackonionbot.commands.moderation.joinleave.SetLeaveChannelCommand;
import com.github.black0nion.blackonionbot.commands.moderation.joinleave.SetWelcomeChannelCommand;
import com.github.black0nion.blackonionbot.commands.music.PlayCommand;
import com.github.black0nion.blackonionbot.commands.music.QueueCommand;
import com.github.black0nion.blackonionbot.commands.music.SkipCommand;
import com.github.black0nion.blackonionbot.commands.music.StopCommand;
import com.github.black0nion.blackonionbot.commands.old.HypixelCommand;
import com.github.black0nion.blackonionbot.enums.CommandVisibility;
import com.github.black0nion.blackonionbot.enums.LogMode;
import com.github.black0nion.blackonionbot.enums.LogOrigin;
import com.github.black0nion.blackonionbot.systems.AntiSpoilerSystem;
import com.github.black0nion.blackonionbot.systems.ContentModeratorSystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandBase extends ListenerAdapter {
	
	public static HashMap<String[], Command> commands = new HashMap<>();
	
	public static EventWaiter waiter;

	public static int commandsLastTenSecs = 0;
	
	public static void addCommands(EventWaiter newWaiter) {
		commands.clear();
		waiter = newWaiter;
		addCommand(new ActivityCommand());
		addCommand(new AvatarCommand());
		addCommand(new ClearCommand());
		addCommand(new HelpCommand());
		addCommand(new NotifyCommand());
		addCommand(new PingCommand());
		addCommand(new ReloadCommand());
		addCommand(new StatusCommand());
		addCommand(new PlayCommand());
		addCommand(new StopCommand());
		addCommand(new QueueCommand());
		addCommand(new ShutdownDBCommand());
		addCommand(new ReactionRolesSetupCommand());
		addCommand(new PastebinCommand());
		addCommand(new HypixelCommand());
		addCommand(new RenameCommand());
		addCommand(new StatsCommand());
		addCommand(new WeatherCommand());
		addCommand(new InstagramCommand());
		addCommand(new AdminHelpCommand());
		addCommand(new TestCommand());
		addCommand(new ConnectFourCommand(waiter));
		addCommand(new SupportCommand());
		addCommand(new LanguageCommand());
		addCommand(new KickCommand());
		addCommand(new BanCommand());
		addCommand(new UnbanCommand());
		addCommand(new GuildLanguageCommand());
		addCommand(new BigbrainMemeCommand());
		addCommand(new GuildInfoCommand());
		addCommand(new UserInfoCommand());
		addCommand(new VirusCommand());
		addCommand(new SkipCommand());
		addCommand(new AutoRolesCommand());
		addCommand(new SetWelcomeChannelCommand());
		addCommand(new SetLeaveChannelCommand());
		addCommand(new PrefixCommand());
		addCommand(new GiveawayCommand());
		addCommand(new AntiSwearCommand());
		addCommand(new BugReportCommand());
		addCommand(new SwearWhitelistCommand());
		addCommand(new BanUsageCommand());
		addCommand(new PollCommand());
		addCommand(new JoinLeaveMessageCommand());
		addCommand(new AntiSpoilerCommand());
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		final User author = event.getAuthor();
		if (author.isBot()) return;
		
		final Guild guild = event.getGuild();
		final String prefix = BotInformation.getPrefix(guild);
		final TextChannel channel = event.getChannel();
		final Member member = event.getMember();
		final Message message = event.getMessage();
		final String msgContent = message.getContentRaw();
		final String log = EmojiParser.parseToAliases(guild.getName() + "(G:" + guild.getId() + ") > " + channel.getName() + "(C:" + channel.getId() + ") | " + author.getName() + "#" + author.getDiscriminator() + "(U:" + author.getId() + "): (M:" + message.getId() + ")" + msgContent.replace("\n", "\\n"));
		final String[] args = msgContent.split(" ");
		
		Logger.log(LogMode.INFORMATION, LogOrigin.BOT, log);
		
		final boolean containsProfanity = ContentModeratorSystem.checkMessageForProfanity(event);
		
		if (AntiSpoilerSystem.removeSpoilers(event)) return;
		
		if (!args[0].startsWith(BotInformation.getPrefix(guild))) return;

		for (String[] c : commands.keySet()) {
			for (String str : c) {
				if (args[0].equalsIgnoreCase(prefix + str)) {
					FileUtils.appendToFile("commandLog", log);
					ValueManager.save("commandsExecuted", ValueManager.getInt("commandsExecuted") + 1);
					commandsLastTenSecs++;
					Command cmd = commands.get(c);
					if (cmd.requiresBotAdmin() && !BotSecrets.isAdmin(author.getIdLong())) {
						continue;
					} else if (cmd.getRequiredPermissions() != null && !member.hasPermission(cmd.getRequiredPermissions())) {
						if (cmd.getVisisbility() != CommandVisibility.SHOWN)
							return;
						channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild)
								.addField(LanguageSystem.getTranslatedString("missingpermissions", author, guild), LanguageSystem.getTranslatedString("requiredpermissions", author, guild) + "\n" + getPermissionString(cmd.getRequiredPermissions()), false).build()).queue();
						return;
					} else if (cmd.getRequiredArgumentCount() + 1 > args.length) {
						channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild)
								.addField(LanguageSystem.getTranslatedString("wrongargumentcount", author, guild), "Syntax: " + prefix + str + (cmd.getSyntax().equals("") ? "" : " " + cmd.getSyntax()), false).build()).queue();
						return;
					}
					
					if (containsProfanity) {
						channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("dontexecuteprofanitycommands", "pleaseremoveprofanity", false).build()).queue();
						return;
					}
					Bot.executor.submit(() -> {
						cmd.execute(args, event, message, member, author, guild, channel);
					});
					return;
				}
			}
		}
		
		channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("commandnotfound", LanguageSystem.getTranslatedString("thecommandnotfound", author, guild).replace("%command%", args[0]), false).build()).queue();
	}
	
	public static void addCommand(Command c, String... command) {
		if (!commands.containsKey(command))
			commands.put(command, c);
	}
	
	public static void addCommand(Command c) {
		if (!commands.containsKey(c.getCommand()))
			commands.put(c.getCommand(), c);
	}
	
	public static String getPermissionString(Permission[] permissions) {
		String output = "```";
		for (int i = 0; i  < permissions.length; i++) {
			output += "- " + permissions[i].getName() + (i == permissions.length-1 ? "```" : "\n");
		}
		return output;
	}
}
