package by.potato.Bot;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import by.potato.Bot.DB.DBHelper;
import by.potato.Bot.Entities.Event;
import by.potato.Bot.Holders.UserHolder;
import by.potato.Bot.Checker.CheckerNewMess;

public class MainBot extends TelegramLongPollingBot{
	
	public static DBHelper dbhelper = new DBHelper();
	public static Map<Long,Event> mMessCreate = new ConcurrentHashMap<>();
	public static Queue <Event> qMessFinish = new ConcurrentLinkedQueue<Event>();
	public static Map<Long,UserHolder> mUserHolder = new ConcurrentHashMap<>();
	public static Queue<SendMessage> qMess = new ConcurrentLinkedQueue<SendMessage>();
	public static Map<Long,SendMessage> mMessDuringCreation = new ConcurrentHashMap<>();
	public static ScheduledExecutorService ex = Executors.newScheduledThreadPool(1);
			
	private ExecutorService esNewMess = Executors.newCachedThreadPool();
	
	public static void main(String[] args) {
		
		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new MainBot());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}	
	}

	public MainBot() {	
		this.sender();
	}

	private void sender() {
		
		ex.scheduleAtFixedRate(()->{
			if(!qMess.isEmpty()) {
				try {
					sendMessage(qMess.poll());
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}
		}, 0, 33, TimeUnit.MILLISECONDS);
	}

	@Override
	public String getBotUsername() {
		return dbhelper.getNameBot();
	}
	
	@Override
	public String getBotToken() {
		return dbhelper.getToken();
	}

	@Override
	public void onUpdateReceived(Update update) {
		Message message = update.getMessage();
		if (message != null && message.hasText()) {
			esNewMess.submit(new CheckerNewMess(message));
		}
	}

}
