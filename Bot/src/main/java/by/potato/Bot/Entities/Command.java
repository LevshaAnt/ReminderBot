package by.potato.Bot.Entities;

public enum Command {
	
	START_GENERAL_MENU("События - планируй события \nАктиваторы - тот кто напомнит \nИдея - напиши свою идею автору \nНастройки - часовой пояс"),
    ACTIVATORS("Активаторы"),
    IDEA("Идея"),
    SETTING("Настройка"),
    

    EVENT("События"),
    EVENT_INFO("Управляй событиями"),
    EVENT_NEW("Новое событие"),
    EVENT_DESCRIPTION("Введите описание события"),
    EVENT_DATE("Когда напомнить \nВведите данные в формате: \nдд.мм.гг чч.мм\n31.12.31 15.00"),
    EVENT_TYPE("Тип события: \n -- напоминать до события (день рождения, свадьба и т.д.) \n -- напоминать после события (не забыть взять ключи, оплатить интернет и т.д."),
    EVENT_COUNT("Количество повторений события \n положительное число"),
    EVENT_COUNT_ALARM("с какой периодичность  напоминать о событии"),
    
    HIDE_BUTTON("убрать кнопки"),
    ERROR_INPUT("Ошибка ввода \n"),
    REPEAT("Повторите ввод"),
    COMPLITE("Записал"),   
    FINISH("Завершить"),

    
    
    ERROR_EVENT_DATE("Ошибка форматы даты"),
    ERROR_EVENT_COUNT("Необходимо ввести целое число больше 0"),
    
    
    
    
    
    
    
    	EVENT_BEFORE("Заранее"),
    		
    	
    	EVENT_AFTER("После"),
    //		EVENT_NEW("Новое событие"),
    	//	EVENT_DESCRIPTION("Введите описание события"),
    	//	EVENT_DATE("Когда напомнить \nВведите данные в формате: \nдд.мм.гг чч.мм\n31.12.31 15.00"),
    		
    		EVENT_PERIOD("Повторять каждые (мин.,час.,дней.) \n положительное число"),
    		EVENT_SELECT_PERIOD("Выбери необходимый период времени"),
    		MINUTE("минут(у)"),
    		HOUR("час(а/ов)"),
    		DAY("день(дней)"),
	
    EVENT_FINISH("Событие создано!"),
    	

	
	
	NOT_COMMAND("Нет команды"),


	START("/start"),
	CANCEL("Отмена"),

	EVENT_SELECT_TYPE_PERIOD("тип периода"),
	EVENT_FUTURE("Грядущие события"),
	EVENT_PAST("Завершённые события"),
	MENU_GENERAL_RETURN("Главное меню"),
	ACTIVATORS_GROUP("Группы"),
	ACTIVATORS_NEW("Новый пользователь");


	
	
	
	
	


	private final String text;
    private final int id;

    private Command(final String text,final int id) {
        this.text = text;
        this.id = id;
    }
    
    private Command(final String text) {
    	this.text = text;
    	this.id = -999;
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