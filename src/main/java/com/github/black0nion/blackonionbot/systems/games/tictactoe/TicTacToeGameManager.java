package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.TextChannel;

public class TicTacToeGameManager {
  private static final List<TicTacToe> games = new ArrayList<>();

  public static final int SIZE = 3;

  public static TicTacToe createGame(final TextChannel channel, final TicTacToePlayer playerX,
      final TicTacToePlayer playerY) {
    final TicTacToe newGame = new TicTacToe(channel, playerX, playerY);
    games.add(newGame);
    return newGame;
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
