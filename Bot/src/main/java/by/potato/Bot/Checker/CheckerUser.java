package by.potato.Bot.Checker;

import static by.potato.Bot.MainBot.mUserHolder;
import static by.potato.Bot.MainBot.dbhelper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map.Entry;

import by.potato.Bot.Holders.UserEventHolder;

public class CheckerUser implements Runnable {

	@Override
	public void run() {
		
		Instant inst = Instant.now().minus(1,ChronoUnit.HOURS);
		
		Iterator<Entry<Long, UserEventHolder>> it = mUserHolder.entrySet().iterator();
		while (it.hasNext()) {
		    Entry<Long, UserEventHolder> pair = it.next();
		    	
		    	UserEventHolder uh = pair.getValue();
		    	if(uh.getLastAppeal().isBefore(inst)) {//user not modife more 1 hour
		    		dbhelper.setClient(uh.getClient());
		    		it.remove(); //safe user to db and clear create message
		    	}
		}

	}

}
