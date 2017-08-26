package by.potato.Bot.Checker;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ChecherEventFromDB implements Runnable {

	@Override
	public void run() {
		
		System.err.println("From db");
		
		LocalDateTime time = LocalDateTime.now().plus(10, ChronoUnit.MINUTES);
		
	}

}
