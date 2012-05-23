package druglord.player;

import druglord.game.City;
import druglord.items.*;

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

	//The player's maximum carrying capacity
	private int carryingCapacity;
	
	//The equipped weapon of this player
	private Weapon equippedWeapon;
	
	//Global player notoriety
	private double notoriety;
	
	//City the player is currently in
	private City currentCity;
	
	public Player(String name, int age, int health, Image avatar)
	{
		this.name = name;
		this.age = age;
		this.health = health;
		this.avatar = avatar;
		inventory = new ArrayList<Item>(8);
		notoriety = 0;
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
}
