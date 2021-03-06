package by.potato.Bot.Entities;

import java.time.DateTimeException;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "_id" })
public class Client {

	private Long id;
	private String name = "";
	private String surname = "";
	private ZoneOffset offset;
	
	public Client() {	}
	
	public Client(Long id, String name, String surname, ZoneOffset offset) {
		super();
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "Client [id=" + id + ", offset=" + offset + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public ZoneOffset getOffset() {
		return offset;
	}

	public void setOffset(ZoneOffset offset) {
		this.offset = offset;
	}
	
	public boolean setOffset(String offsetstr) {
		try {
			this.offset = ZoneOffset.of(offsetstr);
			return true;
		} catch (DateTimeException e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
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
		Client other = (Client) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		return true;
	}
}
