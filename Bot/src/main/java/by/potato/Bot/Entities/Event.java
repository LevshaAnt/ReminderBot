package by.potato.Bot.Entities;

import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(value = { "_id" })
public class Event {
	private String textEvent;
	private String uuid;
	private long countEvent;
	private long countAlarm;
	private long countLeftAlarm;
	private ChronoUnit offsetEvent = null;
	private ChronoUnit offsetAlarm = null;  //for --> "hours", "minutes", "days" "weeks", "months", "years"
	private ZonedDateTime beginTime;
	private ZonedDateTime nextTime;
	private long nextTimeInLong;
	private long idCreateUser;
	private ZoneOffset clinetOffset;// = ZoneOffset.of("-05:00");
	private Set<Integer> idUsers;
	private Boolean directionFlag; // false -- before; true -- after;
	
	public Event() {
		this.idUsers = new HashSet<Integer>();
		this.uuid = UUID.randomUUID().toString();
		this.countAlarm = 0;
	}
	
	public Event(long idCreateUser) {
		this();
		this.idCreateUser = idCreateUser;
	}
	
	public long getNextTimeInLong() {
		return nextTimeInLong;
	}

	public void setNextTimeInLong(long nextTimeInLong) {
		this.nextTimeInLong = nextTimeInLong;
	}

	public ZoneOffset getClinetOffset() {
		return clinetOffset;
	}

	public void setClinetOffset(ZoneOffset clinetOffset) {
		this.clinetOffset = clinetOffset;
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
									this.beginTime.plus(this.countLeftAlarm, this.offsetAlarm):
									this.beginTime.minus(this.countAlarm - this.countLeftAlarm, this.offsetAlarm);
												
				this.nextTimeInLong = this.nextTime.toEpochSecond();
				this.countLeftAlarm++;					
			} else {
				this.countLeftAlarm = 0;
				this.beginTime = this.beginTime.plus(1,this.offsetEvent);
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
	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Информация о событии").append(System.lineSeparator());
		sb.append("Описание события --> ").append(System.lineSeparator());
		sb.append(this.textEvent).append(System.lineSeparator());
		sb.append("Число повторений события --> ").append(this.countEvent).append(System.lineSeparator());
		sb.append("Период события --> ").append(this.offsetEvent).append(System.lineSeparator());
		sb.append("Число напоминаний о событии --> ").append(this.countAlarm).append(System.lineSeparator());
		sb.append("Временной промежуток между напоминаниями --> ").append(this.offsetAlarm).append(System.lineSeparator());
		sb.append("Следующее напоминание в -->" ).append(System.lineSeparator());
		sb.append(this.nextTime).append(System.lineSeparator());
		return sb.toString();
	}
	
	@JsonIgnore
	public String getReminder() {
		StringBuilder sb = new StringBuilder();
		sb.append("Напоминание!").append(System.lineSeparator());
		sb.append(this.textEvent).append(System.lineSeparator());
		
		if(this.countEvent > 0 ) {
			sb.append("Следующее напоминание -->").append(System.lineSeparator());
			sb.append(this.nextTime).append(System.lineSeparator());
			sb.append("Осталось напоминаний для данного события --> ");
			sb.append(this.getCountAlarm());
		} else {
			sb.append("Событие завершено!");
		}

		return sb.toString();	
	}

}
