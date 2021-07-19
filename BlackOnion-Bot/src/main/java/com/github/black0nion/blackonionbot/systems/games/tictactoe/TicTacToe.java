package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Pair;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class TicTacToe {
    Message message;
    TicTacToePlayer playerX;
    TicTacToePlayer playerY;
    FieldType[][] field;
    public FieldType currentPlayer;
    TextChannel channel;
    BlackGuild guild;
    int moves = 0;

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

	channel.sendMessageEmbeds(EmbedUtils.getSuccessEmbed(currentUser, this.guild).setTitle(LanguageSystem.getTranslation("tictactoe", currentUser, this.guild) + " | " + LanguageSystem.getTranslation("currentplayer", currentUser, this.guild) + " | " + Utils.removeMarkdown((this.currentPlayer == FieldType.X ? playerX.getName() : playerY.getName()))).addField("currentstate", this.getFieldString(), false).build()).queue(success -> this.message = success);
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
	if (this.getPlayerX().getId().equals(userId) || this.getPlayerY().getId().equals(userId)) return true;
	return false;
    }

    public int getMoves() {
	return this.moves;
    }

    public FieldType getWinner(final int x, final int y) {
	this.moves++;
	if (this.moves == Math.pow(TicTacToeGameManager.SIZE, 2D)) return FieldType.EMPTY;
	else if (this.won(FieldType.X, x, y)) return FieldType.X;
	else if (this.won(FieldType.O, x, y)) return FieldType.O;
	return null;
    }

    public boolean won(final FieldType player, final int x, final int y) {
	final int n = TicTacToeGameManager.SIZE;

	//check col
	for (int i = 0; i < n; i++) {
	    if (this.field[x][i] != player) {
		break;
	    }
	    if (i == n - 1) return true;
	}

	//check row
	for (int i = 0; i < n; i++) {
	    if (this.field[i][y] != player) {
		break;
	    }
	    if (i == n - 1) return true;
	}

	//check diag
	if (x == y) {
	    //we're on a diagonal
	    for (int i = 0; i < n; i++) {
		if (this.field[i][i] != player) {
		    break;
		}
		if (i == n - 1) return true;
	    }
	}

	//check anti diag (thanks rampion)
	if (x + y == n - 1) {
	    for (int i = 0; i < n; i++) {
		if (this.field[i][(n - 1) - i] != player) {
		    break;
		}
		if (i == n - 1) return true;
	    }
	}
	return false;
    }

    public static Pair<Integer, Integer> getCoordinatesFromString(final String input) {
	final char[] charInput = input.toCharArray();
	return new Pair<Integer, Integer>(Integer.parseInt(String.valueOf(charInput[1])) - 1, Utils.alphabet.indexOf(charInput[0]));
    }

    public boolean isValidInput(final String input) {
	final char[] charInput = input.toCharArray();
	try {
	    final int numbAtOne = Integer.parseInt(String.valueOf(charInput[1])) - 1;
	    if (Utils.alphabet.contains(charInput[0]) && Utils.alphabet.indexOf(charInput[0]) < TicTacToeGameManager.SIZE && numbAtOne >= 0 && numbAtOne < TicTacToeGameManager.SIZE) return true;
	} catch (final Exception ignored) {
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

    public String getFieldString() {
	String output = "```";
	output += "    A   B   C\n  ┌───┬───┬───┐\n";
	for (int y = 0; y < TicTacToeGameManager.SIZE; y++) {
	    output += (y + 1);
	    for (int x = 0; x < TicTacToeGameManager.SIZE; x++) {
		output += " │" + (this.field[y][x] == FieldType.EMPTY ? "  " : " " + this.field[y][x].name());
	    }
	    if (y != TicTacToeGameManager.SIZE - 1) {
		output += " │\n  ├───┼───┼───┤\n";
	    } else {
		output += " │\n  └───┴───┴───┘";
	    }

	}
	return output + "```";
    }
}
