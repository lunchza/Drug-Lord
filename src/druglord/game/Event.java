package druglord.game;

public class Event {
	
	private int ID;
	private int duration;
	private boolean done;
	private Time startTime, endTime;
	
	public void start()
	{
		new Thread()
		{
			public void run()
			{
			done = false;
			doAction();	
				while (!startTime.equals(endTime))
				{
					startTime.increment();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						
					}
				}
				done = true;
			}
		}.start();
	}
	
	public boolean isDone()
	{
		return done;
	}
	
	public void doAction()
	{
		
	}
	
	private class Time
	{
		long timeStamp;
		
		public void increment()
		{
			timeStamp++;
		}
		
		public Time(GameTimer timer)
		{
			timeStamp = timer.getTimeStamp();
		}
		
		public boolean equals(Time t)
		{
			return timeStamp == t.timeStamp;
		}
		
		public long getTimeStamp()
		{
			return timeStamp;
		}
	}	
}
