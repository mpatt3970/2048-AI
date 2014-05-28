package mipatter.mines.edu;

public class AI {
	
	//private static final int SMOOTH_WEIGHT = 5000;
	private static final int EDGE_WEIGHT = 20000;
	private static final int OPEN_TILE_WEIGHT = 20000;
	private static final int LOSING_PENALTY = 50000;
	private static final int[] MIDDLE_FOUR = {5,6,9,10}; // positions of middle three tiles
	private static final int[] CORNER_VALUES = {0,3,12,15};

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
		System.out.println("Depth is " + depth);
		// so this function recurses down, generating an expectiminmax tree
		// if depth is 0 or the board is full, return the heuristic value of that node
		if (depth == 0) {
			// end the recursion and return the heuristic
			return calcHeuristic(board);
		} else if (depth%2 == 0) {
			// this means its the player's turn to make a move
			// check for a terminal state first
			if (terminalCondition(board)) {
				System.out.println("Terminal Condition reached");
				return calcHeuristic(board) - LOSING_PENALTY;
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
		int maxValue = -1;
		int maxPosition = -1;
		for (int i = 0; i < board.getBoardArray().length; ++i) {
			int tileValue = board.getBoardArray()[i].getValue();
			if (tileValue == 0) {
				availableCells++;
			} else {
				if (tileValue > maxValue) {
					maxValue = tileValue;
					maxPosition = i;
				}
				score += tileValue;
			}
		}
		// add value to score if maxPosition isn't in middle 4 tiles
		boolean stuckInMiddle = false;
		for (int badPosition : MIDDLE_FOUR) {
			// sorted list so i can break early if maxPosition is < any of these
			if (maxPosition == badPosition) {
				stuckInMiddle = true;
				break;
			} else if (maxPosition < badPosition) {
				break;
			}
		}
		if (!stuckInMiddle) {
			score += EDGE_WEIGHT;
		}
		// now check if we have maxPosition in a corner which is worth another EDGE_WEIGHT addition
		for (int cornerPosition : CORNER_VALUES) {
			if (maxPosition == cornerPosition) {
				score += EDGE_WEIGHT;
				break;
			}
		}
		return availableCells*OPEN_TILE_WEIGHT + score;
	}
	
	public boolean terminalCondition(Board board) {
		// try every move and return true if any makeMove function returns true
		for (int i = 0; i < 4; ++i) {
			Board tempBoard = new Board(board);
			if (tempBoard.makeMove(i)) {
				return false;
			}
		}
		return true;
	}
}
