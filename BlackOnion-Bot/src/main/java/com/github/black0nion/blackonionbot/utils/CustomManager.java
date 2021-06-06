package com.github.black0nion.blackonionbot.utils;

/**
 * @author _SIM_
*/

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class CustomManager {
	private Document doc;
	private final File file;
	
	public CustomManager(final String name) {
		file = new File("files", name + ".json");
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(final String key, final String value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
	}
	
	public Document save(final String key, final Number value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
		return doc;
	}

	public Document save(final String key, final Boolean value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
		return doc;
	}

	public Document save(final String key, final JsonElement value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
		return doc;
	}
	
	public Document save(final String key, final Document value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
		return doc;
	}

	public Document save(final String key, final Object value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
		return doc;
	}
	
	public Document remove(final String key) {
		doc = Document.loadDocument(file);
		doc.remove(key);
		doc.saveAsFile(file);
		return doc;
	}
	
	public Set<String> getKeys() {
		doc = Document.loadDocument(file);
		return doc.keys();
	}
	
	public JsonElement get(final String key) {
		doc = Document.loadDocument(file);
		return doc.get(key);
	}
	
	public String getString(final String key) {
		doc = Document.loadDocument(file);
		return doc.getString(key);
	}
	
	public int getInt(final String key) {
		doc = Document.loadDocument(file);
		return doc.getInt(key);
	}
	
	public long getLong(final String key) {
		doc = Document.loadDocument(file);
		return doc.getLong(key);
	}
	
	public double getDouble(final String key) {
		doc = Document.loadDocument(file);
		return doc.getDouble(key);
	}
	
	public float getFloat(final String key) {
		doc = Document.loadDocument(file);
		return doc.getFloat(key);
	}
		
	public short getShort(final String key) {
		doc = Document.loadDocument(file);
		return doc.getShort(key);
	}
	
	public boolean getBoolean(final String key) {
		doc = Document.loadDocument(file);
		return doc.getBoolean(key);
	}

	public <T> T getObject(final String key, final Class<T> c) {
		doc = Document.loadDocument(file);
		return doc.getObject(key, c);
	}
	
	/**
	 * @param key
	 * 
	 * @deprecated only use this if you need something other than in this wrapper because the other functions of the document is already in this wrapper, thats what it was written for<br/>
	*/
	@Deprecated
	public Document getDocument(final String key) {
		doc = Document.loadDocument(file);
		return doc.getDocument(key);
	}
	
	/**
	 * @deprecated THIS METHOD WILL WIPE OUT ALL DATA! USE CAREFULLY!
	*/
	@Deprecated
	public void clear() {
		doc = Document.loadDocument(file);
		doc.clear();
		doc.saveAsFile(file);
	}
	
	public JsonArray getJsonArray(final String key) {
		doc = Document.loadDocument(file);
		return doc.getArray(key);
	}
	
	public String[] getStringArray(final String key) {
		doc = Document.loadDocument(file);
		final List<String> list = new ArrayList<String>();
		doc.getArray(key).forEach(entry -> {
			list.add(entry.getAsString());
		});
		final String[] array = new String[list.size()];
		return list.toArray(array);
	}

	public Number[] getNumberArray(final String key) {
		doc = Document.loadDocument(file);
		final List<Number> list = new ArrayList<Number>();
		doc.getArray(key).forEach(entry -> {
			list.add(entry.getAsNumber());
		});
		final Number[] array = new Number[list.size()];
		return list.toArray(array);
	}
	
	public List<String> getArrayAsList(final String key) {
		doc = Document.loadDocument(file);
		JsonArray array = new JsonArray();
		array = doc.getArray(key);
		final List<String> list = new ArrayList<String>();
		for(int i = 0; i < array.size(); i++)
			list.add(array.get(i).getAsString());
		return list;
	}
}
