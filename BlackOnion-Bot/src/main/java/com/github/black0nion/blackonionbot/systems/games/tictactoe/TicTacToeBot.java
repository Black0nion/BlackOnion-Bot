package com.github.black0nion.blackonionbot.systems.games.tictactoe;

import com.github.black0nion.blackonionbot.systems.games.FieldType;

public class TicTacToeBot {
	public static void move(TicTacToe game) {
		FieldType[][] board = game.getfield();
	    boolean mademove = false;
	    final FieldType humanMarker = FieldType.X;
	    final FieldType botSymbol = FieldType.Y;

	    // check if you can take a win horizontally
		for (int i = 0; i < 3; i++) {
	        if (board[0][i].equals(board[1][i]) && board[0][i].equals(botSymbol)) {
	            if (board[2][i] != humanMarker && board[2][i] != botSymbol) {
	                board[2][i] = botSymbol;
	                mademove = true;
	                return;
	            }
	        }
	    }

	    for (int i = 0; i < 3; i++) {
	        if (board[2][i].equals(board[1][i]) && board[2][i].equals(botSymbol)) {
	            if (board[0][i] != humanMarker && board[0][i] != botSymbol) {
	                board[0][i] = botSymbol;
	                mademove = true;
	                return;
	            }
	        }
	    }



	    // check if you can take a win horizontally
	    for (int i = 0; i < 3; i++) {
	        if (board[i][0].equals(board[i][1]) && board[i][0].equals(botSymbol)) {
	            if (board[i][2] != humanMarker && board[i][2] != botSymbol) {
	                board[i][2] = botSymbol;
	                mademove = true;
	                return;
	            }
	        }
	    }

	    for (int i = 0; i < 3; i++) {
	        if (board[i][2].equals(board[i][1]) && board[i][2].equals(botSymbol)) {
	            if (board[i][0] != humanMarker && board[i][0] != botSymbol) {
	                board[i][0] = botSymbol;
	                mademove = true;
	                return;
	            }
	        }
	    }


	    // check if you can take a win diagonally bottom


	    if (board[0][0].equals(board[1][1]) && board[0][0].equals(botSymbol)) {
	        if (board[2][2] != humanMarker && board[2][2] != botSymbol) {
	            board[2][2] = botSymbol;
	            mademove = true;
	            return;
	        }
	    }

	    if (board[2][2].equals(board[1][1]) && board[2][2].equals(botSymbol)) {
	        if (board[0][0] != humanMarker && board[0][0] != botSymbol) {
	            board[0][0] = botSymbol;
	            mademove = true;
	            return;
	        }
	    }

	    if (board[0][0].equals(board[1][1]) && board[0][0].equals(botSymbol)) {
	        if (board[2][2] != humanMarker && board[2][2] != botSymbol) {
	            board[2][2] = botSymbol;
	            mademove = true;
	            return;
	        }
	    }

	    if (board[0][2].equals(board[1][1]) && board[0][2].equals(botSymbol)) {
	        if (board[2][0] != humanMarker && board[2][0] != botSymbol) {
	            board[2][0] = botSymbol;
	            mademove = true;
	            return;
	        }
	    }

	    if (board[2][0].equals(board[1][1]) && board[2][0].equals(botSymbol)) {
	        if (board[0][2] != humanMarker && board[0][2] != botSymbol) {
	            board[0][2] = botSymbol;
	            mademove = true;
	            return;
	        }
	    }


	    // BLOCKS!!!! //

	    // check if you can block a win horizontally
	    for (int i = 0; i < 3; i++) {
	        if (board[0][i].equals(board[1][i]) && board[0][i].equals(humanMarker)) {
	            if (board[2][i] != botSymbol && board[2][i] != humanMarker) {
	                board[2][i] = botSymbol;
	                mademove = true;
	                return;
	            }
	        }
	    }

	    for (int i = 0; i < 3; i++) {
	        if (board[2][i].equals(board[1][i]) && board[0][i].equals(humanMarker)) {
	            if (board[0][i] != botSymbol && board[0][i] != humanMarker) {
	                board[0][i] = botSymbol;
	                mademove = true;
	                return;
	            }
	        }
	    }

	    // check if you can block a win vertically
	    for (int i = 0; i < 3; i++) {
	        if (board[i][0].equals(board[i][1]) && board[i][0].equals(humanMarker)) {
	            if (board[i][2] != botSymbol && board[i][2] != humanMarker) {
	                board[i][2] = botSymbol;
	                mademove = true;
	                return;
	            }
	        }
	    }

	    for (int i = 0; i < 3; i++) {
	        if (board[i][2].equals(board[i][1]) && board[i][2].equals(humanMarker)) {
	            if (board[i][0] != botSymbol && board[i][0] != humanMarker) {
	                board[i][0] = botSymbol;
	                mademove = true;
	                return;
	            }
	        }
	    }

	    for (int i = 0; i < 3; i++) {
	        if (board[2][i].equals(board[1][i]) && board[2][i].equals(humanMarker)) {
	            if (board[0][i] != botSymbol && board[0][i] != humanMarker) {
	                board[0][i] = botSymbol;
	                mademove = true;
	                return;
	            }
	        }
	    }
	    
	    // check if you can block a win diagonally 
	    if (board[0][0].equals(board[1][1]) && board[0][0].equals(humanMarker)) {
	        if (board[2][2] != botSymbol && board[2][2] != humanMarker) {
	            board[2][2] = botSymbol;
	            mademove = true;
	            return;
	        }
	    }

	    if (board[2][2].equals(board[1][1]) && board[2][2].equals(humanMarker)) {
	        if (board[0][0] != botSymbol && board[0][0] != humanMarker) {
	            board[0][0] = botSymbol;
	            mademove = true;
	            return;
	        }
	    }

	    if (board[0][0].equals(board[1][1]) && board[0][0].equals(humanMarker)) {
	        if (board[2][2] != botSymbol && board[2][2] != humanMarker) {
	            board[2][2] = botSymbol;
	            mademove = true;
	            return;
	        }
	    }

	    if (board[0][2].equals(board[1][1]) && board[0][2].equals(humanMarker)) {
	        if (board[2][0] != botSymbol && board[2][0] != humanMarker) {
	            board[2][0] = botSymbol;
	            mademove = true;
	            return;
	        }
	    }

	    if (board[2][0].equals(board[1][1]) && board[2][0].equals(humanMarker)) {
	        if (board[0][2] != botSymbol && board[0][2] != humanMarker) {
	            board[0][2] = botSymbol;
	            mademove = true;
	            return;
	        }
	    }
	    
	    // make random move if above rules dont apply
	    int rand1 = 0;
	    int rand2 = 0;

	    while (!mademove) {
	        rand1 = (int) (Math.random() * 3);
	        rand2 = (int) (Math.random() * 3);

	        if (board[rand1][rand2] != FieldType.X && board[rand1][rand2] != FieldType.Y) {
	            board[rand1][rand2] = botSymbol;
	            mademove = true;        
	        }
	    }
	    
	    // moves have been made, update field
	    game.setfield(board);
	}
}