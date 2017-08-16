package by.potato.Bot.Entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(value = { "_id" })
public class Event {
	private String textEvent;
	private String uuid;
	private long count ;
	private long offset;
	private long nextTime;
	private int idCreateUser;
	private Set<Integer> idUsers;
	
	public Event() {
		
		this.idUsers = new HashSet<Integer>();
		this.uuid = UUID.randomUUID().toString();

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

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getNextTime() {
		return nextTime;
	}

	public void setNextTime(long nextTime) {
		this.nextTime = nextTime;
	}

	public int getIdCreateUser() {
		return idCreateUser;
	}

	public void setIdCreateUser(int idCreateUser) {
		this.idCreateUser = idCreateUser;
	}

	public Set<Integer> getIdUsers() {
		return idUsers;
	}

	public void setIdUsers(Set<Integer> idUsers) {
		this.idUsers = idUsers;
	}

	public void genNextTime() {
		if(this.count>0) {
			--this.count;
			this.nextTime = this.nextTime + this.offset;
			if(count == 0) {
				this.nextTime = 0; //event has been finished
			}
		}
	}
	
	@JsonIgnore
	public String getInfo() {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idCreateUser;
		result = prime * result + (int) (offset ^ (offset >>> 32));
		result = prime * result + ((textEvent == null) ? 0 : textEvent.hashCode());
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
		if (idCreateUser != other.idCreateUser)
			return false;
		if (offset != other.offset)
			return false;
		if (textEvent == null) {
			if (other.textEvent != null)
				return false;
		} else if (!textEvent.equals(other.textEvent))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}	
	
	
}
