package by.potato.Bot.Entities;

public enum Command {
	NOT_COMMAND("Нет команды",-10),
    EVENT("События",100),
    ACTIVATORS("Активаторы",200),
	START("/start",0),
	CANCEL("Отмена",0),
	EVENT_NEW("Новое событие",101),
	EVENT_FUTURE("Грядущие события",102),
	EVENT_PAST("Завершённые события",103),
	MENU_GENERAL_RETURN("Главное меню ",0),
	ACTIVATORS_GROUP("Группы",201),
	ACTIVATORS_NEW("Новый пользователь",202),
	MINUTES("минут(у)",105),
	HOURS("час(а/ов)",106),
	DAYS("день(дней)",107),
	FINISH("Завершить", 108);

	private final String text;
    private final int id;

    private Command(final String text,final int id) {
        this.text = text;
        this.id = id;
    }

    public String getText() {
    	return this.text;
    }
    
    public int getId() {
    	return this.id;
    }
    
    public static Command parse(String str) {
    	
    	Command[] values = Command.values();
    	
    	for(int i=0;i<values.length;i++) {
    		if(values[i].toString().equals(str)) {
    			return values[i];
    		}
    	}	
    	return NOT_COMMAND;
    }
    

    @Override
    public String toString() {
        return this.text;
    }
}