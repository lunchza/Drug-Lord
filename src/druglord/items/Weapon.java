package druglord.items;

public class Weapon extends Item implements Equipabble {
	
	//The ammunition for this weapon
	private int ammo;

	//The damage bounds of this weapon
	private int minDamage, maxDamage;
	
	public Weapon(int ID, String imagePath, String name, String description, double value, int minDamage, int maxDamage) {
		super(ID, imagePath, name, description, value);
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		ammo = 0;
	}
	
	public Weapon(int ID, String imagePath, String name, String description, int quantity, double value, int minDamage, int maxDamage) {
		super(ID, imagePath, name, description, value, quantity);
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		ammo = 0;
	}
	
	public int getAmmo()
	{
		return ammo;
	}
	
	public int getMinDamage()
	{
		return minDamage;
	}
	
	public int getMaxDamage()
	{
		return maxDamage;
	}

}
