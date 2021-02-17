package com.github.black0nion.blackonionbot.bot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.bot.ActivityCommand;
import com.github.black0nion.blackonionbot.commands.bot.AdminHelpCommand;
import com.github.black0nion.blackonionbot.commands.bot.GuildLanguageCommand;
import com.github.black0nion.blackonionbot.commands.bot.HelpCommand;
import com.github.black0nion.blackonionbot.commands.bot.LanguageCommand;
import com.github.black0nion.blackonionbot.commands.bot.NotifyCommand;
import com.github.black0nion.blackonionbot.commands.bot.PingCommand;
import com.github.black0nion.blackonionbot.commands.bot.PrefixCommand;
import com.github.black0nion.blackonionbot.commands.bot.ReloadCommand;
import com.github.black0nion.blackonionbot.commands.bot.ShutdownDBCommand;
import com.github.black0nion.blackonionbot.commands.bot.StatsCommand;
import com.github.black0nion.blackonionbot.commands.bot.StatusCommand;
import com.github.black0nion.blackonionbot.commands.bot.SupportCommand;
import com.github.black0nion.blackonionbot.commands.fun.AvatarCommand;
import com.github.black0nion.blackonionbot.commands.fun.BigbrainMemeCommand;
import com.github.black0nion.blackonionbot.commands.fun.ConnectFourCommand;
import com.github.black0nion.blackonionbot.commands.fun.GiveawayCommand;
import com.github.black0nion.blackonionbot.commands.information.GuildInfoCommand;
import com.github.black0nion.blackonionbot.commands.information.UserInfoCommand;
import com.github.black0nion.blackonionbot.commands.information.WeatherCommand;
import com.github.black0nion.blackonionbot.commands.misc.InstagramCommand;
import com.github.black0nion.blackonionbot.commands.misc.PastebinCommand;
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
import com.github.black0nion.blackonionbot.commands.music.old.JoinCommand;
import com.github.black0nion.blackonionbot.commands.music.old.LeaveCommand;
import com.github.black0nion.blackonionbot.commands.old.HypixelCommand;
import com.github.black0nion.blackonionbot.enums.CommandVisibility;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandBase extends ListenerAdapter {
	
	public static HashMap<String[], Command> commands = new HashMap<>();
	
	public static EventWaiter waiter;
	
	public static void addCommands(EventWaiter newWaiter) {
		waiter = newWaiter;
		//AudioLoadResult.waiter = newWaiter;
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
		addCommand(new LeaveCommand());
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
		addCommand(new JoinCommand());
		addCommand(new SkipCommand());
		addCommand(new AutoRolesCommand());
		addCommand(new SetWelcomeChannelCommand());
		addCommand(new SetLeaveChannelCommand());
		addCommand(new PrefixCommand());
		addCommand(new GiveawayCommand());
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;
		
		System.out.println("Message received: " + event.getMessage().getContentRaw());
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		String prefix = BotInformation.getPrefix(event.getGuild());
		
		for (String[] c : commands.keySet()) {
			for (String str : c) {
				if (event.getMessage().getContentRaw().toLowerCase().split(" ")[0].equalsIgnoreCase(prefix + str)) {
					String message = dtf.format(now) + " | " + event.getChannel().getName() + " | " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ": " + event.getMessage().getContentRaw();
					FileUtils.appendToFile("commandLog", message);
					ValueManager.save("commandsExecuted", ValueManager.getInt("commandsExecuted") + 1);
					String[] args = event.getMessage().getContentRaw().split(" ");
					Command cmd = commands.get(c);
					if (cmd.dmCommand() && event.isFromType(ChannelType.PRIVATE)) {
						event.getChannel().sendMessage(EmbedUtils.getDefaultErrorEmbed(event.getAuthor(), event.getGuild()).setDescription("This command can't be accessed through private chat! Use it on a server!").build()).queue();
						continue;
					}
					if (cmd.requiresBotAdmin() && !BotSecrets.isAdmin(event.getAuthor().getIdLong())) {
						continue;
					} else if (cmd.getRequiredPermissions() != null && !event.getMember().hasPermission(cmd.getRequiredPermissions())) {
						if (cmd.getVisisbility() != CommandVisibility.SHOWN)
							continue;
						event.getChannel().sendMessage(EmbedUtils.getDefaultErrorEmbed(event.getAuthor(), event.getGuild())
								.addField(LanguageSystem.getTranslatedString("missingpermissions", event.getAuthor(), event.getGuild()), LanguageSystem.getTranslatedString("requiredpermissions", event.getAuthor(), event.getGuild()) + "\n" + getPermissionString(cmd.getRequiredPermissions()), false).build()).queue();
						continue;
					} else if (cmd.getRequiredArgumentCount() + 1 > args.length) {
						event.getChannel().sendMessage(EmbedUtils.getDefaultErrorEmbed(event.getAuthor(), event.getGuild())
								.addField(LanguageSystem.getTranslatedString("wrongargumentcount", event.getAuthor(), event.getGuild()), "Syntax: " + prefix + str + (cmd.getSyntax().equals("") ? "" : " " + cmd.getSyntax()), false).build()).queue();
						continue;
					}
					cmd.execute(args, event, event.getMessage(), event.getMember(), event.getAuthor(), event.getGuild(), event.getChannel());
				}
			}
		}
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
