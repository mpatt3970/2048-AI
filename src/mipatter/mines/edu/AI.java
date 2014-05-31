package mipatter.mines.edu;

import java.util.HashMap;

public class AI {

	private static final int SMOOTH_WEIGHT = 1;
	private static final int EDGE_WEIGHT = 50;
	private static final int CORNER_WEIGHT = 10;
	private static final int SCORE_WEIGHT = 5;
	private static final int OPEN_TILE_WEIGHT = 1000;
	private static final int LOSING_PENALTY = 3000;
	private static final int[] MIDDLE_FOUR = {5,6,9,10}; // positions of middle three tiles
	private static final int[] CORNER_VALUES = {0,3,12,15};
	
	private HashMap<int[], Integer> transposition;

	public int chooseBestMove(Board board) {
		transposition = new HashMap<int[], Integer>();
		int openCount = 0;
		for (int i = 0; i < board.getBoardArray().length; ++i) {
			if (board.getBoardArray()[i] == 0) {
				openCount++;
			}
		}
		// constructs the first level of the expectiminmax tree before having generateTree do the rest
		// necessary to get the preferred move out
		int maxScore = -10000000;
		int maxMove = 0;
		for (int i = 0; i < 4; ++i) {
			Board tempBoard = new Board(board);
			if (tempBoard.makeMove(i)) {
				int depth = 8;
				if (openCount > 3) {
					depth = 6;
				} if (openCount > 6) {
					depth = 4;
				}
				int generatedScore = generateTree(tempBoard, depth);
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
		int[] boardArray = board.getBoardArray();
		// so this function recurses down, generating an expectiminmax tree
		// if depth is 0 or the board is full, return the heuristic value of that node
		if (depth == 0) {
			// end the recursion and return the heuristic
			return calcHeuristic(board);
		} else if (transposition.containsKey(boardArray)) {
			// check the transposition table to trim some redundant subtrees
			return transposition.get(boardArray);
		} else if (depth%2 == 1) {
			// this means its the player's turn to make a move
			// check for a terminal state first
			if (terminalCondition(board)) {
				System.out.println("Terminal Condition reached");
				return calcHeuristic(board) - LOSING_PENALTY;
			}
			// now generate a new node for each possible move and recurse down with that new board and (depth -1)
			int maxScore = -1000000; // start with a value lower than any other move would equal
			for (int i = 0; i < 4; i++) {
				Board tempBoard = new Board(board);
				if (tempBoard.makeMove(i)) {
					int generatedScore = generateTree(tempBoard, depth - 1);
					if (generatedScore > maxScore) {
						maxScore = generatedScore;
					}
				}
			}
			transposition.put(boardArray, maxScore);
			return maxScore;
		} else {
			// it's the game's turn to add a tile, this can never be a terminal state
			// because the player must have moved somewhere, which leaves an empty tile somewhere
			// equal chance of being placed in any open spot
			// 90% chance new tile value equals 2
			int openCount = 0;
			int score = 0;
			for (int i = 0; i < boardArray.length; ++i) {
				// only count empty tiles
				if (boardArray[i] == 0) {
					openCount++;
					Board tempBoard = new Board(board);
					tempBoard.insertTile(i, 2);
					score += generateTree(tempBoard, depth - 1);
				}
			}
			int result = score/openCount;
			transposition.put(boardArray, result);
			return result;
		}
	}

	private int calcHeuristic(Board board) {
		int[] boardArray = board.getBoardArray();
		int smoothnessCount = 0; // a count of the difference between every tile and its neighbors
		int availableCells = 0;
		int score = 0;
		int maxValue = -1;
		int secondMaxValue = -1;
		int maxPosition = -1;
		int secondMaxPosition = -1;
		for (int i = 0; i < boardArray.length; ++i) {
			int tileValue = boardArray[i];
			smoothnessCount += calcSmoothness(boardArray, i);
			if (tileValue == 0) {
				availableCells++;
			} else {
				if (tileValue > maxValue) {
					secondMaxValue = maxValue;
					maxValue = tileValue;
					secondMaxPosition = maxPosition;
					maxPosition = i;
				}
				score += tileValue;
			}
		}
		score = score*SCORE_WEIGHT;
		// add value to score if maxPosition isn't in middle 4 tiles
		for (int badPosition : MIDDLE_FOUR) {
			// sorted list so i can break early if maxPosition is < any of these
			if (maxPosition == badPosition) {
				score -= EDGE_WEIGHT;
			}
			if (secondMaxPosition == badPosition) {
				score -= 0.5*EDGE_WEIGHT;
			}
		}
		score = score * EDGE_WEIGHT;
		// now check if we have maxPosition in a corner which is worth another EDGE_WEIGHT addition
		for (int cornerPosition : CORNER_VALUES) {
			if (maxPosition == cornerPosition) {
				score += EDGE_WEIGHT;
				break;
			}
		}
		return availableCells*OPEN_TILE_WEIGHT + score - smoothnessCount*SMOOTH_WEIGHT;
	}
	
	private int calcSmoothness(int[] array, int index) {
		int currentValue = array[index];
		int result = 0;
		switch(index) {
		case 0:
			result += Math.abs(currentValue - array[1]);
			result += Math.abs(currentValue - array[4]);
		case 1:
			result += Math.abs(currentValue - array[0]);
			result += Math.abs(currentValue - array[5]);
			result += Math.abs(currentValue - array[2]);
		case 2:
			result += Math.abs(currentValue - array[1]);
			result += Math.abs(currentValue - array[6]);
			result += Math.abs(currentValue - array[3]);
		case 3:
			result += Math.abs(currentValue - array[2]);
			result += Math.abs(currentValue - array[7]);
		case 4:
			result += Math.abs(currentValue - array[0]);
			result += Math.abs(currentValue - array[5]);
			result += Math.abs(currentValue - array[8]);
		case 5:
			result += Math.abs(currentValue - array[1]);
			result += Math.abs(currentValue - array[4]);
			result += Math.abs(currentValue - array[6]);
			result += Math.abs(currentValue - array[9]);
		case 6:
			result += Math.abs(currentValue - array[2]);
			result += Math.abs(currentValue - array[5]);
			result += Math.abs(currentValue - array[7]);
			result += Math.abs(currentValue - array[10]);
		case 7:
			result += Math.abs(currentValue - array[3]);
			result += Math.abs(currentValue - array[6]);
			result += Math.abs(currentValue - array[11]);
		case 8:
			result += Math.abs(currentValue - array[4]);
			result += Math.abs(currentValue - array[9]);
			result += Math.abs(currentValue - array[12]);
		case 9:
			result += Math.abs(currentValue - array[5]);
			result += Math.abs(currentValue - array[8]);
			result += Math.abs(currentValue - array[10]);
			result += Math.abs(currentValue - array[13]);
		case 10:
			result += Math.abs(currentValue - array[6]);
			result += Math.abs(currentValue - array[9]);
			result += Math.abs(currentValue - array[11]);
			result += Math.abs(currentValue - array[14]);
		case 11:
			result += Math.abs(currentValue - array[7]);
			result += Math.abs(currentValue - array[10]);
			result += Math.abs(currentValue - array[15]);
		case 12:
			result += Math.abs(currentValue - array[8]);
			result += Math.abs(currentValue - array[13]);
		case 13:
			result += Math.abs(currentValue - array[12]);
			result += Math.abs(currentValue - array[9]);
			result += Math.abs(currentValue - array[14]);
		case 14:
			result += Math.abs(currentValue - array[13]);
			result += Math.abs(currentValue - array[10]);
			result += Math.abs(currentValue - array[15]);
		case 15:
			result += Math.abs(currentValue - array[14]);
			result += Math.abs(currentValue - array[11]);
		}
		return result;
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
