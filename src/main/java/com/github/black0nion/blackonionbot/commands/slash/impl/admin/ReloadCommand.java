package com.github.black0nion.blackonionbot.commands.slash.impl.admin;

import com.github.black0nion.blackonionbot.Main;
import com.github.black0nion.blackonionbot.commands.common.utils.UserRespondUtilsImpl;
import com.github.black0nion.blackonionbot.commands.common.utils.event.UserRespondUtils;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReloadCommand extends SlashCommand {

	private static Map<String, Method> reloadableMethods = null;

	public ReloadCommand() {
		super(builder("reload", "Reloads the bot")
			.setAdminGuild());
	}

	@Reloadable("reloadablemethods")
	public static void initReloadableMethods() {
		reloadableMethods = new HashMap<>();
		new Reflections(Main.class.getPackage().getName(), Scanners.MethodsAnnotated)
			.getMethodsAnnotatedWith(Reloadable.class)
			.stream()
			// we don't care at what point the peek will be executed so the SonarLint warning can be ignored
			// also, in this case, I'm accessing my own code with reflections, so I know what I'm doing (I think)
			.peek(m -> m.setAccessible(true))
			.forEach(method -> reloadableMethods.put(method.getAnnotation(Reloadable.class).value(), method));
	}

	public static void reloadAll() {
		if (reloadableMethods == null) {
			initReloadableMethods();
		}
		reloadableMethods.values().forEach(m -> {
			try {
				m.invoke(m.getClass());
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (reloadableMethods == null) {
			initReloadableMethods();
		}

		e.replyComponents(ActionRow.of(StringSelectMenu.create(enrichId("reloadablemethods"))
				.addOptions(reloadableMethods.keySet().stream()
					.map(l -> SelectOption.of(l, l))
					.toArray(SelectOption[]::new))
				.setMaxValues(SelectMenu.OPTIONS_MAX_AMOUNT)
				.build()))
			.setEphemeral(true)
			.queue();
	}

	@Override
	public void handleSelectMenuInteraction(GenericSelectMenuInteractionEvent<?, ?> e) {
		if (!(e instanceof StringSelectInteractionEvent event)) return;
		e.deferReply().queue();
		UserRespondUtils cmde = new UserRespondUtilsImpl(e, BlackGuild.from(event.getGuild()), BlackUser.from(event.getUser())) {
			@Override
			public boolean isEphemeral() {
				return true;
			}
		};

		List<String> options = event.getValues();
		if (options.contains("all")) {
			reloadAll();
			cmde.send("configsreload");
		} else {
			for (String option : options) {
				final @Nullable Method method = reloadableMethods.get(option);
				if (method == null) throw new NullPointerException("Invalid method.");
				try {
					method.invoke(method.getClass());
				} catch (IllegalAccessException | InvocationTargetException ex) {
					cmde.exception(ex);
				}
			}
			// DON'T use cmde.send() here because that'll invoke "reply" which will error because of the deferred reply (for some reason)
			// TODO: setEphemeral() doesn't work here
			e.getHook().sendMessage(cmde.getTranslation("configreloaded", new Placeholder("config", String.join(", ", options.toArray(String[]::new)))))
				.setEphemeral(true)
				.queue();
		}
	}
}
