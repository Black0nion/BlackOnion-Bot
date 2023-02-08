package com.github.black0nion.blackonionbot.bot;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommand;
import com.github.black0nion.blackonionbot.commands.common.AbstractCommandEvent;
import com.github.black0nion.blackonionbot.commands.common.Category;
import com.github.black0nion.blackonionbot.commands.message.MessageCommand;
import com.github.black0nion.blackonionbot.commands.message.MessageCommandEvent;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.commands.slash.impl.admin.BanUsageCommand;
import com.github.black0nion.blackonionbot.commands.slash.impl.bot.ToggleCommand;
import com.github.black0nion.blackonionbot.commands.slash.impl.information.HelpCommand;
import com.github.black0nion.blackonionbot.config.discord.api.repo.SettingsRepo;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.featureflags.FeatureFlags;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.database.DatabaseConnector;
import com.github.black0nion.blackonionbot.inject.Injector;
import com.github.black0nion.blackonionbot.inject.InjectorCreateInstanceException;
import com.github.black0nion.blackonionbot.misc.enums.GuildType;
import com.github.black0nion.blackonionbot.misc.enums.RunMode;
import com.github.black0nion.blackonionbot.stats.StatisticsManager;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.systems.reload.ReloadSystem;
import com.github.black0nion.blackonionbot.systems.reload.Reloadable;
import com.github.black0nion.blackonionbot.utils.*;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * This class contains the main logic behind the commands of the bot.
 *<br>
 * It'll handle things like permissions, command auto-complete, executing commands, etc.
 */
public class SlashCommandBase extends ListenerAdapter implements Reloadable, CommandRegistry {

	private final Map<Category, List<AbstractCommand<?, ?>>> commandsInCategory = new EnumMap<>(Category.class);
	private SettingsRepo<UserSettings> userSettingsRepo;
	private SettingsRepo<GuildSettings> guildSettingsRepo;

	public Map<Category, List<AbstractCommand<?, ?>>> getCommandsInCategory() {
		return commandsInCategory;
	}

	private final Map<String, Pair<Long, AbstractCommand<?, ?>>> commands = new HashMap<>();

	public Map<String, Pair<Long, AbstractCommand<?, ?>>> getCommands() {
		return commands;
	}

	private final Map<Class<? extends AbstractCommand<?, ?>>, AbstractCommand<?, ?>> commandInstances = new HashMap<>();

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
	private final ReloadSystem reloadSystem;

	public SlashCommandBase(Config config, Injector injector, ReloadSystem reloadSystem) {
		instance = this; // NOSONAR
		this.config = config;
		this.injector = injector;
		this.reloadSystem = reloadSystem;
		reloadSystem.registerReloadable(this);
	}

	public void setUserSettingsRepo(SettingsRepo<UserSettings> userSettingsRepo) {
		if (this.userSettingsRepo != null) throw new IllegalStateException("UserSettingsRepo already set!");
		this.userSettingsRepo = userSettingsRepo;
	}

	public void setGuildSettingsRepo(SettingsRepo<GuildSettings> guildSettingsRepo) {
		if (this.guildSettingsRepo != null) throw new IllegalStateException("GuildSettingsRepo already set!");
		this.guildSettingsRepo = guildSettingsRepo;
	}

	@SuppressWarnings("rawtypes")
	public void addCommands() {
		commandCount = 0;
		commands.clear();
		commandsInCategory.clear();
		commandInstances.clear();
		commandsJson = new JSONObject();
		final JSONArray commandsArr = new JSONArray();
		final Reflections reflections = new Reflections(SlashCommand.class.getPackage().getName());
		final Set<Class<? extends AbstractCommand>> annotated = reflections.getSubTypesOf(AbstractCommand.class);

		for (final Class<? extends AbstractCommand> command : annotated) {
			if (Modifier.isAbstract(command.getModifiers())) continue;

			try {
				final AbstractCommand newInstance = injector.createInstance(command, AbstractCommand.class);
				final String[] packageName = command.getPackage().getName().split("\\.");
				final Category parsedCategory = Category.parse(packageName[packageName.length - 1]);
				newInstance.setCategory(parsedCategory != null ? parsedCategory : newInstance.getCategory());

				if (newInstance instanceof Reloadable reloadable) {
					// unregister if the command already exists
					if (reloadSystem.isRegistered(reloadable.getReloadName())) reloadSystem.unregisterReloadableByName(reloadable.getReloadName());

					// add new instance
					this.reloadSystem.registerReloadable(reloadable);
				}

				if (newInstance instanceof SlashCommand slashCommand) {
					SlashCommandData data = slashCommand.getData();
					if (newInstance.getRequiredCustomPermissions() == null || newInstance.getRequiredCustomPermissions().length == 0) {
						commandsArr.put(serializeCommand(slashCommand, data));
					}
				}

				addCommand(newInstance);
			} catch (InjectorCreateInstanceException e) {
				throw e;
			} catch (Exception e) {
				throw new IllegalArgumentException("Could not create instance of " + command.getName(), e);
			}
		}
		commandsJson.put("commands", commandsArr);
		logger.info("Generated Commands JSON: {}", commandsArr);

		Optional.ofNullable(getCommand(ToggleCommand.class)).ifPresent(ToggleCommand::updateAutoComplete);
		Optional.ofNullable(getCommand(HelpCommand.class)).ifPresent(HelpCommand::updateAutoComplete);

		Bot.getInstance().getExecutor().submit(Dashboard::init);
	}

	@Nonnull
	private static JSONObject serializeCommand(SlashCommand slashCommand, SlashCommandData data) {
		final JSONObject commandJSON = new JSONObject()
			.put("name", data.getName())
			.put("description", data.getDescription())
			.put("permissions", slashCommand.getRequiredPermissions())
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
		return commandJSON;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SlashCommand> T getCommand(Class<T> clazz) {
		return (T) commandInstances.get(clazz);
	}

	@Nullable
	@Override
	public AbstractCommand<?, ?> getCommand(String name) {
		if (name == null) return null;
		return Utils.tryGet(() -> getInstance().commands.get(name).getSecond());
	}

	@Override
	public void reload() {
		addCommands();
		updateCommandsDev(this.injector.getInstance(Bot.class).getJDA());
	}

	public void updateCommandsDev(JDA jda) {
		if (config.getDevGuild() != -1) {
			logger.info("Updating dev commands...");
			Optional.ofNullable(jda.getGuildById(config.getDevGuild()))
				.ifPresentOrElse(guild -> guild.updateCommands()
					.addCommands(commands.values().stream()
						.map(Pair::getSecond)
						.map(AbstractCommand::getData)
						.toList())
					.queue(cmds -> logger.info("Successfully updated {} dev commands!", cmds.size())),
					() -> logger.warn("Failed to update dev commands: dev guild set, but not found"));
		} else logger.warn("Failed to update dev commands: dev guild not set");
	}

	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		String name = event.getName();
		if (commands.containsKey(name) && commands.get(name).getSecond() instanceof SlashCommand command) {
			try {
				command.handleAutoComplete(event);
			} catch (Exception e) {
				logger.error("An issue happened trying to handle AutoComplete", e);
			}
		}
	}

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		if ("cancel".equals(event.getComponentId())) {
			// don't delete the defer or it won't delete the message
			event.deferEdit().flatMap(InteractionHook::deleteOriginal).queue();
			return;
		}
		String key = event.getComponentId().split("\\|")[0];
		if (commands.containsKey(key)) {
			try {
				commands.get(key).getSecond().handleButtonPress(event);
			} catch (Exception e) {
				logger.error("An issue happened trying to handle a Button", e);
			}
		}
	}

	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event) {
		String key = event.getModalId().split("\\|")[0];
		if (commands.containsKey(key)) {
			try {
				commands.get(key).getSecond().handleModalInteraction(event);
			} catch (Exception e) {
				logger.error("An issue happened trying to handle a Modal", e);
			}
		}
	}

	@Override
	public void onGenericSelectMenuInteraction(@NotNull GenericSelectMenuInteractionEvent event) {
		String key = event.getComponentId().split("\\|")[0];
		if (commands.containsKey(key)) {
			try {
				commands.get(key).getSecond().handleSelectMenuInteraction(event);
			} catch (Exception e) {
				logger.error("An issue happened trying to handle a SelectMenu", e);
			}
		}
	}

	@Override
	public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
		onCommandInteraction(event, event.getChannel().asTextChannel(), SlashCommand.class);
	}

	@Override
	public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
		onCommandInteraction(event, Objects.requireNonNull(event.getChannel()).asTextChannel(), MessageCommand.class);
	}

	public <E extends GenericCommandInteractionEvent, C extends AbstractCommand<?, ?>> void onCommandInteraction(final E event, TextChannel channel, Class<C> clazz) {
		if (event.getUser().isBot() || (event.getGuild() != null && !event.getGuild().getSelfMember().hasPermission(event.getGuildChannel(), Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)))
			return;

		final BlackUser author = BlackUser.from(event.getUser());
		final BlackGuild guild = BlackGuild.from(event.getGuild());
		final BlackMember member = BlackMember.from(event.getMember());

		assert guild != null && member != null;

		final boolean locked = BanUsageCommand.isBanned(injector.getInstance(FeatureFlags.class), injector.getInstance(DatabaseConnector.class), guild.getIdLong(), author.getIdLong());

		if (config.getRunMode() == RunMode.DEV) {
			final String log = EmojiParser.parseToAliases(guild.getDebugMessage()
				+ " > "
				+ channel.getName()
				+ "(C:" + channel.getId() + ") | " + author.getDebugMessage()
				+ ": (E:" + event.getId() + ")"
				+ event.getFullCommandName().replace(" ", "/") + " "
				+ event.getOptions().stream()
				.map(OptionMapping::toString)
				.collect(Collectors.joining(" "))
				.replace("\n", "\\n"));

			if (locked) logger.warn(log);
			else logger.info(log);
			FileUtils.appendToFile("files/logs/commandUsages.log", log);
			FileUtils.appendToFile("files/logs/messagelog/" + guild.getId() + "/" + EmojiParser.parseToAliases(channel.getName()).replaceAll(":([^:\\s]*(?:::[^:\\s]*)*):", "($1)").replace(":", "_") + "_" + channel.getId() + ".log", log);
		}

		final UserSettings userSettings = userSettingsRepo.getSettings(author.getIdLong());
		final GuildSettings guildSettings = guildSettingsRepo.getSettings(guild.getIdLong());

		if (locked) {
			// haha funni
			event.reply("https://tenor.com/view/you-got-banned-banned-banned-message-mickey-mouse-club-gif-17668002").queue();
			return;
		}

		LanguageSystem languageSystem = injector.getInstance(LanguageSystem.class);
		if (Utils.handleSelfRights(languageSystem, guild, author, channel, event, Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) return;
		if (commands.containsKey(event.getName())) {
			final AbstractCommand<?, ?> command = commands.get(event.getName()).getSecond();
			if (!clazz.isAssignableFrom(command.getClass())) return;

			final C cmd = clazz.cast(command);

			AbstractCommandEvent<?, ?> cmde;
			if (event instanceof SlashCommandInteractionEvent e1) cmde = new SlashCommandEvent((SlashCommand) cmd, e1, guild, member, author, languageSystem.getDefaultLanguage(), userSettings);
			else if (event instanceof MessageContextInteractionEvent e1) cmde = new MessageCommandEvent((MessageCommand) cmd, e1, guild, member, author, languageSystem.getDefaultLanguage(), userSettings);
			else throw new IllegalArgumentException("Unexpected value: " + cmd);

			final boolean disabled = guildSettings.getDisabledCommands().contains(cmd);
			if (disabled) {
				cmde.send("commanddisabled", new Placeholder("cmd", cmd.getName()));
				return;
			}

			if (cmd.getRequiredCustomPermissions() != null && !author.hasPermission(cmd.getRequiredCustomPermissions())) {
				cmde.send("missingpermissions", new Placeholder("perms", Utils.getPermissionString(cmd.getRequiredCustomPermissions())));
				return;
			}

			StatisticsManager.COMMANDS_EXECUTED.labels("slash", event.getFullCommandName().replace(" ", "/"), guild.getId(), guild.getName(), channel.getId(), channel.getName()).inc();
			StatisticsManager.TOTAL_COMMANDS_EXECUTED.inc();

			final Permission[] requiredBotPermissions = cmd.getRequiredBotPermissions() != null ? cmd.getRequiredBotPermissions() : Permission.EMPTY_PERMISSIONS;
			final Permission[] requiredPermissions = cmd.getRequiredPermissions() != null ? cmd.getRequiredPermissions() : Permission.EMPTY_PERMISSIONS;
			if (Utils.handleSelfRights(languageSystem, guild, author, channel, event, requiredBotPermissions)) return;

			if (!member.hasPermission(requiredPermissions)) {
				if (cmd instanceof SlashCommand slashCommand && slashCommand.isHidden(author)) return;
				cmde.send("missingpermissions", new Placeholder("perms", Utils.getPermissionString(cmd.getRequiredPermissions())));
				return;
			}

			if (Utils.handleSelfRights(languageSystem, guild, author, channel, event, requiredBotPermissions))
				return;

			if (cmd.isPremiumCommand() && !guild.getGuildType().higherThanOrEqual(GuildType.PREMIUM)) {
				event.replyEmbeds(injector.getInstance(EmbedUtils.class).premiumRequired(author, guild)).queue();
				return;
			}

			commandPool.submit(() -> {
				try {
					if (cmd instanceof SlashCommand slashCommand) {
						slashCommand.execute((SlashCommandEvent) cmde, (SlashCommandInteractionEvent) event, member, author, guild, channel, userSettings, guildSettings);
					} else if (cmd instanceof MessageCommand messageCommand) {
						messageCommand.execute((MessageCommandEvent) cmde, (MessageContextInteractionEvent) event, member, author, guild, channel, ((MessageContextInteractionEvent) event).getTarget());
					}
				} catch (Exception t) {
					if (!(t instanceof CommandReturnException))
						cmde.exception(t);
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	private void addCommand(final AbstractCommand<?, ?> c) {
		if (c == null) throw new NullPointerException("Command is null");
		if (c.getData() == null) throw new NullPointerException("Command data is null");

		commands.computeIfAbsent(c.getData().getName(), s -> {
			final Category category = c.getCategory();
			final List<AbstractCommand<?, ?>> commandsInCat = Optional.ofNullable(commandsInCategory.get(category)).orElseGet(ArrayList::new);
			commandsInCat.add(c);
			commandsInCategory.put(category, commandsInCat);
			// no clue what the fuck the error message means tbh
			commandInstances.put((Class<? extends AbstractCommand<?, ?>>) c.getClass(), c);
			commandCount++;
			return new Pair<>(null, c);
		});
	}

	private int commandCount = 0;

	public int getCommandCount() {
		return commandCount;
	}
}
