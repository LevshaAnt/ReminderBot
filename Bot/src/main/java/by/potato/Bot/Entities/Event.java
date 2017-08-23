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
	private ChronoUnit offsetPeriod;  //for --> "hours", "minutes", "days" "weeks", "months", "years"
	private LocalDateTime beginTime;
	private LocalDateTime nextTime;
	private Long idCreateUser;
	private Set<Integer> idUsers;
	private Boolean directionFlag; // false -- before; true -- after;
	
	public Event() {
		
		this.idUsers = new HashSet<Integer>();
		this.uuid = UUID.randomUUID().toString();

	}
	
	public Event(Long idCreateUser) {
		this();
		this.idCreateUser = idCreateUser;
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

	public ChronoUnit getOffsetPeriod() {
		return offsetPeriod;
	}

	public void setOffsetPeriod(ChronoUnit offsetPeriod) {
		this.offsetPeriod = offsetPeriod;
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
	
	
	
/*	@JsonIgnore
/*	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("Напоминание!");
		sb.append(this.textEvent).append(System.lineSeparator());
		if(this.count > 0 ) {
			sb.append("След. напоминание ->").append(System.lineSeparator());
			sb.append(new Date(this.nextTime)).append(System.lineSeparator());
			sb.append("Осталось напоминаний ->").append(this.count);
		} else {
			sb.append("Событие завершено!");
		}

		return sb.toString();	
	}
*/
}
