package com.github.black0nion.blackonionbot.systems.games.connectfour;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class ConnectFourGameManager {
  private static final ArrayList<ConnectFour> games = new ArrayList<>();

  public static final int HEIGHT = 8;
  public static final int WIDTH = 9;

  /**
   *
   * @param playerX UserID of Player X
   * @param playerY UserID of Player Y
   * @return The new created Game
   */
  public static ConnectFour createGame(final MessageChannel channel, final User playerX,
      final User playerY) {
    final ConnectFour newGame = new ConnectFour(channel, playerX, playerY);
    games.add(newGame);
    return newGame;
  }

  public static void deleteGame(final ConnectFour game) {
    games.remove(game);
  }

  public static boolean isIngame(final String userID) {
    for (final ConnectFour game : games)
      if (game.getPlayerX().getId().equals(userID) || game.getPlayerY().getId().equals(userID))
        return true;
    return false;
  }
}
