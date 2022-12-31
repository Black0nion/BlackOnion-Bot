package com.github.black0nion.blackonionbot.systems.reload;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReloadSystem {

	private static final Logger logger = LoggerFactory.getLogger(ReloadSystem.class);

	/**
	 * Sometimes, a reloadable will get registered while reloading is in progress.
	 * This queue is used to store the reloadables that were registered during the reload.
	 * They will be registered after the reload is finished.
	 */
	private final Queue<Reloadable> addQueue = new ConcurrentLinkedQueue<>();
	/**
	 * Sometimes, a reloadable will get unregistered while reloading is in progress.
	 * This queue is used to store the reloadables that were unregistered during the reload.
	 * They will be unregistered after the reload is finished.
	 */
	private final Queue<Reloadable> removeQueue = new ConcurrentLinkedQueue<>();

	private final Map<String, Reloadable> reloadables = new HashMap<>();

	private boolean reloading = false;

	public void registerReloadable(Reloadable reload) {
		logger.debug("Registering reloadable '{}'", reload.getReloadName());
		if (reloadables.containsKey(reload.getReloadName())) {
			logger.warn("Reloadable '{}' is already registered!", reload.getReloadName());
		}
		if (reloading) {
			logger.warn("Reload in progress, adding reloadable '{}' to add queue", reload.getReloadName());
			addQueue.add(reload);
		} else {
			reloadables.put(reload.getReloadName(), reload);
		}
	}

	public void unregisterReloadable(Reloadable reload) {
		logger.debug("Unregistering reloadable '{}'", reload.getReloadName());

		if (!reloadables.containsKey(reload.getReloadName())) {
			logger.warn("Reloadable '{}' not registered!", reload.getReloadName());
			return;
		}

		Reloadable stored = reloadables.get(reload.getReloadName());
		if (stored != reload) {
			logger.warn("Reloadable '{}' is not the same as the stored one!", reload.getReloadName());
			return;
		}

		if (reloading) {
			logger.warn("Reload in progress, adding reloadable '{}' to remove queue", reload.getReloadName());
			removeQueue.add(reload);
		} else {
			reloadables.remove(reload.getReloadName());
		}
	}

	public void unregisterReloadableByName(String reloadName) {
		if (!reloadables.containsKey(reloadName)) {
			logger.warn("Trying to remote Reloadable '{}', but there isn't any instance registered!", reloadName);
			return;
		}

		unregisterReloadable(reloadables.get(reloadName));
	}

	public void reloadAll() {
		logger.info("Reloading everything...");
		int count = 0;
		int total = reloadables.size();
		this.reloading = true;
		for (Reloadable reloadable : reloadables.values()) {
			logger.debug("Reloading '{}' ({}/{})...", reloadable.getReloadName(), ++count, total);
			reloadable.reload();
			logger.debug("Reloaded '{}'", reloadable.getReloadName());
		}
		this.handleQueue();
		logger.info("Reloaded everything!");
	}

	/**
	 * Will also handle the queue => don't call this method if you are already handling the queue!
	 * @throws IllegalArgumentException if no reloadable with the given name exists
	 */
	public void reload(String option) {
		if (!reloadables.containsKey(option)) throw new IllegalArgumentException("No reloadable with the name " + option + " found!");

		logger.info("Reloading '{}'...", option);

		this.reloading = true;
		reloadables.get(option).reload();
		this.handleQueue();

		logger.info("Reloaded '{}'!", option);
	}

	private void handleQueue() {
		logger.debug("Handling queue...");
		this.reloading = false;
		Reloadable reloadable;
		while ((reloadable = removeQueue.poll()) != null) {
			logger.debug("Unregistering reloadable '{}' from remove queue", reloadable.getReloadName());
			reloadables.remove(reloadable.getReloadName());
		}

		while ((reloadable = addQueue.poll()) != null) {
			logger.debug("Registering reloadable '{}' from add queue", reloadable.getReloadName());
			reloadables.put(reloadable.getReloadName(), reloadable);
		}
		logger.debug("Queue handled!");
	}

	/**
	 * @return an unmodifiable list of all registered reloadables
	 */
	public List<Reloadable> getReloadables() {
		return ImmutableList.copyOf(reloadables.values());
	}

	public boolean isRegistered(String reloadName) {
		return reloadables.containsKey(reloadName);
	}
}
