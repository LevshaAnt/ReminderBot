package by.potato.Bot.Entities;

import java.time.temporal.ChronoUnit;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(value = { "_id" })
public class Event {
	private String textDescription;
	private String textEvent;
	private String uuid;
	private long countEvent;
	private long countAlarm;
	private long countLeftAlarm;
	private long countOffsetEvent;
	private long countOffsetAlart;
	private ChronoUnit offsetEvent = null;
	private ChronoUnit offsetAlarm = null; 
	private ZonedDateTime beginTime;
	private ZonedDateTime nextTime;
	private long nextTimeInLong;
	private long idCreateUser;
	private ZoneOffset clientOffset;
	private Set<Integer> idUsers;
	private Boolean directionFlag; 
	
	public Event() {
		this.idUsers = new HashSet<Integer>();
		this.uuid = UUID.randomUUID().toString();
		this.countAlarm = 0;
		this.countLeftAlarm = 1;
	}
	
	public Event(long idCreateUser, ZoneOffset zoneOffset) {
		this();
		this.idCreateUser = idCreateUser;
		this.clientOffset = zoneOffset;
	}
	 
	public long getCountOffsetEvent() {
		return countOffsetEvent;
	}

	public void setCountOffsetEvent(long countOffsetEvent) {
		this.countOffsetEvent = countOffsetEvent;
	}

	public long getCountOffsetAlart() {
		return countOffsetAlart;
	}

	public void setCountOffsetAlart(long countOffsetAlart) {
		this.countOffsetAlart = countOffsetAlart;
	}

	public String getTextDescription() {
		return textDescription;
	}

	public void setTextDescription(String textDescription) {
		this.textDescription = textDescription;
	}

	public long getNextTimeInLong() {
		return nextTimeInLong;
	}

	public void setNextTimeInLong(long nextTimeInLong) {
		this.nextTimeInLong = nextTimeInLong;
	}

	public ZoneOffset getClientOffset() {
		return clientOffset;
	}

	public void setClientOffset(ZoneOffset clinetOffset) {
		this.clientOffset = clinetOffset;
	}

	public ChronoUnit getOffsetEvent() {
		return offsetEvent;
	}

	public void setOffsetEvent(ChronoUnit offsetEvent) {
		this.offsetEvent = offsetEvent;
	}

	public ChronoUnit getOffsetAlarm() {
		return offsetAlarm;
	}

	public void setOffsetAlarm(ChronoUnit offsetAlarm) {
		this.offsetAlarm = offsetAlarm;
	}

	public void updateNextEventTime() {

		if(this.countEvent > 0) { //есть ещё события запланированные
			
			if(this.countLeftAlarm < this.countAlarm) {	
				this.nextTime = this.directionFlag?
									this.beginTime.plus(this.countOffsetAlart * this.countLeftAlarm , this.offsetAlarm):
									this.beginTime.minus( (this.countAlarm - this.countLeftAlarm + 1) * this.countOffsetAlart, this.offsetAlarm);
												
				this.nextTimeInLong = this.nextTime.toEpochSecond();
				this.countLeftAlarm++;					
			} else {
				this.countLeftAlarm = 1;
				this.beginTime = this.beginTime.plus(this.countOffsetEvent,this.offsetEvent);
				this.updateNextEventTime();
				this.countEvent--;
			}
		}
	}	

	public String getTextEvent() {
		return textEvent;
	}

	public void setTextEvent(String textEvent) {
		this.textEvent = textEvent;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getCountEvent() {
		return countEvent;
	}

	public void setCountEvent(long countEvent) {
		this.countEvent = countEvent;
	}

	public long getCountAlarm() {
		return countAlarm;
	}

	public void setCountAlarm(long countAlarm) {
		this.countAlarm = countAlarm;
	}
	

	public long getCountLeftAlarm() {
		return countLeftAlarm;
	}

	public void setCountLeftAlarm(long countLeftAlarm) {
		this.countLeftAlarm = countLeftAlarm;
	}

	public ZonedDateTime getNextTime() {
		return nextTime;
	}

	public void setNextTime(ZonedDateTime nextTime) {
		this.nextTime = nextTime;
	}

	public ZonedDateTime getBeginTime() {
		return beginTime;
	}
	
	public void setBeginTime(ZonedDateTime beginTime) {
		this.beginTime = beginTime;
	}

	public long getIdCreateUser() {
		return idCreateUser;
	}

	public void setIdCreateUser(long idCreateUser) {
		this.idCreateUser = idCreateUser;
	}

	public Set<Integer> getIdUsers() {
		return idUsers;
	}

	public void setIdUsers(Set<Integer> idUsers) {
		this.idUsers = idUsers;
	}

	public Boolean getDirectionFlag() {
		return directionFlag;
	}

	public void setDirectionFlag(Boolean directionFlag) {
		this.directionFlag = directionFlag;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beginTime == null) ? 0 : beginTime.hashCode());
		result = prime * result + (int) (idCreateUser ^ (idCreateUser >>> 32));
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (beginTime == null) {
			if (other.beginTime != null)
				return false;
		} else if (!beginTime.equals(other.beginTime))
			return false;
		if (idCreateUser != other.idCreateUser)
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@JsonIgnore
	public StringBuilder getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Информация о событии").append(System.lineSeparator());
		sb.append("Описание события >> ").append(System.lineSeparator());
		sb.append(this.textDescription).append(System.lineSeparator());
		sb.append("Текст события >> ").append(System.lineSeparator());
		sb.append(this.textEvent).append(System.lineSeparator());
		sb.append("Число повторений события >> ").append(this.countEvent).append(System.lineSeparator());
		sb.append("Период события >> ").append(this.offsetEvent + " * " + this.countOffsetEvent).append(System.lineSeparator());
		sb.append("Период события >> ").append(this.offsetEvent).append(System.lineSeparator());
		sb.append("Число напоминаний о событии >> ").append(this.countAlarm - this.countLeftAlarm + 1).append(System.lineSeparator());
		sb.append("Период напоминания >> ").append(this.offsetAlarm + " * " + this.getCountOffsetAlart()).append(System.lineSeparator());
		sb.append("Следующее напоминание в >>" ).append(System.lineSeparator());
		sb.append(this.nextTime).append(System.lineSeparator());
		
		return new StringBuilder(sb);
	}
	
	@JsonIgnore
	public String getReminder() {
		StringBuilder sb = new StringBuilder();

		
		if(this.countEvent > 0 ) {
			sb.append("Напоминание!").append(System.lineSeparator());
			sb.append("Описание события >> ").append(System.lineSeparator());
			sb.append(this.textDescription).append(System.lineSeparator());
			sb.append("Текст события >> ").append(System.lineSeparator());
			sb.append(this.textEvent).append(System.lineSeparator());
			sb.append("Следующее напоминание >> ").append(System.lineSeparator());
			sb.append(this.nextTime).append(System.lineSeparator());
			sb.append("Осталось событий >> ").append(this.countEvent).append(System.lineSeparator());
			sb.append("Осталось напоминаний для текущего события >> ");
			sb.append(this.countAlarm - this.countLeftAlarm + 1);
			
		} else {
			sb.append("Событие завершено!");
		}

		return sb.toString();	
	}

}
