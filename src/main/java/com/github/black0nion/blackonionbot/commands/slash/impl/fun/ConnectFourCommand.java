package com.github.black0nion.blackonionbot.commands.slash.impl.fun;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFour;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFourGameManager;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ConnectFourCommand extends SlashCommand {
	private static final String USER = "user";

	private final LanguageSystem languageSystem;

	public ConnectFourCommand(LanguageSystem languageSystem) {
		super(builder(Commands.slash("connect4", "Use this command to play Connect 4 with another user.")
			.addOption(OptionType.USER, USER, "The user you want to challenge", true)));
		this.languageSystem = languageSystem;
	}

	@Override
	public void execute(SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel, UserSettings userSettings) {
		var challenged = BlackUser.from(Objects.requireNonNull(e.getOption(USER, OptionMapping::getAsUser)));
		if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
			e.replyEmbeds(EmbedUtils.getErrorEmbed(cmde.getLanguage(), author, guild).addField(cmde.getTranslation("errorcantplayagainst").replace("%enemy%", (challenged.isBot() ? cmde.getTranslation("bot") : cmde.getTranslation("yourself"))), cmde.getTranslation("nofriends"), false).build()).queue();
			return;
		}
		if (ConnectFourGameManager.isIngame(author.getId()) || ConnectFourGameManager.isIngame(challenged.getId())) {
			e.replyEmbeds(EmbedUtils.getErrorEmbed(cmde.getLanguage(), author, guild).addField(cmde.getTranslation("alreadyingame"), cmde.getTranslation("nomultitasking"), false).build()).queue();
			return;
		}
		e.reply(cmde.getTranslation("c4_askforaccept").replace("%challenged%", challenged.getAsMention()).replace("%challenger%", author.getAsMention()) + " " + cmde.getTranslation("answerwithyes")).queue();
		Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, event -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), event -> {
			final BlackUser answerUser = BlackUser.from(event.getAuthor());
			if (!answerUser.isBot() && answerUser.getId().equals(challenged.getId())) {
				if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
					e.replyEmbeds(EmbedUtils.getSuccessEmbed(cmde.getLanguage(), answerUser, guild).addField(languageSystem.getTranslation("challengeaccepted", answerUser, guild), languageSystem.getTranslation("playingagainst", answerUser, guild).replace("%challenger%", author.getEscapedName()), false).build()).queue();

					final ConnectFour game = ConnectFourGameManager.createGame(languageSystem.getDefaultLanguage(), channel, author, challenged);
					this.rerun(game, cmde);
				} else {
					cmde.send("challengedeclined");
				}
			}
		}, 1, TimeUnit.MINUTES, () -> e.getHook().sendMessageEmbeds(EmbedUtils.getErrorEmbed(languageSystem.getDefaultLanguage(), challenged, guild).addField(languageSystem.getTranslation("timeout", challenged, guild), cmde.getTranslation("tooktoolong"), false).build()).queue());
	}

	public void rerun(final ConnectFour game, final SlashCommandEvent cmde) {
		Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, answerEvent -> game.isPlayer(answerEvent.getAuthor().getId()), answerEvent -> {
			final String msg = answerEvent.getMessage().getContentRaw();
			final BlackUser author = BlackUser.from(answerEvent.getAuthor());
			final BlackGuild guild = BlackGuild.from(answerEvent.getGuild());
			answerEvent.getMessage().delete().queue();
			if (Utils.equalsOneIgnoreCase(msg, "exit", "stop", "cancel", "leave")) {
				game.getMessage().editMessageEmbeds(EmbedUtils.getSuccessEmbed(languageSystem.getDefaultLanguage(), author, guild).setTitle(cmde.getTranslation("gaveup")).addField(cmde.getTranslation("usergaveup").replace("%user%", author.getAsMention()), cmde.getTranslation("sadloose"), false).build()).queue();
				ConnectFourGameManager.deleteGame(game);
				return;
			} else if (!author.equals(game.currentUser == FieldType.X ? game.getPlayerX() : game.getPlayerY())) {
				game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(languageSystem.getDefaultLanguage(), author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.escapeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getFieldString(), false).setDescription(cmde.getTranslation("wrongturn")).build()).queue();
				this.rerun(game, cmde);
				return;
			} else if (game.isValidInput(msg)) {
				final Point coords = game.getCoordinatesFromString(msg);
				final FieldType[][] temp = new FieldType[ConnectFourGameManager.HEIGHT][ConnectFourGameManager.WIDTH];
				System.arraycopy(game.getField(), 0, temp, 0, game.getField().length);
				if (temp[coords.x][coords.y] != FieldType.EMPTY) {
					game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(languageSystem.getDefaultLanguage(), author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.escapeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getFieldString(), false).setDescription(cmde.getTranslation("fieldoccopied")).build()).queue();
					game.nextUser();
					this.rerun(game, cmde);
					return;
				}

				temp[coords.x][coords.y] = game.currentUser;
				game.setField(temp);
				game.nextUser();
				game.getMessage().editMessageEmbeds(EmbedUtils.getSuccessEmbed(languageSystem.getDefaultLanguage(), author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.escapeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getFieldString(), false).build()).queue();

				if (game.getWinner() != FieldType.EMPTY) {
					game.getMessage().editMessageEmbeds(EmbedUtils.getSuccessEmbed(languageSystem.getDefaultLanguage(), author, guild).addField("WE HAVE A WINNER!", "And the winner is....\n" + Utils.escapeMarkdown((game.getWinner() == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName())) + "!", false).build()).queue();
					ConnectFourGameManager.deleteGame(game);
					return;
				}
			} else {
				game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(languageSystem.getDefaultLanguage(), author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.escapeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getFieldString(), false).setDescription(cmde.getTranslation("wronginput")).build()).queue();
			}
			this.rerun(game, cmde);
		}, 1, TimeUnit.MINUTES, () -> {
			Language lang = Optional.ofNullable(cmde.getGuild().getLanguage()).orElseGet(this.languageSystem::getDefaultLanguage);
			game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(lang).addField(lang.getTranslation("timeout"), lang.getTranslation("tooktoolong"), false).build()).queue();
			ConnectFourGameManager.deleteGame(game);
		});
	}
}
