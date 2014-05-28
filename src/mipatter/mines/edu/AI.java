package mipatter.mines.edu;

public class AI {

	public int chooseBestMove(Board board, int depth) {
		// constructs the first level of the expectiminmax tree before having generateTree do the rest
		// necessary to get the preferred move out
		int maxScore = -1;
		int maxMove = 0;
		for (int i = 0; i < 4; ++i) {
			Board tempBoard = new Board(board);
			if (tempBoard.makeMove(i)) {
				int generatedScore = generateTree(tempBoard, depth - 1);
				System.out.println("In chooseBestMove for move: " + i + ", score=" + generatedScore);
				if (generatedScore > maxScore) {
					maxMove = i;
					maxScore = generatedScore;
				}
			}
		}
		return maxMove;
	}
	
	private int generateTree(Board board, int depth) {
		// so this function recurses down, generating an expectiminmax tree
		// if depth is 0 or the board is full, return the heuristic value of that node
		if (depth == 0) {
			// end the recursion and return the heuristic
			return calcHeuristic(board);
		} else if (depth%2 == 0) {
			// this means its the player's turn to make a move
			// check for a terminal state first
			if (terminalCondition(board)) {
				return calcHeuristic(board);
			}
			// now generate a new node for each possible move and recurse down with that new board and (depth -1)
			int maxScore = -1; // start with a value lower than any other move would equal
			for (int i = 0; i < 4; i++) {
				Board tempBoard = new Board(board);
				if (tempBoard.makeMove(i)) {
					int generatedScore = generateTree(tempBoard, depth - 1);
					if (generatedScore > maxScore) {
						maxScore = generatedScore;
					}
				}
			}
			return maxScore;
		} else {
			// it's the game's turn to add a tile, this can never be a terminal state
			// because the player must have moved somewhere, which leaves an empty tile somewhere
			// equal chance of being placed in any open spot
			// 90% chance new tile value equals 2
			
			int result = 0;
			int count = 0;
			for (int i = 0; i < board.getBoardArray().length; ++i) {
				// only access empty tiles
				if (board.getBoardArray()[i].getValue() == 0) {
					count++;
					// add a 2 at this position
					Board tempBoard = new Board(board);
					// chance of a two being chosen
					tempBoard.insertTile(i, 2);
					result += 0.9*generateTree(tempBoard, depth - 1);
					// add a 4 at this position
					tempBoard = new Board(board);
					tempBoard.insertTile(i, 4);
					result += 0.1*generateTree(tempBoard, depth - 1);
				}
			}
			// get the average of the results
			return result/count;
		}
	}

	private int calcHeuristic(Board board) {
		int availableCells = 0;
		int score = 0;
		for (int i = 0; i < board.getBoardArray().length; ++i) {
			int tileValue = board.getBoardArray()[i].getValue();
			if (tileValue == 0) {
				availableCells++;
			} else {
				score += tileValue;
			}
		}
		return availableCells*2 + score;
	}
	
	private boolean terminalCondition(Board board) {
		// try every move and return true if any makeMove function returns true
		for (int i = 0; i < 4; ++i) {
			Board tempBoard = new Board(board);
			if (tempBoard.makeMove(i)) {
				return true;
			}
		}
		return false;
	}
}
