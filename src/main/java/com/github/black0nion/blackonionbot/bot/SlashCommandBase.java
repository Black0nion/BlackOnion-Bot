package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.commands.admin.BanUsageCommand;
import com.github.black0nion.blackonionbot.misc.*;
import com.github.black0nion.blackonionbot.systems.antiswear.AntiSwearSystem;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.systems.logging.StatisticsManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.FileUtils;
import com.github.black0nion.blackonionbot.utils.Pair;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mongodb.client.model.Filters;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

public class SlashCommandBase extends ListenerAdapter {

	public static HashMap<Category, List<SlashCommand>> commandsInCategory = new HashMap<>();

	public static HashMap<String, Pair<Long, SlashCommand>> commands = new HashMap<>();

	public static JSONObject commandsJson;

	@Reloadable("commands")
	public static void addCommands() {
		commands.clear();
		commandsInCategory.clear();
		commandsJson = new JSONObject();
		JSONArray commands = new JSONArray();
		final Reflections reflections = new Reflections(SlashCommand.class.getPackage().getName());
		final Set<Class<? extends SlashCommand>> annotated = reflections.getSubTypesOf(SlashCommand.class);

		for (final Class<?> command : annotated) {
			try {
				final SlashCommand newInstance = (SlashCommand) command.getConstructor().newInstance();
				final String[] packageName = command.getPackage().getName().split("\\.");
				final Category parsedCategory = Category.parse(packageName[packageName.length - 1]);
				newInstance.setCategory(parsedCategory != null ? parsedCategory : newInstance.getCategory());

				SlashCommandData data = newInstance.getData();
				if (newInstance.getRequiredCustomPermissions() == null || newInstance.getRequiredCustomPermissions().length == 0)
					commands.put(new JSONObject().put("name", data.getName()).put("description", data.getDescription()).put("arguments", data.getOptions()).put("permissions", newInstance.getRequiredPermissions()));
				if (newInstance.shouldAutoRegister()) {
					if (newInstance.getData() != null) addCommand(newInstance);
					// should be covered by the builder
					else throw new RuntimeException("Command " + command.getName() + " has no data!");
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		commandsJson.put("commands", commands);
		System.out.println(commandsJson);
		// TODO: remove (debug)
		// Bot.jda.updateCommands().addCommands(commands.values().stream().map(Pair::getValue).map(SlashCommand::getData).collect(Collectors.toList())).queue();

		// StringSelection stringSelection = new
		// StringSelection(commandsJSON.toString());
		// Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// clipboard.setContents(stringSelection, null);
		// System.out.println(commandsJSON);
		Bot.executor.submit(Dashboard::init);
	}

	@Override
	public void onMessageUpdate(final MessageUpdateEvent event) {
		if (event.getChannelType() == ChannelType.TEXT) {
			// TODO: LMAO remove dis pls
			if (event.getMessage().getContentRaw().startsWith("register")) {
				event.getGuild().updateCommands().addCommands(SlashCommandBase.commands.values().stream().map(Pair::getValue).map(SlashCommand::getData).toList()).queue();
				System.out.println("Updated! :D");
			}
			AntiSwearSystem.check(BlackGuild.from(event.getGuild()), BlackMember.from(event.getMember()), event.getMessage(), event.getTextChannel());
		}
	}

	@Override
	public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
		StatisticsManager.commandExecuted();
		if (event.getUser().isBot()) return;

		final BlackUser author = BlackUser.from(event.getUser());
		final BlackGuild guild = BlackGuild.from(event.getGuild());
		final BlackMember member = BlackMember.from(event.getMember());

		assert author != null && guild != null && member != null;

		final TextChannel channel = event.getTextChannel();
		final boolean locked = BanUsageCommand.collection.find(Filters.or(Filters.eq("guildid", guild.getIdLong()), Filters.eq("userid", author.getIdLong()))).first() != null;
		// TODO: remove personal information & only log on exception
		final String log = EmojiParser.parseToAliases(guild.getName() + "(G:" + guild.getId() + ") > " + channel.getName() + "(C:" + channel.getId() + ") | " + author.getName() + "#" + author.getDiscriminator() + "(U:" + author.getId() + "): (M:" + event.getId() + ")" + event.getCommandPath() + " " + event.getOptions().stream().map(OptionMapping::toString).collect(Collectors.joining(" ")).replace("\n", "\\n"));

		Logger.log(locked ? LogMode.WARNING : LogMode.INFORMATION, LogOrigin.DISCORD, log);
		FileUtils.appendToFile("files/logs/messagelog/" + guild.getId() + "/" + EmojiParser.parseToAliases(channel.getName()).replaceAll(":([^:\\s]*(?:::[^:\\s]*)*):", "($1)").replace(":", "_") + "_" + channel.getId() + ".log", log);

		if (locked) return;

		final SlashCommandEvent cmde = new SlashCommandEvent(event, guild, member, author);

		if (Utils.handleRights(guild, author, channel, Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) return;
		if (commands.containsKey(event.getName())) {
			final SlashCommand cmd = commands.get(event.getName()).getValue();
			FileUtils.appendToFile("files/logs/commandUsages.log", log);
			if (cmd.getRequiredCustomPermissions() != null && !author.hasPermission(cmd.getRequiredCustomPermissions())) {
				cmde.error("missingpermissions", cmde.getTranslation("requiredcustompermissions") + "\n" + Utils.getPermissionString(cmd.getRequiredCustomPermissions()));
				return;
			}

			StatisticsManager.commandExecuted();

			final Permission[] requiredBotPermissions = cmd.getRequiredBotPermissions() != null ? cmd.getRequiredBotPermissions() : new Permission[] {};
			final Permission[] requiredPermissions = cmd.getRequiredPermissions() != null ? cmd.getRequiredPermissions() : new Permission[] {};
			if (Utils.handleRights(guild, author, channel, requiredBotPermissions)) return;

			if (!member.hasPermission(Utils.concatenate(requiredPermissions, requiredBotPermissions))) {
				if (!cmd.isVisible(author)) return;
				cmde.error("missingpermissions", cmde.getTranslation("requiredpermissions") + "\n" + Utils.getPermissionString(cmd.getRequiredPermissions()));
				return;
			} else if (Utils.handleRights(guild, author, channel, requiredBotPermissions)) return;
			else if (cmd.isPremiumCommand() && !guild.getGuildType().higherThanOrEqual(GuildType.PREMIUM)) {
				event.replyEmbeds(EmbedUtils.premiumRequired(author, guild)).queue();
				return;
			}

			System.out.println("excecutdasdf");

			Bot.executor.submit(() -> {
				System.out.println("bfore set");
				cmde.setCommand(cmd);
				System.out.println("after set: " + cmd);
				cmd.execute(cmde, event, member, author, guild, channel);
				System.out.println("excteudas downe");
			});
			System.out.println("set thingygsdfsdfadfs thru");
		}
	}

	public static void addCommand(final SlashCommand c) {
		final String commandName = c.getData().getName();
		if (!commands.containsKey(commandName)) {
			final Category category = c.getCategory();
			final List<SlashCommand> commandsInCat = Optional.ofNullable(commandsInCategory.get(category)).orElse(new ArrayList<>());
			commandsInCat.add(c);
			commandsInCategory.put(category, commandsInCat);

			commands.put(commandName, new Pair<>(null, c));
		}
	}
}