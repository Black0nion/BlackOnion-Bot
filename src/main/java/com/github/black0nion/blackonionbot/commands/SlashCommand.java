package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.misc.enums.Category;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.misc.enums.Progress;
import com.github.black0nion.blackonionbot.wrappers.StartsWithArrayList;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.*;

import static com.github.black0nion.blackonionbot.utils.Utils.gOD;

/**
 * This class represents a SlashCommand that can be executed by users.
 * On every execution, it will run the {@link SlashCommand#execute(SlashCommandEvent, SlashCommandInteractionEvent, BlackMember, BlackUser, BlackGuild, TextChannel) execute} method.
 * <p>
 * Implement a command by doing this:
 * <pre>{@code
 * public class MyCommand extends SlashCommand {
 * 	public MyCommand() {
 * 		super(builder(Commands.slash("mycommand", "My command description")
 * 				.addOption(OptionType.STRING, "name", "The name of the person to greet", true))
 * 			.setCategory(Category.MISC)
 * 			.permissions(CustomPermission.MY_PERMISSION)
 * 		);
 *        }
 *
 *    @Override
 *    public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
 * 		cmde.send("hello", new Placeholder("name", e.getOption("name", OptionMapping::getAsString)));
 *    }
 * }</pre>
 */
public abstract class SlashCommand {
	private final SlashCommandData data;
	private Category category;
	private final Progress progress;
	private final Permission[] requiredPermissions;
	private final Permission[] requiredBotPermissions;
	private final CustomPermission[] requiredCustomPermissions;
	private final boolean isToggleable;
	private final boolean shouldAutoRegister;
	private final boolean isPremium;
	private final boolean isEphemeral;
	private final boolean isAdminGuild;
	/**
	 * option name : choices
	 */
	private final Map<String, StartsWithArrayList> autoCompletes = new HashMap<>();
	protected final Config config;

	protected SlashCommand(String name, String description) {
		this(name, description, null);
	}

	/**
	 * Creates a new SlashCommand with an empty builder that only has the required {@link SlashCommandData}.
	 */
	protected SlashCommand(String name, String description, Config config) {
		this(builder(Commands.slash(name, description)), config);
	}

	protected SlashCommand(SlashCommandBuilder builder) {
		this(builder, null);
	}

	protected SlashCommand(SlashCommandBuilder builder, Config config) {
		this.data = builder.getData();
		this.category = gOD(builder.getCategory(), Category.OTHER);
		this.progress = gOD(builder.getProgress(), Progress.DONE);
		this.requiredPermissions = builder.getRequiredPermissions();
		this.requiredBotPermissions = builder.getRequiredBotPermissions();
		this.requiredCustomPermissions = builder.getRequiredCustomPermissions();
		this.isToggleable = builder.isToggleable();
		this.shouldAutoRegister = builder.shouldAutoRegister();
		this.isPremium = builder.isPremium();
		this.isEphemeral = builder.isEphemeral();
		this.isAdminGuild = builder.isAdminGuild();
		this.config = config;
		if (!builder.getAutoComplete().isEmpty()) {
			builder.getAutoComplete().entrySet().forEach(this::updateAutoComplete);
		}
	}

	public abstract void execute(final SlashCommandEvent cmde, final SlashCommandInteractionEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) throws SQLException;

	protected void updateAutoComplete(Map.Entry<String, StartsWithArrayList> entry) {
		Checks.notNull(entry, "Entry");
		this.updateAutoComplete(entry.getKey(), entry.getValue());
	}

	protected void updateAutoComplete(String option, Collection<String> values) {
		Checks.notNull(option, "Option");
		Checks.notNull(values, "Values");
		Checks.notEmpty(values, "Values");
		autoCompletes.put(option, values instanceof StartsWithArrayList value ? value : new StartsWithArrayList(values));
	}

	public void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
		StartsWithArrayList autoComplete = autoCompletes.get(event.getFocusedOption().getName());
		Checks.notNull(autoComplete, "AutoComplete Choices");
		List<String> options = autoComplete.getElementsStartingWith(event.getFocusedOption().getValue(), true);
		event.replyChoices(options.stream().map(m -> new Command.Choice(m, m)).limit(25).toList()).queue();
	}

	public String getName() {
		return data.getName();
	}

	//region Getters
	public SlashCommandData getData() {
		return data;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Progress getProgress() {
		return progress;
	}

	public Permission[] getRequiredPermissions() {
		return requiredPermissions;
	}

	public Permission[] getRequiredBotPermissions() {
		return requiredBotPermissions;
	}

	public CustomPermission[] getRequiredCustomPermissions() {
		return requiredCustomPermissions;
	}

	public boolean isToggleable() {
		return isToggleable;
	}

	public boolean shouldAutoRegister() {
		return shouldAutoRegister;
	}

	public boolean isPremiumCommand() {
		return isPremium;
	}

	public boolean isEphemeral() {
		return isEphemeral;
	}

	public boolean isAdminGuild() {
		return isAdminGuild;
	}
	//endregion

	/**
	 * @return if the user doesn't have the required {@link SlashCommand#requiredCustomPermissions}. REQUIRES ALL PERMISSIONS!
	 */
	public boolean isHidden(final BlackUser user) {
		return !user.hasPermission(this.requiredCustomPermissions);
	}

	@Nonnull
	protected static SlashCommandBuilder builder(@Nonnull SlashCommandData data) {
		return new SlashCommandBuilder(data);
	}

	@Nonnull
	protected static SlashCommandBuilder builder(@Nonnull String name, @Nonnull String description) {
		return new SlashCommandBuilder(Commands.slash(name, description));
	}

	@Override
	public String toString() {
		return "SlashCommand{" +
			"data=" + data +
			", category=" + category +
			", progress=" + progress +
			", requiredPermissions=" + Arrays.toString(requiredPermissions) +
			", requiredBotPermissions=" + Arrays.toString(requiredBotPermissions) +
			", requiredCustomPermissions=" + Arrays.toString(requiredCustomPermissions) +
			", isToggleable=" + isToggleable +
			", shouldAutoRegister=" + shouldAutoRegister +
			", isPremium=" + isPremium +
			", isEphemeral=" + isEphemeral +
			", isAdminGuild=" + isAdminGuild +
			", autoCompletes=" + autoCompletes +
			'}';
	}
}
