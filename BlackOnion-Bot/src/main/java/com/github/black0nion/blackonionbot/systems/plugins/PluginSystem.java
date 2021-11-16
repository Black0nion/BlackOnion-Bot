package com.github.black0nion.blackonionbot.systems.plugins;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.LogOrigin;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.systems.logging.Logger;
import com.github.black0nion.blackonionbot.utils.BlackThread;
import com.github.black0nion.blackonionbot.utils.Document;
import com.github.black0nion.blackonionbot.utils.Utils;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author _SIM_
 */
public class PluginSystem {

	private static final HashMap<Plugin, PluginInformation> plugins = new HashMap<>();
	private static final List<String> pluginNames = new ArrayList<>();
	private static final List<String> pluginClasses = new ArrayList<>();

	private static void loadPlugin(final File jarFile) {
		System.out.println("Loading " + jarFile);
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
			final Plugin plugin = (Plugin) Class.forName(classInfo.getName(), true, new URLClassLoader(new URL[]{jarFile.toURI().toURL()})).getDeclaredConstructor().newInstance();
			final String pluginName = plugin.getName();

			Logger.logInfo("Loading Plugin \"" + pluginName + "\" stored in file \"" + jarName + "\"...", LogOrigin.PLUGINS);

			ThreadGroup threadGroup = new ThreadGroup("[PLUGIN THREADS] " + pluginName);
			PluginInformation information = new PluginInformation(plugin, new BlackThread(threadGroup, () -> {
				try {
					new BlackThread(plugin::onEnable)
							.setThreadName("[PLUGIN MAIN] " + pluginName)
							.startThread()
							.join();
				} catch(Exception e) {
					e.printStackTrace();
					plugins.get(plugin).setState(PluginState.ERRORED);
					Logger.logInfo("Plugin " + pluginName + " errored!", LogOrigin.PLUGINS);
				} finally{
					Logger.logInfo("Plugin " + pluginName + " stopped by itself.", LogOrigin.PLUGINS);
				}
			}, "[PLUGIN LOADER] " + pluginName).startThread(), threadGroup);
			information.setState(PluginState.RUNNING);
			pluginNames.add(pluginName);
			pluginClasses.add(classInfo.getName());

			plugins.put(plugin, information);
			Logger.logInfo("The Plugin \"" + pluginName + "\" stored in file \"" + jarName + "\" got loaded successfully!", LogOrigin.PLUGINS);
		} catch (final Exception e) {
			e.printStackTrace();
			Logger.logError("Plugin " + jarName + " couldn't get loaded! Stack Trace: " + e.getMessage() + "\n" + Utils.arrayToString("\n", e.getStackTrace()), LogOrigin.PLUGINS);
		}
	}

	public static void disablePlugins() {
		plugins.forEach((plugin, info) -> {
			ThreadGroup threadGroup = info.getThreadGroup();
			new BlackThread(threadGroup, () -> {
				try {
					info.setState(PluginState.DISABLING);
					BlackThread disableThread = new BlackThread(threadGroup, plugin::onDisable).setThreadName("[ONDISABLE] " + plugin).startThread();
					Thread.sleep(4000);
					Thread pluginThread = info.getMainThread();
					if (pluginThread.isAlive()) {
						info.setState(PluginState.DISABLING_NOW);
						BlackThread disableNowThread = new BlackThread(threadGroup, plugin::onDisableNow).setThreadName("[ONDISABLENOW] " + plugin).startThread();
						Thread.sleep(1000);
						if (disableNowThread.isAlive() || disableThread.isAlive() || pluginThread.isAlive()) {
							if (disableNowThread.isAlive()) disableNowThread.interrupt();
							if (disableThread.isAlive()) disableThread.interrupt();
							if (pluginThread.isAlive()) pluginThread.interrupt();
							info.setState(PluginState.TERMINATED);
						} else {
							info.setState(PluginState.STOPPED);
						}
						Thread.sleep(500);
						threadGroup.interrupt();
						threadGroup.destroy();
					} else {
						info.setState(PluginState.STOPPED);
					}
					pluginNames.remove(plugin.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).setThreadName("[PLUGIN SHUTDOWN HANDLER] " + plugin).start();
		});
		plugins.clear();
		pluginClasses.clear();
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
	 * @return the pluginnames
	 */
	public static List<String> getPluginNames() {
		return pluginNames;
	}

	public static List<String> getPluginClasses() {
		return pluginClasses;
	}
}