package com.github.black0nion.blackonionbot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

@SuppressWarnings("all")
public class Document {

	public static Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().setLenient()
			.create();
	protected static final JsonParser PARSER = new JsonParser();

	protected String name;
	private File file;

	private JsonObject dataCatcher;

	public Document(final String name) {
		this.name = name;
		this.dataCatcher = new JsonObject();
	}

	public Document(final String name, final JsonObject source) {
		this.name = name;
		this.dataCatcher = source;
	}

	public Document(final File file, final JsonObject jsonObject) {
		this.file = file;
		this.dataCatcher = jsonObject;
	}

	public Document(final String key, final String value) {
		this.dataCatcher = new JsonObject();
		this.append(key, value);
	}

	public Document(final String key, final Object value) {
		this.dataCatcher = new JsonObject();
		this.append(key, value);
	}

	public Document(final String key, final Number value) {
		this.dataCatcher = new JsonObject();
		this.append(key, value);
	}

	public Document(final Document defaults) {
		this.dataCatcher = defaults.dataCatcher;
	}

	public Document(final Document defaults, final String name) {
		this.dataCatcher = defaults.dataCatcher;
		this.name = name;
	}

	public Document() {
		this.dataCatcher = new JsonObject();
	}

	public Document(final JsonObject source) {
		this.dataCatcher = source;
	}

	public JsonObject obj() {
		return dataCatcher;
	}

	public boolean contains(final String key) {
		return this.dataCatcher.has(key);
	}

	public Document append(final String key, final String value) {
		if (value == null)
			return this;
		this.dataCatcher.addProperty(key, value);
		return this;
	}

	public Document append(final String key, final Number value) {
		if (value == null)
			return this;
		this.dataCatcher.addProperty(key, value);
		return this;
	}

	public Document append(final String key, final Boolean value) {
		if (value == null)
			return this;
		this.dataCatcher.addProperty(key, value);
		return this;
	}

	public Document append(final String key, final JsonElement value) {
		if (value == null)
			return this;
		this.dataCatcher.add(key, value);
		return this;
	}

	public Document append(final String key, final Document value) {
		if (value == null)
			return this;
		this.dataCatcher.add(key, value.dataCatcher);
		return this;
	}

	public Document append(final String key, final Object value) {
		if (value == null)
			return this;
		if (value instanceof Document) {
			this.append(key, (Document) value);
			return this;
		}
		this.dataCatcher.add(key, GSON.toJsonTree(value));
		return this;
	}

	public Document appendValues(final Map<String, Object> values) {
		for (final Map.Entry<String, Object> valuess : values.entrySet())
			append(valuess.getKey(), valuess.getValue());
		return this;
	}

	public Document remove(final String key) {
		this.dataCatcher.remove(key);
		return this;
	}

	public Set<String> keys() {
		final Set<String> c = new HashSet<>();

		for (final Map.Entry<String, JsonElement> x : dataCatcher.entrySet())
			c.add(x.getKey());

		return c;
	}

	public boolean has(final String key) {
		return dataCatcher.has(key);
	}

	public JsonElement get(final String key) {
		if (!dataCatcher.has(key))
			return null;
		return dataCatcher.get(key);
	}

	public String getString(final String key) {
		if (!dataCatcher.has(key))
			return null;
		return dataCatcher.get(key).getAsString();
	}

	public int getInt(final String key) {
		if (!dataCatcher.has(key))
			return 0;
		return dataCatcher.get(key).getAsInt();
	}

	public long getLong(final String key) {
		if (!dataCatcher.has(key))
			return 0L;
		return dataCatcher.get(key).getAsLong();
	}

	public double getDouble(final String key) {
		if (!dataCatcher.has(key))
			return 0D;
		return dataCatcher.get(key).getAsDouble();
	}

	public boolean getBoolean(final String key) {
		if (!dataCatcher.has(key))
			return false;
		return dataCatcher.get(key).getAsBoolean();
	}

	public float getFloat(final String key) {
		if (!dataCatcher.has(key))
			return 0F;
		return dataCatcher.get(key).getAsFloat();
	}

	public short getShort(final String key) {
		if (!dataCatcher.has(key))
			return 0;
		return dataCatcher.get(key).getAsShort();
	}

	public <T> T getObject(final String key, final Class<T> class_) {
		if (!dataCatcher.has(key))
			return null;
		final JsonElement element = dataCatcher.get(key);

		return GSON.fromJson(element, class_);
	}

	public Document getDocument(final String key) {
		if (!dataCatcher.has(key))
			return null;
		return new Document(dataCatcher.get(key).getAsJsonObject());
	}

	public Document clear() {
		for (final String key : keys())
			remove(key);
		return this;
	}

	public Document loadProperties(final Properties properties) {
		final Enumeration<?> enumeration = properties.propertyNames();
		while (enumeration.hasMoreElements()) {
			final Object x = enumeration.nextElement();
			this.append(x.toString(), properties.getProperty(x.toString()));
		}
		return this;
	}

	public JsonArray getArray(final String key) {
		return dataCatcher.get(key).getAsJsonArray();
	}

	public String convertToJson() {
		return GSON.toJson(dataCatcher);
	}

	public String convertToJsonString() {
		return dataCatcher.toString();
	}

	public boolean saveAsFile(final File backend) {
		if (backend == null)
			return false;

		if (backend.exists())
			backend.delete();

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(backend), "UTF-8")) {
			GSON.toJson(dataCatcher, (writer));
			return true;
		} catch (final IOException ex) {
			ex.getStackTrace();
		}
		return false;
	}

	@Deprecated
	public boolean saveAsFile0(final File backend) {
		if (backend == null)
			return false;

		if (backend.exists())
			backend.delete();

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(backend), "UTF-8")) {
			GSON.toJson(dataCatcher, (writer));
			return true;
		} catch (final IOException ex) {
			ex.getStackTrace();
		}
		return false;
	}

	public boolean saveAsFile(final Path path) {
		try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), "UTF-8")) {
			GSON.toJson(dataCatcher, outputStreamWriter);
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean saveAsFile(final String path) {
		return saveAsFile(Paths.get(path));
	}

	public static Document loadDocument(final String string) {
		final JsonReader reader = new JsonReader(new StringReader(string));
		reader.setLenient(true);
		return new Document(PARSER.parseReader(reader).getAsJsonObject());
	}

	public static Document loadDocument(final File backend) {
		try {
			backend.mkdirs();
			backend.createNewFile();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return loadDocument(backend.toPath());
	}

	public static Document $loadDocument(final File backend) throws Exception {
		try {
			final JsonReader reader = new JsonReader(
					new StringReader(new String(Files.readAllBytes(backend.toPath()), StandardCharsets.UTF_8)));
			reader.setLenient(true);
			return new Document(PARSER.parse(reader).getAsJsonObject());
		} catch (final Exception ex) {
			throw new Exception(ex);
		}
	}

	public static Document loadDocument(final Path backend) {
		try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(backend), "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(reader)) {
			final JsonObject object = PARSER.parse(bufferedReader).getAsJsonObject();
			return new Document(object);
		} catch (final Exception ex) {
			ex.getStackTrace();
		}
		return new Document();
	}

	public Document loadToExistingDocument(final File backend) {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(backend), "UTF-8")) {

			this.dataCatcher = PARSER.parse(reader).getAsJsonObject();
			this.file = backend;
			return this;
		} catch (final Exception ex) {
			ex.getStackTrace();
		}
		return new Document();
	}

	public Document loadToExistingDocument(final Path path) {
		try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path), "UTF-8")) {

			this.dataCatcher = PARSER.parse(reader).getAsJsonObject();
			return this;
		} catch (final Exception ex) {
			ex.getStackTrace();
		}
		return new Document();
	}

	@Deprecated
	public static Document load(final String input) {
		try (InputStreamReader reader = new InputStreamReader(new StringBufferInputStream(input), "UTF-8")) {
			return new Document(PARSER.parse(new BufferedReader(reader)).getAsJsonObject());
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return new Document();
	}

	@Override
	public String toString() {
		return convertToJsonString();
	}

	public static Document load(final JsonObject input) {
		return new Document(input);
	}

	public <T> T getObject(final String key, final Type type) {
		if (!contains(key))
			return null;

		return GSON.fromJson(dataCatcher.get(key), type);
	}

	public byte[] toBytesAsUTF_8() {
		return convertToJsonString().getBytes(StandardCharsets.UTF_8);
	}

	public byte[] toBytes() {
		return convertToJson().getBytes();
	}
}
