package com.github.black0nion.blackonionbot.systems.games.connectfour;

import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ConnectFour {
	long messageID;
	final User playerX;
	final User playerY;
	FieldType[][] field;
	public FieldType currentUser;
	final MessageChannel channel;

	public ConnectFour(final MessageChannel channel, final User playerX, final User playerY) {

		this.field = new FieldType[ConnectFourGameManager.HEIGHT][ConnectFourGameManager.WIDTH];

		this.currentUser = ThreadLocalRandom.current().nextInt(2) == 0 ? FieldType.X : FieldType.O;

		for (int x = 0; x < ConnectFourGameManager.WIDTH; x++) {
			for (int y = 0; y < ConnectFourGameManager.HEIGHT; y++) {
				this.field[y][x] = FieldType.EMPTY;
			}
		}

		channel.sendMessageEmbeds(EmbedUtils.getSuccessEmbed().setTitle("Connect 4 | Aktueller Spieler: " + Utils.escapeMarkdown((this.currentUser == FieldType.X ? playerX.getName() : playerY.getName()))).addField("Current State:", this.getFieldString(), false).build()).queue(success -> this.messageID = success.getIdLong());
		this.channel = channel;
		this.playerX = playerX;
		this.playerY = playerY;
	}

	public Message getMessage() {
		return this.channel.retrieveMessageById(this.messageID).submit().join();
	}

	public User getPlayerX() {
		return this.playerX;
	}

	public User getPlayerY() {
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

	public FieldType getWinner() {
		if (this.won(FieldType.X)) return FieldType.X;
		else if (this.won(FieldType.O)) return FieldType.O;
		return FieldType.EMPTY;
	}

	public boolean won(final FieldType player) {
		// horizontalCheck
		for (int j = 0; j < ConnectFourGameManager.WIDTH - 3; j++) {
			for (int i = 0; i < ConnectFourGameManager.HEIGHT; i++)
				if (this.field[i][j] == player && this.field[i][j + 1] == player && this.field[i][j + 2] == player && this.field[i][j + 3] == player)
					return true;
		}
		// verticalCheck
		for (int i = 0; i < ConnectFourGameManager.HEIGHT - 3; i++) {
			for (int j = 0; j < ConnectFourGameManager.WIDTH; j++)
				if (this.field[i][j] == player && this.field[i + 1][j] == player && this.field[i + 2][j] == player && this.field[i + 3][j] == player)
					return true;
		}
		// ascendingDiagonalCheck
		for (int i = 3; i < ConnectFourGameManager.HEIGHT; i++) {
			for (int j = 0; j < ConnectFourGameManager.WIDTH - 3; j++)
				if (this.field[i][j] == player && this.field[i - 1][j + 1] == player && this.field[i - 2][j + 2] == player && this.field[i - 3][j + 3] == player)
					return true;
		}
		// descendingDiagonalCheck
		for (int i = 3; i < ConnectFourGameManager.HEIGHT; i++) {
			for (int j = 3; j < ConnectFourGameManager.WIDTH; j++)
				if (this.field[i][j] == player && this.field[i - 1][j - 1] == player && this.field[i - 2][j - 2] == player && this.field[i - 3][j - 3] == player)
					return true;
		}

		return false;
	}

	public Point getCoordinatesFromString(final String input) {
		final char[] charInput = input.toCharArray();
		int y = -1;
		for (int i = 0; i < Utils.ALPHABET.size(); i++) if (Utils.ALPHABET.get(i) == charInput[0]) y = i;
		// should never get called because isValidInput is called first
		return new Point(Integer.parseInt(String.valueOf(charInput[1])), y);
	}

	public boolean isValidInput(final String input) {
		final char[] charInput = input.toCharArray();
		try {
			final int numbAtOne = Integer.parseInt(String.valueOf(charInput[1]));
			if (Utils.ALPHABET.contains(charInput[0]) && Utils.ALPHABET.indexOf(charInput[0]) < ConnectFourGameManager.WIDTH && numbAtOne >= 0 && numbAtOne < ConnectFourGameManager.HEIGHT)
				return true;
		} catch (final Exception ignored) {
		}
		return false;
	}

	public void nextUser() {
		if (this.currentUser == FieldType.X) {
			this.currentUser = FieldType.O;
		} else {
			this.currentUser = FieldType.X;
		}
	}

	public String getFieldString() {
		StringBuilder output = new StringBuilder();
		output.append("   A   B   C   D   E   F   G   H   I\n ----------------------------\n");
		for (int y = 0; y < ConnectFourGameManager.HEIGHT; y++) {
			for (int x = 0; x < ConnectFourGameManager.WIDTH; x++) {
				output.append("\\| ").append(this.field[y][x] == FieldType.EMPTY ? "    " : " " + this.field[y][x].name() + " ");
			}
			output.append("| (").append(y).append(")\n----------------------------\n");
		}
		return output.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConnectFour that = (ConnectFour) o;
		return messageID == that.messageID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(messageID);
	}
}