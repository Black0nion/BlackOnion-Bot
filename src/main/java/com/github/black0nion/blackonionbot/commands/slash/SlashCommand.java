package com.github.black0nion.blackonionbot.commands.slash;

import com.github.black0nion.blackonionbot.commands.common.AbstractCommand;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.wrappers.StartsWithArrayList;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.*;

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
public abstract class SlashCommand extends AbstractCommand<SlashCommandBuilder, SlashCommandData> {

	/**
	 * option name : choices
	 */
	private final Map<String, StartsWithArrayList> autoCompletes = new HashMap<>();


	//region Constructors
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
		super(builder, config);

		// TODO: test if this works
		this.data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(this.requiredPermissions));

		if (!builder.getAutoComplete().isEmpty()) {
			builder.getAutoComplete().entrySet().forEach(this::updateAutoComplete);
		}
	}
	//endregion

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
