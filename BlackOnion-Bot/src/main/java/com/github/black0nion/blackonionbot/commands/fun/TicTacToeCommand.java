package com.github.black0nion.blackonionbot.commands.fun;

import static com.github.black0nion.blackonionbot.systems.language.LanguageSystem.getTranslation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToe;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToeBot;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToeGameManager;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToePlayer;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Pair;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class TicTacToeCommand extends Command {

	public TicTacToeCommand() {
		this.setCommand("tictactoe", "ttt").setSyntax("<@User / mention me to play against me!>").setRequiredBotPermissions(Permission.MESSAGE_MANAGE);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		if (message.getMentionedBlackUsers().size() != 0) {
			final BlackUser challenged = message.getMentionedBlackUsers().get(0);
			if (challenged.getIdLong() == e.getJDA().getSelfUser().getIdLong()) {
				this.rerun(TicTacToeGameManager.createGame(e.getChannel(), new TicTacToePlayer(author), new TicTacToePlayer()), e.getChannel());
				return;
			} else if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("errorcantplayagainst", author, guild).replace("%enemy%", (challenged.isBot() ? getTranslation("bot", author, guild) : getTranslation("yourself", author, guild))), getTranslation("nofriends", author, guild), false).build()).queue();
				return;
			} else if (TicTacToeGameManager.isIngame(author.getId()) || TicTacToeGameManager.isIngame(challenged.getId())) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("alreadyingame", author, guild), getTranslation("nomultitasking", author, guild), false).build()).queue();
				return;
			}
			message.reply(getTranslation("ttt_askforaccept", author, guild).replace("%challenged%", challenged.getAsMention()).replace("%challenger%", author.getAsMention()) + " " + getTranslation("answerwithyes", author, guild)).queue();
			CommandBase.waiter.waitForEvent(MessageReceivedEvent.class, event -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), event -> {
				final BlackUser eventAuthor = BlackUser.from(event.getAuthor());
				if (!eventAuthor.isBot() && eventAuthor.getId().equals(challenged.getId()))
					if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {

						message.reply(EmbedUtils.getSuccessEmbed(eventAuthor, guild).addField(getTranslation("challengeaccepted", eventAuthor, guild), getTranslation("playingagainst", eventAuthor, guild).replace("%challenger%", author.getAsMention()), false).build()).queue();

						// ANGENOMMEN
						final TicTacToe game = TicTacToeGameManager.createGame(e.getChannel(), new TicTacToePlayer(author), new TicTacToePlayer(challenged));
						this.rerun(game, e.getChannel());
						return;
					} else if (event.getMessage().getContentRaw().equalsIgnoreCase("no")) {
						message.reply(EmbedUtils.getErrorEmbed(author, guild).setTitle(getTranslation("declined", eventAuthor, guild)).addField(getTranslation("challengedeclined", eventAuthor, guild), getTranslation("arentyoubraveenough", eventAuthor, guild), false).build()).queue();
						return;
					} else {
						message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("challengedeclined", eventAuthor, guild), getTranslation("answerwithyes", eventAuthor, guild), false).build()).queue();
						return;
					}
			}, 1, TimeUnit.MINUTES, () -> message.reply(EmbedUtils.getErrorEmbed(challenged, guild).addField(getTranslation("timeout", challenged, guild), getTranslation("tooktoolong", author, guild), false).build()).queue());
		} else {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("nousermentioned", author, guild), getTranslation("inputusertoplayagainst", author, guild), false).build()).queue();
			return;
		}
	}

	public void rerun(final TicTacToe game, final TextChannel channel) {
		final BlackGuild guild = BlackGuild.from(channel.getGuild());
		CommandBase.waiter.waitForEvent(ButtonClickEvent.class, answerEvent -> answerEvent.getGuild().getIdLong() == channel.getGuild().getIdLong() && answerEvent.getMessage().getIdLong() == game.getMessage().getIdLong() && game.isPlayer(answerEvent.getUser().getId()), answerEvent -> {
			answerEvent.deferEdit().queue();
			final BlackUser author = BlackUser.from(answerEvent.getUser());
			final String id = answerEvent.getButton().getId();
			final Message message = game.getMessage();
			if (id.equalsIgnoreCase("leave")) {
				message.editMessage(getTranslation("usergaveup", author, guild).replace("%user%", author.getAsMention())).setActionRows().queue();
				TicTacToeGameManager.deleteGame(game);
				return;
			} else if (!author.getId().equals(game.currentPlayer == FieldType.X ? game.getPlayerX().getId() : game.getPlayerY().getId())) {
				this.rerun(game, channel);
				return;
			}

			Pair<Integer, Integer> coords = new Pair<>(Integer.valueOf(id.substring(0, 1)), Integer.valueOf(id.substring(1, 2)));
			final FieldType[][] temp = new FieldType[TicTacToeGameManager.SIZE][TicTacToeGameManager.SIZE];
			System.arraycopy(game.getField(), 0, temp, 0, game.getField().length);

			final TicTacToePlayer player = game.currentPlayer == FieldType.X ? game.getPlayerX() : game.getPlayerY();

			if (temp[coords.getKey()][coords.getValue()] != FieldType.EMPTY) {
				message.editMessage(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + player.getAsMention() + " | " + getTranslation("fieldoccupied", author, guild)).queue();
				// game.nextUser();
				this.rerun(game, channel);
				return;
			}

			temp[coords.getKey()][coords.getValue()] = game.currentPlayer;
			game.setField(temp);

			if (this.handleWin(game, coords))
				return;

			final List<ActionRow> placeAt = placeAt(game.getRows(), coords, game.currentPlayer);
			game.setRows(placeAt);
			message.editMessage(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentPlayer == FieldType.O ? game.getPlayerX().getName() : game.getPlayerY().getName()))).setActionRows(placeAt).queue();
			game.nextUser();

			if (game.getPlayerY().isBot()) {
				try {
					Thread.sleep(Bot.random.nextInt(1500) + 1000 * (game.getMoves() / 2 + 1));
				} catch (final InterruptedException ex) {
					ex.printStackTrace();
				}

				coords = TicTacToeBot.move(game);
				temp[coords.getKey()][coords.getValue()] = game.currentPlayer;
				game.setField(temp);

				if (this.handleWin(game, coords))
					return;

				final List<ActionRow> placeAtBot = placeAt(game.getRows(), coords, game.currentPlayer);
				game.setRows(placeAtBot);
				message.editMessage(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentPlayer == FieldType.O ? game.getPlayerX().getName() : game.getPlayerY().getName()))).setActionRows(placeAtBot).queue();
				game.nextUser();
				this.rerun(game, channel);
				return;
			} else {
				this.rerun(game, channel);
				return;
			}
		}, 1, TimeUnit.MINUTES, () -> {
			game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed().addField(guild.getLanguage().getTranslationNonNull("timeout"), guild.getLanguage().getTranslationNonNull("tooktoolong"), false).build()).setActionRows().queue();
			TicTacToeGameManager.deleteGame(game);
			return;
		});
	}

	private static List<ActionRow> placeAt(final List<ActionRow> actionRowsRaw, final Pair<Integer, Integer> coords, final FieldType currentPlayer) {
		final List<ActionRow> actionRows = new ArrayList<>(actionRowsRaw);
		final List<Button> buttons = new ArrayList<>(actionRows.get(coords.getKey()).getButtons());
		buttons.set(coords.getValue(), Button.primary(coords.getKey() + "" + coords.getValue(), currentPlayer.name()));
		final ActionRow of = ActionRow.of(buttons);
		actionRows.set(coords.getKey(), of);
		return actionRows;
	}

	private boolean handleWin(final TicTacToe game, final Pair<Integer, Integer> coords) {
		final FieldType firstWinner = game.getWinner(coords.getKey(), coords.getValue());
		if (firstWinner == null)
			return false;
		else {
			final List<ActionRow> placeAt = placeAt(game.getRows(), coords, game.currentPlayer);
			placeAt.remove(3);
			if (firstWinner != FieldType.EMPTY) {
				game.getMessage().editMessage("WE HAVE A WINNER!\nAnd the winner is....\n" + Utils.removeMarkdown((firstWinner == FieldType.X ? game.getPlayerX().getAsMention() : game.getPlayerY().getAsMention())) + "!").setActionRows(placeAt).queue();
				TicTacToeGameManager.deleteGame(game);
				return true;
			} else {
				game.getMessage().editMessage("WE HAVE NO WINNER!\nu both succ, nobody won, lul").setActionRows(placeAt).queue();
				TicTacToeGameManager.deleteGame(game);
				return true;
			}
		}
	}
}