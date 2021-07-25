package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class TicTacToe {
	Message message;
	TicTacToePlayer playerX;
	TicTacToePlayer playerY;
	FieldType[][] field;
	public FieldType currentPlayer;
	TextChannel channel;
	BlackGuild guild;
	int moves = 0;
	private static final List<ActionRow> initialRows = new ArrayList<>();

	static {
		try {
			IntStream.range(0, 3).forEach(horizontal -> {
				final List<Button> row = new ArrayList<>();
				IntStream.range(0, 3).forEach(vertical -> row.add(Button.primary(horizontal + "" + vertical, " ")));
				initialRows.add(ActionRow.of(row));
			});
			initialRows.add(ActionRow.of(Button.danger("leave", "Leave")));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public TicTacToe(final TextChannel channel, final TicTacToePlayer playerX, final TicTacToePlayer playerY) {
		this.field = new FieldType[TicTacToeGameManager.SIZE][TicTacToeGameManager.SIZE];
		this.guild = BlackGuild.from(channel.getGuild());

		this.currentPlayer = Bot.random.nextInt(1) == 0 ? FieldType.X : FieldType.O;

		for (int x = 0; x < TicTacToeGameManager.SIZE; x++) {
			for (int y = 0; y < TicTacToeGameManager.SIZE; y++) {
				this.field[y][x] = FieldType.EMPTY;
			}
		}

		final BlackUser currentUser = BlackUser.from(this.currentPlayer == FieldType.X ? playerX.getUser() : (playerY.isBot() ? playerX.getUser() : playerY.getUser()));

		channel.sendMessage(LanguageSystem.getTranslation("tictactoe", currentUser, this.guild) + " | " + LanguageSystem.getTranslation("currentplayer", currentUser, this.guild) + (this.currentPlayer == FieldType.X ? playerX.getAsMention() : playerY.getAsMention())).setActionRows(this.rows).queue(success -> this.message = success);
		this.channel = channel;
		this.playerX = playerX;
		this.playerY = playerY;
	}

	public Message getMessage() {
		return this.message;
	}

	public TicTacToePlayer getPlayerX() {
		return this.playerX;
	}

	public TicTacToePlayer getPlayerY() {
		return this.playerY;
	}

	public FieldType[][] getField() {
		return this.field;
	}

	public void setField(final FieldType[][] field) {
		this.field = field;
	}

	public boolean isPlayer(final String userId) {
		if (this.getPlayerX().getId().equals(userId) || this.getPlayerY().getId().equals(userId))
			return true;
		return false;
	}

	public int getMoves() {
		return this.moves;
	}

	public FieldType getWinner(final int x, final int y) {
		this.moves++;
		if (this.moves == Math.pow(TicTacToeGameManager.SIZE, 2D))
			return FieldType.EMPTY;
		else if (this.won(FieldType.X, x, y))
			return FieldType.X;
		else if (this.won(FieldType.O, x, y))
			return FieldType.O;
		return null;
	}

	public boolean won(final FieldType player, final int x, final int y) {
		final int n = TicTacToeGameManager.SIZE;

		// check col
		for (int i = 0; i < n; i++) {
			if (this.field[x][i] != player) {
				break;
			}
			if (i == n - 1)
				return true;
		}

		// check row
		for (int i = 0; i < n; i++) {
			if (this.field[i][y] != player) {
				break;
			}
			if (i == n - 1)
				return true;
		}

		// check diag
		if (x == y) {
			// we're on a diagonal
			for (int i = 0; i < n; i++) {
				if (this.field[i][i] != player) {
					break;
				}
				if (i == n - 1)
					return true;
			}
		}

		// check anti diag (thanks rampion)
		if (x + y == n - 1) {
			for (int i = 0; i < n; i++) {
				if (this.field[i][(n - 1) - i] != player) {
					break;
				}
				if (i == n - 1)
					return true;
			}
		}
		return false;
	}

	public void nextUser() {
		if (this.currentPlayer == FieldType.X) {
			this.currentPlayer = FieldType.O;
		} else {
			this.currentPlayer = FieldType.X;
		}
	}

	private List<ActionRow> rows = new ArrayList<>(initialRows);

	public void setRows(final List<ActionRow> placeAt) {
		this.rows = placeAt;
	}

	public List<ActionRow> getRows() {
		return this.rows;
	}
}