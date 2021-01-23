package com.github.black0nion.blackonionbot.utils;

import java.io.File;

public class CredentialsManager {
	private Document doc;
	private File file;
	
	public CredentialsManager(String name) {
		file = new File("files", "credentials." + name + ".json");
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getString(String key) {
		doc = Document.loadDocument(file);
		return doc.getString(key);
	}
	
	public boolean has(String key) {
		doc = Document.loadDocument(file);
		return doc.has(key);
	}

	public <T> T getObject(String key, Class<T> c) {
		doc = Document.loadDocument(file);
		return doc.getObject(key, c);
	}
}
