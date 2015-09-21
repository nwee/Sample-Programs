package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import logic.Board;
import logic.Player;

@SuppressWarnings("serial")
public class PlayerGUI extends JPanel {
	JPanel titlePanel;		//Stores title panels
	public JPanel stopWatchPanel;	//Stores timer panel
	JPanel rightTitlePanel;	//Stores "Next" title
	JPanel midPanel;		//Stores store, board, next panels
	public JPanel storePanel;
	JPanel boardPanel;
	public JPanel nextPanel;

	JLabel name;

	Player player;
	Board board;

	public PlayerGUI(Player player, String playerName) {
		this.player = player;
		this.board = player.board;

		titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());

		//For "Timer" 
		stopWatchPanel = new StopWatchGUI(player.game.stopWatch);

		//For "Next" 
		rightTitlePanel = new JPanel();
		rightTitlePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		rightTitlePanel.add(new JLabel("Next"));

		name = new JLabel(playerName);
		name.setHorizontalAlignment(SwingConstants.CENTER);

		titlePanel.add("West",stopWatchPanel);
		titlePanel.add("Center",name);
		titlePanel.add("East",rightTitlePanel);

		// Storage, Game, Next Panels
		midPanel = new JPanel();
		midPanel.setLayout(new BorderLayout());

		storePanel = new InfoGUI(this);

		boardPanel = board.boardGUI;
		nextPanel = new NextGUI(this);

		midPanel.add("West", storePanel);
		midPanel.add("Center", boardPanel);
		midPanel.add("East", nextPanel);

		BorderLayout border = new BorderLayout();
		setLayout(border);
		add(titlePanel, BorderLayout.NORTH);
		add(midPanel, BorderLayout.CENTER);	
	}
}
