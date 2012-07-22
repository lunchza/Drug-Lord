package druglord.utils;

import druglord.items.Drug;
import druglord.items.Item;
import druglord.items.Weapon;

public class Globals {
	
	//Game version
	public final static String VERSION = "0.1a";
	
	//max and min stat values
	public static final int MAX_STAT_VALUE = 30;
	public static final int MIN_STAT_VALUE = 8;
	
	//Maximum health
	public static final int MAX_HEALTH = 100;
	
	//maximum notoriety
	public static final int MAX_NOTORIETY = 1000;
	
	//Item ID's
	public static final int DRUG = 1;
	/**
	 * DRUGS
	 */
	public static final int WEED = 2;
	public static final int MDMA = 3;
	public static final int SPEED = 4;
	public static final int HEROIN = 5;
	
	public static final int WEAPON = 9;
	/**
	 * WEAPONS
	 */
	public static final int PISTOL = 10;
	
	public static final int STACK_MAXIMUM = 99;
	
	public static final int[] drugList = {WEED, MDMA, SPEED, HEROIN};
	public static final String[] drugNameList = {"WEED", "MDMA", "SPEED", "HEROIN"};
	
	public static boolean isDrug(int ID)
	{
		return ID == DRUG || ID == WEED || ID == MDMA || ID == SPEED || ID == HEROIN;
	}
	
	public static Item convertIDToItem(int ID)
	{
		switch(ID)
		{
		case WEED:
			return new Drug(ID, "images/items/weed.jpg", "weed", "WEED", 0.0, 70.0);
		case MDMA:
			return new Drug(ID, "images/items/mdma.png", "mdma", "MDMA", 0.0, 70.0);
		case SPEED:
			return new Drug(ID, "images/items/speed.png", "speed", "SPEED", 0.0, 70.0);
		case HEROIN:
			return new Drug(ID, "images/items/heroin.png", "heroin", "HEROIN", 0.0, 70.0);
			
		case PISTOL:
			return new Weapon(ID, "images/pistol.jpg", "pistol", "GLOCK", 0.0, 2, 4);
			
			default:
				return null;
		}
	}

}
