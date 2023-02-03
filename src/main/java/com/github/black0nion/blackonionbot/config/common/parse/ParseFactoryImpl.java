package com.github.black0nion.blackonionbot.config.common.parse;

import java.util.function.Function;

public class ParseFactoryImpl<I, O> implements ParseFactory<I, O> {

	private final Function<I, O> parseFunction;
	private final Class<O> outputClass;

	public ParseFactoryImpl(Class<O> outputClass, Function<I, O> parseFunction) {
		this.outputClass = outputClass;
		this.parseFunction = parseFunction;
	}

	@Override
	public Class<O> getOutputClass() {
		return outputClass;
	}

	@Override
	public O parseImpl(I input) throws Exception {
		return parseFunction.apply(input);
	}
}
