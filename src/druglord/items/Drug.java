package druglord.items;

public class Drug extends Item {
	
	//The purity level of this drug
	private double purity;

	public Drug(int ID, String imagePath, String name, String description, double value, double initialPurity) {
		super(ID, imagePath, name, description, value);
		this.purity = initialPurity;
	}
	
	public Drug(int ID, String imagePath, String name, String description, double value, int quantity, double initialPurity) {
		super(ID, imagePath, name, description, value, quantity);
		this.purity = initialPurity;
	}
	
	public double getPurity()
	{
		return purity;
	}
}
