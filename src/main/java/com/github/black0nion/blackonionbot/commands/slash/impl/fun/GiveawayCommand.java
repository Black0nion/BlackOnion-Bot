package com.github.black0nion.blackonionbot.commands.slash.impl.fun;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.giveaways.Giveaway;
import com.github.black0nion.blackonionbot.systems.giveaways.GiveawaySystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;

public class GiveawayCommand extends SlashCommand {
	private static final String WINNERS = "winners";
	private static final String ITEM_TO_GIVE = "item";
	// Units
	private static final String MINUTES = "minutes";
	private static final String HOURS = "hours";
	private static final String DAYS = "days";
	private static final String WEEKS = "weeks";
	private static final String MONTHS = "months";
	private static final String YEARS = "years";
	private static final int MAX_TIMEOUT = Integer.MAX_VALUE;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

	private static final String MESSAGE_ID = "message_id";

	private final GiveawaySystem giveawaySystem;

	public GiveawayCommand(GiveawaySystem giveawaySystem) {
		super(builder(Commands.slash("giveaway", "Manage giveaways")
			.addSubcommands(
				new SubcommandData("create", "Create a giveaway").addOptions(
					new OptionData(OptionType.STRING, ITEM_TO_GIVE, "The item to give away", true),
					new OptionData(OptionType.INTEGER, WINNERS, "The count of giveaway winners", true).setRequiredRange(1, 100),
					new OptionData(OptionType.INTEGER, MINUTES, "The giveaway duration in minutes", false).setMinValue(1),
					new OptionData(OptionType.INTEGER, HOURS, "The giveaway duration in hours", false).setRequiredRange(1, MAX_TIMEOUT),
					new OptionData(OptionType.INTEGER, DAYS, "The giveaway duration in days", false).setRequiredRange(1, MAX_TIMEOUT),
					new OptionData(OptionType.INTEGER, WEEKS, "The giveaway duration in weeks", false).setRequiredRange(1, MAX_TIMEOUT),
					new OptionData(OptionType.INTEGER, MONTHS, "The giveaway duration in months", false).setRequiredRange(1, MAX_TIMEOUT),
					new OptionData(OptionType.INTEGER, YEARS, "The giveaway duration in years", false).setRequiredRange(1, MAX_TIMEOUT)),
				new SubcommandData("end", "End a giveaway").addOption(OptionType.STRING, MESSAGE_ID, "The message id of the giveaway.", true)
			))
		);
		this.giveawaySystem = giveawaySystem;
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, Guild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		switch (cmde.getSubcommandName()) {
			case "create" -> createGiveaway(cmde, e, author, guild, channel);
			case "end" -> endGiveaway(cmde, e, member, author, guild, channel);
			default -> cmde.sendPleaseUse();
		}
	}

	private void createGiveaway(SlashCommandEvent cmde, SlashCommandInteractionEvent e, User author, Guild guild, TextChannel channel) {
		Integer min = e.getOption(MINUTES, OptionMapping::getAsInt);
		Integer hours = e.getOption(HOURS, OptionMapping::getAsInt);
		Integer days = e.getOption(DAYS, OptionMapping::getAsInt);
		Integer weeks = e.getOption(WEEKS, OptionMapping::getAsInt);
		Integer months = e.getOption(MONTHS, OptionMapping::getAsInt);
		Integer years = e.getOption(YEARS, OptionMapping::getAsInt);
		if (min == null && hours == null && days == null && weeks == null && months == null && years == null) {
			cmde.send("timenotspecfied");
			return;
		}

		long time = 0;
		if (min != null) time += min * 60 * 1000;
		if (hours != null) time += hours * 60 * 60 * 1000;
		if (days != null) time += days * 24 * 60 * 60 * 1000;
		if (weeks != null) time += (long) weeks * 7 * 24 * 60 * 60 * 1000;
		if (months != null) time += (long) months * 30 * 24 * 60 * 60 * 1000;
		if (years != null) time += (long) years * 365 * 24 * 60 * 60 * 1000;

		var data = LocalDateTime.from(Instant.now().plusMillis(time));
		var item = e.getOption(ITEM_TO_GIVE, OptionMapping::getAsString);
		var winners = e.getOption(WINNERS, OptionMapping::getAsInt);
		final EmbedBuilder giveawayMessage = cmde.success()
			.setTitle(cmde.getTranslation("giveawayfor", new Placeholder("item", item)))
			.setDescription(cmde.getTranslation("giveawaydesc",
				new Placeholder("item", item),
				new Placeholder("winners", String.valueOf(winners)),
				new Placeholder("end", DATE_FORMAT.format(data).replace("_", " ")),
				new Placeholder("user", author.getAsMention())));

		cmde.reply(giveawayMessage, message -> {
			message.addReaction(Emoji.fromUnicode("U+1F389")).queue();
			message.editMessageEmbeds(giveawayMessage.setFooter(cmde.getTranslation("giveawayid", new Placeholder("id", message.getId()))).build()).queue();
			giveawaySystem.createGiveaway(data, message.getIdLong(), channel.getIdLong(), author.getIdLong(), guild.getIdLong(), item, winners);
		});
	}

	private void endGiveaway(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, Guild guild, TextChannel channel) {
		var id = e.getOption(MESSAGE_ID, OptionMapping::getAsString);
		if (Utils.isLong(id)) {
			final long idLong = Long.parseLong(id);
			final Giveaway giveaway = giveawaySystem.getGiveaway(idLong);

			if (giveaway == null || giveaway.channelId() != channel.getIdLong()) {
				cmde.error("giveawaynotfound", "giveawaynotfounddesc");
			} else if (giveaway.createrId() != author.getIdLong() && !member.hasPermission(channel, Permission.MESSAGE_MANAGE)) {
				cmde.error("nogiveawayendrights", "mustbeadminorgiveawaycreater");
			} else {
				channel.retrieveMessageById(idLong).queue(msg -> {
					if (msg == null) {
						cmde.exception();
					} else {
						giveawaySystem.endGiveaway(giveaway, msg, guild);
						cmde.success("giveawayended", "giveawaygotended");
					}
				});
			}
		} else {
			cmde.error("notanumber", "invalidmessageid");
		}
	}
}
