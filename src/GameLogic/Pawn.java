package GameLogic;


import javax.swing.*;

public abstract class Pawn implements Comparable<Pawn> {
	
	public Pawn()
	{
		
	}
	
	public Pawn(int level, HealthBar healthBar, JLabel label, String icon)
	{
		this.level = level;
		this.healthBar = healthBar;
		this.setLabel(label);
		speed = 1;
		image = new ImageIcon(getClass().getResource(icon));
	}
	
	protected int level;
	protected int maxHP;
	protected int HP;
	protected int speed;
	private JLabel label;
	protected ImageIcon image;
	public HealthBar healthBar;
	
	public int getLevel()
	{
		return level;
	}
	
	public ImageIcon getIcon()
	{
		return image;
	}
	
	public HealthBar getHealthBar()
	{
		return healthBar;
	}
	
	public boolean isDead()
	{
		if(HP < 1)
			return true;
		
		return false;
	}

	//Does something when its this pawns turn
	public abstract void yourTurn(JTextArea log, JPanel abilityPanel);
	
	//gets the name for output on the screen
	public abstract String getName();
	
	//what happens when the pawn is attacked
	public abstract void getAttacked(int damage, JTextArea log);

	public JLabel getLabel() {
		return label;
	}

	public void setLabel(JLabel label) {
		this.label = label;
	}
	
}
