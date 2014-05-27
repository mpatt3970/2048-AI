package mipatter.mines.edu;

public class AI {

	public int chooseBestMove(Board board, int depth) {
		// so this function recurses down, generating an expectiminmax tree
		// if depth is 0 or the board is full, return the heuristic value of that node
		if (depth == 0) {
			// end the recursion and return the heuristic
		} else if (depth%2 == 0) {
			// this means its the player's turn to make a move
		} else {
			// it's the game's turn to add a tile
		}
		int maxMove = 0;
		int maxScore = 0;
		// begin checking every possible move
		for (int i = 0; i < 4; i++) {
			Board tempBoard = new Board(board);
			tempBoard.makeMove(i);
			int tempScore = max(tempBoard, depth);
			if (tempScore > maxScore) {
				maxScore = tempScore;
				maxMove = i;
			}
		}
		return maxMove;
	}

	public int max(Board b, int depth) {
		if (depth > 0) {
			// for every empty cell, try it
			Board tempBoard = new Board(b);
			return rand(b, depth);
		} else {
			return calcHeuristic(b);
		}
	}

	public int rand(Board b, int depth) {
		return 0;
	}

	public int calcHeuristic(Board board) {
		int availableCells = 0;
		for (int i = 0; i < board.getBoardArray().length; ++i) {
			if (board.getBoardArray()[i].getValue() == 0) {
				availableCells++;
			}
		}
		return availableCells;
	}

}
