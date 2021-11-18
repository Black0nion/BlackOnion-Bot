package com.github.black0nion.blackonionbot.utils;

import java.io.File;

import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.systems.logging.Logger;

public class CredentialsManager {
    private Document doc;
    private final File file;

    public CredentialsManager(final String name) {
	this.file = new File("files", "credentials." + name + ".json");
	this.file.getParentFile().mkdirs();
	try {
	    this.file.createNewFile();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    public String getString(final String key) {
	this.doc = Document.loadDocument(this.file);
	if (!this.doc.has(key)) {
	    Logger.logError("No Credential for Key " + key + "!", LogOrigin.BOT);
	}
	return this.doc.getString(key);
    }

    public String getString(final String key, final String defaultValue) {
	final String fromFile = this.getString(key);
	return fromFile != null ? fromFile : defaultValue;
    }

    public int getInt(final String key) {
	this.doc = Document.loadDocument(this.file);
	if (!this.doc.has(key)) {
	    Logger.logError("No credential for Key " + key + "!", LogOrigin.BOT);
	}
	return this.doc.getInt(key);
    }

    public boolean has(final String key) {
	this.doc = Document.loadDocument(this.file);
	return this.doc.has(key);
    }

    public <T> T getObject(final String key, final Class<T> c) {
	this.doc = Document.loadDocument(this.file);
	return this.doc.getObject(key, c);
    }
}
