package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.misc.Warn;
import com.github.black0nion.blackonionbot.utils.NotImplementedException;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.Page;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WarnsCommand extends SlashCommand {
	private static final String USER = "user";
	private static final String WARN_ID = "warnid";
	private static final String ID = "id";
	private static final String ALL = "all";

	public WarnsCommand() {
		super(builder(Commands.slash("warns", "Used to get the warns of a userid.")
			.addSubcommands(
				new SubcommandData(ID, "Inspect a certain warn").addOption(OptionType.INTEGER, WARN_ID, "The id of the warn to inspect", true),
				new SubcommandData(USER, "Get the warns of an user").addOption(OptionType.USER, USER, "The user to get the warns of", true),
				new SubcommandData(ALL, "Get all warns of the guild")
			))
			.setRequiredPermissions(Permission.KICK_MEMBERS));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		switch (e.getSubcommandName()) {
			case ID -> handleId(cmde);
			case USER -> handleUser(cmde);
			case ALL -> handleAll(cmde);
			default -> throw new NotImplementedException(e.getSubcommandName());
		}
	}

	private static void handleId(SlashCommandEvent cmde) {
		var warnId = cmde.getEvent().getOption(WARN_ID, OptionMapping::getAsLong);

		if (warnId != null) {
			Warn warn = cmde.getGuild().getWarn(warnId);
			StringBuilder result = new StringBuilder("empty");
			if (warn != null) {
				result = new StringBuilder()
					.append(BotInformation.datePattern.format(new Date(Warn.START_TIME_STAMP + warn.id())))
					.append(": <@").append(warn.issuer()).append("> > ")
					.append("<@").append(warn.userid()).append("> with the reason: `")
					.append(warn.reason() != null ? warn.getReasonEscaped() : "none")
					.append("` (ID: ").append(warn.id()).append(")`");
			}
			cmde.send(result.toString());
		} else {
			cmde.send("invalidwarnid");
		}
	}

	private static void handleUser(SlashCommandEvent cmde) {
		var warnMember = cmde.getEvent().getOption(USER, OptionMapping::getAsMember);

		if (warnMember != null) {
			var blackMember = BlackMember.from(cmde.getGuild().retrieveMemberById(warnMember.getId()).submit().join());
			if (blackMember == null) {
				cmde.send("memberisnull");
				return;
			}

			final List<Warn> warns = blackMember.getWarns();
			sendWarns(cmde, warns);
		} else {
			cmde.send("notamember");
		}
	}

	private static void handleAll(SlashCommandEvent cmde) {
		sendWarns(cmde, cmde.getGuild().getWarns());
	}

	private static void sendWarns(SlashCommandEvent cmde, List<Warn> warns) {
		List<Page> pages = null;
		if (!warns.isEmpty()) {
			List<MessageEmbed.Field> fields = new ArrayList<>();
			warns.stream().sorted(Comparator.comparingLong(Warn::id).reversed()).forEach(warn ->
				fields.add(new MessageEmbed.Field(
					BotInformation.datePattern.format(new Date(Warn.START_TIME_STAMP + warn.id())),
					"<@" + warn.issuer() + "> > <@" + warn.userid() + "> with reason: `" + (warn.reason() != null ? warn.reason() : "none") + "` (ID: " + warn.id() + ")",
					false)
				)
			);
			pages = Utils.getPages(cmde.success(), fields,2);
		}
		final List<Page> finalList = pages;
		if (pages != null) cmde.reply((MessageEmbed) pages.get(0).getContent(),
			success -> success.retrieveOriginal().queue(message ->
				Pages.paginate(message, finalList, true, 2, TimeUnit.MINUTES, true, u -> u.getIdLong() == cmde.getUser().getIdLong())));
		else cmde.send("nowarns");
	}
}