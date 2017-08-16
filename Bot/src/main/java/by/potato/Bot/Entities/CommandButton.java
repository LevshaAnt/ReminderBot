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
	
	public static ReplyKeyboard getKeyboard(int id) {	
			
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(true);	
		
		List<KeyboardRow> keyboardList = new ArrayList<KeyboardRow>();
		KeyboardRow keyboardColum = new KeyboardRow();
		
		switch (id) {
		
		case 0:
		    keyboardList.add( getKeyboardRow(Command.EVENT.getText()));
		    keyboardList.add( getKeyboardRow(Command.ACTIVATORS.getText()));
		break;
  
		case 100:
			keyboardList.add( getKeyboardRow(Command.EVENT_NEW.getText()));
			keyboardList.add( getKeyboardRow(Command.EVENT_FUTURE.getText()));
			keyboardList.add( getKeyboardRow(Command.EVENT_PAST.getText()));
			keyboardList.add( getKeyboardRow(Command.MENU_GENERAL_RETURN.getText()));
		break;
		case 101:
			keyboardColum.addAll( getKeyboardRow(Command.MINUTES.getText()));
			keyboardColum.addAll( getKeyboardRow(Command.HOURS.getText()));
			keyboardColum.addAll( getKeyboardRow(Command.DAYS.getText()));
			keyboardList.add(keyboardColum);
		break;
		
		case 102:
			keyboardList.add( getKeyboardRow(Command.FINISH.getText()));
			keyboardList.add( getKeyboardRow(Command.MENU_GENERAL_RETURN.getText()));
		break;
		
		case 200:
			keyboardList.add( getKeyboardRow(Command.ACTIVATORS_NEW.getText()));
			keyboardList.add( getKeyboardRow(Command.ACTIVATORS_GROUP.getText()));
			keyboardList.add( getKeyboardRow(Command.MENU_GENERAL_RETURN.getText()));
		break;
		
		case 999:
			return new ReplyKeyboardRemove();//remove button
		default:
			break;
		}

		replyKeyboardMarkup.setKeyboard(keyboardList);
		return replyKeyboardMarkup;
	}
	
	
}
