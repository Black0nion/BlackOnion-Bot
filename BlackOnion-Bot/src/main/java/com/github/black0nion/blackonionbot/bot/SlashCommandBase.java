package com.github.black0nion.blackonionbot.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.SlashCommandVisibility;
import com.github.black0nion.blackonionbot.systems.dashboard.Dashboard;
import com.github.black0nion.blackonionbot.systems.dashboard.values.DashboardValue;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandBase extends ListenerAdapter {
	
	public static HashMap<String[], SlashCommand> SlashCommandsArray = new HashMap<>();
	
	public static HashMap<Category, List<SlashCommand>> SlashCommandsInCategory = new HashMap<>();
	
	public static HashMap<String, SlashCommand> SlashCommands = new HashMap<>();
	
	public static EventWaiter waiter;

	public static int messagesLastTenSecs = 0;
	public static int SlashCommandsLastTenSecs = 0;
	
	private static JSONObject SlashCommandsJSON = new JSONObject();
	
	public static void addSlashCommands(final EventWaiter newWaiter) {
		SlashCommands.clear();
		SlashCommandsInCategory.clear();
		waiter = newWaiter;
		final Reflections reflections = new Reflections(SlashCommand.class.getPackage().getName());
		final Set<Class<? extends SlashCommand>> annotated = reflections.getSubTypesOf(SlashCommand.class);

		for (final Class<?> SlashCommand : annotated)
			try {
				final SlashCommand newInstance = (SlashCommand) SlashCommand.getConstructor().newInstance();
				final String[] packageName = SlashCommand.getPackage().getName().split("\\.");
				final Category parsedCategory = Category.parse(packageName[packageName.length-1]);
				newInstance.setCategory(parsedCategory != null ? parsedCategory : newInstance.getCategory());
				
				if (newInstance.shouldAutoRegister())
					if (newInstance.getSlashCommand() != null)
						addSlashCommand(newInstance);
					else
						System.err.println(newInstance.getClass().getName() + " doesn't have a SlashCommand!");
			} catch (final Exception e) {
				e.printStackTrace();
			}
		
		Bot.executor.submit(() -> {
			Dashboard.init();
			for (final Map.Entry<Category, List<SlashCommand>> entry : SlashCommandsInCategory.entrySet()) {
				final JSONArray array = new JSONArray();
				for (final SlashCommand SlashCommand : entry.getValue().stream().filter(cmd -> cmd.getVisibility() == SlashCommandVisibility.SHOWN && cmd.isDashboardSlashCommand()).collect(Collectors.toList())) {				
					final JSONObject SlashCommandJSON = new JSONObject();
					SlashCommandJSON.put("SlashCommand", SlashCommand.getSlashCommand());
					final String translation = LanguageSystem.getDefaultLanguage().getTranslation("help" + SlashCommand.getSlashCommand()[0]);
					SlashCommandJSON.put("description", translation != null ? translation : LanguageSystem.getDefaultLanguage().getTranslationNonNull("empty"));
					SlashCommandJSON.put("isToggleable", SlashCommand.isToggleable());
					if (Dashboard.hasValues(SlashCommand)) {
						final JSONArray values = new JSONArray();
						for (final DashboardValue value : Dashboard.getValues(SlashCommand))
							values.put(value.toJSON());
						SlashCommandJSON.put("values", values);
					}
					array.put(SlashCommandJSON);
				}
				SlashCommandsJSON.put(entry.getKey().name(), array);
			}
//			StringSelection stringSelection = new StringSelection(SlashCommandsJSON.toString());
//			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//			clipboard.setContents(stringSelection, null);
//			System.out.println(SlashCommandsJSON);
		});
	}
	
	@Override
	public void onSlashCommand(final SlashCommandEvent event) {
		
	}
	
	@Deprecated
	public static void addSlashCommand(final SlashCommand c, final String... SlashCommand) {
		for (final String s : SlashCommand)
			if (!SlashCommands.containsKey(s))
				SlashCommands.put(s, c);
	}
	
	public static void addSlashCommand(final SlashCommand c) {
		if (!SlashCommandsArray.containsKey(c.getSlashCommand())) {			
			final Category category = c.getCategory();
			final List<SlashCommand> SlashCommandsInCat = Optional.ofNullable(SlashCommandsInCategory.get(category)).orElse(new ArrayList<>());
			SlashCommandsInCat.add(c);
			SlashCommandsInCategory.put(category, SlashCommandsInCat);
			SlashCommandsArray.put(c.getSlashCommand(), c);
			
			for (final String SlashCommand : c.getSlashCommand())
				if (!SlashCommands.containsKey(SlashCommand))
					SlashCommands.put(SlashCommand, c);
		}
	}
}