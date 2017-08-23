package by.potato.Bot.Entities;

import java.util.List;
import java.util.ArrayList;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

public class CommandButton {

	private static KeyboardRow getKeyboardRow(String str) {
		KeyboardRow kr = new KeyboardRow();
		kr.add(str);
		return kr;
	}
	
	public static ReplyKeyboard getKeyboard(Command comm) {	
			
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(true);	
		
		List<KeyboardRow> keyboardList = new ArrayList<KeyboardRow>();
		KeyboardRow keyboardColum = new KeyboardRow();
		
		switch (comm) {
		
		case START:
		    keyboardList.add( getKeyboardRow(Command.EVENT.getText()));
		    keyboardList.add( getKeyboardRow(Command.ACTIVATORS.getText()));
		    keyboardList.add( getKeyboardRow(Command.IDEA.getText()));
		    keyboardList.add( getKeyboardRow(Command.SETTING.getText()));
		    
		break;
  
		case EVENT:
			keyboardList.add( getKeyboardRow(Command.EVENT_NEW.getText()));
			keyboardList.add( getKeyboardRow(Command.EVENT_FUTURE.getText()));
			keyboardList.add( getKeyboardRow(Command.EVENT_PAST.getText()));
			keyboardList.add( getKeyboardRow(Command.MENU_GENERAL_RETURN.getText()));
		break;
		
		case EVENT_TYPE:
			keyboardList.add( getKeyboardRow(Command.EVENT_BEFORE.getText()));
			keyboardList.add( getKeyboardRow(Command.EVENT_AFTER.getText()));
		break;
			
		case EVENT_SELECT_TYPE_PERIOD:
			keyboardColum.addAll( getKeyboardRow(Command.MINUTE.getText()));
			keyboardColum.addAll( getKeyboardRow(Command.HOUR.getText()));
			keyboardColum.addAll( getKeyboardRow(Command.DAY.getText()));
			keyboardList.add(keyboardColum);
		break;
	
		case FINISH:
			keyboardList.add( getKeyboardRow(Command.FINISH.getText()));
			keyboardList.add( getKeyboardRow(Command.MENU_GENERAL_RETURN.getText()));
		break;
		
	/*	
		case 200:
			keyboardList.add( getKeyboardRow(Command.ACTIVATORS_NEW.getText()));
			keyboardList.add( getKeyboardRow(Command.ACTIVATORS_GROUP.getText()));
			keyboardList.add( getKeyboardRow(Command.MENU_GENERAL_RETURN.getText()));
		break;
		*/
		case HIDE_BUTTON:
			return new ReplyKeyboardRemove();//remove button
		default:
			break;
		}

		replyKeyboardMarkup.setKeyboard(keyboardList);
		return replyKeyboardMarkup;
	}
	
	
}
