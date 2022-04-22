package com.github.black0nion.blackonionbot.commands;

import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.CustomPermission;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.github.black0nion.blackonionbot.wrappers.StartsWithArrayList;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.*;

import static com.github.black0nion.blackonionbot.utils.Utils.gOD;

@SuppressWarnings("unused")
public abstract class SlashCommand extends GenericCommand {

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

    protected SlashCommand(String name, String description) {
        this(builder(Commands.slash(name, description)));
    }

    protected SlashCommand(SlashCommandBuilder builder) {
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
        if (!builder.getAutoComplete().isEmpty()) {
            builder.getAutoComplete().entrySet().forEach(this::updateAutoComplete);
        }
    }

    public abstract void execute(final SlashCommandEvent cmde, final SlashCommandInteractionEvent e, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel);

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

    @Override
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

    @Override
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

    public boolean isHidden(final BlackUser user) {
        return !user.hasPermission(this.requiredCustomPermissions);
    }

    @Nonnull
    protected static SlashCommandBuilder builder(@Nonnull SlashCommandData data) {
        return SlashCommandBuilder.builder(data);
    }

    @Nonnull
    protected static SlashCommandBuilder builder(@Nonnull String name, @Nonnull String description) {
        return SlashCommandBuilder.builder(Commands.slash(name, description));
    }

    @Override
    public String toString() {
        return "SlashCommand [data=" + this.data + ", category=" + this.category + ", progress=" + this.progress + ", requiredPermissions=" + Arrays.toString(this.requiredPermissions) + ", requiredBotPermissions=" + Arrays.toString(this.requiredBotPermissions) + ", requiredCustomPermissions=" + Arrays.toString(this.requiredCustomPermissions) + ", isToggleable=" + this.isToggleable + ", shouldAutoRegister=" + this.shouldAutoRegister + ", isPremium=" + this.isPremium + "]";
    }
}