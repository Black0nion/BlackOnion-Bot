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
	TicTacToePlayer playerX;
	TicTacToePlayer playerY;
	FieldType[][] field;
	public FieldType currentUser;
	MessageChannel channel;
	
	public TicTacToe(MessageChannel channel, TicTacToePlayer playerX, TicTacToePlayer playerY) {
		field = new FieldType[TicTacToeGameManager.SIZE][TicTacToeGameManager.SIZE];
		
		currentUser = new Random().nextInt(1) == 0 ? FieldType.X : FieldType.Y;
		
		for (int x = 0; x < TicTacToeGameManager.SIZE; x++) {
			for (int y = 0; y < TicTacToeGameManager.SIZE; y++) {
				field[y][x] = FieldType.EMPTY;
			}
		}
		
		channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle("Connect 4 | Aktueller Spieler: " + Utils.removeMarkdown((currentUser == FieldType.X ? playerX.getName() : playerY.getName()))).addField("Current State:", getField(), false).build()).queue(success -> messageID = success.getIdLong());
		this.channel = channel;
		this.playerX = playerX;
		this.playerY = playerY;
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
	
	public TicTacToePlayer getPlayerX() {
		return playerX;
	}
	
	public TicTacToePlayer getPlayerY() {
		return playerY;
	}
	
	public FieldType[][] getfield() {
		return field;
	}
	
	public void setfield(FieldType[][] field) {
		this.field = field;
	}
	
	public boolean isPlayer(String userId) {
		if (getPlayerX().getId().equals(userId) || getPlayerY().getId().equals(userId))
			return true;
		return false;
	}
	
	public FieldType getWinner(int x, int y) {
		if (won(FieldType.X, x, y)) {
			return FieldType.X;
		} else if (won(FieldType.Y, x, y)) {
			return FieldType.Y;
		}
		return FieldType.EMPTY;
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
	
	public Map.Entry<Integer, Integer> getCoordinatesFromString(String input) {
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
			int numbAtOne = Integer.parseInt(String.valueOf(charInput[1]))-1;
			if (Utils.alphabet.contains(charInput[0]) && Utils.alphabet.indexOf(charInput[0]) < TicTacToeGameManager.SIZE && numbAtOne >= 0 && numbAtOne < TicTacToeGameManager.SIZE) {
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
