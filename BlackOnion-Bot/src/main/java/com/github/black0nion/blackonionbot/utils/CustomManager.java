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
	private File file;
	
	public CustomManager(String name) {
		file = new File("files", name + ".json");
		file.mkdirs();
	}
	
	public void save(String key, String value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
	}
	
	public void save(String key, Number value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
	}

	public void save(String key, Boolean value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
	}

	public void save(String key, JsonElement value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
	}
	
	public void save(String key, Document value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
	}

	public void save(String key, Object value) {
		doc = Document.loadDocument(file);
		doc.append(key, value);
		doc.saveAsFile(file);
	}
	
	public void remove(String key) {
		doc = Document.loadDocument(file);
		doc.remove(key);
		doc.saveAsFile(file);
	}
	
	public Set<String> getKeys() {
		doc = Document.loadDocument(file);
		return doc.keys();
	}
	
	public JsonElement get(String key) {
		doc = Document.loadDocument(file);
		return doc.get(key);
	}
	
	public String getString(String key) {
		doc = Document.loadDocument(file);
		return doc.getString(key);
	}
	
	public int getInt(String key) {
		doc = Document.loadDocument(file);
		return doc.getInt(key);
	}
	
	public long getLong(String key) {
		doc = Document.loadDocument(file);
		return doc.getLong(key);
	}
	
	public double getDouble(String key) {
		doc = Document.loadDocument(file);
		return doc.getDouble(key);
	}
	
	public float getFloat(String key) {
		doc = Document.loadDocument(file);
		return doc.getFloat(key);
	}
		
	public short getShort(String key) {
		doc = Document.loadDocument(file);
		return doc.getShort(key);
	}
	
	public boolean getBoolean(String key) {
		doc = Document.loadDocument(file);
		return doc.getBoolean(key);
	}

	public <T> T getObject(String key, Class<T> c) {
		doc = Document.loadDocument(file);
		return doc.getObject(key, c);
	}
	
	/**
	 * @param key
	 * 
	 * @deprecated only use this if you need something other than in this wrapper because the other functions of the document is already in this wrapper, thats what it was written for<br/>
	*/
	@Deprecated
	public Document getDocument(String key) {
		doc = Document.loadDocument(file);
		return doc.getDocument(key);
	}
	
	/**
	 * @deprecated THIS METHOD WILL WIPE OUT ALL DATA! USE CAREFULLY!
	*/
	public void clear() {
		doc = Document.loadDocument(file);
		doc.clear();
		doc.saveAsFile(file);
	}
	
	public JsonArray getJsonArray(String key) {
		doc = Document.loadDocument(file);
		return doc.getArray(key);
	}
	
	public String[] getStringArray(String key) {
		doc = Document.loadDocument(file);
		List<String> list = new ArrayList<String>();
		doc.getArray(key).forEach(entry -> {
			list.add(entry.getAsString());
		});
		String[] array = new String[list.size()];
		return list.toArray(array);
	}

	public Number[] getNumberArray(String key) {
		doc = Document.loadDocument(file);
		List<Number> list = new ArrayList<Number>();
		doc.getArray(key).forEach(entry -> {
			list.add(entry.getAsNumber());
		});
		Number[] array = new Number[list.size()];
		return list.toArray(array);
	}
	
	public List<String> getArrayAsList(String key) {
		doc = Document.loadDocument(file);
		JsonArray array = new JsonArray();
		array = doc.getArray(key);
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < array.size(); i++){
		    list.add(array.get(i).getAsString());
		}
		return list;
	}
}
