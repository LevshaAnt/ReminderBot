package by.potato.Bot.Entities;

import java.time.temporal.ChronoUnit;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(value = { "_id" })
public class Event {
	private String textEvent;
	private String uuid;
	private Long countEvent;
	private Long countAlarm;
	private Long countLeftAlarm;
	private ChronoUnit offsetEvent = null;
	private ChronoUnit offsetAlarm = null;  //for --> "hours", "minutes", "days" "weeks", "months", "years"
	private LocalDateTime beginTime;
	private LocalDateTime nextTime;
	private Long idCreateUser;
	private Set<Integer> idUsers;
	private Boolean directionFlag; // false -- before; true -- after;
	
	public Event() {
		this.idUsers = new HashSet<Integer>();
		this.uuid = UUID.randomUUID().toString();
		this.countAlarm = 0L;
	}
	
	public Event(Long idCreateUser) {
		this();
		this.idCreateUser = idCreateUser;
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
									
				this.countLeftAlarm++;					
			} else {
				this.countLeftAlarm = 0L;
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

	public Long getCountEvent() {
		return countEvent;
	}

	public void setCountEvent(Long countEvent) {
		this.countEvent = countEvent;
	}

	public Long getCountAlarm() {
		return countAlarm;
	}

	public void setCountAlarm(Long countAlarm) {
		this.countAlarm = countAlarm;
	}
	

	public Long getCountLeftAlarm() {
		return countLeftAlarm;
	}

	public void setCountLeftAlarm(Long countLeftAlarm) {
		this.countLeftAlarm = countLeftAlarm;
	}


	public LocalDateTime getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(LocalDateTime beginTime) {
		this.beginTime = beginTime;
	}

	public LocalDateTime getNextTime() {
		return nextTime;
	}

	public void setNextTime(LocalDateTime nextTime) {
		this.nextTime = nextTime;
	}

	public Long getIdCreateUser() {
		return idCreateUser;
	}

	public void setIdCreateUser(Long idCreateUser) {
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
		result = prime * result + ((idCreateUser == null) ? 0 : idCreateUser.hashCode());
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
		if (idCreateUser == null) {
			if (other.idCreateUser != null)
				return false;
		} else if (!idCreateUser.equals(other.idCreateUser))
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
		sb.append("Описание события -->").append(System.lineSeparator());
		sb.append(this.textEvent).append(System.lineSeparator());
		sb.append("Число повторений события -->").append(System.lineSeparator());
		sb.append(this.countEvent).append(System.lineSeparator());
		sb.append("Период события -->").append(System.lineSeparator());
		sb.append(this.offsetEvent).append(System.lineSeparator());
		sb.append("Число напоминаний о событии -->").append(System.lineSeparator());
		sb.append(this.countAlarm).append(System.lineSeparator());
		sb.append("Временной промежуток между напоминаниями -->").append(System.lineSeparator());
		sb.append(this.offsetAlarm).append(System.lineSeparator());
		sb.append("Следующее напоминание в -->").append(System.lineSeparator());
		sb.append(this.nextTime).append(System.lineSeparator());
		return sb.toString();
	}
	
	@JsonIgnore
	public String getReminder() {
		StringBuilder sb = new StringBuilder();
		sb.append("Напоминание!");
		sb.append(this.textEvent).append(System.lineSeparator());
		
		if(this.countEvent > 0 ) {
			sb.append("Следующее напоминание -->").append(System.lineSeparator());
			sb.append(this.nextTime).append(System.lineSeparator());
		} else {
			sb.append("Событие завершено!");
		}

		return sb.toString();	
	}

}
