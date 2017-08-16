package by.potato.Bot.Holders;

import java.time.Instant;

import by.potato.Bot.Entities.Client;
import by.potato.Bot.Entities.DataType;

public class UserHolder {

	private Client client;
	private Long lastAppeal;
	private DataType dataType;
	private boolean needTextInp;
	private boolean error;
	private String errorMess;
	

	public UserHolder(Client client) {
		this.client = client;
		this.errorMess ="";
		updateLastAppeal();
	}

	public Long getLastAppeal() {
		return lastAppeal;
	}

	public void updateLastAppeal() {
		this.lastAppeal = Instant.now().getEpochSecond();	

	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
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
