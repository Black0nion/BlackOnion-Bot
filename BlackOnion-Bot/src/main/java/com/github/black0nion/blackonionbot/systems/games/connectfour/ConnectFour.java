package com.github.black0nion.blackonionbot.systems.games.connectfour;

import java.util.Map;
import java.util.Random;

import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class ConnectFour {
	long messageID;
	User playerX;
	User playerY;
	FieldType[][] field;
	public FieldType currentUser;
	MessageChannel channel;
	
	public ConnectFour(MessageChannel channel, User playerX, User playerY) {
		
		field = new FieldType[ConnectFourGameManager.Y][ConnectFourGameManager.X];
		
		currentUser = new Random().nextInt(1) == 0 ? FieldType.X : FieldType.Y;
		
		for (int x = 0; x < ConnectFourGameManager.X; x++) {
			for (int y = 0; y < ConnectFourGameManager.Y; y++) {
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
	
	public User getPlayerX() {
		return playerX;
	}

	public User getPlayerY() {
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
	    for (int j = 0; j<ConnectFourGameManager.X-3 ; j++ ){
	        for (int i = 0; i<ConnectFourGameManager.Y; i++){
	            if (field[i][j] == player && field[i][j+1] == player && field[i][j+2] == player && field[i][j+3] == player){
	                return true;
	            }           
	        }
	    }
	    // verticalCheck
	    for (int i = 0; i<ConnectFourGameManager.Y-3 ; i++ ){
	        for (int j = 0; j<ConnectFourGameManager.X; j++){
	            if (field[i][j] == player && field[i+1][j] == player && field[i+2][j] == player && field[i+3][j] == player){
	                return true;
	            }           
	        }
	    }
	    // ascendingDiagonalCheck 
	    for (int i=3; i<ConnectFourGameManager.Y; i++){
	        for (int j=0; j<ConnectFourGameManager.X-3; j++){
	            if (field[i][j] == player && field[i-1][j+1] == player && field[i-2][j+2] == player && field[i-3][j+3] == player)
	                return true;
	        }
	    }
	    // descendingDiagonalCheck
	    for (int i=3; i<ConnectFourGameManager.Y; i++){
	        for (int j=3; j<ConnectFourGameManager.X; j++){
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
			if (Utils.alphabet.contains(charInput[0]) && Utils.alphabet.indexOf(charInput[0]) < ConnectFourGameManager.X && numbAtOne >= 0 && numbAtOne < ConnectFourGameManager.Y) {
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
		output += "   A   B   C   D   E   F   G   H   I\n ----------------------------\n";
		for (int y = 0; y < ConnectFourGameManager.Y; y++) {
			for (int x = 0; x < ConnectFourGameManager.X; x++) {
				output += "\\| " + (field[y][x] == FieldType.EMPTY ? "    " : " " + field[y][x].name() + " ");
			}
			output += "| (" + y + ")\n----------------------------\n";
		}
		return output;
	}
}
