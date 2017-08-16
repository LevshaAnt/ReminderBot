package by.potato.Bot.Checker;

import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import by.potato.Bot.Entities.Event;
import by.potato.Bot.Entities.Client;
import by.potato.Bot.Entities.Command;
import by.potato.Bot.Entities.DataType;
import by.potato.Bot.Holders.UserHolder;
import by.potato.Bot.Entities.CommandButton;

import static by.potato.Bot.MainBot.qMess;
import static by.potato.Bot.MainBot.dbhelper;
import static by.potato.Bot.MainBot.mMessCreate;
import static by.potato.Bot.MainBot.mUserHolder;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class CheckerNewMess implements Runnable {

	private String text;
	private Message mess;
	private Long chartID;
	private UserHolder userHolder;
	private List<SendMessage> lMess;
	private Event event;
	
	
	public CheckerNewMess(Message mess) {
		this.mess = mess;
		this.chartID = mess.getChatId();
		this.text = mess.getText();
		this.lMess = new ArrayList<SendMessage>();	
		
		getUser();
		getEvent();
	}
	
	private void getUser() {
		if(mUserHolder.containsKey(this.chartID)) {
			this.userHolder = mUserHolder.get(this.chartID);
		} else {
			Optional<User> optUser = Optional.ofNullable(this.mess.getFrom());
			String name = "";
			String surname = "";
			
			if(optUser.isPresent()) {
				Optional<String> optName =  Optional.ofNullable(optUser.get().getFirstName());
				name = optName.orElse("not name");
				
				Optional<String> optSubname = Optional.ofNullable(optUser.get().getLastName());
				surname = optSubname.orElse("not surname");
			}
			
			Client client = dbhelper.getClient(this.chartID, name, surname);
			
			this.userHolder = new UserHolder(client);
			
			mUserHolder.putIfAbsent(this.chartID, this.userHolder);
		}
	}
	
	private void getEvent() {
		if(mMessCreate.containsKey(this.chartID)) {
			this.event = mMessCreate.get(this.chartID);
		} else {
			this.event = new Event(this.chartID);
		}
	}
	
	private void updateConcurrentStructures() {
		mUserHolder.replace(this.chartID, this.userHolder);
		mMessCreate.replace(this.chartID, this.event);
	}
	
	private SendMessage getNewMess() {
		SendMessage sm = new SendMessage();
		sm.setChatId(this.chartID);
		sm.enableMarkdown(true);
		return sm;
	}

//	private boolean
	@Override
	public void run() {
	
		SendMessage mess = getNewMess();
		this.userHolder.updateLastAppeal();
		System.out.println(this.text);
		
		boolean repeat = true;
		while(repeat) {
			switch (Command.parse(text)) {	
			
			case START:
			case CANCEL:
					mess.setText(DataType.START_GENERAL_MENU.getText());
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.START.getId()));
					repeat = false;
				break;
			case EVENT:
					mess.setText(DataType.EVENT_TYPE.getText());
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.EVENT.getId()));
					repeat = false;
				break;
			case EVENT_NEW:
					mess.setText(DataType.WAIT_EVENT_DESCRIPTION.getText());
					this.userHolder.setDataType(DataType.WAIT_EVENT_DESCRIPTION);
					this.userHolder.setNeedTextInp(true);
					repeat = false;
				break;
				
			default:
				
				if(this.userHolder.isNeedTextInp()) {
					switch (this.userHolder.getDataType()) {
					case WAIT_EVENT_DESCRIPTION:
							
						break;

					default:
						break;
					}
				} else {		
					mess.setText("Повторите ввод");
				}
				break;
			}
		}
		
		this.lMess.add(mess);
		
		for(SendMessage element: this.lMess) { 
			qMess.add(element);//FIFO
		}
		
		
		updateConcurrentStructures();
	}

}
