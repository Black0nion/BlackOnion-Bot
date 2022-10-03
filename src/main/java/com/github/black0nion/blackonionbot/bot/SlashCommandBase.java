package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.commands.admin.BanUsageCommand;
import com.github.black0nion.blackonionbot.commands.bot.ToggleCommand;
import com.github.black0nion.blackonionbot.commands.information.HelpCommand;
import com.github.black0nion.blackonionbot.config.api.Config;
import com.github.black0nion.blackonionbot.inject.Injector;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.GuildType;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.misc.RunMode;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.utils.*;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.mongodb.client.model.Filters;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * This class contains the main logic behind the commands of the bot.
 *
 * It'll handle things like permissions, command auto-complete, executing commands, etc.
 */
public class SlashCommandBase extends ListenerAdapter {

	private final Map<Category, List<SlashCommand>> commandsInCategory = new EnumMap<>(Category.class);

	public Map<Category, List<SlashCommand>> getCommandsInCategory() {
		return commandsInCategory;
	}

	private final Map<String, Pair<Long, SlashCommand>> commands = new HashMap<>();

	public Map<String, Pair<Long, SlashCommand>> getCommands() {
		return commands;
	}

	private final Map<Class<? extends SlashCommand>, SlashCommand> commandInstances = new HashMap<>();

	private JSONObject commandsJson;

	public JSONObject getCommandsJson() {
		return commandsJson;
	}

	private final ExecutorService commandPool = Executors.newCachedThreadPool();

	private static final Logger logger = LoggerFactory.getLogger(SlashCommandBase.class);
	private static SlashCommandBase instance;

	public static SlashCommandBase getInstance() {
		return instance;
	}

	private final Config config;
	private final Injector injector;

	public SlashCommandBase(Config config, Injector injector) {
		instance = this;
		this.config = config;
		this.injector = injector;
	}

	public void addCommands() {
		commandCount = 0;
		commands.clear();
		commandsInCategory.clear();
		commandInstances.clear();
		commandsJson = new JSONObject();
		final JSONArray commandsArr = new JSONArray();
		final Reflections reflections = new Reflections(SlashCommand.class.getPackage().getName());
		final Set<Class<? extends SlashCommand>> annotated = reflections.getSubTypesOf(SlashCommand.class);

		for (final Class<?> command : annotated) {
			try {
				final SlashCommand newInstance = injector.createInstance(command, SlashCommand.class);
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

					commandsArr.put(commandJSON);
				}

				addCommand(newInstance);
			} catch (Exception e) {
				e.printStackTrace();
				// 10/10 error handling
				System.exit(-1);
			}
		}
		commandsJson.put("commands", commandsArr);
		logger.info("Generated Commands JSON: {}", commandsArr);
		// Bot.jda.updateCommands().addCommands(commands.values().stream().map(Pair::getValue).map(SlashCommand::getData).collect(Collectors.toList())).queue();

		Optional.ofNullable(getCommand(ToggleCommand.class)).ifPresent(ToggleCommand::updateAutoComplete);
		Optional.ofNullable(getCommand(HelpCommand.class)).ifPresent(HelpCommand::updateAutoComplete);

		Bot.getInstance().getExecutor().submit(Dashboard::init);
	}

	@SuppressWarnings("unchecked")
	public static <T extends SlashCommand> T getCommand(Class<T> clazz) {
		return (T) getInstance().commandInstances.get(clazz);
	}

	@Nullable
	public static SlashCommand getCommand(String name) {
		if (name == null) return null;
		return Utils.tryGet(() -> getInstance().commands.get(name).getValue());
	}

	@Reloadable("dev commands")
	private static void updateCommandsDev() {
		getInstance().updateCommandsDev(Bot.getInstance().getJDA());
	}

	public void updateCommandsDev(JDA jda) {
		if (config.getDevGuild() != -1) {
			logger.info("Updating dev commands...");
			Optional.ofNullable(jda.getGuildById(config.getDevGuild()))
				.ifPresentOrElse(guild -> guild.updateCommands()
					.addCommands(commands.values().stream()
						.map(Pair::getValue)
						.map(SlashCommand::getData)
						.toList())
					.queue(), () -> logger.warn("Failed to update dev commands: dev guild set, but not found"));
		} else logger.warn("Failed to update dev commands: dev guild not set");
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
		if (event.getUser().isBot() || (event.getGuild() != null && !event.getGuild().getSelfMember().hasPermission(event.getGuildChannel(), Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)))
			return;

		final BlackUser author = BlackUser.from(event.getUser());
		final BlackGuild guild = BlackGuild.from(event.getGuild());
		final BlackMember member = BlackMember.from(event.getMember());

		assert guild != null && member != null;

		final TextChannel channel = event.getChannel().asTextChannel();

		final boolean locked = BanUsageCommand.collection.find(Filters.or(Filters.eq("guildid", guild.getIdLong()), Filters.eq("userid", author.getIdLong()))).first() != null;
		final String log = EmojiParser.parseToAliases(guild.getName() + "(G:" + guild.getId() + ") > " + channel.getName() + "(C:" + channel.getId() + ") | " + author.getName() + "#" + author.getDiscriminator() + "(U:" + author.getId() + "): (M:" + event.getId() + ")" + event.getCommandPath() + " " + event.getOptions().stream().map(OptionMapping::toString).collect(Collectors.joining(" ")).replace("\n", "\\n"));

		if (config.getRunMode() == RunMode.DEV) {
			if (locked) logger.warn(log);
			else logger.info(log);
			FileUtils.appendToFile("files/logs/messagelog/" + guild.getId() + "/" + EmojiParser.parseToAliases(channel.getName()).replaceAll(":([^:\\s]*(?:::[^:\\s]*)*):", "($1)").replace(":", "_") + "_" + channel.getId() + ".log", log);
		}

		if (locked) {
			// haha funni
			event.reply("https://tenor.com/view/you-got-banned-banned-banned-message-mickey-mouse-club-gif-17668002").queue();
			return;
		}

		if (Utils.handleSelfRights(guild, author, channel, event, Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) return;
		if (commands.containsKey(event.getName())) {
			final SlashCommand cmd = commands.get(event.getName()).getValue();
			final SlashCommandEvent cmde = new SlashCommandEvent(cmd, event, guild, member, author);
			final boolean disabled = !guild.isCommandActivated(cmd);
			if (disabled) {
				cmde.send("commanddisabled", new Placeholder("cmd", cmd.getName()));
				return;
			}

			FileUtils.appendToFile("files/logs/commandUsages.log", log);
			if (cmd.getRequiredCustomPermissions() != null && !author.hasPermission(cmd.getRequiredCustomPermissions())) {
				cmde.send("missingpermissions", new Placeholder("perms", Utils.getPermissionString(cmd.getRequiredCustomPermissions())));
				return;
			}

			StatisticsManager.COMMANDS_EXECUTED.labels("slash", event.getCommandPath(), guild.getId(), guild.getName(), channel.getId(), channel.getName()).inc();
			StatisticsManager.TOTAL_COMMANDS_EXECUTED.inc();

			final Permission[] requiredBotPermissions = cmd.getRequiredBotPermissions() != null ? cmd.getRequiredBotPermissions() : Permission.EMPTY_PERMISSIONS;
			final Permission[] requiredPermissions = cmd.getRequiredPermissions() != null ? cmd.getRequiredPermissions() : Permission.EMPTY_PERMISSIONS;
			if (Utils.handleSelfRights(guild, author, channel, event, requiredBotPermissions)) return;

			if (!member.hasPermission(requiredPermissions)) {
				if (cmd.isHidden(author)) return;
				cmde.send("missingpermissions", new Placeholder("perms", Utils.getPermissionString(cmd.getRequiredPermissions())));
				return;
			} else if (Utils.handleSelfRights(guild, author, channel, event, requiredBotPermissions))
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

	private void addCommand(final SlashCommand c) {
		if (c == null) throw new NullPointerException("Command is null");
		if (c.getData() == null) throw new NullPointerException("Command data is null");

		getInstance().commands.computeIfAbsent(c.getData().getName(), s -> {
			final Category category = c.getCategory();
			final List<SlashCommand> commandsInCat = Optional.ofNullable(getInstance().commandsInCategory.get(category)).orElseGet(ArrayList::new);
			commandsInCat.add(c);
			getInstance().commandsInCategory.put(category, commandsInCat);
			getInstance().commandInstances.put(c.getClass(), c);
			getInstance().commandCount++;
			return new Pair<>(null, c);
		});
	}

	private int commandCount = 0;

	public static int getCommandCount() {
		return getInstance().commandCount;
	}
}
