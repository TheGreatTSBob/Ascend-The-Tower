package GameLogic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;

import GameLogic.Ability.AbilityType;

/*Currently all the heros are of the hero class. Further inheritance could be used to allow for multiple. 
 * Hero Types. The stats are all generic and for testing purposes
 */
public class Hero extends Pawn{

	public Hero(int level,String name, HealthBar healthBar, JLabel label, String image) {
		super(level, healthBar, label, image);
		this.name = new StringBuilder(name);		
		maxHP = 10 + level * 2;
		HP = maxHP;
		speed = level*2;
		chosenAbility = -1;
		setAbilities(new ArrayList<Ability>());
		this.healthBar.setHealth(HP, maxHP);
		this.healthBar.updateDamage();
		this.healthBar.revalidate();
		this.addAbility("Attack", 5, AbilityType.DAMAGE);
	}

	protected StringBuilder name;
	protected int chosenAbility;
	protected ArrayList<Hero> party;
	protected ArrayList<Enemy> attackers;
	private ArrayList<Ability> abilities;
	
	@Override
	public String getName() {
		return name.toString();
	}
	
	public void setParty(ArrayList<Hero> party)
	{
		this.party = party;
	}
	
	public void setEnemy(ArrayList<Enemy> enemy)
	{
		this.attackers = enemy;
	}

	@Override
	public void getAttacked(int damage, JTextArea log) {
		
		//Example: This gives ~10% chance to dodge to all heroes
		//Warning: this will affect all moves directed at the hero!!
		if(Math.random() < .9)
		{
			HP -= damage;
			log.append("\n" + name  + " hit for " + damage );
		}
		else
		{
			log.append("\n" + name  + " dodged the attack!" );
		}
		
		if(HP > maxHP)
			HP = maxHP;
		
		if(HP < 0)
			HP = 0;
		healthBar.setHealth(HP, maxHP);
		healthBar.updateDamage();
	}
	
	public void addAbility(String name, int damage, AbilityType type)
	{
		Ability newability = new Ability(name, damage, type);
		getAbilities().add(newability);
	}

	@Override
	public void yourTurn(JTextArea log, JPanel abilityPanel) {		
	
		if(HP == 0)
		{
			return;
		}
		
		//highlight this hero and add his/her turn to the log
		getLabel().setBorder(BorderFactory.createLineBorder(Color.BLUE));
		log.append("\nIts " + name + "'s Turn: ");
		
		//assert(name != null);
		
		//change ability list for this hero
		abilityPanel.removeAll();
		abilityPanel.revalidate();
		abilityPanel.repaint();
		
		for(Ability a : getAbilities())
		{
			JButton button = new JButton(a.name);
			abilityPanel.add(button);
			
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					startAction(arg0.getActionCommand());
				}
			});
		}		
	}
	
	public void startAction(String name)
	{
		//find the corresponding ability
		for(int i =0; i < getAbilities().size(); i++)
		{
			Ability a = getAbilities().get(i);
			if(a.name == name)
			{
				if(a.type == AbilityType.DAMAGE)
				{
					for(Enemy t : attackers)
					{
						t.toggleTargetable();
					}
				}
				else if(a.type == AbilityType.HEALING)
				{
					for(Hero h : party)
					{
						h.toggleTargetable();
					}
				}
				else if(a.type == AbilityType.STATUS)
				{
					//Does nothing for now
				}
				chosenAbility = i;
			}
		}
	}
	
	public void toggleTargetable()
	{
		if(getLabel().getBorder()== null || ((LineBorder) getLabel().getBorder()).getLineColor()== Color.BLUE)
			getLabel().setBorder(BorderFactory.createLineBorder(Color.GREEN));
		else
			getLabel().setBorder(null);
	}
		
	public boolean completeAction(Pawn target, JTextArea log, JPanel abilityPanel)
	{
		log.append( name + " uses " + 
						getAbilities().get(chosenAbility).name);
		
		target.getAttacked(getAbilities().get(chosenAbility).damage, log);
		
		if(getAbilities().get(chosenAbility).type == AbilityType.DAMAGE)
		{
			for(Enemy t : attackers)
			{
				t.toggleTargetable();
				getLabel().setBorder(null);
				getLabel().validate();
				getLabel().repaint();
			}
		}
		else if(getAbilities().get(chosenAbility).type == AbilityType.HEALING)
		{
			for(Hero h : party)
			{
				h.toggleTargetable();
				getLabel().setBorder(null);
				getLabel().validate();
				getLabel().repaint();
			}
		}
		
		abilityPanel.removeAll();
		abilityPanel.revalidate();
		abilityPanel.repaint();
		
		return true;
	}

	@Override
	public int compareTo(Pawn o) {
		
		if(this.speed < o.speed)
			return -1;
		if(this.speed > o.speed)
			return 1;
		
		return 0;
	}

	public ArrayList<Ability> getAbilities() {
		return abilities;
	}

	public void setAbilities(ArrayList<Ability> abilities) {
		this.abilities = abilities;
	}
}
