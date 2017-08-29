package by.potato.Bot.Checker;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Queue;

import by.potato.Bot.Entities.Event;

import static by.potato.Bot.MainBot.qEvent;
import static by.potato.Bot.MainBot.dbhelper;


public class ChecherEventFromDB implements Runnable {

	@Override
	public void run() {
		
		System.err.println("From db");
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC)
								.plus(10, ChronoUnit.MINUTES);
		
		long timeInLong = utc.toEpochSecond();
		
	
		Queue<Event> eventMap = dbhelper.getEvent(timeInLong);
		System.err.println("Size from DB --> " + eventMap.size());
		
		qEvent.addAll(eventMap);
	}

}
