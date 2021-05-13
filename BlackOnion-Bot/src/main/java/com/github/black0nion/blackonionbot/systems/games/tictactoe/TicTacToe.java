package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import java.util.Map;
import java.util.Random;

import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class TicTacToe {
	long messageID;
	String playerX;
	String playerY;
	String playerNameX;
	String playerNameY;
	FieldType[][] field;
	public FieldType currentUser;
	MessageChannel channel;
	
	public TicTacToe(MessageChannel channel, String playerX, String playerNameX, String playerY, String playerNameY) {
		
		field = new FieldType[TicTacToeGameManager.Y][TicTacToeGameManager.X];
		
		currentUser = new Random().nextInt(1) == 0 ? FieldType.X : FieldType.Y;
		
		for (int x = 0; x < TicTacToeGameManager.X; x++) {
			for (int y = 0; y < TicTacToeGameManager.Y; y++) {
				field[y][x] = FieldType.EMPTY;
			}
		}
		
		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle("Connect 4 | Aktueller Spieler: " + Utils.removeMarkdown((currentUser == FieldType.X ? playerNameX : playerNameY))).addField("Current State:", getField(), false).build()).queue(success -> messageID = success.getIdLong());
		this.channel = channel;
		this.playerX = playerX;
		this.playerY = playerY;
		this.playerNameX = playerNameX;
		this.playerNameY = playerNameY;
	}
	
	public Message getMessage() {
		return channel.retrieveMessageById(messageID).complete();
	}

	public long getMessageID() {
		return messageID;
	}
	public void setMessageID(long messageID) {
		this.messageID = messageID;
	}
	public String getPlayerX() {
		return playerX;
	}
	public void setPlayerX(String playerX) {
		this.playerX = playerX;
	}
	public String getPlayerY() {
		return playerY;
	}
	public void setPlayerY(String playerY) {
		this.playerY = playerY;
	}
	public FieldType[][] getfield() {
		return field;
	}
	public void setfield(FieldType[][] field) {
		this.field = field;
	}
	
	public String getPlayerNameX() {
		return playerNameX;
	}

	public String getPlayerNameY() {
		return playerNameY;
	}
	
	public boolean isPlayer(String userId) {
		if (getPlayerX().equals(userId) || getPlayerY().equals(userId))
			return true;
		return false;
	}
	
	public FieldType getWinner() {
		if (won(FieldType.X)) {
			return FieldType.X;
		} else if (won(FieldType.Y)) {
			return FieldType.Y;
		}
		return FieldType.EMPTY;
	}
	
	public boolean won(FieldType player) {
	    // horizontalCheck 
	    for (int j = 0; j<TicTacToeGameManager.X-3 ; j++ ){
	        for (int i = 0; i<TicTacToeGameManager.Y; i++){
	            if (field[i][j] == player && field[i][j+1] == player && field[i][j+2] == player && field[i][j+3] == player){
	                return true;
	            }           
	        }
	    }
	    // verticalCheck
	    for (int i = 0; i<TicTacToeGameManager.Y-3 ; i++ ){
	        for (int j = 0; j<TicTacToeGameManager.X; j++){
	            if (field[i][j] == player && field[i+1][j] == player && field[i+2][j] == player && field[i+3][j] == player){
	                return true;
	            }           
	        }
	    }
	    // ascendingDiagonalCheck 
	    for (int i=3; i<TicTacToeGameManager.Y; i++){
	        for (int j=0; j<TicTacToeGameManager.X-3; j++){
	            if (field[i][j] == player && field[i-1][j+1] == player && field[i-2][j+2] == player && field[i-3][j+3] == player)
	                return true;
	        }
	    }
	    // descendingDiagonalCheck
	    for (int i=3; i<TicTacToeGameManager.Y; i++){
	        for (int j=3; j<TicTacToeGameManager.X; j++){
	            if (field[i][j] == player && field[i-1][j-1] == player && field[i-2][j-2] == player && field[i-3][j-3] == player)
	                return true;
	        }
	    }

		return false;
	}
	
	public Map.Entry<Integer, Integer> getCoordinatesFromString(String input) {
		char[] charInput = input.toCharArray();
		return new Map.Entry<Integer, Integer>() {
			@Override
			public Integer getKey() {
				// the letters
				return Integer.parseInt(String.valueOf(charInput[1]));
			}

			@Override
			public Integer getValue() {
				// the numbers
				for (int i = 0; i < Utils.alphabet.size(); i++) {
					if (Utils.alphabet.get(i) == charInput[0])
						return i;
				}
				// should never get called because isValidInput is called first
				return null;
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
			int numbAtOne = Integer.parseInt(String.valueOf(charInput[1]));
			if (Utils.alphabet.contains(charInput[0]) && numbAtOne >= 0 && numbAtOne < TicTacToeGameManager.Y) {
				return true;
			}
		} catch (Exception ignored) {}
		return false;
	}
	
	public void nextUser() {
		if (currentUser == FieldType.X)
			currentUser = FieldType.Y;
		else 
			currentUser = FieldType.X;
	}
	
	public String getField() {
		String output = "";
		output += "   A   B   C\n ----------------------------\n";
		for (int y = 0; y < TicTacToeGameManager.Y; y++) {
			for (int x = 0; x < TicTacToeGameManager.X; x++) {
				output += "\\| " + (field[y][x] == FieldType.EMPTY ? "    " : " " + field[y][x].name() + " ");
			}
			output += "| (" + y + ")\n----------------------------\n";
		}
		return output;
	}
}
