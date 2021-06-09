package com.github.black0nion.blackonionbot.utils;

import java.io.File;

import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.logging.Logger;

public class CredentialsManager {
	private Document doc;
	private final File file;
	
	public CredentialsManager(final String name) {
		file = new File("files", "credentials." + name + ".json");
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getString(final String key) {
		doc = Document.loadDocument(file);
		if (!doc.has(key))
			Logger.logError("No Credential for Key " + key + "!", LogOrigin.BOT);
		return doc.getString(key);
	}
	
	public int getInt(final String key) {
		doc = Document.loadDocument(file);
		if (!doc.has(key))
			Logger.logError("No credential for Key " + key + "!", LogOrigin.BOT);
		return doc.getInt(key);
	}
	
	public boolean has(final String key) {
		doc = Document.loadDocument(file);
		return doc.has(key);
	}

	public <T> T getObject(final String key, final Class<T> c) {
		doc = Document.loadDocument(file);
		return doc.getObject(key, c);
	}
}
