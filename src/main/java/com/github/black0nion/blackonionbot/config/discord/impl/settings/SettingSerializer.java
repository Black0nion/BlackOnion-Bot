package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;

import java.io.IOException;

public class SettingSerializer extends StdSerializer<Setting<?>> {

	public SettingSerializer() {
		this(null);
	}

	protected SettingSerializer(Class<Setting<?>> t) {
		super(t);
	}

	@Override
	public void serialize(Setting<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("name", value.getName());
		gen.writeObjectField("value", value.toSerializedValue());
		gen.writeEndObject();
	}
}
