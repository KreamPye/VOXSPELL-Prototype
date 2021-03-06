package VOXSPELL;
/**
 * This class is the JFrame GUI that utilises a CardLayout. Different GUIs (JPanels) are added
 * to the cardlist. Depending on which button is pressed, it will show that associated card.
 * A mediator is associated with this class. It provides a means to allow objects to communicate
 * without coupling them tightly.
 * 
 * @author jacky
 * 
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;


import VOXSPELL.spelling.spellingGUI;
import VOXSPELL.stats.CurrentStatsGUI;
import VOXSPELL.stats.currentStats;
import VOXSPELL.stats.viewStatsGUI;
import VOXSPELL.swingworker.FFMPEGWorker;


public class GUICardLayout{
	JPanel cards;
	private spellingGUI spelling;
	private mainMenuGUI main;
	private viewStatsGUI viewStats;
	private CurrentStatsGUI sidePanel;
	private GUIMediator GUIManager = new GUIMediator();

	public void addComponentToPane(Container pane, JFrame frame){
		GUIManager = new GUIMediator(); //instantiating the mediator

		// creating the first card - which is displayed first, it is the mainMenu gui
		JPanel card1 = new JPanel();
		main = new mainMenuGUI(GUIManager);
		JPanel mainMenu = main.creatingGUI();
		card1.add(mainMenu);

		// instantiating the spellingGUI
		JPanel card2 = new JPanel();
		spelling = new spellingGUI(GUIManager);
		JPanel newGame = spelling.creatingGUI();
		card2.add(newGame);

		// instantiating the viewingStatsGUI
		JPanel card3 = new JPanel();
		viewStats = new viewStatsGUI(GUIManager);
		JPanel view = viewStats.creatingGUI();
		card3.add(view);

		cards = new JPanel(new CardLayout());
		cards.add(card1, "MAIN"); // the string that is associated with each card
		cards.add(card2, "GUI");
		cards.add(card3, "VIEW");

		GUIManager.addGUI(main); // adding the JPanel GUIs to the mediator
		GUIManager.addGUI(spelling);
		GUIManager.addGUI(viewStats);
		GUIManager.addMainGUI(this);
		
		sidePanel = new CurrentStatsGUI(GUIManager);
		JPanel separatorPanel = sidePanel.creatingGUI();
		
		frame.add(separatorPanel, BorderLayout.EAST); // this adds the sidePanel and is displayed at all times
		currentStats sideStats = new currentStats();
		
		GUIManager.addGUI(sidePanel);
		sidePanel.setModel(sideStats);
		sideStats.addGUI(sidePanel);
		
		pane.add(cards, BorderLayout.CENTER);

	}

	protected void changeLayout(String e){
		// switch the card depending on what the string is
		CardLayout cl = (CardLayout)(cards.getLayout());
		cl.show(cards, e);
	}


	private static void createAndShowGUI() {
		//Create and set up the window.
		 try {
             UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		 } catch (Exception e){
		 }
		JFrame frame = new JFrame("Spelling Aid");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		GUICardLayout GUILayout = new GUICardLayout();
		GUILayout.addComponentToPane(frame.getContentPane(), frame);
		

		//Display the window.
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}
	
	private static void setupSpookyVideo() {
		
		if (!new File(".media/SPOOKY.avi").exists()) { // Only create the video if it doesn't exist already. - Victor
			new FFMPEGWorker(".media/big_buck_bunny_1_minute.avi").execute();
		}
	}

	public static void main(String[] args) {
		try {
			new File(".mastered.txt").createNewFile(); // If these files already exist, they will not be created/override existing files.
			new File(".faulted.txt").createNewFile();
			new File(".failed.txt").createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		setupSpookyVideo();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}