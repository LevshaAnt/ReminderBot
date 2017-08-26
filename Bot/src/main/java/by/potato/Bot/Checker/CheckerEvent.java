package by.potato.Bot.Checker;

import static by.potato.Bot.MainBot.qMess;
import static by.potato.Bot.MainBot.qEvent;
import static by.potato.Bot.MainBot.dbhelper;

import java.time.LocalDateTime;
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
		LocalDateTime current = LocalDateTime.now();
		
		Iterator<Event> iter = qEvent.iterator();		
		while(iter.hasNext()) {
			Event e = iter.next();
	
			if(e.getNextTime().isBefore(current)) {
				e.updateNextEventTime();
				System.err.println("Время след события -> " +  e.getNextTime());
				qMess.add(getNewMess(e));
			}
		}
		
		current.plus(10,ChronoUnit.MINUTES);
		iter = qEvent.iterator();
		while(iter.hasNext()) {
			Event e = iter.next();
			
			if(e.getNextTime().isBefore(current)) {
				System.err.println("Перемещение в бд");
				dbhelper.setEvent(e);
				iter.remove();
			}
		}
	}

}