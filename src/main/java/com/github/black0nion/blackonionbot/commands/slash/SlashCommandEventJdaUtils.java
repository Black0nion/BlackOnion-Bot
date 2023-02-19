package com.github.black0nion.blackonionbot.commands.slash;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public interface SlashCommandEventJdaUtils extends SlashCommandEventUtilsBase {

	default String getCommandHelp(SlashCommand command) {
		return "/" + command.getData().getName() + " " + command.getData().getOptions().stream().map(data -> data.getName() + " : " + data.getType().name()).collect(Collectors.joining(", "));
	}

	@Nonnull
	default <T> T getOption(@Nonnull String name, @Nonnull Function<? super OptionMapping, ? extends T> resolver) {
		return requireNonNull(getOptionNullable(name, resolver));
	}

	@Nullable
	default <T> T getOptionNullable(@Nonnull String name, @Nonnull Function<? super OptionMapping, ? extends T> resolver) {
		return getEvent().getOption(name, resolver);
	}

	default <T> T getOption(@Nonnull String name, @Nullable Supplier<? extends T> fallback, @Nonnull Function<? super OptionMapping, ? extends T> resolver) {
		return requireNonNull(getEvent().getOption(name, fallback, resolver));
	}

	default String getSubcommandName() {
		return requireNonNull(getEvent().getSubcommandName());
	}

	default String getSubcommandGroup() {
		return requireNonNull(getEvent().getSubcommandGroup());
	}
}
