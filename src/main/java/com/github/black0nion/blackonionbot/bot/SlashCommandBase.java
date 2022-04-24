package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.slashcommands.SlashCommand;
import com.github.black0nion.blackonionbot.slashcommands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.slashcommands.admin.BanUsageCommand;
import com.github.black0nion.blackonionbot.slashcommands.bot.ToggleCommand;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.slashcommands.information.HelpCommand;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.utils.*;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.mongodb.client.model.Filters;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SlashCommandBase extends ListenerAdapter {

	public static final HashMap<Category, List<SlashCommand>> commandsInCategory = new HashMap<>();

	public static final HashMap<String, Pair<Long, SlashCommand>> commands = new HashMap<>();

	public static final HashMap<Class<? extends SlashCommand>, SlashCommand> commandInstances = new HashMap<>();

	public static JSONObject commandsJson;

	private static final ExecutorService commandPool = Executors.newCachedThreadPool();

	private static final Logger logger = LoggerFactory.getLogger(SlashCommandBase.class);

	public static void addCommands() {
		commands.clear();
		commandsInCategory.clear();
		commandsJson = new JSONObject();
		final JSONArray commands = new JSONArray();
		final Reflections reflections = new Reflections(SlashCommand.class.getPackage().getName());
		final Set<Class<? extends SlashCommand>> annotated = reflections.getSubTypesOf(SlashCommand.class);

		for (final Class<?> command : annotated) {
			try {
				final SlashCommand newInstance = (SlashCommand) command.getConstructor().newInstance();
				final String[] packageName = command.getPackage().getName().split("\\.");
				final Category parsedCategory = Category.parse(packageName[packageName.length - 1]);
				newInstance.setCategory(parsedCategory != null ? parsedCategory : newInstance.getCategory());

				SlashCommandData data = newInstance.getData();
				if (newInstance.getRequiredCustomPermissions() == null || newInstance.getRequiredCustomPermissions().length == 0) {
					final JSONObject commandJSON = new JSONObject()
						.put("name", data.getName())
						.put("description", data.getDescription())
						.put("permissions", newInstance.getRequiredPermissions())
						.put("options", Utils.optionsToJson(data.getOptions()));

					JSONArray groupJson = new JSONArray();
					for (SubcommandGroupData subcommandGroupData : data.getSubcommandGroups()) {
						final JSONObject subcommandGroupJson = new JSONObject()
							.put("name", subcommandGroupData.getName())
							.put("description", subcommandGroupData.getDescription());

						final JSONArray subcommandJson = new JSONArray();
						for (SubcommandData subcommandData : subcommandGroupData.getSubcommands()) {
							JSONObject subcommandObjJson = new JSONObject()
								.put("name", subcommandData.getName())
								.put("description", subcommandData.getDescription());
							subcommandObjJson.put("options", Utils.optionsToJson(subcommandData.getOptions()));
							subcommandJson.put(subcommandObjJson);
						}
						subcommandGroupJson.put("subcommands", subcommandJson);
						groupJson.put(subcommandGroupJson);
					}
					commandJSON.put("subcommand_groups", groupJson);

					JSONObject subcommandJson = new JSONObject();
					for (SubcommandData subcommandData : data.getSubcommands()) {
						subcommandJson.put(subcommandData.getName(), new JSONObject()
							.put("name", subcommandData.getName())
							.put("description", subcommandData.getDescription())
							.put("options", Utils.optionsToJson(subcommandData.getOptions())));
					}
					commandJSON.put("subcommands", subcommandJson);

					commands.put(commandJSON);
				}

				if (newInstance.getData() != null) addCommand(newInstance);
					// should be covered by the builder
				else throw new RuntimeException("Command " + command.getName() + " has no data!");
			} catch (Exception e) {
				e.printStackTrace();
				// 10/10 error handling
				System.exit(-1);
			}
		}
		commandsJson.put("commands", commands);
		logger.info("Generated Commands JSON: " + commands);
		// Bot.jda.updateCommands().addCommands(commands.values().stream().map(Pair::getValue).map(SlashCommand::getData).collect(Collectors.toList())).queue();

		Optional.ofNullable(getCommand(ToggleCommand.class)).ifPresent(ToggleCommand::updateAutoComplete);
		Optional.ofNullable(getCommand(HelpCommand.class)).ifPresent(HelpCommand::updateAutoComplete);

		Bot.getInstance().getExecutor().submit(Dashboard::init);
	}

	@SuppressWarnings("unchecked")
	public static <T extends SlashCommand> T getCommand(Class<T> clazz) {
		return (T) commandInstances.get(clazz);
	}

	public static void updateCommandsDev(JDA jda) {
		if (Config.dev_guild != -1) {
			Objects.requireNonNull(jda.getGuildById(Config.dev_guild))
				.updateCommands()
				.addCommands(commands.values().stream()
					.map(Pair::getValue)
					.map(SlashCommand::getData)
					.toList())
				.queue();
		}
	}

	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		String name = event.getName();
		if (commands.containsKey(name)) {
			try {
				commands.get(name).getValue().handleAutoComplete(event);
			} catch (Exception e) {
				logger.error("An issue happened trying to handle AutoComplete", e);
			}
		}
	}

	@Override
	public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
		if (event.getUser().isBot()) return;

		final BlackUser author = BlackUser.from(event.getUser());
		final BlackGuild guild = BlackGuild.from(event.getGuild());
		final BlackMember member = BlackMember.from(event.getMember());

		assert guild != null && member != null;

		final TextChannel channel = event.getTextChannel();

		final boolean locked = BanUsageCommand.collection.find(Filters.or(Filters.eq("guildid", guild.getIdLong()), Filters.eq("userid", author.getIdLong()))).first() != null;
		final String log = EmojiParser.parseToAliases(guild.getName() + "(G:" + guild.getId() + ") > " + channel.getName() + "(C:" + channel.getId() + ") | " + author.getName() + "#" + author.getDiscriminator() + "(U:" + author.getId() + "): (M:" + event.getId() + ")" + event.getCommandPath() + " " + event.getOptions().stream().map(OptionMapping::toString).collect(Collectors.joining(" ")).replace("\n", "\\n"));

		if (Config.run_mode == RunMode.DEV) {
			if (locked) logger.warn(log);
			else logger.info(log);
			FileUtils.appendToFile("files/logs/messagelog/" + guild.getId() + "/" + EmojiParser.parseToAliases(channel.getName()).replaceAll(":([^:\\s]*(?:::[^:\\s]*)*):", "($1)").replace(":", "_") + "_" + channel.getId() + ".log", log);
		}

		if (locked) {
			// haha funni
			event.reply("https://tenor.com/view/you-got-banned-banned-banned-message-mickey-mouse-club-gif-17668002").queue();
			return;
		}

		final SlashCommandEvent cmde = new SlashCommandEvent(event, guild, member, author);

		if (Utils.handleRights(guild, author, channel, Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) return;
		if (commands.containsKey(event.getName())) {
			final SlashCommand cmd = commands.get(event.getName()).getValue();
			cmde.setCommand(cmd);
			final boolean disabled = !guild.isCommandActivated(cmd);
			if (disabled) {
				cmde.send("commanddisabled", new Placeholder("cmd", "/" + cmd.getName()));
				return;
			}
			FileUtils.appendToFile("files/logs/commandUsages.log", log);
			if (cmd.getRequiredCustomPermissions() != null && !author.hasPermission(cmd.getRequiredCustomPermissions())) {
				cmde.error("missingpermissions", cmde.getTranslation("requiredcustompermissions") + "\n" + Utils.getPermissionString(cmd.getRequiredCustomPermissions()));
				return;
			}

			StatisticsManager.COMMANDS_EXECUTED.labels("slash", event.getCommandPath(), guild.getId(), guild.getName(), channel.getId(), channel.getName()).inc();
			StatisticsManager.TOTAL_COMMANDS_EXECUTED.inc();

			final Permission[] requiredBotPermissions = cmd.getRequiredBotPermissions() != null ? cmd.getRequiredBotPermissions() : new Permission[] {};
			final Permission[] requiredPermissions = cmd.getRequiredPermissions() != null ? cmd.getRequiredPermissions() : new Permission[] {};
			if (Utils.handleRights(guild, author, channel, requiredBotPermissions)) return;

			if (!member.hasPermission(Utils.concatenate(requiredPermissions, requiredBotPermissions))) {
				if (cmd.isHidden(author)) return;
				cmde.error("missingpermissions", cmde.getTranslation("requiredpermissions") + "\n" + Utils.getPermissionString(cmd.getRequiredPermissions()));
				return;
			} else if (Utils.handleRights(guild, author, channel, requiredBotPermissions))
				return;
			else if (cmd.isPremiumCommand() && !guild.getGuildType().higherThanOrEqual(GuildType.PREMIUM)) {
				event.replyEmbeds(EmbedUtils.premiumRequired(author, guild)).queue();
				return;
			}

			commandPool.submit(() -> {
				try {
					cmd.execute(cmde, event, member, author, guild, channel);
				} catch (Throwable t) {
					if (!(t instanceof DummyException))
						cmde.exception(t);
				}
			});
		}
	}

	public static void addCommand(final SlashCommand c) {
		assert c != null;
		final String commandName = c.getData().getName();
		if (!commands.containsKey(commandName)) {
			final Category category = c.getCategory();
			final List<SlashCommand> commandsInCat = Optional.ofNullable(commandsInCategory.get(category)).orElseGet(ArrayList::new);
			commandsInCat.add(c);
			commandsInCategory.put(category, commandsInCat);
			commands.put(commandName, new Pair<>(null, c));
			commandInstances.put(c.getClass(), c);
		}
	}
}