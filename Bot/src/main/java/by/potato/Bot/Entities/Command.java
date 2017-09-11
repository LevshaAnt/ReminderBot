package by.potato.Bot.Entities;

public enum Command {
	
	START_GENERAL_MENU("События - планируй события\nИдея - напиши свою идею автору \nНастройки - часовой пояс"),
	//START_GENERAL_MENU("События - планируй события \nАктиваторы - тот кто напомнит \nИдея - напиши свою идею автору \nНастройки - часовой пояс"),
    ACTIVATORS("Активаторы"),
    IDEA("Идея"),
    IDEA_INFO("Введите Вашу идею! Идея будет отправлена создателю бота )"),
    SETTING("Настройка"),
  
    EVENT("События"),
    EVENT_INFO("Управляй событиями"),
    EVENT_NEW("Новое событие"),
    EVENT_DESCRIPTION("Введите описание события \n(до 50 символов)."),
    EVENT_TEXT("Введите текст события."),
    EVENT_DATE("Когда напомнить \nВведите данные в формате: \nдд.мм.гг чч.мм\n31.12.31 15.00"),
    EVENT_TYPE("Тип события: \n -- напоминать до события (день рождения, свадьба и т.д.) \n -- напоминать после события (не забыть взять ключи, выпить таблетку и т.д.)"),
 	EVENT_BEFORE("Заранее"),
	EVENT_AFTER("После"),
    EVENT_COUNT("Количество повторений события \nположительное число"),
    EVENT_COUNT_ALARM("Количество напоминаний о событии \nположительное число"),
    EVENT_COUNT_OFFSET("Повторять событие каждые (положительное число):"),
    EVENT_COUNT_ALARM_OFFSET("Повторять напоминание каждые (положительное число):"),
    EVENT_COUNT_DELETE("Введите номер события которое необходимо удалить "),
    EVENT_PERIOD("Выбери необходимый период времени"),
    EVENT_PERIOD_ALARM("Выбери необходимый период времени"),
	EVENT_FUTURE("Грядущие события"),
	EVENT_PAST("Завершённые события"),
	EVENT_ATTACH("Прикрелённые события"),
	EVENT_DELETE("Удалить событие"),
	EVENT_DELETE_ALL("Удалить все события"),
	EVENT_DELETE_INFO("Удалено!"),
 
    MINUTE("минута"),
	HOUR("час"),
	DAY("день"),
	WEEK("неделя"),
	MONTH("месяц"),
	YEAR("год"),
   
 
    HIDE_BUTTON("убрать кнопки"),
    ERROR_INPUT("Ошибка ввода \n"),
    REPEAT("Повторите ввод"),
    COMPLITE("Записал"),   
    FINISH("Завершить"),

    SETTING_INFO("Введите часовой пояс в след.формате \n+03:00  или -03:00 "),
    ERROR_EVENT_DATE("Ошибка форматы даты"),
    ERROR_EVENT_COUNT("Необходимо ввести целое число больше 0"),
    EVENT_FINISH("Событие создано!"),
	NOT_COMMAND("Нет команды"),
	START("/start"),
	CANCEL("Отмена"),

	EVENT_NOT("Нет событий."),
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