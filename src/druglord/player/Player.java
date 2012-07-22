package druglord.player;

import druglord.game.City;
import druglord.items.*;
import druglord.utils.Globals;

import java.awt.Image;
import java.util.ArrayList;

public class Player {
	
	//Player name
	private String name;
	
	//Player age
	private int age;
	
	//Player health
	private int health;
	
	//Player avatar
	private Image avatar;

	//Player inventory
	private static ArrayList<Item> inventory;
	
	//The amount of cash carried by this player
	private double cash;

	//The player's maximum carrying capacity
	private int carryingCapacity;
	
	//The equipped weapon of this player
	private Weapon equippedWeapon;
	
	//Global player notoriety
	private int notoriety;
	
	//City the player is currently in
	private City currentCity;
	
	public Player(String name, int age, int health, Image avatar)
	{
		this.name = name;
		this.age = age;
		this.health = health;
		this.avatar = avatar;
		inventory = new ArrayList<Item>(8);
		for (int i = 0; i < inventory.size(); i++)
			inventory.add(null);
		notoriety = 325;
		cash = 1000.0;
		carryingCapacity = 8;
	}
	
	//get city
	public City getCurrentCity()
	{
		return currentCity;
	}
	
	//change player city
	public void setPlayerCity(City c)
	{
		currentCity = c;
	}
	
	public ArrayList<Item> getInventory()
	{
		return inventory;
	}
	
	public void addCash(double cash)
	{
		this.cash += cash;
	}
	
	public String[] inventoryToArray()
	{
		int len = 0;
		for (int i = 0; i < inventory.size(); i++)
			if (inventory.get(i) != null)
				len++;
				
		String[] inv = new String[len];
		for (int i = 0; i < inv.length; i++)
		{
			String item = inventory.get(i).getDescription();
			for (int j = 0; j < 15-inventory.get(i).getDescription().length(); j++)
				item = item.concat(" ");
			item = item.concat("x"+inventory.get(i).getQuantity());
			
			inv[i] = item;
		}
		return inv;
	}
	
	public double getCash()
	{
		return cash;
	}
	
	public void giveItem(Item i)
	{
		inventory.add(i);
	}
	
	public void equipWeapon(Weapon i)
	{
		equippedWeapon = i;
	}
	
	public Weapon getEquippedWeapon()
	{
		return equippedWeapon;
	}
	
	public void removeItem(Item i)
	{
		for (Item item: inventory)
			if (item == i)
				item = null;
	}
	
	public void removeItem(int index)
	{
		inventory.remove(index);
	}

	public boolean hasFullInventory() {
		return inventory.size() == carryingCapacity;
	}

	public void setCarryingCapacity(int carryingCapacity) {
		this.carryingCapacity = carryingCapacity;
	}

	public int getHealth() {
		return health;
	}
	
	public void heal(int amount)
	{
		health += amount;
		if (health >= Globals.MAX_HEALTH)
			health = 100;
	}

	public void damage(int amount) {
		health -= amount;
	}
	
	public boolean isDead()
	{
		return health <= 0;
	}

	public int getNotoriety() {
		return notoriety;
	}

	public void addNotoriety(int notoriety) {
		this.notoriety += notoriety;
	}
}
