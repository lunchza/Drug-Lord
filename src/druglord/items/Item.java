package druglord.items;

import java.awt.Image;

import javax.swing.ImageIcon;

public abstract class Item {
	
	//The name of this item
	protected String name;
	
	//The description of this item
	protected String description;
	
	//How many of this item is in this stack
	protected int quantity;

	//The value of this item
	protected double value;
	
	//Unique item ID
	protected int ID;
	
	//The image for this item
	private Image image;
	
	public Item(int ID, String imagePath, String name, String description, double value)
	{
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.value = value;
		
		image = new ImageIcon(imagePath).getImage();
		
		quantity = 0;
	}
	
	public Item(int ID, String imagePath, String name, String description, double value, int quantity)
	{
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.value = value;
		
		image = new ImageIcon(imagePath).getImage();
		
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getQuantity() {
		return quantity;
	}

	public void remove(int quantity) {
		this.quantity -= quantity;
	}
	
	public void removeAll()
	{
		quantity = 0;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
	
	public Image getImage()
	{
		return image;
	}
}
