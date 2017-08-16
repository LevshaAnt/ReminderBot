package by.potato.Bot.DB;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import by.potato.Bot.Entities.Event;
import by.potato.Bot.Entities.Client;

public class DBHelper {
	private MongoClient mongo;
	private String dbName = "reminderbot";
	private String collConnect = "connect";
	private String collUser = "user";
	private String collEvent = "event";
	private String token;
	private String nameBot;
	private DB db;
	
	public DBHelper() {
		try {
			mongo = new MongoClient( "localhost" , 27017 );
			
			this.db = this.mongo.getDB(this.dbName);
			DBCollection dbcoll = this.db.getCollection(collConnect);
			DBObject dbo = dbcoll.findOne();
			this.token = (String) dbo.get("token");
			this.nameBot = (String) dbo.get("name");
			
		} catch (UnknownHostException e) {
			System.err.println("Error to connect DB " + e.getCause());
		}
		
	}
	
	public String getToken() {
		return this.token;
	}
	
	public String getNameBot() {
		return this.nameBot;
	}

	public Client getClient(Long id,String name,String surname) {
		
		DBCollection dbcoll = db.getCollection(this.collUser);
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("id", id);
		
		DBCursor cursor = dbcoll.find(whereQuery);
		if(cursor.hasNext()) {
			
			BasicDBObject bdbo = (BasicDBObject) cursor.next();
		
			try {
				Client client = new ObjectMapper().readValue(bdbo.toString(), Client.class);
				return client;
			} catch (IOException e) {
				System.err.println("User in BD is corrupt " + e.getMessage());
			}
		}	
		
		return new Client(id,name,surname);
	}
	
	public boolean setClient(Client user) {
		DBCollection dbcoll = db.getCollection(this.collUser);
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("id", user.getId());
		
		try {
			String userStr = new ObjectMapper().writeValueAsString(user);
			DBObject dbObject = (DBObject) JSON.parse(userStr);
			dbcoll.update(whereQuery,dbObject,true,false);
			
			return true;
		} catch (JsonProcessingException e) {
			System.err.println("Event in BD is corrupt " + e.getMessage());
		}
		
		return false;
	}
	
	public Queue<Event> getEvent(long needTime) {
		
		Queue<Event> eventMap = new ConcurrentLinkedQueue<>();
		
		DBCollection dbcoll = db.getCollection(this.collEvent);
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("nextTime", new BasicDBObject("$lt", needTime).append("$gt", 1));
		
		DBCursor cursor = dbcoll.find(whereQuery);
		
		while (cursor.hasNext()) {
			BasicDBObject bdbo = (BasicDBObject) cursor.next();
			
			try {
				Event event = new ObjectMapper().readValue(bdbo.toString(), Event.class);
				eventMap.add(event);
			} catch (IOException e) {
				System.err.println("Event in BD is corrupt " + e.getMessage());
			}
		}

		return eventMap;
	}
	
	public boolean setEvent(Event event) {
		DBCollection dbcoll = db.getCollection(this.collEvent);
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("UUID", event.getUuid());
		
		try {
			String eventStr = new ObjectMapper().writeValueAsString(event);
			DBObject dbObject = (DBObject) JSON.parse(eventStr);
			dbcoll.update(whereQuery,dbObject,true,false);
			
			return true;
		} catch (JsonProcessingException e) {
			System.err.println("Event in BD is corrupt " + e.getMessage());
		}
		
		return false;
	}
}
