package by.potato.Bot.Checker;

import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import by.potato.Bot.Entities.Event;
import by.potato.Bot.Entities.Client;
import by.potato.Bot.Entities.Command;
import by.potato.Bot.Holders.UserHolder;
import by.potato.Bot.Entities.CommandButton;

import static by.potato.Bot.MainBot.qMess;
import static by.potato.Bot.MainBot.dbhelper;
import static by.potato.Bot.MainBot.mMessCreate;
import static by.potato.Bot.MainBot.mUserHolder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class CheckerNewMess implements Runnable {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH.mm");
			
	private String text;
	private Message mess;
	private Long chartID;
	private Integer messageID; 
	private UserHolder userHolder;
	private List<SendMessage> lMess;
	private Event event;
	
	public CheckerNewMess(Message mess) {
		this.mess = mess;
		this.chartID = mess.getChatId();
		this.messageID= mess.getMessageId();
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

	private void CheckerInput(Command dataType) {
		
		switch (dataType) {
		case EVENT_DESCRIPTION:
				this.event.setTextEvent(this.text);
				this.userHolder.setError(false);
			break;
	
		case EVENT_DATE:	
			try {
				this.event.setBeginTime(LocalDateTime.parse(this.text, formatter));
				this.userHolder.setError(false);
			} catch (DateTimeParseException e) {
				System.err.println(e.getMessage());
				System.err.println(e.getCause());
				this.userHolder.setErrorMess(Command.ERROR_EVENT_DATE.getText());
				this.userHolder.setError(true);
			}
			break;
		
		case EVENT_COUNT:
			try {
				long count = Long.parseLong(text);
				if(count > 1) {
					 this.event.setCountEvent(count);
					 this.userHolder.setError(false);
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				this.userHolder.setErrorMess(Command.ERROR_EVENT_COUNT.getText());
				this.userHolder.setError(true);
			}
			break;
		case EVENT_COUNT_ALARM:
			try {
				long count = Long.parseLong(text);
				if(count > 1) {
					 this.event.setCountAlarm(count);
					 this.userHolder.setError(false);
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				this.userHolder.setErrorMess(Command.ERROR_EVENT_COUNT.getText());
				this.userHolder.setError(true);
			}
			break;
			
	/*	
		case EVENT_PERIOD:	
			try {
				long count = Long.parseLong(text);
				if(count > 1) {
					this.event.setOffset(count);
					this.userHolder.setError(false);
				} else {
					throw new NumberFormatException();
				}
				
			} catch (NumberFormatException e) {
				this.userHolder.setErrorMess(Command.REPEAT.getText());
				this.userHolder.setError(true);
			}	
			break;	
		*/	
		default:
			break;
		}
	}

	@Override
	public void run() {
	
		
		this.userHolder.updateLastAppeal();
		System.out.println(this.text);
		
		boolean repeat = true;
		while(repeat) {
			
			SendMessage mess = getNewMess();
			
			repeat = false;
			switch (Command.parse(text)) {	

			case START:
			case CANCEL:
			case MENU_GENERAL_RETURN:
				mess.setText(Command.START_GENERAL_MENU.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.START));
				this.userHolder.setNeedTextInp(false);
				break;
				
			case EVENT:
				mess.setText(Command.EVENT_INFO.getText());					
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.EVENT));
				this.userHolder.setNeedTextInp(false);
				break;
				
			case EVENT_NEW:
				mess.setText(Command.EVENT_DESCRIPTION.getText());	
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
				this.userHolder.setDataType(Command.EVENT_DESCRIPTION);
				this.userHolder.setNeedTextInp(true);
				break;
				
			case EVENT_DATE:
				mess.setText(Command.EVENT_DATE.getText());
				this.userHolder.setDataType(Command.EVENT_DATE);
				this.userHolder.setNeedTextInp(true);
				break;
				
			case EVENT_TYPE:
				mess.setText(Command.EVENT_TYPE.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.EVENT_TYPE));
				this.userHolder.setNeedTextInp(false);
				break;
				
			case EVENT_BEFORE:
				mess.setText(Command.EVENT_COUNT.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
				this.event.setDirectionFlag(false);
				this.userHolder.setDataType(Command.EVENT_COUNT);
				this.userHolder.setNeedTextInp(true);
				break;
				
			case EVENT_AFTER:
				mess.setText(Command.EVENT_COUNT.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
				this.event.setDirectionFlag(true);
				this.userHolder.setDataType(Command.EVENT_COUNT);
				this.userHolder.setNeedTextInp(true);
				break;
				
			case EVENT_COUNT_ALARM:
				mess.setText(Command.EVENT_COUNT_ALARM.getText());
				this.userHolder.setDataType(Command.EVENT_COUNT_ALARM);
				this.userHolder.setNeedTextInp(true);
				break;
				
			case EVENT_SELECT_PERIOD:
				mess.setText(Command.EVENT_SELECT_PERIOD.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.EVENT_SELECT_PERIOD));
				this.userHolder.setNeedTextInp(false);
				break;
				
			case MINUTE:
				mess.setText(Command.FINISH.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
				this.userHolder.setNeedTextInp(false);
				this.event.setOffsetPeriod(ChronoUnit.MINUTES);
				break;
			
			case HOUR:
				mess.setText(Command.FINISH.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
				this.userHolder.setNeedTextInp(false);
				this.event.setOffsetPeriod(ChronoUnit.HOURS);
				break;
			
			case DAY:
				mess.setText(Command.FINISH.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
				this.userHolder.setNeedTextInp(false);
				this.event.setOffsetPeriod(ChronoUnit.DAYS);
				break;
				
			case WEEK:
				mess.setText(Command.FINISH.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
				this.userHolder.setNeedTextInp(false);
				this.event.setOffsetPeriod(ChronoUnit.WEEKS);
				break;
				
			case MONTH:
				mess.setText(Command.FINISH.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
				this.userHolder.setNeedTextInp(false);
				this.event.setOffsetPeriod(ChronoUnit.MONTHS);
				break;
				
			case YEAR:
				mess.setText(Command.FINISH.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
				this.userHolder.setNeedTextInp(false);
				this.event.setOffsetPeriod(ChronoUnit.YEARS);
				break;
			
				
			default:
				
				if(this.userHolder.isNeedTextInp()) {
					
					CheckerInput(this.userHolder.getDataType());
					repeat = true;
					
					if(this.userHolder.isError()) {
						mess.setText(Command.ERROR_INPUT.getText() + this.userHolder.getErrorMess());
						repeat = false;
					}
					
					switch (this.userHolder.getDataType()) {
					case EVENT_DESCRIPTION:
						if(!this.userHolder.isError()) {//not problem in Input
							this.text =Command.EVENT_DATE.getText();
							mess.setText(Command.COMPLITE.getText());
							mess.setReplyToMessageId(this.messageID);
						} 
						break;
					case EVENT_DATE:
						if(!this.userHolder.isError()) {//not problem in Input
							this.text =Command.EVENT_TYPE.getText();
							mess.setText(Command.COMPLITE.getText());
							mess.setReplyToMessageId(this.messageID);
						}
						break;
						
					case EVENT_COUNT:
						if(!this.userHolder.isError()) {//not problem in Input
							this.event.setTextEvent(this.text);
							this.text =Command.EVENT_COUNT_ALARM.getText();
							mess.setText(Command.COMPLITE.getText());
							mess.setReplyToMessageId(this.messageID);
						}
						break;
					
					case EVENT_COUNT_ALARM:
						if(!this.userHolder.isError()) {//not problem in Input
							this.event.setTextEvent(this.text);
							this.text =Command.EVENT_SELECT_PERIOD.getText();
							mess.setText(Command.COMPLITE.getText());
							mess.setReplyToMessageId(this.messageID);
						}
						break;
						
					default:
						break;	
				
					}	
				}  else {		
					mess.setText(Command.REPEAT.getText());
					repeat = false;
				}
				break;
			}
				this.lMess.add(mess);
				
		}	
				
		qMess.addAll(this.lMess);
		
		updateConcurrentStructures();
	}

}
