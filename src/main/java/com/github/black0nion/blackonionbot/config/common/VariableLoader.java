package com.github.black0nion.blackonionbot.config.common;

import com.github.black0nion.blackonionbot.utils.Incrementer;
import com.github.black0nion.blackonionbot.utils.Utils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a list of strings in the format of {@code key=value} and calls the supplied BiConsumer.
 */
public class VariableLoader {

	private static final Pattern ENV_FILE_PATTERN = Pattern.compile("^(\\w+)=(.*)$");

	private VariableLoader() {}


	@SuppressWarnings("CheckStyle")
	public static void loadVariables(List<String> list, Incrementer count, BiConsumer<String, String> consumer) {
		list.stream()
			.filter(line -> !line.startsWith("#"))
			.map(ENV_FILE_PATTERN::matcher)
			.filter(Matcher::matches)
			.peek(c -> { if (count != null) count.increment(); }) // NOSONAR
			.forEach(split -> consumer.accept(split.group(1), split.group(2)));
	}
}
