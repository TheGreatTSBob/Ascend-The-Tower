package GUI;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

import GameLogic.Ability;
import GameLogic.Enemy;
import GameLogic.HealthBar;
import GameLogic.Hero;
import GameLogic.Pawn;
import GameLogic.Skeleton;
import GameLogic.Ability.AbilityType;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Game_GUI {
	
	public Game_GUI()
	{
		home = new HomeGUI();
		battle = new BattleGUI();
		client = new ClientGUI();
		party = new ArrayList<Hero>();
		attackers = new ArrayList<Enemy>();
		allPawns = new ArrayList<Pawn>();
		level = 3; 
		isConnected= false;
		isSinglePlayer = true;
		isServer = true;
	}
	
	ArrayList<Hero> party;
	ArrayList<Enemy> attackers;
	ArrayList<Pawn> allPawns;
	
	HomeGUI home;
	BattleGUI battle;
	ClientGUI client;
	int level;
	int lastTurn;
	boolean turnComplete;
	boolean isConnected;
	boolean isServer;
	boolean isSinglePlayer;
	
	public void showHome()
	{
		home.show();
	}
	
	protected class HomeGUI
	{
		JFrame frame;
		JButton start;
		
		JRadioButton singleButton ;
		JRadioButton multiButton ;
		JRadioButton clientButton ;
		JRadioButton serverButton ;
		
		private void show()
		{
			frame = new JFrame();
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			JPanel buttonPanel = new JPanel();
			start = new JButton("Start Game");
			
			singleButton = new JRadioButton("Single Player");
			multiButton = new JRadioButton("Multiplayer");
			clientButton = new JRadioButton("Client");
			serverButton = new JRadioButton("Server");
			
			ButtonGroup singleOrMulti = new ButtonGroup();
			singleOrMulti.add(singleButton);
			singleOrMulti.add(multiButton);
			
			ButtonGroup clientOrServer = new ButtonGroup();
			clientOrServer.add(clientButton);
			clientOrServer.add(serverButton);
			
			singleButton.addActionListener(new SingleListener());
			multiButton.addActionListener(new MultiListener());
			clientButton.addActionListener(new ClientListener());
			serverButton.addActionListener(new ServerListener());
			
			
			singleButton.setSelected(true);
						
			start.addActionListener(new StartListener());
			
			buttonPanel.add(singleButton);
			buttonPanel.add(multiButton);
			buttonPanel.add(serverButton);
			buttonPanel.add(clientButton);
			buttonPanel.add(start);
			
			clientButton.setVisible(false);
			serverButton.setVisible(false);
			
			frame.getContentPane().add(BorderLayout.CENTER, buttonPanel);
			frame.setSize(160, 160);
			frame.setResizable(false);
			frame.setVisible(true);
		}
		
		class ServerListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if(!isServer)
				{
					boolean show = ((JRadioButton) arg0.getSource()).isSelected();
					home.start.setVisible(show);
					isServer = true;
				}
			}	
		}
		
		class StartListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				battle.show();
				home.frame.setVisible(false);
				
			}
			
		}
		
		class SingleListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean show = ((JRadioButton) arg0.getSource()).isSelected();
				home.clientButton.setVisible(!show);
				home.serverButton.setVisible(!show);
				home.start.setVisible(show);
				isSinglePlayer=true;
			}
		}
		
		class MultiListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				boolean show = ((JRadioButton) arg0.getSource()).isSelected();
				home.clientButton.setVisible(show);
				home.serverButton.setVisible(show);
				isSinglePlayer=false;
			}
		}
		
		class ClientListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				if(isServer)
				{
					isServer = false;
					//start client thread
					boolean show = ((JRadioButton) arg0.getSource()).isSelected();
					home.start.setVisible(!show);
				}
				client.show();
			}
			
		}
	}
	
	private class ClientGUI
	{
		JFrame frame;
		protected JTextArea combatLog;
		
		public void show()
		{
			frame = new JFrame("Client");
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			
			combatLog = new JTextArea();
			JScrollPane scroll = new JScrollPane(combatLog, 
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			DefaultCaret caret = (DefaultCaret) combatLog.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			combatLog.setEditable(false);
			
			frame.getContentPane().add(BorderLayout.CENTER, scroll);
			frame.setSize(400, 500);
			frame.setVisible(true);
			
			home.frame.setVisible(false);
			
			Runnable job = new ClientThread();
			Thread thread = new Thread(job);
			
			thread.start();
		}
		
		class ClientThread implements Runnable{

			@Override
			public void run() {

				Socket clientSocket;
				InputStreamReader streamReader;
				BufferedReader reader;
				//Setup connection
				try 
				{
					clientSocket = new Socket("localhost", 5000);
					streamReader= new InputStreamReader(clientSocket.getInputStream());
					reader = new BufferedReader(streamReader);
				
					while(!isServer && !isSinglePlayer)
					{	
						combatLog.setText("Battle Started: " +
								"\nWhen the battle if over the log will be shown below");
						
						String line;
						while((line = reader.readLine()) != null)
						{	
							combatLog.append("\n" + line);
							System.out.println(line);
						}
						reader.close();
					}	
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
			}
		}
	}
	
	private class BattleGUI
	{
		JFrame frame;
		
		protected JTextArea combatLog;
		protected JPanel abilityPanel;
		
		protected JLabel hero1Label;
		protected JLabel hero2Label;
		protected JLabel hero3Label;
		protected JLabel hero4Label;
		
		protected JLabel enemyName1;
		protected JLabel enemyName2;
		protected JLabel enemyName3;
		protected JLabel enemyName4;
		protected JLabel enemyName5;
		protected JLabel enemyName6;
		
		protected JLabel enemyLabel1;
		protected JLabel enemyLabel2;
		protected JLabel enemyLabel3;
		protected JLabel enemyLabel4;
		protected JLabel enemyLabel5;
		protected JLabel enemyLabel6;
		
		protected HealthBar enemy1HP;
		protected HealthBar enemy2HP;
		protected HealthBar enemy3HP;
		protected HealthBar enemy4HP;
		protected HealthBar enemy5HP;
		protected HealthBar enemy6HP;
		
		private void show()
		{
			//six or so images for the enemies
			
			frame = new JFrame();
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			hero1Label = new JLabel();
			hero2Label = new JLabel();
			hero3Label = new JLabel();
			hero4Label = new JLabel();
			
			/* **************************************************************************************************
			 * 
			 * Remove the starting stuff
			 * 
			 * **************************************************************************************************/
			enemyName1 = new JLabel();
			enemyName2 = new JLabel();
			enemyName3 = new JLabel();
			enemyName4 = new JLabel();
			enemyName5 = new JLabel();
			enemyName6 = new JLabel();
			
			combatLog = new JTextArea();
			combatLog.setEditable(false);
			
			enemyLabel1 = new JLabel();
			enemyLabel2 = new JLabel();
			enemyLabel3 = new JLabel();
			enemyLabel4 = new JLabel();
			enemyLabel5 = new JLabel();
			enemyLabel6 = new JLabel();
			
			enemy1HP = new HealthBar();
			enemy2HP = new HealthBar();
			enemy3HP = new HealthBar();
			enemy4HP = new HealthBar();
			enemy5HP = new HealthBar();
			enemy6HP = new HealthBar();
			
			ArrayList<JLabel> enemyLabels = new ArrayList<JLabel>();
			enemyLabels.add(enemyLabel1);
			enemyLabels.add(enemyLabel2);
			enemyLabels.add(enemyLabel3);
			enemyLabels.add(enemyLabel4);
			enemyLabels.add(enemyLabel5);
			enemyLabels.add(enemyLabel6);
			
			party.add(new Hero(6, "Robert", new HealthBar(), hero1Label, 
					"/Images/Hero1.png"));
			party.add(new Hero(4, "Mariane", new HealthBar(), hero2Label, 
					"/Images/Hero2.png"));
			party.add(new Hero(5, "Haley", new HealthBar(), hero3Label,
					"/Images/Hero3.png"));
			party.add(new Hero(3, "George", new HealthBar(), hero4Label,
					"/Images/Hero4.png"));
			
			Ability newability = new Ability("Heal", -10, AbilityType.HEALING);
			party.get(2).getAbilities().add(newability);
			
			newability = new Ability("Fireball", 7, AbilityType.DAMAGE);
			party.get(3).getAbilities().add(newability);
			
			party.get(0).setParty(party);
			party.get(1).setParty(party);
			party.get(2).setParty(party);
			party.get(3).setParty(party);
			
			JPanel enemyPanel = new JPanel();
			enemyPanel.setBackground(Color.WHITE);
			JPanel partyPanel = new JPanel();
			partyPanel.setBackground(Color.WHITE);
			abilityPanel = new JPanel();
			abilityPanel.setBackground(Color.GRAY);
			
			enemyPanel.setLayout(new GridBagLayout());
			
			resetEnemyPanel(enemyPanel);
			
			JLabel hero1Name = new JLabel(party.get(0).getName());
			JLabel hero2Name = new JLabel(party.get(1).getName());
			JLabel hero3Name = new JLabel(party.get(2).getName());
			JLabel hero4Name = new JLabel(party.get(3).getName());
			
			hero1Label.setIcon(party.get(0).getIcon());
			hero2Label.setIcon(party.get(1).getIcon());
			hero3Label.setIcon(party.get(2).getIcon());
			hero4Label.setIcon(party.get(3).getIcon());
			
			//Setup Party Panel
			partyPanel.setLayout(new BoxLayout(partyPanel, BoxLayout.Y_AXIS));
			partyPanel.add(hero1Name);
			partyPanel.add(hero1Label);
			partyPanel.add(party.get(0).getHealthBar());
			partyPanel.add(hero2Name);
			partyPanel.add(hero2Label);
			partyPanel.add(party.get(1).getHealthBar());
			partyPanel.add(hero3Name);
			partyPanel.add(hero3Label);
			partyPanel.add(party.get(2).getHealthBar());
			partyPanel.add(hero4Name);
			partyPanel.add(hero4Label);
			partyPanel.add(party.get(3).getHealthBar());
			
			abilityPanel.add(new Label("Abilities will go here"));
			
			frame.getContentPane().add(BorderLayout.CENTER, enemyPanel);
			frame.getContentPane().add(BorderLayout.EAST, partyPanel);
			frame.getContentPane().add(BorderLayout.SOUTH, abilityPanel);
			abilityPanel.setPreferredSize(new Dimension(250, 85));
			abilityPanel.revalidate();
			frame.setSize(460, 460);
			frame.setResizable(false);
			frame.setVisible(true);
			
			/* Play the game. This should be a loop that contains executeRound until either
			 * the party or the attackers cannot fight. BattleUtils can have a check that happens
			 * after each turn
			 * 
			 */
			
			//Get the list of attackers (based on floor and party?)
			attackers = getBattle(level, party, enemyLabels, combatLog);
			party.get(0).setEnemy(attackers);
			party.get(1).setEnemy(attackers);
			party.get(2).setEnemy(attackers);
			party.get(3).setEnemy(attackers);
			
			party.get(0).getLabel().addMouseListener(new TargetListener());
			party.get(1).getLabel().addMouseListener(new TargetListener());
			party.get(2).getLabel().addMouseListener(new TargetListener());
			party.get(3).getLabel().addMouseListener(new TargetListener());
			
			//add attacker pawns to board
			for(int i=0;i < attackers.size(); i++)
			{
				addEnemy(attackers.get(i), enemyPanel, i);
				attackers.get(i).getLabel().addMouseListener( new TargetListener());
				attackers.get(i).setParty(party);
			}
			
			//play a round; everyone gets one turn, order based on speed stat
			
			allPawns.clear();		
			allPawns.addAll(attackers);
			allPawns.addAll(party);
			
			executeLevel(combatLog, abilityPanel);
			abilityPanel.removeAll();
			abilityPanel.revalidate();
			abilityPanel.repaint();
		}
		
		private void gameOver()
		{
			combatLog.append("\n Game Over... Try again");
		}
		
		private void victory()
		{
			combatLog.append("\n You Win!");
		}
		
		//this just picks the enemies in the battle
		public ArrayList<Enemy> getBattle(int floor, ArrayList<Hero> Party, ArrayList<JLabel> labels, JTextArea text)
		{
			
			ArrayList<Enemy> battle = new ArrayList<Enemy>();
			
			// TODO change this to a real system later
			switch(floor)
			{
			
			case 0:     // 1 lvl 3 skeleton
				battle.add(new Skeleton(3, new HealthBar(), labels.get(0), 
						"/Images/Skeleton1.png"));
				text.setText(text.getText() + "\nNew Battle!: The Party Faces A LVL 3 Skeleton");
				break;
			case 1:     // 2 lvl 3 skeletons
				battle.add(new Skeleton(3, new HealthBar(), labels.get(0), 
						"/Images/Skeleton2.png"));
				battle.add(new Skeleton(3, new HealthBar(), labels.get(1), 
						"/Images/Skeleton1.png"));
				text.setText(text.getText() + "\nNew Battle!: The Party Faces 2 LVL 3 Skeletons");
				break;
			case 2:		// 3 lvl 3 skeleton
				battle.add(new Skeleton(3, new HealthBar(), labels.get(0), 
						"/Images/Skeleton1.png"));
				battle.add(new Skeleton(3, new HealthBar(), labels.get(1), 
						"/Images/Skeleton2.png"));
				battle.add(new Skeleton(3, new HealthBar(), labels.get(2), 
						"/Images/Skeleton1.png"));
				text.setText(text.getText() + "\nNew Battle!: The Party Faces 3 LVL 3 Skeletons");
				break;
			case 3:     // 4 lvl 3 skeletons
				battle.add(new Skeleton(3, new HealthBar(), labels.get(0), 
						"/Images/Skeleton1.png"));
				battle.add(new Skeleton(3, new HealthBar(), labels.get(1), 
						"/Images/Skeleton2.png"));
				battle.add(new Skeleton(3, new HealthBar(), labels.get(2), 
						"/Images/Skeleton1.png"));
				battle.add(new Skeleton(3, new HealthBar(), labels.get(3), 
						"/Images/Skeleton2.png"));
				text.setText(text.getText() + "\nNew Battle!: The Party Faces 4 LVL 3 Skeletons");
				break;
			case 4:     // 5 lvl 4 skeletons
				battle.add(new Skeleton(4, new HealthBar(), labels.get(0), 
						"/Images/Skeleton1.png"));
				battle.add(new Skeleton(4, new HealthBar(), labels.get(1), 
						"/Images/Skeleton2.png"));
				battle.add(new Skeleton(4, new HealthBar(), labels.get(2), 
						"/Images/Skeleton1.png"));
				battle.add(new Skeleton(4, new HealthBar(), labels.get(3), 
						"/Images/Skeleton2.png"));
				battle.add(new Skeleton(4, new HealthBar(), labels.get(4), 
						"/Images/Skeleton1.png"));
				text.setText(text.getText() + "\nNew Battle!: The Party Faces 5 LVL 4 Skeletons");
				break;
			case 5:     // 6 lvl 7 skeletons
				battle.add(new Skeleton(7, new HealthBar(), labels.get(0), 
						"/Images/Skeleton1.png"));
				battle.add(new Skeleton(7, new HealthBar(), labels.get(1), 
						"/Images/Skeleton2.png"));
				battle.add(new Skeleton(7, new HealthBar(), labels.get(2), 
						"/Images/Skeleton2.png"));
				battle.add(new Skeleton(7, new HealthBar(), labels.get(3), 
						"/Images/Skeleton1.png"));
				battle.add(new Skeleton(7, new HealthBar(), labels.get(4), 
						"/Images/Skeleton2.png"));
				battle.add(new Skeleton(7, new HealthBar(), labels.get(5), 
						"/Images/Skeleton1.png"));
				
				text.setText(text.getText() + "\nNew Battle!: The Party Faces 6 LVL 7 Skeletons");
				
				break;
			}
			
			return battle;
		}
		
		
		/*This handles the game this should take a list of the pawns, sort them by speed,
		 * and execute each pawns turn*/
		public void executeLevel(JTextArea log, JPanel abilityPanel)
		{	
			
			Runnable job = new Engine();
			Thread thread = new Thread(job);
			
			thread.start();
		}
		
		class TargetListener implements MouseListener {

			@Override
			public void mouseClicked(MouseEvent e) {
				JLabel label = (JLabel) e.getComponent();
				if (label.getBorder() != null)
				{
					
					for(Enemy enemy: attackers)
					{
						if(enemy.getLabel() == e.getComponent())
						{
							turnComplete = ((Hero) allPawns.get(lastTurn)).completeAction(enemy, 
									battle.combatLog, battle.abilityPanel);
						}
					}
					for(Hero hero: party)
					{
						if(hero.getLabel() == e.getComponent())
						{
							turnComplete = ((Hero) allPawns.get(lastTurn)).completeAction(hero, 
									battle.combatLog, battle.abilityPanel);
						}
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		}
		
		//This engine will control the turn assignments
		class Engine implements Runnable{

			@Override
			/*
			 * This should be called when a player turn is started. It allows  
			 * the game to progress once the turn has ended.
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public synchronized void run() {
				
				Collections.sort(allPawns);
				Collections.reverse(allPawns);
			
				if(isSinglePlayer)
				{
					while(true)
					{
						int i=0;
						
						while(i < allPawns.size())
						{
							turnComplete = false;
							lastTurn = i;
							if(allPawns.get(i).isDead())
							{
								i++;
								continue;
							}
							allPawns.get(i).yourTurn(combatLog, abilityPanel);
							
							if(allPawns.get(i).getClass() == Hero.class)
							{
								while(true)
								{
									try {
										Thread.sleep(200);
									} catch (InterruptedException e) {	
									e.printStackTrace();
									}
									
									if(turnComplete)
									{
										break;
									}
								}
								
								boolean partyDead = true;
								boolean attackersDead = true;
								
								for(int y= 0; y < allPawns.size(); y++)
								{
									if(!allPawns.get(y).isDead())
									{
										if(allPawns.get(y).getClass() == Hero.class)
											partyDead = false;
										else
											attackersDead = false;
									}		
								}
								
								if(partyDead)
								{
									gameOver();
									return;
								}
								if(attackersDead)
								{
									victory();
									return;
								}
							}//close Big If
							else
							{
								boolean partyDead = true;
								boolean attackersDead = true;
								
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								for(int y= 0; y < allPawns.size(); y++)
								{
									if(!allPawns.get(y).isDead())
									{
										if(allPawns.get(y).getClass() == Hero.class)
											partyDead = false;
										else
											attackersDead = false;
									}		
								}
								
								if(partyDead)
								{
									gameOver();
									return;
								}
								if(attackersDead)
								{
									victory();
									return;
								}
							}
							i++;
						}//close inner while (a round is done)
					}//close outer while (game is done)
				}
				
				
				if(isServer && !isSinglePlayer)
				{
					try 
					{
						ServerSocket serverSocket = new ServerSocket(5000);
						Socket socket = serverSocket.accept();
						PrintWriter writer = new PrintWriter(socket.getOutputStream());
						
						
						while(true)
						{
							int i=0;
							
							while(i < allPawns.size())
							{
								turnComplete = false;
								lastTurn = i;
								if(allPawns.get(i).isDead())
								{
									i++;
									continue;
								}
								allPawns.get(i).yourTurn(combatLog, abilityPanel);
								
								if(allPawns.get(i).getClass() == Hero.class)
								{
									while(true)
									{
										try {
											Thread.sleep(200);
										} catch (InterruptedException e) {	
										e.printStackTrace();
										}
										
										if(turnComplete)
										{
											break;
										}
									}
									
									boolean partyDead = true;
									boolean attackersDead = true;
									
									for(int y= 0; y < allPawns.size(); y++)
									{
										if(!allPawns.get(y).isDead())
										{
											if(allPawns.get(y).getClass() == Hero.class)
												partyDead = false;
											else
												attackersDead = false;
										}		
									}
									
									if(partyDead)
									{
										gameOver();
										writer.print(combatLog.getText() + "\n");
										writer.flush();
										return;
									}
									else if(attackersDead)
									{
										victory();
										writer.print(combatLog.getText() + "\n");
										writer.flush();
										return;
									}
								}//close Big If
								else
								{
									boolean partyDead = true;
									boolean attackersDead = true;
									
									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									for(int y= 0; y < allPawns.size(); y++)
									{
										if(!allPawns.get(y).isDead())
										{
											if(allPawns.get(y).getClass() == Hero.class)
												partyDead = false;
											else
												attackersDead = false;
										}		
									}
									
									if(partyDead)
									{
										gameOver();
										writer.print(combatLog.getText() + "\nParty Wiped!");
										writer.flush();
										return;
									}
									else if(attackersDead)
									{
										victory();
										writer.print(combatLog.getText() + "\nParty Wins!");
										writer.flush();
										return;
									}
								}
								i++;
							}//close inner while (a round is done)
						}//close outer while (game is done)
					} 
					catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		private void resetEnemyPanel(JPanel enemyPanel) {
			
			enemyPanel.removeAll();
			
			JScrollPane scroll = new JScrollPane(combatLog, 
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			DefaultCaret caret = (DefaultCaret) combatLog.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.ipady = 15;
			gbc.ipadx = 130;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy = 0;
			enemyPanel.add(enemyName1, gbc);
			gbc.gridx = 1;
			gbc.gridy = 0;
			enemyPanel.add(enemyName2, gbc);
			gbc.gridx = 2;
			gbc.gridy = 0;
			enemyPanel.add(enemyName3, gbc);
			gbc.gridx = 0;
			gbc.gridy = 4;
			enemyPanel.add(enemyName4, gbc);
			gbc.gridx = 1;
			gbc.gridy = 4;
			enemyPanel.add(enemyName5, gbc);
			gbc.gridx = 2;
			gbc.gridy = 4;
			enemyPanel.add(enemyName6, gbc);
			
			
			gbc.ipady = 55;
			gbc.gridx = 0;
			gbc.gridy = 1;
			enemyPanel.add(enemyLabel1, gbc);
			gbc.gridx = 1;
			gbc.gridy = 1;
			enemyPanel.add(enemyLabel2, gbc);
			gbc.gridx = 2;
			gbc.gridy = 1;
			enemyPanel.add(enemyLabel3, gbc);
			gbc.gridx = 0;
			gbc.gridy = 5;
			enemyPanel.add(enemyLabel4, gbc);
			gbc.gridx = 1;
			gbc.gridy = 5;
			enemyPanel.add(enemyLabel5, gbc);
			gbc.gridx = 2;
			gbc.gridy = 5;
			enemyPanel.add(enemyLabel6, gbc);
			
			gbc.ipady = 15;
			gbc.gridx = 0;
			gbc.gridy = 2;
			enemyPanel.add(enemy1HP,gbc);
			gbc.gridx = 0;
			gbc.gridy = 2;
			enemyPanel.add(enemy2HP,gbc);
			gbc.gridx = 0;
			gbc.gridy = 2;
			enemyPanel.add(enemy3HP,gbc);
			gbc.gridx = 0;
			gbc.gridy = 2;
			enemyPanel.add(enemy4HP,gbc);
			gbc.gridx = 0;
			gbc.gridy = 2;
			enemyPanel.add(enemy5HP,gbc);
			gbc.gridx = 0;
			gbc.gridy = 2;
			enemyPanel.add(enemy6HP,gbc);
			
			gbc.gridwidth = 3;
			gbc.gridx = 0;
			gbc.gridy = 7;
			gbc.ipady = 120;
			enemyPanel.add(new JLabel(), gbc);
			gbc.gridx = 0;
			gbc.gridy = 8;
			gbc.gridwidth = 3;
			gbc.ipady = 40;
			gbc.anchor = GridBagConstraints.LAST_LINE_START;
		
			enemyPanel.add(scroll, gbc);
		}
		
		public void addEnemy(Enemy enemy, JPanel enemyPanel, int slot) {
			if(slot < 0 || slot > 5)
				return;
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.ipadx = 50;
			
			if(slot == 0)
			{
				
				gbc.gridx = 0;
				gbc.gridy = 0;
				enemyName1.setText(enemy.getName());
				enemyPanel.add(enemyName1, gbc);
				
				gbc.gridx = 0;
				gbc.gridy = 1;
				enemyLabel1.setIcon(enemy.getIcon());
				enemyPanel.add(enemyLabel1, gbc);
				
				gbc.gridx = 0;
				gbc.gridy = 2;
				enemy1HP = enemy.getHealthBar();
				enemyPanel.add(enemy1HP, gbc);
			}
			else if(slot == 1)
			{
				gbc.gridx = 1;
				gbc.gridy = 0;
				enemyName2.setText(enemy.getName());
				enemyPanel.add(enemyName2, gbc);
				
				gbc.gridx = 1;
				gbc.gridy = 1;
				enemyLabel2.setIcon(enemy.getIcon());
				enemyPanel.add(enemyLabel2, gbc);
				
				gbc.gridx = 1;
				gbc.gridy = 2;
				enemy2HP = enemy.getHealthBar();
				enemyPanel.add(enemy2HP, gbc);
			}
			else if(slot == 2)
			{
				gbc.gridx = 2;
				gbc.gridy = 0;
				enemyName3.setText(enemy.getName());
				enemyPanel.add(enemyName3, gbc);
				
				gbc.gridx = 2;
				gbc.gridy = 1;
				enemyLabel3.setIcon(enemy.getIcon());
				enemyPanel.add(enemyLabel3, gbc);
				
				gbc.gridx = 2;
				gbc.gridy = 2;
				enemy3HP = enemy.getHealthBar();
				enemyPanel.add(enemy3HP, gbc);
			}
			else if(slot == 3)
			{
				gbc.gridx = 0;
				gbc.gridy = 4;
				enemyName4.setText(enemy.getName());
				enemyPanel.add(enemyName4, gbc);
				
				gbc.gridx = 0;
				gbc.gridy = 5;
				enemyLabel4.setIcon(enemy.getIcon());
				enemyPanel.add(enemyLabel4, gbc);
				
				gbc.gridx = 0;
				gbc.gridy = 6;
				enemy4HP = enemy.getHealthBar();
				enemyPanel.add(enemy4HP, gbc);
			}
			else if(slot == 4)
			{
				gbc.gridx = 1;
				gbc.gridy = 4;
				enemyName5.setText(enemy.getName());
				enemyPanel.add(enemyName5, gbc);
				
				gbc.gridx = 1;
				gbc.gridy = 5;
				enemyLabel5.setIcon(enemy.getIcon());
				enemyPanel.add(enemyLabel5, gbc);
				
				gbc.gridx = 1;
				gbc.gridy = 6;
				enemy5HP = enemy.getHealthBar();
				enemyPanel.add(enemy5HP, gbc);
			}
			else if(slot == 5)
			{
				gbc.gridx = 2;
				gbc.gridy = 4;
				enemyName6.setText(enemy.getName());
				enemyPanel.add(enemyName6, gbc);
				
				gbc.gridx = 2;
				gbc.gridy = 5;
				enemyLabel6.setIcon(enemy.getIcon());
				enemyPanel.add(enemyLabel6, gbc);
				
				gbc.gridx = 2;
				gbc.gridy = 6;
				enemy6HP = enemy.getHealthBar();
				enemyPanel.add(enemy6HP, gbc);
			}
		}
	}
}
/*
class CompareSomeThings implements Comparator<Hero>
{
	@Override
	public int compare(Hero arg0, Hero arg1) {
		
		return ((Integer) arg0.getLevel()).compareTo(arg1.getLevel());
	}
	public void doit()
	{
		ArrayList<Hero> heroList = new ArrayList<Hero>();
		
		CompareSomeThings compare = new CompareSomeThings();
		Collections.sort(heroList, compare);
	
		for(int i =0; i < 10; i++)
		{
			//do something
		}
		i = 5;
	}	
}
*/