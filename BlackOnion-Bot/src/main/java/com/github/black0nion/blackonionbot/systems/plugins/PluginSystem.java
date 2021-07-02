package com.github.black0nion.blackonionbot.systems.plugins;

import java.io.File;
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

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

/**
 * @author _SIM_
 */
public class PluginSystem {

    private static final List<Plugin> plugins = new ArrayList<>();
    private static final List<String> pluginNames = new ArrayList<>();

    private static void loadPlugin(final File jarFile) {
	final String jarName = jarFile.getName();
	final ScanResult scanResult = new ClassGraph().enableAnnotationInfo().overrideClasspath(jarFile.getAbsolutePath()).scan();

	final String superclass = Plugin.class.getName();

	final Optional<ClassInfo> optionalClassInfo = scanResult.getAllClasses().stream().filter(classInfo -> classInfo.extendsSuperclass(superclass)).findFirst();

	if (!optionalClassInfo.isPresent()) {
	    Logger.logError("No plugin detected in file \"" + jarName + "\".", LogOrigin.PLUGINS);
	    return;
	}

	final ClassInfo classInfo = optionalClassInfo.get();

	try {
	    final Plugin plugin = (Plugin) Class.forName(classInfo.getName(), true, new URLClassLoader(new URL[] { jarFile.toURI().toURL() })).getDeclaredConstructor().newInstance();
	    final String pluginName = plugin.getName();

	    Logger.logInfo("Loading Plugin \"" + pluginName + "\" stored in file \"" + jarName + "\"...", LogOrigin.PLUGINS);

	    plugins.add(plugin);
	    pluginNames.add(classInfo.getName());
	    plugin.onEnable();

	    Logger.logInfo("The Plugin \"" + pluginName + "\" stored in file \"" + jarName + "\" got loaded successfully!", LogOrigin.PLUGINS);
	} catch (final Exception e) {
	    Logger.logError("Plugin " + jarName + " couldn't get loaded! Stack Trace: " + e.toString() + "\n" + e.fillInStackTrace(), LogOrigin.PLUGINS);
	    e.printStackTrace();
	}
    }

    public static void disablePlugins() {
	plugins.forEach(Plugin::onDisable);
	plugins.clear();
	pluginNames.clear();
    }

    @Reloadable("plugins")
    public static void loadPlugins() {
	loadPlugins(null);
    }

    public static void loadPlugins(final Consumer<Void> callback) {
	Bot.executor.submit(() -> {
	    final File folder = new File("plugins");
	    if (!folder.exists()) {
		folder.mkdirs();
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

    /**
     * @return the pluginnames
     */
    public static List<String> getPluginNames() {
	return pluginNames;
    }
}