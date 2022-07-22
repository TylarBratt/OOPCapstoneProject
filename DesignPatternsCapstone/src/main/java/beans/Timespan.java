package beans;

public class Timespan {
	int days;
	int hours;
	int minutes;
	int seconds;
	public Timespan(long timeInMilliseconds){
		this.days = (int)(timeInMilliseconds / (1000 * 60 * 60 * 24)) % 365;
		this.hours = (int)(timeInMilliseconds / (1000 * 60 * 60)) % 24;
		this.minutes = (int)(timeInMilliseconds / (1000 * 60))% 60;
		this.seconds = (int)(timeInMilliseconds / 1000) % 60;
	}
	
	@Override
	public String toString() {
		//Only return the two most significant items
		if (days != 0)
			return days+"d "+hours+"h";
		else if (hours != 0)
			return hours+"h "+minutes+"m";
		else if (minutes != 0)
			return minutes+"m "+seconds+"s";
		else
			return seconds+"s";
	}
}
