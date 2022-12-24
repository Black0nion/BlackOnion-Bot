package com.github.black0nion.blackonionbot.commands.slash.impl.fun;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToe;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToeBot;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToeGameManager;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToePlayer;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Pair;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.github.black0nion.blackonionbot.systems.language.LanguageSystem.getTranslation;

public class TicTacToeCommand extends SlashCommand {
	private static final String USER = "user";

	public TicTacToeCommand() {
		super(builder(Commands.slash("tictactoe", "Used to play TicTacToe with the someone.")
			.addOption(OptionType.USER, USER, "The user to play with.")));
	}

	public void rerun(final TicTacToe game, final TextChannel channel) {
		final BlackGuild guild = BlackGuild.from(channel.getGuild());
		Bot.getInstance().getEventWaiter().waitForEvent(ButtonInteractionEvent.class, answerEvent -> answerEvent.getGuild() != null && answerEvent.getGuild().getIdLong() == channel.getGuild().getIdLong() && answerEvent.getMessage().getIdLong() == game.getMessage().getIdLong() && game.isPlayer(answerEvent.getUser().getId()), answerEvent -> {
			answerEvent.deferEdit().queue();
			final BlackUser author = BlackUser.from(answerEvent.getUser());
			final String id = answerEvent.getButton().getId();
			assert id != null;

			final Message message = game.getMessage();
			if (id.equalsIgnoreCase("leave")) {
				message.editMessage(getTranslation("usergaveup", author, guild).replace("%user%", author.getAsMention()))
					.setComponents(game.getRows().stream().map(row -> row.getButtons().stream().map(Button::asDisabled).toList()).map(ActionRow::of).toList()).queue();
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

			if (temp[coords.getFirst()][coords.getSecond()] != FieldType.EMPTY) {
				message.editMessage(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + player.getAsMention() + " | " + getTranslation("fieldoccupied", author, guild)).queue();
				this.rerun(game, channel);
				return;
			}

			temp[coords.getFirst()][coords.getSecond()] = game.currentPlayer;
			game.setField(temp);

			if (this.handleWin(game, coords)) return;

			List<ActionRow> placeAt = placeAt(game.getRows(), coords, game.currentPlayer);
			if (game.getPlayerY().isBot()) {
				placeAt = placeAt.stream().map(row -> ActionRow.of(row.getButtons().stream().map(b -> {
					if (!Objects.equals(b.getId(), "leave")) return b.asDisabled();
					else return b;
				}).toList())).toList();
			}
			game.setRows(placeAt);
			message.editMessage(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + (game.currentPlayer == FieldType.O ? game.getPlayerX().getAsMention() : game.getPlayerY().getAsMention())).setComponents(placeAt).queue();
			game.nextUser();

			if (game.getPlayerY().isBot()) {
				try {
					// fake delay
					Thread.sleep(ThreadLocalRandom.current().nextInt(1000) + 1000 * (game.getMoves() / 2L + 1));
				} catch (final InterruptedException ex) {
					ex.printStackTrace();
				}

				coords = TicTacToeBot.move(game);
				temp[coords.getFirst()][coords.getSecond()] = game.currentPlayer;
				game.setField(temp);

				if (this.handleWin(game, coords)) return;

				final List<ActionRow> placeAtBot = placeAt(game.getRows(), coords, game.currentPlayer).stream().map(row -> ActionRow.of(row.getButtons().stream().map(b -> {
					if (b.getLabel().equals(" ")) return b.asEnabled();
					else return b;
				}).toList())).toList();
				game.setRows(placeAtBot);
				message.editMessage(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + (game.currentPlayer == FieldType.O ? game.getPlayerX().getAsMention() : game.getPlayerY().getAsMention())).setComponents(placeAtBot).queue();
				game.nextUser();
				this.rerun(game, channel);
			} else {
				this.rerun(game, channel);
			}
		}, 1, TimeUnit.MINUTES, () -> {
			Language language = Optional.ofNullable(guild.getLanguage()).orElseGet(LanguageSystem::getDefaultLanguage);
			game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed().addField(language.getTranslationNonNull("timeout"), language.getTranslationNonNull("tooktoolong"), false).build()).setActionRow().queue();
			TicTacToeGameManager.deleteGame(game);
		});
	}

	private static List<ActionRow> placeAt(final List<ActionRow> actionRowsRaw, final Pair<Integer, Integer> coords, final FieldType currentPlayer) {
		final List<ActionRow> actionRows = new ArrayList<>(actionRowsRaw);
		final List<Button> buttons = new ArrayList<>(actionRows.get(coords.getFirst()).getButtons());
		buttons.set(coords.getSecond(), Button.of(currentPlayer == FieldType.X ? ButtonStyle.SUCCESS : ButtonStyle.PRIMARY, coords.getFirst() + "" + coords.getSecond(), currentPlayer.name()).asDisabled());
		final ActionRow of = ActionRow.of(buttons);
		actionRows.set(coords.getFirst(), of);
		return actionRows;
	}

	private boolean handleWin(final TicTacToe game, final Pair<Integer, Integer> coords) {
		final FieldType firstWinner = game.getWinner(coords.getFirst(), coords.getSecond());
		if (firstWinner == null) return false;
		else {
			final List<ActionRow> placeAt = placeAt(game.getRows(), coords, game.currentPlayer);
			placeAt.remove(3);
			if (firstWinner == FieldType.EMPTY) {
				game.getMessage().editMessage("WE HAVE NO WINNER!\nu both succ, nobody won, lul").setComponents(placeAt).queue();
			} else {
				game.getMessage().editMessage("WE HAVE A WINNER!\nAnd the winner is....\n" + (firstWinner == FieldType.X ? game.getPlayerX().getAsMention() : game.getPlayerY().getAsMention()) + "!").setComponents(placeAt).queue();
			}
			TicTacToeGameManager.deleteGame(game);
			return true;
		}
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		var challenged = BlackUser.from(Objects.requireNonNull(e.getOption(USER, OptionMapping::getAsUser)));
		if (challenged.getIdLong() == e.getJDA().getSelfUser().getIdLong()) {
			this.rerun(TicTacToeGameManager.createGame(e.getChannel().asTextChannel(), new TicTacToePlayer(author), new TicTacToePlayer()), e.getChannel().asTextChannel());
			return;
		} else if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
			e.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("errorcantplayagainst", author, guild).replace("%enemy%", (challenged.isBot() ? getTranslation("bot", author, guild) : getTranslation("yourself", author, guild))), getTranslation("nofriends", author, guild), false).build()).queue();
			return;
		} else if (TicTacToeGameManager.isIngame(author.getId()) || TicTacToeGameManager.isIngame(challenged.getId())) {
			e.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("alreadyingame", author, guild), getTranslation("nomultitasking", author, guild), false).build()).queue();
			return;
		}
		e.reply(getTranslation("ttt_askforaccept", author, guild).replace("%challenged%", challenged.getAsMention()).replace("%challenger%", author.getAsMention()) + " " + getTranslation("answerwithyes", author, guild)).queue();
		Bot.getInstance().getEventWaiter().waitForEvent(MessageReceivedEvent.class, event -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), event -> {
			if (event.getAuthor().isBot()) return;
			final BlackUser eventAuthor = BlackUser.from(event.getAuthor());
			if (eventAuthor.getId().equals(challenged.getId()))
				if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {

					e.getHook().sendMessageEmbeds(cmde.success().addField(getTranslation("challengeaccepted", eventAuthor, guild), getTranslation("playingagainst", eventAuthor, guild).replace("%challenger%", author.getAsMention()), false).build()).queue();

					// Accepted
					final TicTacToe game = TicTacToeGameManager.createGame(e.getChannel().asTextChannel(), new TicTacToePlayer(author), new TicTacToePlayer(challenged));
					this.rerun(game, e.getChannel().asTextChannel());
				} else {
					ConnectFourCommand.isDeclined(e, guild, event, author, getTranslation("declined", eventAuthor, guild), getTranslation("challengedeclined", eventAuthor, guild), getTranslation("arentyoubraveenough", eventAuthor, guild), getTranslation("answerwithyes", eventAuthor, guild));
				}
		}, 1, TimeUnit.MINUTES, () -> e.replyEmbeds(EmbedUtils.getErrorEmbed(challenged, guild).addField(getTranslation("timeout", challenged, guild), getTranslation("tooktoolong", author, guild), false).build()).queue());
	}
}
