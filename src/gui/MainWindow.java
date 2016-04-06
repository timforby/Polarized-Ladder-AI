package gui;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import game.Main;

public class MainWindow {

	private int player;
	private boolean ai;
	private JRadioButton[] positions;
	private JFrame frame;
	JTextArea playerText;
	JRadioButton aiStart;
	private static Main game;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		player =0;
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton players1vAI = new JButton("1 Player");
		players1vAI.setBounds(100,100,100,50);
		players1vAI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				MainWindow.this.ai = true;
				setup();
			}
			
		});
		
		JButton players2 = new JButton("2 Players");
		players2.setBounds(210,100,100,50);
		players2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				MainWindow.this.ai = false;
				setup();
			}
			
		});
		frame.add(players1vAI);
		frame.add(players2);
	}
	
	private void winner(int pl){
		frame.setEnabled(false);
		String dip;
		if(pl == -1){
			dip = "DRAW";
		}else{
			dip = "WIN: PLAYER "+Integer.toString(pl);
		}
		playerText.setText(dip);
	}
	
	private void setup(){
		//instantiating the Main part of the game and setting if ai
		game = new Main(ai);
		
		//removing all previous buttons
		frame.getContentPane().removeAll();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
		//setting the text that states the player's turn and if player won
		playerText = new JTextArea("Turn: Player "+Integer.toString(player+1));
		playerText.setBounds(230, 30, 100, 20);
		playerText.setEditable(false);
		playerText.setBackground(frame.getBackground());
		frame.getContentPane().add(playerText);
		
		//if AI selected add option for AI to start
		if(ai){
			aiStart = new JRadioButton("AI Start?");
			aiStart.setBounds(300, 200, 120, 18);
			frame.getContentPane().add(aiStart);
			aiStart.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent event) {
					int state = event.getStateChange();
					if(state==ItemEvent.SELECTED){		
						Color color = (player==0)?Color.RED:Color.YELLOW;	
						((JRadioButton) event.getItem()).setEnabled(false);
						//play first move for ai
						int ai_move = MainWindow.game.update(MainWindow.this.player, -1);
						if(MainWindow.this.ai){
							color = (player==0)?Color.RED:Color.YELLOW;
							positions[ai_move].setBackground(color);
							positions[ai_move].setEnabled(false);
							MainWindow.this.player = (player+1)%2;
							MainWindow.this.playerText.setText("Turn: Player "+Integer.toString(MainWindow.this.player+1));
						}
					}
				}
			});
		}
		
		//placing the triangle that holds the discs
		positions = new JRadioButton[49];
		int xMax =13;
		int yHeight =0;	
		int xoffset = 0;
		for(int i = 0; i < positions.length; i++){		
			if(i == xMax){
				yHeight++;
				xoffset = (i-xMax)+yHeight;
				xMax += (13-(2*yHeight));		
			}
			
			positions[i] = new JRadioButton(Integer.toString(i));
			//position based on i that changes the xoffset and the yoffset 
			positions[i].setBounds(10+(20*xoffset),200-(20*yHeight),18,18);
			frame.getContentPane().add(positions[i]);
			//item listener that changes color and disables
			//and UPDATES in main
			positions[i].addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent event) {
					int state = event.getStateChange();
					if(state==ItemEvent.SELECTED){
						
						if(ai){
							frame.getContentPane().remove(aiStart);
							frame.getContentPane().revalidate();
							frame.getContentPane().repaint();
						}
						Color color = (player==0)?Color.RED:Color.YELLOW;						
						((JRadioButton) event.getItem()).setBackground(color);
						((JRadioButton) event.getItem()).setEnabled(false);
						String id =((JRadioButton) event.getItem()).getText();
						//ai_move = 0 if no ai or ai move is 0, -1 if player won
						int ai_move = MainWindow.game.update(MainWindow.this.player, Integer.parseInt(id));
						//set to no winner
						int win = -1;
						//if player not won
						if(ai_move >-1){
							MainWindow.this.player = (player+1)%2;
							//if ai plays then place move (ai_move)
							if(MainWindow.this.ai){
								color = (player==0)?Color.RED:Color.YELLOW;
								positions[ai_move].setBackground(color);
								positions[ai_move].setEnabled(false);
								MainWindow.this.player = (player+1)%2;
							}
							//see if ai_move won
							win = MainWindow.game.winner();	
						}else{
							//player won
							win = ai_move!=-2?MainWindow.this.player:-2;
						}
						MainWindow.this.playerText.setText("Turn: Player "+Integer.toString(MainWindow.this.player+1));
						if(win !=-1){
							winner(win+1);
							//endgame function
						}
						
					}
				}			
			});
			xoffset++;
		}
	}
	
	
}
