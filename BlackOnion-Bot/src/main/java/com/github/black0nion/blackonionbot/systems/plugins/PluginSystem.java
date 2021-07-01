package com.github.black0nion.blackonionbot.systems.plugins;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.CatchLogs;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

/**
 * @author _SIM_
 */
public class PluginSystem {

    private static final List<Plugin> plugins = new ArrayList<>();

    private static void loadPlugin(final File jarFile) {
	final ScanResult scanResult = new ClassGraph().enableAnnotationInfo().overrideClasspath(jarFile.getAbsolutePath()).scan();

	final String superclass = Plugin.class.getName();

	final Optional<ClassInfo> optionalClassInfo = scanResult.getAllClasses().stream().filter(classInfo -> classInfo.extendsSuperclass(superclass)).findFirst();

	if (!optionalClassInfo.isPresent()) {
	    Logger.logError("No Plugin-Superclass found in " + jarFile.getName() + ".", LogOrigin.PLUGINS);
	    return;
	}

	final ClassInfo classInfo = optionalClassInfo.get();

	try {
	    final Plugin plugin = (Plugin) Class.forName(classInfo.getName(), true, new URLClassLoader(new URL[] { jarFile.toURI().toURL() })).getDeclaredConstructor().newInstance();

	    Logger.logInfo("Loading Plugin " + plugin.getName() + "...", LogOrigin.PLUGINS);

	    getPlugins().add(plugin);
	    CatchLogs.enableForClass(plugin.getClass());
	    plugin.onEnable();

	    Logger.logInfo("The Plugin " + plugin.getName() + " got loaded successfully!", LogOrigin.PLUGINS);
	} catch (final Exception e) {
	    Logger.logError("Plugin " + jarFile.getName() + " couldn't get loaded! Stack Trace: " + e.toString() + "\n" + e.fillInStackTrace(), LogOrigin.PLUGINS);
	    e.printStackTrace();
	}
    }

    public static void disablePlugins() {
	plugins.forEach(Plugin::onDisable);
	plugins.clear();
    }

    @Reloadable("plugins")
    public static void loadPlugins() {
	loadPlugins(null);
    }

    public static void loadPlugins(final Consumer<Void> callback) {
	Bot.executor.submit(() -> {
	    final File folder = new File("plugins");
	    if (!folder.exists()) {
		try {
		    folder.createNewFile();
		} catch (final IOException e) {
		    e.printStackTrace();
		}
	    }

	    final File[] files = folder.listFiles();
	    disablePlugins();
	    if (files == null || files.length == 0) {
		Logger.logInfo("No plugins found.", LogOrigin.PLUGINS);
		return;
	    }
	    Logger.logInfo("Loading plugins...");
	    for (final File file : files) {
		if (!file.isDirectory() && file.getName().endsWith(".jar")) {
		    loadPlugin(file);
		}
	    }
	    if (callback != null) {
		callback.accept(null);
	    }
	});
    }

    /**
     * @return the plugins
     */
    public static List<Plugin> getPlugins() {
	return plugins;
    }
}