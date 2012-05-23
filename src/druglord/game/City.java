package druglord.game;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import druglord.items.Drug;

public class City {
	
	//The width and height(pixels)  of the city location rectangle
	private final static int CITY_RECT_SIZE = 10;
	
	//City name
	private String name;
	
	//The location of this city on the map
	private Rectangle location;
	
	//This city's prices (Maps ID's to prices)
	private Map<Integer, Double> priceList;
	
	public City(String name, int x, int y)
	{
		this.name = name;
		location = new Rectangle(x, y, CITY_RECT_SIZE, CITY_RECT_SIZE);
		priceList = new HashMap<Integer, Double>();
	}
	
	//determines if this city is currently being hovered over by the user
	public boolean isFocussed(int mouseX, int mouseY)
	{
		return location.contains(mouseX, mouseY);
	}
	
	//returns the rectangle bounds for the city rect
	public Rectangle getCityRectangle()
	{
		return location;
	}
	
	//return city name
	public String getName()
	{
		return name;
	}
	
	public int getX()
	{
		return location.x;
	}
	
	public int getY()
	{
		return location.y;
	}
	
	public Map<Integer, Double> getPriceList()
	{
		return priceList;
	}
	
	public void setPrices(Map<Integer, Double> prices)
	{
		priceList = prices;
	}

}
