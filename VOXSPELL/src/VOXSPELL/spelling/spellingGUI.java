package VOXSPELL.spelling;

/**
 * this class is the JPanel GUI for the spelling, particularly review and new game.
 * it is user driven and all processing is associated with its newGame model.
 * 
 * @author jacky
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane; 
import javax.swing.JTextArea; 
import javax.swing.JTextField;

import VOXSPELL.Command;
import VOXSPELL.GUI;
import VOXSPELL.GUIMediator;
import VOXSPELL.MediaPlayer;

public class spellingGUI extends GUI implements ActionListener{
	private JTextField txt = new JTextField("");
	private JTextArea txtOutput = new JTextArea(10, 20);
	private newGame modelController;
	private int count = 0;
	private int iterations = 0;
	protected JButton btnEnter = new JButton("Enter"); 
	private JButton btnBack = new JButton("Back");
	protected JButton btnRelisten = new JButton("Relisten");
	private JComboBox<String> festivalSelect;
	JButton btnStart = new JButton("Start");
	private JLabel spellingVoiceLabel = new JLabel("Speaking Voices");
	private JProgressBar progressBar = new JProgressBar();
	private JButton btnVideo = new JButton("Play Video Reward"); 
	private JButton btnAdvanceLevel = new JButton("Level Up");
	private JLabel correctLabel = new JLabel("Words Mastered");
	private JLabel currentLevel = new JLabel("Current Level:");


	public spellingGUI(GUIMediator m) {
		// associating this GUI with a mediator to notify changes.
		super(m);
	}

	public JPanel creatingGUI() {

		JPanel spellingPanel = new JPanel();
		spellingPanel.setLayout(new BorderLayout());
		
		// related to setting the state of the jcomponents
		btnAdvanceLevel.setEnabled(false);
		txtOutput.setEditable(false);
		progressBar.setStringPainted(true);
		btnVideo.setEnabled(false);
		txt.setEditable(false);
		btnEnter.setEnabled(false);
		btnRelisten.setEnabled(false);
		progressBar.setStringPainted(true);
		progressBar.setString("");
		
		// adding associated actionlisteners to these components
		txt.addActionListener(this);
		btnEnter.addActionListener(this); 
		btnBack.addActionListener(this);
		btnRelisten.addActionListener(this);
		btnStart.addActionListener(this);
		btnVideo.addActionListener(this);
		btnAdvanceLevel.addActionListener(this);
		
		txt.setPreferredSize(new Dimension(200, 40));
		JScrollPane scroll = new JScrollPane(txtOutput);
		scroll.setPreferredSize(new Dimension(340, 250));

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		textPanel.add(scroll);

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));

		// The block below gets the available Festival voices and stores it in a drop-down menu - Victor
		String bashCmd = "ls /usr/share/festival/voices/english";

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", bashCmd);
		Process process;
		String[] voicesArray = null;
		try {
			process = builder.start();

			List<String> voices = new ArrayList<String>();

			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String voice = stdoutBuffered.readLine();
			while ((voice  != null)) {

				voices.add(voice);
				voice = stdoutBuffered.readLine();
			}
			voicesArray = voices.toArray(new String[0]);

		} catch (IOException e) {
		}

		festivalSelect = new JComboBox<String>(voicesArray);
		festivalSelect.addActionListener(this);
		festivalSelect.setMaximumSize(new Dimension(200, btnRelisten.getMinimumSize().height));
		
		btnRelisten.setMaximumSize(new Dimension(200, btnRelisten.getMinimumSize().height));
		btnRelisten.setAlignmentX(Component.CENTER_ALIGNMENT);


		spellingVoiceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnPanel.add(spellingVoiceLabel);

		btnPanel.add(festivalSelect);
		// this adds essentially a 'transparent box' which enables a gap between jcomponents
		btnPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		btnPanel.add(btnRelisten);
		btnPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		btnPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		correctLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnPanel.add(correctLabel);

		btnPanel.add(progressBar);
		btnPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		btnVideo.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnPanel.add(btnVideo);
		btnPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		btnAdvanceLevel.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnAdvanceLevel.setMaximumSize(new Dimension(btnVideo.getMinimumSize().width, btnVideo.getMinimumSize().height));
		btnPanel.add(btnAdvanceLevel);

		JPanel btnTextPanel = new JPanel();
		btnTextPanel.setLayout(new BorderLayout());
		btnTextPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		btnTextPanel.add(txt, BorderLayout.CENTER);
		btnTextPanel.add(btnEnter, BorderLayout.EAST);

		JPanel topBtnPanel = new JPanel();
		topBtnPanel.setLayout(new BoxLayout(topBtnPanel, BoxLayout.X_AXIS));
		btnBack.setPreferredSize(new Dimension(20, 20));
		topBtnPanel.add(btnBack);
		topBtnPanel.add(btnStart);
		topBtnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		topBtnPanel.add(currentLevel);

		spellingPanel.add(textPanel, BorderLayout.LINE_START);
		spellingPanel.add(btnPanel, BorderLayout.EAST);
		spellingPanel.add(btnTextPanel, BorderLayout.SOUTH);
		spellingPanel.add(topBtnPanel, BorderLayout.NORTH);
		
		return spellingPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(btnBack)){
			// prompting user to confirm if they want to end this game.
			int PromptResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to leave this session?", "Confirmation", 
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if(PromptResult == 0){
				// this resets the view of the GUI to prevent error-prone states
				btnStart.setEnabled(true);
				btnRelisten.setEnabled(false);
				btnEnter.setEnabled(false);
				iterations  = 0;
				txtOutput.setText("");
				modelController.setVoice(this.festivalSelect.getItemAt(0));
				festivalSelect.setSelectedIndex(0);
				progressBar.setValue(0);
				progressBar.setString("");
				btnAdvanceLevel.setEnabled(false);
				btnVideo.setEnabled(false);
				mediator.sendUpdateToGUI("MAIN"); // sends them back to the main menu GUI
			}
		} else if(e.getSource().equals(btnEnter)) {
			if(iterations != 10 || modelController.getWordListSize() > iterations){
				String userInput = txt.getText(); // gets what the user entered into the JTextField
				txt.setText("");
				if(!modelController.isValid(userInput)){
					// sends a warning if any symbols are entered into the field
					JOptionPane.showMessageDialog(null, "Must enter a valid input (no non-apostrophe symbols or empty field)!", "Warning!", JOptionPane.WARNING_MESSAGE);
				} else if(modelController.getWordListSize() > 0){
					// this is the 'mastered' branch, it will notify the model to do appropriate processing

					txtOutput.append(userInput+"\n"); // Added to display user input

					if(count == 0 && modelController.isCorrect(userInput)){
						txtOutput.append("Correct!\n");
						iterations++;
						modelController.processCondition("mastered");
						if(!modelController._review){
							mediator.updateSideStats(this.modelController._level, true);
						}
					} else if(modelController.isCorrect(userInput)){
						// this is the faulted branch - specifically if count > 0, then it means they've had another try
						txtOutput.append("Correct!\n");
						iterations++;
						modelController.processCondition("faulted");
						if(!modelController._review){
							mediator.updateSideStats(this.modelController._level, false);
						}
					} else if(count == 0){
						// this is if they've failed the word the first try
						modelController.textToSpeech("festival -b '(voice_"+modelController.getVoice()+")' '(SayText \"Incorrect, try once more: "+modelController.getCurrentWord()+"\")'", "",false);
						modelController.textToSpeech("festival -b '(voice_"+modelController.getVoice()+")' '(SayText \""+modelController.getCurrentWord()+"\")'", "",true);
						txtOutput.append("Incorrect, try once more: ");
						count++;
					}
					else {
						// this is if they've failed the word two times in a row
						txtOutput.append("Incorrect!\n");
						iterations++;
						modelController.processCondition("failed");
						if(!modelController._review){
							mediator.updateSideStats(this.modelController._level, false);
						}
					}
					// reset the iterations, and text field, and send them back to the MAIN gui.
					if(iterations == 10 || modelController.getWordListSize() == iterations){
						String levelCompleteMsg = "Level complete.";
						btnRelisten.setEnabled(false);
						iterations = 0;
						
						if (!modelController._review) {
							// This is when user gets at least 9 words correct, it will display appropriate messages and button enables - Victor
							if(modelController._wordsCorrect > 8){
								String levelMasteredMsg = levelCompleteMsg + " Well done for mastering 9 or more words!";
								btnVideo.setEnabled(true);
								if (!modelController._level.equals("%Level 11")) { // If level is less than 11 Advance Level option is enabled, with appropriate message
									btnAdvanceLevel.setEnabled(true);
									JOptionPane.showMessageDialog(null, levelMasteredMsg + " You may now choose to watch a reward video or move onto the next level.");
								} else { // If at Level 11 user has reached highest level and can't progress to next level
									JOptionPane.showMessageDialog(null, levelMasteredMsg + " You may now choose to watch a reward video. No more levels available to progress.");
								}
							} else if (modelController._wordsCorrect == 0) { // When user gets 0 words correct, the spooky video is unlocked! - Victor
								int promptResult = JOptionPane.showConfirmDialog(null, "You've unlocked a secret, \"spooky\" version of the reward video! Would you like to watch?", "Hidden Reward!",
										JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
								if (promptResult == 0) {
									new MediaPlayer(".media/SPOOKY.avi",true).setupGUI();
								}
							} else { // User is informed they need at least 9 words correct to progress to next level and view video - Victor
								JOptionPane.showMessageDialog(null, levelCompleteMsg + " Unfortunately you need to master at least 9 or more words to watch the reward video and progressing to the next level. Better luck next time!");
							}
						}
						modelController.execute();
					}
				}
			}
		} else if (e.getSource() == btnRelisten){
			modelController.spell();
		} else if (e.getSource() == btnStart){
			// this enable and disables certain buttons to avoid error states - i.e, pressing video when you haven't finished a level
			txtOutput.setText("");
			modelController.proceedToNextWord("");
			txt.setEditable(true);
			btnEnter.setEnabled(false);
			btnStart.setEnabled(false);
			btnVideo.setEnabled(false);
			btnAdvanceLevel.setEnabled(false);
		} else if (e.getSource() == festivalSelect){
			// this enables the user to select a voice available from local festival packages
			modelController.setVoice((String)festivalSelect.getSelectedItem());
		} else if (e.getSource() == btnAdvanceLevel) {
			// increment the spelling level by one and reset states
			modelController.setLevel("%Level "+(Integer.parseInt(modelController._level.split(" ")[1])+1));
			modelController.execute();
			btnAdvanceLevel.setEnabled(false);
			btnStart.doClick();
		} else if (e.getSource() == btnVideo) { // opens a reward video for user to watch - victor
			new MediaPlayer(".media/big_buck_bunny_1_minute.avi",false).setupGUI();
		} else if (e.getSource() == txt) { // enter button now submits attempt - victor
			btnEnter.doClick();
		}
	}

	public void resetSpelling(){
		count = 0;
	}

	protected boolean promptUserToRelisten(){
		// this will prompt the user if they want to relisten to the word.
		int PromptResult = JOptionPane.showConfirmDialog(null, "Would you like to listen to the spelling of the word?", "Listen to Spelling",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if(PromptResult == 0){
			return true;
		} else {
			return false;
		}
	}

	protected void setJProgress(int min, int max, int current){
		// this sets the jprogressbar and updates the view
		if (progressBar.getMinimum() == 0 && progressBar.getMaximum() == 0){
			// this condition applies as the jprogressbar has not its boundaries set yet
			this.progressBar.setMaximum(max);
			this.progressBar.setMinimum(min);
		} else {
			// updates the jprogressbar
			this.progressBar.setValue(current*100/max);
			this.progressBar.setString(""+current+"/"+max);
		}

	}

	protected String getVoiceField(){
		// returns the selected voice
		return this.festivalSelect.getItemAt(0);
	}
	
	protected void setTxtField(String txtToSet){
		txtOutput.setText(txtToSet);
	}

	public void appendTxtField(String txtToAppend){
		txtOutput.append(txtToAppend);
	}

	public void setModel(Command cmd) {
		modelController = (newGame) cmd;		
	}
	
	public void setLevelLabel(String level){
		this.currentLevel.setText("Current Level: "+level);
	}
}
