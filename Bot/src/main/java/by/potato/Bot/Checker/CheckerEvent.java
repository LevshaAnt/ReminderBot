package by.potato.Bot.Checker;

import static by.potato.Bot.MainBot.qMess;
import static by.potato.Bot.MainBot.qEvent;
import static by.potato.Bot.MainBot.dbhelper;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map.Entry;

import org.telegram.telegrambots.api.methods.send.SendMessage;

import by.potato.Bot.Entities.Event;

public class CheckerEvent implements Runnable {

	private SendMessage getNewMess(Event e) {
		SendMessage sm = new SendMessage();
		sm.setChatId(e.getIdCreateUser());
		sm.enableMarkdown(true);
		sm.setText(e.getReminder());
		return sm;
	}
	
	@Override
	public void run() {
		
		ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);
		
		sendSelectedFinishedEvent(currentTime);
		saveFutureEventToDB(currentTime);

	}
	
	private void sendSelectedFinishedEvent(ZonedDateTime currentTime) {
		
		long utcLong = currentTime.toEpochSecond();
		
		Iterator<Event> iter = qEvent.values().iterator(); 
		
		while(iter.hasNext()) {
			Event e = iter.next();

			if(e.getNextTimeInLong() < utcLong) {
				e.updateNextEventTime();
				qMess.add(getNewMess(e));
			}
		}
	}
	
	private void saveFutureEventToDB (ZonedDateTime currentTime) {
		
		
		currentTime = currentTime.plus(3,ChronoUnit.MINUTES);
		long utcLong = currentTime.toEpochSecond();
		
		Iterator<Event> iter = qEvent.values().iterator();	
		
		while(iter.hasNext()) {
			Event e = iter.next();
			
			System.err.format("nextTime %d    currenTime+3min %d \n",e.getNextTimeInLong(),utcLong );

			
			if(e.getNextTimeInLong() > utcLong || e.getCountEvent() == 0) {
				System.err.println("Перемещение в бд");
				dbhelper.setEventorUpdateIfPresent(e);
				iter.remove();
			}
		}
	}

}
