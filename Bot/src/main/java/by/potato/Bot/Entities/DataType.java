package by.potato.Bot.Entities;

public enum DataType {
	START_GENERAL_MENU("События - планируй события \nАктиваторы - тот кто напомнит"),
	EVENT_TYPE("Управляй событиями"),
	WAIT_EVENT_DESCRIPTION("Введите описание события"),
	WAIT_EVENT_DATE("Когда напомнить \nВведите данные в формате: \n дд.мм.гг чч.мм\n31.12.31 15.00"),
	WAIT_EVENT_COUNT("Число повторений события \n положительное число"),
	WAIT_EVET_PERIOD("Положительное число");
	
	private final String text;

	private DataType(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
    @Override
    public String toString() {
        return this.text;
    }
}
