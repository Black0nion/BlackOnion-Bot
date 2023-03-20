package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeBot {

	public static final FieldType ai = FieldType.O;
	public static final FieldType human = FieldType.X;

	public static Pair<Integer, Integer> move(final TicTacToe game) {
		final FieldType[][] board = game.getField();

		Pair<Integer, Integer> bestMove = null;
		int bestScore = -100;
		for (final Pair<Integer, Integer> position : getAvailablePositions(board)) {
			board[position.getFirst()][position.getSecond()] = ai;
			final int score = minimax(board);
			board[position.getFirst()][position.getSecond()] = FieldType.EMPTY;
			if (score > bestScore) {
				bestScore = score;
				bestMove = position;
			}
		}
		return bestMove;
	}

	public static int minimax(final FieldType[][] board) {
		final FieldType winner = checkWon(board);
		int score;
		if (winner != null) {
			if (winner == ai) {
				score = 1;
				return score;
			} else if (winner == FieldType.EMPTY) {
				score = 0;
				return score;
			} else {
				score = -1;
				return score;
			}
		}

		int aiCount = 0;
		int humanCount = 0;

		for (FieldType[] fieldTypes : board) {
			for (FieldType fieldType : fieldTypes) {
				if (fieldType == ai) {
					aiCount++;
				} else if (fieldType == human) {
					humanCount++;
				}
			}
		}

		int bestScore;
		if (humanCount > aiCount) {
			bestScore = -1;
		} else {
			bestScore = 1;
		}

		for (final Pair<Integer, Integer> position : getAvailablePositions(board)) {
			board[position.getFirst()][position.getSecond()] = humanCount > aiCount ? ai : human;
			final int currentScore = minimax(board);
			board[position.getFirst()][position.getSecond()] = FieldType.EMPTY;
			if (humanCount > aiCount ? currentScore > bestScore : currentScore < bestScore) {
				bestScore = currentScore;
			}
		}
		return bestScore;
	}

	private static List<Pair<Integer, Integer>> getAvailablePositions(final FieldType[][] board) {
		final List<Pair<Integer, Integer>> availablePositions = new ArrayList<>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == FieldType.EMPTY) {
					availablePositions.add(new Pair<>(i, j));
				}
			}
		}
		return availablePositions;
	}

	private static FieldType checkWon(final FieldType[][] board) {
		FieldType checkingPlayer;

		// check columns
		for (int i = 0; i < board.length; i++) {
			checkingPlayer = board[0][i];
			if (checkingPlayer == FieldType.EMPTY) {
				continue;
			}
			for (int j = 0; j < board[i].length; j++) {
				if (board[j][i] != checkingPlayer) {
					checkingPlayer = null;
					break;
				}
			}
			if (checkingPlayer != null)
				return checkingPlayer;
		}

		// check rows
		for (final FieldType[] cellStates : board) {
			checkingPlayer = cellStates[0];
			if (checkingPlayer == FieldType.EMPTY) {
				continue;
			}
			for (final FieldType cellState : cellStates) {
				if (cellState != checkingPlayer) {
					checkingPlayer = null;
					break;
				}
			}
			if (checkingPlayer != null)
				return checkingPlayer;
		}

		// check diagonals
		checkingPlayer = board[0][0];
		if (board[1][1] == checkingPlayer && board[2][2] == checkingPlayer && checkingPlayer != FieldType.EMPTY)
			return checkingPlayer;

		checkingPlayer = board[2][0];
		if (board[1][1] == checkingPlayer && board[0][2] == checkingPlayer && checkingPlayer != FieldType.EMPTY)
			return checkingPlayer;

		// check tie;
		for (final FieldType[] cellStates : board) {
			for (final FieldType cellState : cellStates) {
				if (cellState == FieldType.EMPTY)
					return null;
			}
		}

		return FieldType.EMPTY;
	}
}
