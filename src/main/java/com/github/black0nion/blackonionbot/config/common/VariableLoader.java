package com.github.black0nion.blackonionbot.config.common;

import com.github.black0nion.blackonionbot.utils.Incrementer;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a list of strings in the format of {@code key=value} and calls the supplied BiConsumer.
 */
public class VariableLoader {

	private final Pattern envFilePattern;

	public VariableLoader() {
		this.envFilePattern = Pattern.compile("^(\\w+)=(.*)$");
	}

	public VariableLoader(Pattern pattern) {
		this.envFilePattern = pattern;
	}


	@SuppressWarnings("CheckStyle")
	public void loadVariables(List<String> list, Incrementer count, BiConsumer<String, String> consumer) {
		list.stream()
			.filter(line -> !line.startsWith("#"))
			.map(envFilePattern::matcher)
			.filter(Matcher::matches)
			.peek(c -> { if (count != null) count.increment(); }) // NOSONAR
			.forEach(split -> consumer.accept(split.group(1), split.group(2)));
	}
}
