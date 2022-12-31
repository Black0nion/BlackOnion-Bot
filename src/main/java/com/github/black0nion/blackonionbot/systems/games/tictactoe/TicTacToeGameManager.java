package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class TicTacToeGameManager {
	private static final List<TicTacToe> games = new ArrayList<>();

	public static final int SIZE = 3;

	public static void createGame(final LanguageSystem languageSystem, final BlackGuild guild, final IReplyCallback callback, final TicTacToePlayer playerX, final TicTacToePlayer playerY, Consumer<TicTacToe> onSent) {
		new TicTacToe(languageSystem, callback, guild, playerX, playerY, newGame -> {
			games.add(newGame);
			onSent.accept(newGame);
		});
	}

	public static void deleteGame(final TicTacToe game) {
		games.remove(game);
	}

	public static boolean isIngame(final String userID) {
		for (final TicTacToe game : games)
			if (game.getPlayerX().getId().equals(userID) || game.getPlayerY().getId().equals(userID))
				return true;
		return false;
	}

}
