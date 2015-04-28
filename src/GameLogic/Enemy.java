package GameLogic;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.*;

public abstract class Enemy extends Pawn{

	public Enemy(int level, HealthBar healthBar, JLabel label,String image) {
		super(level, healthBar, label,  image);
	}
	
	protected ArrayList<Hero> party;
	
	public void setParty(ArrayList<Hero> party)
	{
		this.party = party;
	}
	
	
	public void attack(Hero target, JTextArea log)
	{
		int damage = (int) ((Math.random() * level));
		target.getAttacked(damage, log);
	}
	
	public void toggleTargetable()
	{
		if(getLabel().getBorder()== null)
			getLabel().setBorder(BorderFactory.createLineBorder(Color.RED));
		else
			getLabel().setBorder(null);
	}
}
