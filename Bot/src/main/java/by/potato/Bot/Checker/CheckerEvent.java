package by.potato.Bot.Checker;

import static by.potato.Bot.MainBot.qMess;
import static by.potato.Bot.MainBot.qEvent;
import static by.potato.Bot.MainBot.dbhelper;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;



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
		
		System.err.println("Размер пула событий -> " + qEvent.size());
		
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
		long utcLong = utc.toEpochSecond();
		
		Iterator<Event> iter = qEvent.iterator();		
		while(iter.hasNext()) {
			Event e = iter.next();

			if(e.getNextTimeInLong() < utcLong) {
				e.updateNextEventTime();
				System.err.println("Время след события -> " +  e.getNextTime());
				qMess.add(getNewMess(e));
			}
		}
		
		utc.plus(1,ChronoUnit.MINUTES);
		utcLong = utc.toEpochSecond();
		
		iter = qEvent.iterator();
		while(iter.hasNext()) {
			Event e = iter.next();
			
			System.err.println("Check event set db");
			System.err.println("NextTimeInLong         " + e.getNextTimeInLong() );
			System.err.println("CurrentTime + 10 minut " + utcLong);
			if(e.getNextTimeInLong() > utcLong) {
				System.err.println("Перемещение в бд");
				dbhelper.setEvent(e);
				iter.remove();
			}
		}
	}

}
