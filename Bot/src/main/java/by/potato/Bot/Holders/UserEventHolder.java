package by.potato.Bot.Holders;

import java.time.Instant;

import by.potato.Bot.Entities.Client;
import by.potato.Bot.Entities.Command;

public class UserEventHolder {

	private Client client;
	private Instant lastAppeal;
	private Command dataType;
	private boolean needTextInp;
	private boolean error;
	private String errorMess;
	private boolean flagEvent;
	

	public UserEventHolder(Client client) {
		this.client = client;
		this.errorMess ="";
		this.flagEvent = true;
		updateLastAppeal();
	}
	
	public boolean isFlagEvent() {
		return flagEvent;
	}

	public void setFlagEvent(boolean flagEvent) {
		this.flagEvent = flagEvent;
	}

	public void updateLastAppeal() {
		this.lastAppeal = Instant.now();
	}

	public Instant getLastAppeal() {
		return lastAppeal;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Command getDataType() {
		return dataType;
	}

	public void setDataType(Command dataType) {
		this.dataType = dataType;
	}

	public boolean isNeedTextInp() {
		return needTextInp;
	}

	public void setNeedTextInp(boolean needTextInp) {
		this.needTextInp = needTextInp;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorMess() {
		return errorMess;
	}

	public void setErrorMess(String errorMess) {
		this.errorMess = errorMess;
	}

	
}
