package by.potato.Bot.Checker;

import org.telegram.telegrambots.api.objects.User;

import java.util.regex.*;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.methods.send.SendMessage;


import by.potato.Bot.Entities.Event;
import by.potato.Bot.Entities.Client;
import by.potato.Bot.Entities.Command;
import by.potato.Bot.Holders.UserEventHolder;
import by.potato.Bot.Entities.CommandButton;

import static by.potato.Bot.MainBot.qMess;
import static by.potato.Bot.MainBot.dbhelper;
import static by.potato.Bot.MainBot.mMessCreate;
import static by.potato.Bot.MainBot.mUserHolder;
import static by.potato.Bot.MainBot.qEvent;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;



public class CheckerNewMess implements Runnable {

	private enum Const {
		START_POSITION(0),
		MAX_POSITION_FOR_DESCRIPTION(53);
		
		private int pos;

		private Const(int pos) {
			this.pos = pos;
		}
		
		public int getPosition() {
			return this.pos;
		}
	}
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH.mm");
	private static final Pattern pattern = Pattern.compile("^[-+]{1}\\d{2}:\\d{2}");
	
	private String text;
	private Message mess;
	private Long chartID;
	private Integer messageID; 
	private UserEventHolder userHolder;
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
			
			this.userHolder = new UserEventHolder(client);
			
			mUserHolder.putIfAbsent(this.chartID, this.userHolder);
		}
	}
	
	private void getEvent() {
		if(mMessCreate.containsKey(this.chartID)) {
			this.event = mMessCreate.get(this.chartID);
		} else {
			this.event = new Event(this.chartID,this.userHolder.getClient().getOffset());
			mMessCreate.put(this.chartID, this.event);//create new event to map
		}
	}
	
	private void updateConcurrentStructures() {
		mUserHolder.replace(this.chartID, this.userHolder);
		mMessCreate.replace(this.chartID, this.event);
	}
	
	private void migrationEvent() {
		mMessCreate.remove(this.chartID);
		this.event.updateNextEventTime();		
		qEvent.put(this.event.getUuid(), this.event);
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
				if(this.text.length()>Const.MAX_POSITION_FOR_DESCRIPTION.getPosition()) {
					this.text = this.text.substring(Const.START_POSITION.getPosition(),Const.MAX_POSITION_FOR_DESCRIPTION.getPosition()); 
				}
						
				this.event.setTextDescription(this.text);
				this.userHolder.setError(false);
			break;
			
		case EVENT_TEXT:
				this.event.setTextEvent(this.text);
				this.userHolder.setError(false);
			 break;
			
		case IDEA:
				dbhelper.setIdea(this.text,this.chartID);
			break;
			
		case EVENT_COUNT_DELETE:	
			try {
				long index = Long.parseLong(text);
				if(index > 0) {
					this.userHolder.setError(false);
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				this.userHolder.setErrorMess(Command.ERROR_EVENT_COUNT.getText());
				this.userHolder.setError(true);
			}
			break;
			
		case EVENT_DATE:	
			try {		
				this.text = this.text.replaceAll(",", ".");
				this.text = this.text.replaceAll(":", ".");
				
				LocalDateTime ldt = LocalDateTime.parse(this.text, formatter);				
				ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.ofOffset("GMT",this.userHolder.getClient().getOffset()));
				this.event.setClientOffset(this.userHolder.getClient().getOffset());
				this.event.setBeginTime(zdt);
				this.userHolder.setError(false);
			
			} catch (DateTimeParseException e) {
				this.userHolder.setErrorMess(Command.ERROR_EVENT_DATE.getText());
				this.userHolder.setError(true);
			}
			break;
		
		case EVENT_COUNT:
		case EVENT_COUNT_ALARM:
			try {
				long count = Long.parseLong(text);
				if(count > 0) {
					if(this.userHolder.isFlagEvent()) {
						this.event.setCountEvent(count);
					} else {
						this.event.setCountAlarm(count);
					}
				
					this.userHolder.setError(false);
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				this.userHolder.setErrorMess(Command.ERROR_EVENT_COUNT.getText());
				this.userHolder.setError(true);
			}
			break;
			
		case EVENT_COUNT_OFFSET:
			try {
				long count = Long.parseLong(text);
				if(count > 0) {
					this.event.setCountOffsetEvent(count);		
					this.userHolder.setError(false);
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				this.userHolder.setErrorMess(Command.ERROR_EVENT_COUNT.getText());
				this.userHolder.setError(true);
			}
			break;
			
		case EVENT_COUNT_ALARM_OFFSET:
			try {
				long count = Long.parseLong(text);
				if(count > 0) {
					this.event.setCountOffsetAlart(count);		
					this.userHolder.setError(false);
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				this.userHolder.setErrorMess(Command.ERROR_EVENT_COUNT.getText());
				this.userHolder.setError(true);
			}
			break;
			
		case SETTING_INFO:
			Matcher m = pattern.matcher(text);
			if(m.find()) {
				this.userHolder.getClient().setOffset(text);
			} else {
				this.userHolder.setErrorMess(Command.REPEAT.getText());
				this.userHolder.setError(true);
			}
			break;
			
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
				
			case EVENT_FUTURE:
				mess.setText(dbhelper.getEvents(true, this.chartID));
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.EVENT_FUTURE));
				this.userHolder.setNeedTextInp(false);
				break;
				
			case EVENT_PAST:
				mess.setText(dbhelper.getEvents(false, this.chartID));
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.EVENT_PAST));
				this.userHolder.setNeedTextInp(false);
				break;
				
			case EVENT_DELETE:
				mess.setText(Command.EVENT_COUNT_DELETE.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));	
				this.userHolder.setDataType(Command.EVENT_COUNT_DELETE);
				this.userHolder.setNeedTextInp(true);
				this.event.setDirectionFlag(false);
				break;
			
			case EVENT_DELETE_ALL:
				mess.setText(Command.EVENT_DELETE_INFO.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.EVENT_PAST));		
				dbhelper.deleteEvents();
				this.userHolder.setNeedTextInp(false);
				break;
				
			case EVENT_NEW:
				mess.setText(Command.EVENT_DESCRIPTION.getText());	
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
				this.userHolder.setDataType(Command.EVENT_DESCRIPTION);
				this.userHolder.setNeedTextInp(true);
				break;
				
			case EVENT_TEXT:
				mess.setText(Command.EVENT_TEXT.getText());	
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
				this.userHolder.setDataType(Command.EVENT_TEXT);
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
				
			case EVENT_COUNT_OFFSET:
				mess.setText(Command.EVENT_COUNT_OFFSET.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
				this.userHolder.setDataType(Command.EVENT_COUNT_OFFSET);
				this.userHolder.setNeedTextInp(true);
				break;
				
			case EVENT_PERIOD:	
				mess.setText(Command.EVENT_PERIOD.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.EVENT_PERIOD));
				this.userHolder.setNeedTextInp(false);
				break;
								
			case EVENT_COUNT_ALARM:
				mess.setText(Command.EVENT_COUNT_ALARM.getText());
				this.userHolder.setDataType(Command.EVENT_COUNT_ALARM);
				this.userHolder.setNeedTextInp(true);
				break;	
				
			case EVENT_COUNT_ALARM_OFFSET:
				mess.setText(Command.EVENT_COUNT_ALARM_OFFSET.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
				this.userHolder.setDataType(Command.EVENT_COUNT_ALARM_OFFSET);
				this.userHolder.setNeedTextInp(true);
				break;	
				
			case EVENT_PERIOD_ALARM:
				mess.setText(Command.EVENT_PERIOD_ALARM.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.EVENT_PERIOD));
				this.userHolder.setNeedTextInp(false);
				break;
				
			case MINUTE:				
				if(this.userHolder.isFlagEvent()) {
					this.event.setOffsetEvent(ChronoUnit.MINUTES);
					this.text = Command.EVENT_COUNT_ALARM.getText();
					this.userHolder.setFlagEvent(false);
					
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
					mess.setText(Command.COMPLITE.getText());
					
					repeat = true;
					
				} else {
					this.event.setOffsetAlarm(ChronoUnit.MINUTES);
					mess.setText(Command.FINISH.getText());
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
					this.userHolder.setNeedTextInp(false);
					this.userHolder.setFlagEvent(true);
				}
				
				break;
			
			case HOUR:
				if(this.userHolder.isFlagEvent()) {
					this.event.setOffsetEvent(ChronoUnit.HOURS);
					this.text = Command.EVENT_COUNT_ALARM.getText();
					this.userHolder.setFlagEvent(false);
					
					repeat = true;
					
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
					mess.setText(Command.COMPLITE.getText());
					
				} else {
					this.event.setOffsetAlarm(ChronoUnit.HOURS);
					mess.setText(Command.FINISH.getText());
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
					this.userHolder.setNeedTextInp(false);
					this.userHolder.setFlagEvent(true);
				}

				break;
			
			case DAY:
				if(this.userHolder.isFlagEvent()) {
					this.event.setOffsetEvent(ChronoUnit.DAYS);
					this.text = Command.EVENT_COUNT_ALARM.getText();
					this.userHolder.setFlagEvent(false);
					repeat = true;
					
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
					mess.setText(Command.COMPLITE.getText());
					
				} else {
					this.event.setOffsetAlarm(ChronoUnit.DAYS);
					mess.setText(Command.FINISH.getText());
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
					this.userHolder.setNeedTextInp(false);
					this.userHolder.setFlagEvent(true);
				}

				break;
				
			case WEEK:
				if(this.userHolder.isFlagEvent()) {
					this.event.setOffsetEvent(ChronoUnit.WEEKS);
					this.text = Command.EVENT_COUNT_ALARM.getText();
					this.userHolder.setFlagEvent(false);
					repeat = true;

					mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
					mess.setText(Command.COMPLITE.getText());
					
				} else {
					this.event.setOffsetAlarm(ChronoUnit.WEEKS);
					mess.setText(Command.FINISH.getText());
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
					this.userHolder.setNeedTextInp(false);
					this.userHolder.setFlagEvent(true);
				}
				
				break;
				
			case MONTH:
				if(this.userHolder.isFlagEvent()) {
					this.event.setOffsetEvent(ChronoUnit.MONTHS);
					this.text = Command.EVENT_COUNT_ALARM.getText();
					this.userHolder.setFlagEvent(false);
					repeat = true;
					
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
					mess.setText(Command.COMPLITE.getText());
					
				} else {
					this.event.setOffsetAlarm(ChronoUnit.MONTHS);
					mess.setText(Command.FINISH.getText());
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
					this.userHolder.setNeedTextInp(false);
					this.userHolder.setFlagEvent(true);
				}
				
				break;
				
			case YEAR:			
				if(this.userHolder.isFlagEvent()) {
					this.event.setOffsetEvent(ChronoUnit.YEARS);
					this.text = Command.EVENT_COUNT_ALARM.getText();
					this.userHolder.setFlagEvent(false);
					
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
					mess.setText(Command.COMPLITE.getText());
					
					repeat = true;
				} else {
					this.event.setOffsetAlarm(ChronoUnit.YEARS);
					mess.setText(Command.FINISH.getText());
					mess.setReplyMarkup(CommandButton.getKeyboard(Command.FINISH));
					this.userHolder.setNeedTextInp(false);
					this.userHolder.setFlagEvent(true);
				}
				
				break;
				
			case FINISH:
				mess.setText(Command.EVENT_FINISH.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.START));
				this.userHolder.setNeedTextInp(false);
				migrationEvent();
				System.out.println(event.getInfo());
				break;
			
			case SETTING:
				mess.setText(Command.SETTING_INFO.getText());
				this.userHolder.setDataType(Command.SETTING_INFO);
				this.userHolder.setNeedTextInp(true);
				break;	
				
			case IDEA:
				mess.setText(Command.IDEA_INFO.getText());
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.HIDE_BUTTON));
				this.userHolder.setDataType(Command.IDEA);
				this.userHolder.setNeedTextInp(true);
			break;
			
			case ACTIVATORS:
				mess.setText("Функционал в разработке");
				mess.setReplyMarkup(CommandButton.getKeyboard(Command.START));
				this.userHolder.setNeedTextInp(false);
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
							this.text =Command.EVENT_TEXT.getText();
							mess.setText(Command.COMPLITE.getText());
							mess.setReplyToMessageId(this.messageID);
						} 
						break;
						
					case EVENT_TEXT:
						if(!this.userHolder.isError()) {//not problem in Input
							this.text =Command.EVENT_DATE.getText();
							mess.setText(Command.COMPLITE.getText());
							mess.setReplyToMessageId(this.messageID);
						} 
						break;	
						
					case IDEA:
						if(!this.userHolder.isError()) {//not problem in Input
							this.text =Command.START.getText();
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
						
					case EVENT_COUNT_DELETE:
						if(!this.userHolder.isError()) {//not problem in Input
							dbhelper.deleteEvent(true, this.chartID,Integer.parseInt(text));
							this.text =Command.EVENT_FUTURE.getText();
							mess.setText(Command.COMPLITE.getText());
							mess.setReplyToMessageId(this.messageID);
						}
						break;
						
					case EVENT_COUNT:
						if(!this.userHolder.isError()) {//not problem in Input
							this.text =Command.EVENT_COUNT_OFFSET.getText();
							mess.setText(Command.COMPLITE.getText());
							mess.setReplyToMessageId(this.messageID);
						}
						break;
						
					
					case EVENT_COUNT_ALARM:
						if(!this.userHolder.isError()) {//not problem in Input
							this.text =Command.EVENT_COUNT_ALARM_OFFSET .getText();
							mess.setText(Command.COMPLITE.getText());
							mess.setReplyToMessageId(this.messageID);
						}
						break;
						
					case EVENT_COUNT_OFFSET:
						if(!this.userHolder.isError()) {//not problem in Input
							this.text =Command.EVENT_PERIOD.getText();
							mess.setText(Command.COMPLITE.getText());
							mess.setReplyToMessageId(this.messageID);
						}
						break;
						
					case EVENT_COUNT_ALARM_OFFSET:
						if(!this.userHolder.isError()) {//not problem in Input
							this.text =Command.EVENT_PERIOD_ALARM.getText();
							mess.setText(Command.EVENT_DELETE_INFO.getText());
							mess.setReplyToMessageId(this.messageID);
						}
						break;
						
					case SETTING_INFO:
						if(!this.userHolder.isError()) {//not problem in Input
							this.text =Command.START.getText();
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
