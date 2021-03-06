package mipatter.mines.edu;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;



public class ControlView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int SIZE_X = Board.calcMax();
	private static int SIZE_Y = Board.calcMax() + 70;
	private static String NEW_GAME_STR = "New Game?";
	private static String SINGLE_MOVE_STR = "Single AI Move";
	private static String PLAY_STR = "AI Play";
	private static String PAUSE_STR = "Pause";

	private Board board;
	private AI aiPlayer;
	private JPanel buttonsPanel;
	private JButton newGame;
	private JButton singleMove;
	private JToggleButton playPause;

	private volatile Thread aiRunning;



	public ControlView(Board b) {
		board = b;
		aiPlayer = new AI();
		buttonsPanel = new JPanel();
		newGame = new JButton();
		singleMove = new JButton();
		playPause = new JToggleButton();
		newGame.setText(NEW_GAME_STR);
		newGame.addActionListener(new NewListener());
		singleMove.setText(SINGLE_MOVE_STR);
		singleMove.addActionListener(new AIListener());
		playPause.setText(PLAY_STR);
		playPause.addActionListener(new PlayListener());
		buttonsPanel.add(newGame);
		buttonsPanel.add(singleMove);
		buttonsPanel.add(playPause);
		board.addKeyListener(new MyKeyListener());
		board.setFocusable(true);
		buttonsPanel.addKeyListener(new MyKeyListener());
		buttonsPanel.setFocusable(true);
		this.addKeyListener(new MyKeyListener());
		this.setLayout(new BorderLayout());
		this.add(b, BorderLayout.CENTER);
		this.add(buttonsPanel, BorderLayout.PAGE_END);
		this.setSize(SIZE_X, SIZE_Y);
		this.setVisible(true);

		// close when red button is clicked
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				System.exit(0);
			}
		});
	}

	private class NewListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			board.fillEmpty();
			board.initRand();
			board.repaint();
			requestFocus();
		}

	}

	private class AIListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// make a move if it's possible that is based on the AI class generating an expectiminmax tree
			// multiply depth by 2 so that the passed in depth always satisfies (depth%2 == 0) which means player is next
			int moveChoice = aiPlayer.chooseBestMove(board);
			System.out.println("Decided to move " + moveChoice);
			if (board.makeMove(moveChoice)) {
				board.addTile();
				board.repaint();
			}
		}

	}

	private class PlayListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (playPause.isSelected()) {
				// begin playing the ai version
				aiRunning = new PlayAiThread();
				aiRunning.start();
				playPause.setText(PAUSE_STR);
			} else {
				aiRunning = null;
				playPause.setText(PLAY_STR);

			}
			requestFocus();
		}

	}

	private class MyKeyListener implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			boolean add = false;
			switch(e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				add = board.makeMove(Board.RIGHT);
				break;
			case KeyEvent.VK_LEFT:
				add = board.makeMove(Board.LEFT);
				break;
			case KeyEvent.VK_DOWN:
				add = board.makeMove(Board.DOWN);
				break;
			case KeyEvent.VK_UP:
				add = board.makeMove(Board.UP);
				break;
			}
			if (add) {
				board.addTile();
			}
			board.repaint();
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}



	}

	private class PlayAiThread extends Thread {
		public void run() {
			Thread thisThread = Thread.currentThread();
			while(aiRunning == thisThread && !aiPlayer.terminalCondition(board)) {
				int moveChoice = aiPlayer.chooseBestMove(board);
				System.out.println("Decided to move " + moveChoice);
				if (board.makeMove(moveChoice)) {
					board.addTile();
					board.repaint();
				}
			}
		}
	}



}
