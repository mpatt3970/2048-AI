package mipatter.mines.edu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;


public class Board extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * represent as a n^2 element array that holds tiles
	 * order is like reading a book, scan horizontally left to right first, then top to bottom
	 */
	// define my colors
	private static Color BACKGROUND = new Color(0xBAAD9F);
	private static Color EMPTY = new Color(0xCBC0B2);
	private static Color C2 = new Color(0xEDE4D9);
	private static Color C4 = new Color(0xECE0C6);
	private static Color C8 = new Color(0xEEB275);
	private static Color C16 = new Color(0xF59565);
	private static Color C32 = new Color(0xF07E5D);
	private static Color C64 = new Color(0xEF6139);
	private static Color C128 = new Color(0xEDCE71);
	private static Color C256 = new Color(0xEDCC63);
	private static Color C512 = new Color(0xEBC840);
	private static Color C1024 = new Color(0xEAC526);
	private static Color C2048 = new Color(0xEEC22E);
	private static Color C4096 = new Color(0x3C3A31);
	private static Color C8192 = new Color(0x3C3A31);
	private static Color C16384 = new Color(0x3C3A31);


	public static int RIGHT = 0;
	public static int LEFT = 1;
	public static int DOWN = 2;
	public static int UP = 3;

	private static int TILE_SIZE = 100;
	private static int ARC_SIZE = 25;
	private static int BUFFER_SIZE = 10;
	private static int SIZE = 4;
	public static int  calcMax() {
		return TILE_SIZE*SIZE + BUFFER_SIZE*(1+SIZE);
	}
	private int[] boardArray;

	public Board() {
		boardArray = new int[SIZE*SIZE];
		fillEmpty();
		initRand();
		this.setSize(calcMax(), calcMax());
	}
	
	public Board(Board b) {
		boardArray = new int[b.getBoardArray().length];
		for (int i = 0; i < b.getBoardArray().length; ++i) {
			boardArray[i] = b.getBoardArray()[i];
		}
	}

	public void fillEmpty() {
		for (int i = 0; i < boardArray.length; i++) {
			boardArray[i] = 0;
		}
	}

	public void initRand() {
		// place two random tiles down to begin the game
		for (int i = 0; i < 2; i++) {
			addTile();
		}
	}

	public void addTile() {
		// make sure this spot is empty before filling
		Double place = (Math.random()*boardArray.length);
		int placement = place.intValue();
		// ensure this tile is empty
		while (boardArray[placement] != 0) {
			place = (Math.random()*boardArray.length);
			placement = place.intValue();
		}
		if (twoNext()) {
			boardArray[placement] = 2;
		} else {
			boardArray[placement] = 4;
		}
	}

	public boolean makeMove(int direction) {
		// save the state to see if the move has an effect
		int[] originalArray = new int[16];
		for (int i = 0; i < 16; i++) {
			originalArray[i] = boardArray[i];
		}
		// get each row individually since they behave independently of each other
		for (int i = 0; i < 4; i++) {
			// construct the currentRow
			int[] currentRow = buildRow(direction, i);
			// move the row towards the 0 index
			currentRow = merge(currentRow);
			currentRow = shift(currentRow);
			// update the row in the original boardArray variable
			updateBoardArray(currentRow, direction, i);
		}
		// check if a move actually happened and only add a tile if it did
		if ( ! sameBoard(originalArray, boardArray)) {
			return true;
		} else {
			return false;
		}
	}

	public int[] buildRow(int direction, int index) {
		int[] result = new int[4];
		// return an array of 4 Tiles
		// represents the row/column with the tile being moved towards 0-indexed
		switch(direction) {
		case 0:
			//right
			result[0] = boardArray[3 + (index*4)];
			result[1] = boardArray[2 + (index*4)];
			result [2] = boardArray[1 + (index*4)];
			result[3] = boardArray[index*4];
			break;
		case 1:
			//left
			result[0] = boardArray[(index*4)];
			result[1] = boardArray[1 + (index*4)];
			result [2] = boardArray[2 + (index*4)];
			result[3] = boardArray[3 + (index*4)];
			break;
		case 2:
			//down
			result[0] = boardArray[index + 12];
			result[1] = boardArray[index + 8];
			result [2] = boardArray[index + 4];
			result[3] = boardArray[index];
			break;
		case 3:
			//up
			result[0] = boardArray[index];
			result[1] = boardArray[index + 4];
			result [2] = boardArray[index + 8];
			result[3] = boardArray[index + 12];
			break;
		}
		return result;
	}

	public int[] merge(int[] row) {
		int[] result = new int[4];
		// init this as an empty row
		for (int i = 0; i < 4; i++) {
			result[i] = row[i];
		}
		// perform merges first
		// then place all tiles on the farthest side(i.e. as close to zero as possible)
		// check values against immediately preceding values
		int lastValue = row[0];
		int lastIndex = 0;
		for (int i = 1; i < 4; ++i) {
			int currentValue = row[i];
			if (currentValue != 0) {
				if (lastValue == currentValue) {
					// there's a match and it's not empty
					// merge the two by creating a new tile of twice the value
					result[lastIndex] = lastValue*2;
					result[i] = 0;
					// set currentValue to 0 to prevent a third match
					lastValue = 0;
				} else {
					// no match
					// update result's value to reflect the values and keep checking
					result[i] = currentValue;
					lastValue = currentValue;
					lastIndex = i;
				}
			}
		}
		return result;
	}

	public int[] shift(int[] row) {
		// moves the row towards the 0-index tile
		int[] result = new int[4];

		// now shift the result towards 0 until all empty tiles are removed
		// and fill the backend with 0's
		int amtToShift = 0;
		for (int i = 0; i < 4; i++) {
			if (row[i] == 0) {
				// this one's empty, increase how far tiles are moving
				result[i] = 0; // can be overwritten later
				amtToShift++;
			} else {
				// move this amtToShift towards 0
				int currentValue = row[i];
				result[i] = 0;
				result[i - amtToShift] = currentValue;
			}
		}
		// returns the result
		return result;
	}

	public void updateBoardArray(int[] newRow, int direction, int index) {
		// opposite of buildRow functionality for boardArray
		// instead of getting the values at these specific indices
		// replace the contents with the new row values stored at these same indices
		// 0-index is the beginning of each row
		switch(direction) {
		case 0:
			//right
			boardArray[3 + (index*4)] = newRow[0];
			boardArray[2 + (index*4)] = newRow[1];
			boardArray[1 + (index*4)] = newRow[2];
			boardArray[index*4] = newRow[3];
			break;
		case 1:
			//left
			boardArray[(index*4)]  = newRow[0];
			boardArray[1 + (index*4)] = newRow[1];
			boardArray[2 + (index*4)] = newRow[2];
			boardArray[3 + (index*4)] = newRow[3];
			break;
		case 2:
			//down
			boardArray[index + 12] = newRow[0];
			boardArray[index + 8] = newRow[1];
			boardArray[index + 4] = newRow[2];
			boardArray[index] = newRow[3];
			break;
		case 3:
			//up
			boardArray[index] = newRow[0];
			boardArray[index + 4] = newRow[1];
			boardArray[index + 8] = newRow[2];
			boardArray[index + 12] = newRow[3];
			break;
		}
	}
	private boolean twoNext() {
		// determine if the next tile is a 2 or a 4
		// Math.random() returns a value between 0 and 1
		// this gives a 90 percent chance of returning true
		return Math.random() < .9;
	}

	private boolean sameBoard(int[] a, int[] b) {
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; ++i) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(BACKGROUND);
		g.fillRect(0, 0, calcMax(), calcMax());
		for (int i = 0; i < SIZE; ++i) {
			for (int j = 0; j < SIZE; ++j) {
				// so draw rectangles to make a grid of rects with top-left corners at j*TILE_SIZE, i*TILE_SIZE
				int index = i*4 + j;
				int x_start = TILE_SIZE*j + BUFFER_SIZE*(j+1);
				int y_start = TILE_SIZE*i + BUFFER_SIZE*(i+1);
				int value = boardArray[index];
				boolean draw = true;
				switch(value) {
				case 0:
					// empty tile
					draw = false;
					g.setColor(EMPTY);
					break;
				case 2:
					g.setColor(C2);
					break;
				case 4:
					g.setColor(C4);
					break;
				case 8:
					g.setColor(C8);
					break;
				case 16:
					g.setColor(C16);
					break;
				case 32:
					g.setColor(C32);
					break;
				case 64:
					g.setColor(C64);
					break;
				case 128:
					g.setColor(C128);
					break;
				case 256:
					g.setColor(C256);
					break;
				case 512:
					g.setColor(C512);
					break;
				case 1024:
					g.setColor(C1024);
					break;
				case 2048:
					g.setColor(C2048);
					break;
				case 4096:
					g.setColor(C4096);
					break;
				case 8192:
					g.setColor(C8192);
					break;
				case 16384:
					g.setColor(C16384);
					break;
				default:
					break;
				}
				if (draw) {
					g.fillRoundRect(x_start, y_start, TILE_SIZE, TILE_SIZE, ARC_SIZE, ARC_SIZE);
					// and write text from the value
					g.setColor(Color.white);
					g.setFont(new Font("Dialog", Font.BOLD, 20));
					String valueStr = ((Integer) value).toString();
					g.drawString(valueStr, x_start+TILE_SIZE/4, y_start+TILE_SIZE/2);
				} else {
					// empty tile
					g.fillRoundRect(x_start, y_start, TILE_SIZE, TILE_SIZE, ARC_SIZE, ARC_SIZE);
				}
			}
		}
	}
	
	public void insertTile(int index, int value) {
		boardArray[index] = value;
	}

	public int[] getBoardArray() {
		return boardArray;
	}

}
