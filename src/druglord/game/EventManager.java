package druglord.game;

import java.util.ArrayList;

public class EventManager implements Runnable {
	private ArrayList<Event> eventsList;
	
	public EventManager()
	{
		eventsList = new ArrayList<Event>();
	}
	
	public int getNumEvents()
	{
		return eventsList.size();
	}
	
	public void processEvent(Event e)
	{
		eventsList.add(e);
		e.start();
	}

	@Override
	public void run() {
		while(true)
		{
			for (Event event: eventsList)
			{
				if (event.isDone())
				{
					eventsList.remove(event);
				}
			}
		}
		
	}
}
