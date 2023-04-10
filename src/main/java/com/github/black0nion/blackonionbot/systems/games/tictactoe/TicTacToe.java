package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepo;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class TicTacToe {
	Message message;
	TicTacToePlayer playerX;
	TicTacToePlayer playerY;
	FieldType[][] field;
	public FieldType currentPlayer;
	Guild guild;
	int moves = 0;
	private static final List<ActionRow> initialRows = new ArrayList<>();

	static {
		try {
			IntStream.range(0, 3).forEach(horizontal -> {
				final List<Button> row = new ArrayList<>();
				IntStream.range(0, 3).forEach(vertical -> row.add(Button.secondary(horizontal + "" + vertical, " ")));
				initialRows.add(ActionRow.of(row));
			});
			initialRows.add(ActionRow.of(Button.danger("leave", "Leave")));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public TicTacToe(final LanguageSystem languageSystem, final IReplyCallback callback, final UserSettingsRepo userSettingsRepo, final Guild guild, final GuildSettings guildSettings, final TicTacToePlayer playerX, final TicTacToePlayer playerY, Consumer<TicTacToe> onSent) {
		this.field = new FieldType[TicTacToeGameManager.SIZE][TicTacToeGameManager.SIZE];
		this.guild = guild;

		this.currentPlayer = playerY.isBot() || ThreadLocalRandom.current().nextInt(2) == 0 ? FieldType.X : FieldType.O;

		for (int x = 0; x < TicTacToeGameManager.SIZE; x++) {
			for (int y = 0; y < TicTacToeGameManager.SIZE; y++) {
				this.field[y][x] = FieldType.EMPTY;
			}
		}

		final TicTacToePlayer currentPlayer = this.currentPlayer == FieldType.X ? playerX : playerY;
		final User currentUser = currentPlayer.isBot() ? playerX.getUser() : currentPlayer.getUser();
		assert currentUser != null;
		final UserSettings userSettings = userSettingsRepo.getSettings(currentUser);

		callback.reply(
			languageSystem.getTranslation("tictactoe", userSettings, guildSettings)
				+ " | "
				+ languageSystem.getTranslation("currentplayer", userSettings, guildSettings)
				+ (this.currentPlayer == FieldType.X ? playerX.getAsMention() : playerY.getAsMention())
			).setComponents(this.rows)
			.flatMap(InteractionHook::retrieveOriginal)
			.queue(success -> {
				this.message = success;
				onSent.accept(this);
			});

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
		return this.getPlayerX().getId().equals(userId) || this.getPlayerY().getId().equals(userId);
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

	@SuppressWarnings("ConstantConditions")
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
