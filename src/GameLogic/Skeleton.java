package GameLogic;

import java.util.ArrayList;

import javax.swing.*;

public class Skeleton extends Enemy {
	
	public Skeleton(int level, HealthBar healthBar, JLabel label, String image)
	{
		super(level, healthBar, label, image);
		maxHP = 15 + level*5;
		HP = maxHP;
		speed = level*2;
		damage = 1 + level;
		this.healthBar.setHealth(HP, maxHP);
		this.healthBar.updateDamage();
		this.healthBar.revalidate();
	}

	protected int damage;
	
	@Override
	public String getName() {
		return "Lvl " + level + " Skeleton";
	}

	@Override
	public void getAttacked(int damage, JTextArea log) {
		HP -= damage;
		
		if(HP > maxHP)
			HP = maxHP;
		
		if(HP < 0)
			HP = 0;
		
		healthBar.setHealth(HP, maxHP);
		healthBar.updateDamage();
		log.append( " and " + getName() + " hit for " + damage );
	}

	@Override
	public void yourTurn(JTextArea log, JPanel abilityPanel) {
		
		abilityPanel.removeAll();
		abilityPanel.revalidate();
		abilityPanel.repaint();
		
		if(HP == 0)
		{
			return;
		}
		
		//attack target at random?
		int target = (int) (Math.random() * 100) % 4;
		log.append( "\n" + getName() + " Attacks ");
		party.get(target).getAttacked(damage, log);
	}

	@Override
	public int compareTo(Pawn o) {
		
		if(this.speed < o.speed)
			return -1;
		if(this.speed > o.speed)
			return 1;
		
		return 0;
	}
	
}
