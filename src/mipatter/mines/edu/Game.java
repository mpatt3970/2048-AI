package mipatter.mines.edu;



public class Game {

	private Board board;
	
	public Game() {
		board = new Board();
		new ControlView(board);
	}
	
	public static void main(String[] args) {
		new Game();
	}

}
