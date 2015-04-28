package GameLogic;

import java.awt.Color;

import javax.swing.*;


public class HealthBar extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6768897178262526010L;

	private int max, current;
	
	public HealthBar()
	{
		max = 10;
		current = 10;
	}
	public HealthBar(int current, int max)
	{
		this.max = max;
		this.current = current;
	}

	public void updateDamage()
	{
		this.setForeground(Color.GREEN);
		if(current <= max/2)
			this.setForeground(Color.ORANGE);
		if(current <= max/4)
			this.setForeground(Color.RED);
		
		this.setText(current + " / " + max);
		this.revalidate();
	}
	public void setHealth(int HP, int maxHP) {
		max = maxHP;
		current = HP;
	}
}
