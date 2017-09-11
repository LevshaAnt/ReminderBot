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

		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC)
								.plus(3, ChronoUnit.MINUTES);
		
		long timeInLong = utc.toEpochSecond();
		
		
		Queue<Event> eventMap = dbhelper.getEvent(timeInLong);
	
		eventMap.parallelStream()
				.filter(e -> !qEvent.containsKey(e.getUuid())) //ATTENTION DO NOT CONTAIN KEY
				.forEach(e -> qEvent.put(e.getUuid(), e));
		
		System.err.println("Колл вычитанных событий из БД = " + eventMap.size());
	}

}
