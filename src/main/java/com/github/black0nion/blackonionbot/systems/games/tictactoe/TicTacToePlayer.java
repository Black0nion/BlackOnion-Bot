package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import javax.annotation.Nullable;

import net.dv8tion.jda.api.entities.User;

public class TicTacToePlayer {
  public User user = null;

  public TicTacToePlayer() {}

  public TicTacToePlayer(final User user) {
    this.user = user;
  }

  @Nullable
  public User getUser() {
    return this.user;
  }

  public String getId() {
    return this.user != null ? this.user.getId() : "bot";
  }

  public String getName() {
    return this.user != null ? this.user.getName() : "Bot";
  }

  public String getAsMention() {
    return this.user != null ? this.user.getAsMention() : "Bot";
  }

  public boolean isBot() {
    return this.user == null;
  }
}
