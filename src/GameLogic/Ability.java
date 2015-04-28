package GameLogic;

public class Ability {
	
	public Ability(String name, int damage, AbilityType type)
	{
		this.name = name;
		this.damage = damage;
		this.type = type;
	}
	
	
	public enum AbilityType {DAMAGE, HEALING, STATUS};
	
	protected AbilityType type;
	protected String name;
	protected int damage;

}
