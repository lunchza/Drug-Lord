package druglord.game;

public class GameTimer implements Runnable{
	
	private int second;
	private int minute;
	private int hour;
	private int day_date;
	private int day;
	private int month;
	private int year;
	
	private int week_counter = 0;
	
	private static final int MONDAY = 0;
	private static final int TUESDAY = 1;
	private static final int WEDNESDAY = 2;
	private static final int THURSDAY = 3;
	private static final int FRIDAY = 4;
	private static final int SATURDAY = 5;
	private static final int SUNDAY = 6;
	
	private static final int JANUARY = 0;
	private static final int FEBRUARY = 1;
	private static final int MARCH = 2;
	private static final int APRIL = 3;
	private static final int MAY = 4;
	private static final int JUNE = 5;
	private static final int JULY = 6;
	private static final int AUGUST = 7;
	private static final int SEPTEMBER = 8;
	private static final int OCTOBER = 9;
	private static final int NOVEMBER = 10;
	private static final int DECEMBER = 11;
	
	public static final double NORMAL_SPEED = 1;
	public static final double DOUBLE_SPEED = 0.5;
	public static final double QUAD_SPEED = 0.25;
	public static final double INSANE_SPEED = 0.01;
	
	//City prices need to be updated
	private boolean due_for_update = false;
	
	private double current_speed;
	
	private boolean paused;
	
	public GameTimer()
	{
		current_speed = NORMAL_SPEED;
		
		//initial time set to noon on tuesday 1 January 2013
		second = 0;
		minute = 0;
		hour = 12;
			
		day = TUESDAY;
		day_date = 1;
		month = JANUARY;
		year = 2013;
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public void setPaused(boolean p)
	{
		paused = p;
	}
	
	public void setSpeed(double new_speed)
	{
		if (paused)
			paused = false;
		
		current_speed = new_speed;
	}
	
	public boolean isDueForUpdate()
	{
		return due_for_update;
	}
	
	public void removeUpdateFlag()
	{
		due_for_update = false;
	}
	
	private void tick()
	{
		second++;
		if (second == 60)
		{
			second = 0;
			minute++;
			if(minute % 5 == 0)
				due_for_update = true; //prices update every 5 minutes
			if (minute == 60)
			{
				minute = 0;
				hour++;

				if (hour == 24)
				{
					hour = 0;
					day++;
					day_date++;
					if (day == SUNDAY+1)
					{
						day = MONDAY;
						week_counter++;
						if (week_counter == 3)
						{
							month++;
							week_counter = 0;
							day_date = 0;
							if (month == DECEMBER+1)
							{
								month = JANUARY;
								year++;
							}
						}
					}
				}
			}
		}
	}
	
	public String getDateInfo()
	{
		StringBuffer s = new StringBuffer();
		switch(day)
		{
		case MONDAY:
			s.append("Mon");
			break;
		case TUESDAY:
			s.append("Tue");
			break;
		case WEDNESDAY:
			s.append("Wed");
			break;
		case THURSDAY:
			s.append("Thu");
			break;
		case FRIDAY:
			s.append("Fri");
			break;
		case SATURDAY:
			s.append("Sat");
			break;
		case SUNDAY:
			s.append("Sun");
			break;			
		}
		s.append(" ");
		
		switch(month)
		{
		case JANUARY:
			s.append("January");
			break;
		case FEBRUARY:
			s.append("February");
			break;
		case MARCH:
			s.append("March");
			break;
		case APRIL:
			s.append("April");
			break;
		case MAY:
			s.append("May");
			break;
		case JUNE:
			s.append("June");
			break;
		case JULY:
			s.append("July");
			break;
		case AUGUST:
			s.append("August");
			break;
		case SEPTEMBER:
			s.append("September");
			break;
		case OCTOBER:
			s.append("October");
			break;
		case NOVEMBER:
			s.append("November");
			break;
		case DECEMBER:
			s.append("December");
			break;
		}
		
		s.append(" ");
		
		if (day_date < 10)
			s.append("0" + day_date);
		else
			s.append(day_date);
		
		s.append(" " + year + "\n");
		
		return s.toString();
	}
	
	public double getSpeed()
	{
		return current_speed;
	}
	
	public String getTimeInfo()
	{
		StringBuilder s = new StringBuilder();
		if (hour < 10)
			s.append("0"+hour);
		else
			s.append(hour);
		s.append(":");
		
		if (minute < 10)
			s.append("0"+minute);
		else
			s.append(minute);
		s.append(":");
		
		if (second < 10)
			s.append("0"+second);
		else
			s.append(second);
		
		return s.toString();
	}
	
	/*
	public String toString()
	{
		
	}*/

	@Override
	public void run() {

		while (true)
		{
			if (!paused)
			{
				tick();
				try {
					Thread.sleep((long)(1000*current_speed));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public long getTimeStamp()
	{
		String representation = "";
		
		representation.concat(""+year);
		representation.concat(""+month);
		representation.concat(""+day);
		representation.concat(""+hour);
		representation.concat(""+minute);
		representation.concat(""+second);
		
		return Long.parseLong(representation);
	}
}
