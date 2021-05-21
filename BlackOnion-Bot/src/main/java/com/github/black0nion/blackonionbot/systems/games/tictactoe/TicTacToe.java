package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import java.util.Map;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class TicTacToe {
	Message message;
	TicTacToePlayer playerX;
	TicTacToePlayer playerY;
	FieldType[][] field;
	public FieldType currentPlayer;
	TextChannel channel;
	Guild guild;
	int moves = 0;
	
	public TicTacToe(TextChannel channel, TicTacToePlayer playerX, TicTacToePlayer playerY) {
		field = new FieldType[TicTacToeGameManager.SIZE][TicTacToeGameManager.SIZE];
		this.guild = channel.getGuild();
		
		currentPlayer = Bot.random.nextInt(1) == 0 ? FieldType.X : FieldType.Y;
		
		for (int x = 0; x < TicTacToeGameManager.SIZE; x++) {
			for (int y = 0; y < TicTacToeGameManager.SIZE; y++) {
				field[y][x] = FieldType.EMPTY;
			}
		}
		
		User currentUser = currentPlayer == FieldType.X ? playerX.getUser() : (playerY.isBot() ? playerX.getUser() : playerY.getUser());
		
		channel.sendMessage(EmbedUtils.getSuccessEmbed(currentUser, guild).setTitle(LanguageSystem.getTranslation("tictactoe", currentUser, guild) + " | " + LanguageSystem.getTranslation("currentplayer", currentUser, guild) + " | " + Utils.removeMarkdown((currentPlayer == FieldType.X ? playerX.getName() : playerY.getName()))).addField("currentstate", getFieldString(), false).build()).queue(success -> this.message = success);
		this.channel = channel;
		this.playerX = playerX;
		this.playerY = playerY;
	}
	
	public Message getMessage() {
		return message;
	}
	
	public TicTacToePlayer getPlayerX() {
		return playerX;
	}
	
	public TicTacToePlayer getPlayerY() {
		return playerY;
	}
	
	public FieldType[][] getField() {
		return field;
	}
	
	public void setField(FieldType[][] field) {
		this.field = field;
	}
	
	public boolean isPlayer(String userId) {
		if (getPlayerX().getId().equals(userId) || getPlayerY().getId().equals(userId))
			return true;
		return false;
	}
	
	public int getMoves() {
		return moves;
	}
	
	public FieldType getWinner(int x, int y) {
		moves++;
		if (moves == Math.pow((double) TicTacToeGameManager.SIZE, 2D)) {
			return FieldType.EMPTY;
		} else if (won(FieldType.X, x, y)) {
			return FieldType.X;
		} else if (won(FieldType.Y, x, y)) {
			return FieldType.Y;
		}
		return null;
	}
	
	public boolean won(FieldType player, int x, int y) {
		int n = TicTacToeGameManager.SIZE;
		
		//check col
        for(int i = 0; i < n; i++){
            if(field[x][i] != player)
                break;
            if(i == n-1){
                return true;
            }
        }

        //check row
        for(int i = 0; i < n; i++){
            if(field[i][y] != player)
                break;
            if(i == n-1){
                return true;
            }
        }

        //check diag
        if(x == y){
            //we're on a diagonal
            for(int i = 0; i < n; i++){
                if(field[i][i] != player)
                    break;
                if(i == n-1){
                    return true;
                }
            }
        }

        //check anti diag (thanks rampion)
        if(x + y == n - 1){
            for(int i = 0; i < n; i++){
                if(field[i][(n-1)-i] != player)
                    break;
                if(i == n-1){
                    return true;
                }
            }
        }
        
		return false;
	}
	
	public static Map.Entry<Integer, Integer> getCoordinatesFromString(String input) {
		char[] charInput = input.toCharArray();
		return new Map.Entry<Integer, Integer>() {
			@Override
			public Integer getKey() {
				// the letters
				return Integer.parseInt(String.valueOf(charInput[1]))-1;
			}

			@Override
			public Integer getValue() {
				// the numbers
				return Utils.alphabet.indexOf(charInput[0]);
			}

			@Override
			public Integer setValue(Integer value) {
				return null;
			}
		};
	}
	
	public boolean isValidInput(String input) {
		char[] charInput = input.toCharArray();
		try {
			int numbAtOne = Integer.parseInt(String.valueOf(charInput[1]))-1;
			if (Utils.alphabet.contains(charInput[0]) && Utils.alphabet.indexOf(charInput[0]) < TicTacToeGameManager.SIZE && numbAtOne >= 0 && numbAtOne < TicTacToeGameManager.SIZE) {
				return true;
			}
		} catch (Exception ignored) {}
		return false;
	}
	
	public void nextUser() {
		if (currentPlayer == FieldType.X)
			currentPlayer = FieldType.Y;
		else 
			currentPlayer = FieldType.X;
	}
	
	public String getFieldString() {
		String output = "```";
		output += "    A   B   C\n  ┌───┬───┬───┐\n";
		for (int y = 0; y < TicTacToeGameManager.SIZE; y++) {
			output += (y+1);
			for (int x = 0; x < TicTacToeGameManager.SIZE; x++) {
				output += " │" + (field[y][x] == FieldType.EMPTY ? "  " :  " " + field[y][x].name());
			}
			if (y != TicTacToeGameManager.SIZE-1)
				output += " │\n  ├───┼───┼───┤\n";
			else
				output += " │\n  └───┴───┴───┘";
				
		}
		return output + "```";
	}
}
