package druglord.game;

import druglord.utils.Globals;

public class Market {
	
	public static final int STEEP_DECLINE = -2;
	public static final int SLIGHT_DECLINE = -1;
	public static final int STABLE = 0;
	public static final int SLIGHT_BOOM = 1;
	public static final int STEEP_BOOM = 2;

	public void adjustPrices(City city)
	{
		int factor = roll(100);
		switch (city.getCurrentTrend())
		{
		case STEEP_DECLINE:
			//10% chance of stabilising
			if (factor >= 90)
				city.setCurrentTrend(STABLE);
			//50% chance of going to slight decline
			else if (factor >= 50)
				city.setCurrentTrend(SLIGHT_DECLINE);
			break;
		case SLIGHT_DECLINE:
			//5% chance of going to slight boom
			if (factor >= 95)
				city.setCurrentTrend(SLIGHT_BOOM);
			//50% chance of stabilising
			else if (factor >= 50)
				city.setCurrentTrend(STABLE);
			//20% chance of going to steep decline
			else if (factor >= 80)
				city.setCurrentTrend(STEEP_DECLINE);
			break;
		case STABLE:
			//20% chance of going either way
			if (factor >= 0 && factor <= 20)
				city.setCurrentTrend(SLIGHT_DECLINE);
			else if (factor >= 80 && factor <= 100)
				city.setCurrentTrend(SLIGHT_BOOM);
			break;
		case SLIGHT_BOOM:
			//5% chance of going to slight decline
			if (factor >= 95)
				city.setCurrentTrend(SLIGHT_DECLINE);
			//50% chance of stabilising
			else if (factor >= 50)
				city.setCurrentTrend(STABLE);
			//20% chance of going to steep boom
			else if (factor >= 80)
				city.setCurrentTrend(STEEP_BOOM);
			break;
		case STEEP_BOOM:
			//10% chance of stabilising
			//50% chance of going to slight boom
			if (factor >= 50)
				city.setCurrentTrend(SLIGHT_BOOM);
			else if (factor >= 90)
				city.setCurrentTrend(STABLE);
			break;
		}
		
		if (city.getName().equals("Cape Town"))
			System.out.println("Cape Town's economy is now " + city.getCurrentTrend());
		
		//Now that city trend has been adjusted, determine new prices
		switch(city.getCurrentTrend())
		{
		case STEEP_DECLINE: //All drugs lose 10% of their current value
			for (int i = Globals.WEED; i <= Globals.HEROIN; i++)
			{
				double curPrice = city.getPriceList().get(i);
				double newPrice = curPrice-(curPrice*0.1);
				city.getPriceList().put(i, (double)Math.round(newPrice * 10) / 10);
				if (i == Globals.WEED)
					city.getWeedPriceHistory().add(newPrice);				
			}
			break;
		case SLIGHT_DECLINE: //All drugs lose 5% of their current value
			for (int i = Globals.WEED; i <= Globals.HEROIN; i++)
			{
				double curPrice = city.getPriceList().get(i);
				double newPrice = curPrice-(curPrice*0.05);
				city.getPriceList().put(i, (double)Math.round(newPrice * 10) / 10);
				if (i == Globals.WEED)
					city.getWeedPriceHistory().add(newPrice);
			}
			break;
		case STABLE: 
			break;
			
		case SLIGHT_BOOM: //All drugs gain 5% of their current value
			for (int i = Globals.WEED; i <= Globals.HEROIN; i++)
			{
				double curPrice = city.getPriceList().get(i);
				double newPrice = curPrice+(curPrice*0.05);
				city.getPriceList().put(i, (double)Math.round(newPrice * 10) / 10);
				if (i == Globals.WEED)
					city.getWeedPriceHistory().add(newPrice);
			}
			break;
		case STEEP_BOOM: //All drugs gain 10% of their current value
			for (int i = Globals.WEED; i <= Globals.HEROIN; i++)
			{
				double curPrice = city.getPriceList().get(i);
				double newPrice = curPrice+(curPrice*0.1);
				city.getPriceList().put(i, (double)Math.round(newPrice * 10) / 10);
				if (i == Globals.WEED)
					city.getWeedPriceHistory().add(newPrice);
			}
			break;
		}
	}
	
	//produce random integer between 1 and N
	private int roll(int N)
	{
		return (int)(Math.random()*N +1);
	}
}
