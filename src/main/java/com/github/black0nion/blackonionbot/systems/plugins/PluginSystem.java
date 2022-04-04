package com.github.black0nion.blackonionbot.systems.plugins;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.BlackThread;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author _SIM_
 */
public class PluginSystem {

	private static final Map<Plugin, PluginInformation> plugins = new HashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(PluginSystem.class);

	private static void loadPlugin(final File jarFile) {
		System.out.println("Loading " + jarFile);
		final String jarName = jarFile.getName();
		final ScanResult scanResult = new ClassGraph().enableAnnotationInfo().overrideClasspath(jarFile.getAbsolutePath()).scan();

		final String superclass = Plugin.class.getName();

		final Optional<ClassInfo> optionalClassInfo = scanResult.getAllClasses().stream().filter(classInfo -> classInfo.extendsSuperclass(superclass)).findFirst();

		if (optionalClassInfo.isEmpty()) {
			logger.error("No plugin detected in file \"{}\".", jarName);
			return;
		}

		final ClassInfo classInfo = optionalClassInfo.get();

		try {
			final Plugin plugin = (Plugin) Class.forName(classInfo.getName(), true, new URLClassLoader(new URL[]{jarFile.toURI().toURL()})).getDeclaredConstructor().newInstance();
			final String pluginName = plugin.getName();

			logger.info("Loading Plugin \"{}\" stored in file \"{}\"", pluginName, jarName);

			ThreadGroup threadGroup = new ThreadGroup("[PLUGIN THREADS] " + pluginName);
			PluginInformation information = new PluginInformation(plugin, new BlackThread(threadGroup, () -> {
				try {
					new BlackThread(plugin::onEnable)
							.setThreadName("[PLUGIN MAIN] " + pluginName)
							.startThread()
							.join();
				} catch(Exception e) {
					plugins.get(plugin).setState(PluginState.ERRORED);
					logger.error("Plugin " + pluginName + " crashed!", e);
				} finally {
					logger.info("Plugin {} terminated.", pluginName);
				}
			}, "[PLUGIN LOADER] " + pluginName).startThread(), threadGroup);
			information.setState(PluginState.RUNNING);

			plugins.put(plugin, information);
			logger.info("The Plugin \"{}\" stored in file \"{}\" got loaded successfully!", pluginName, jarName);
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error("Plugin " + jarName + " could not be loaded!", e);
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
					} else {
						info.setState(PluginState.STOPPED);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).setThreadName("[PLUGIN SHUTDOWN HANDLER] " + plugin).start();
		});
		plugins.clear();
	}

	@Reloadable("plugins")
	public static void loadPlugins() {
		loadPlugins(null);
	}

	public static void loadPlugins(final Consumer<Void> callback) {
		Bot.getInstance().getExecutor().submit(() -> {
			final File folder = new File("plugins");
			if (!folder.exists()) {
				folder.mkdirs();
			}

			final File[] files = folder.listFiles();
			disablePlugins();
			if (files == null || files.length == 0) {
				logger.info("No plugins found.");
				return;
			}
			logger.info("Loading plugins...");
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
}