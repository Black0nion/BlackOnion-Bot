package com.github.black0nion.blackonionbot.config.discord.api.container;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SettingsContainerSerializer extends StdSerializer<SettingsContainer<?>> {

	public SettingsContainerSerializer() {
		this(null);
	}

	protected SettingsContainerSerializer(Class<SettingsContainer<?>> t) {
		super(t);
	}

	@Override
	public void serialize(SettingsContainer<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeObjectField("settings", value.getSettings());
		gen.writeObjectField("id", value.getIdentifier());
		gen.writeEndObject();
	}
}
