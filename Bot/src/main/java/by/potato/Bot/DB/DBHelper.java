package by.potato.Bot.DB;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import by.potato.Bot.Entities.Event;
import by.potato.Bot.Entities.Client;
import by.potato.Bot.Entities.Command;

public class DBHelper {
	private MongoClient mongo;
	private String dbName = "reminderbot";
	private String collConnect = "connect";
	private String collUser = "user";
	private String collEvent = "event";
	private String token;
	private String nameBot;
	private DB db;
	private ZoneOffset defaultZoneOffsetServer;
	private ZoneOffset defaultZoneOffsetUser;
	
	public DBHelper() {
		try {
			mongo = new MongoClient( "localhost" , 27017 );
			
			this.db = this.mongo.getDB(this.dbName);
			DBCollection dbcoll = this.db.getCollection(collConnect);
			DBObject dbo = dbcoll.findOne();
			this.token = (String) dbo.get("token");
			this.nameBot = (String) dbo.get("name");
			this.defaultZoneOffsetUser = ZoneOffset.of((String)dbo.get("defaultZoneOffsetUser"));
			this.defaultZoneOffsetServer = ZoneOffset.UTC;
			
		} catch (UnknownHostException e) {
			System.err.println("Error to connect DB " + e.getCause());
		}
		
	}

	public ZoneOffset getDefaultZoneOffsetUser() {
		return defaultZoneOffsetUser;
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
		
		return new Client(id,name,surname,this.defaultZoneOffsetUser);
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
		

		List<BasicDBObject> objList = new ArrayList<BasicDBObject>();
		
		BasicDBObject mainQuery = new BasicDBObject();
		
		BasicDBObject whereQueryFirst = new BasicDBObject();
		whereQueryFirst.put("nextTimeInLong", new BasicDBObject("$lt", needTime).append("$gt", 1));
		
		BasicDBObject whereQuerySecond = new BasicDBObject();
		whereQuerySecond.put("countEvent", new BasicDBObject("$ne", 0));
		
		objList.add(whereQueryFirst);
		objList.add(whereQuerySecond);
		
		mainQuery.put("$and", objList);

		DBCursor cursor = dbcoll.find(mainQuery);
		
		while (cursor.hasNext()) {
			BasicDBObject bdbo = (BasicDBObject) cursor.next();
			
			try {
				ObjectMapper om = new ObjectMapper();
				om.findAndRegisterModules();
				
				Event event = om.readValue(bdbo.toString(), Event.class);
				eventMap.add(event);
			} catch (IOException e) {
				System.err.println("Event in BD is corrupt " + e.getMessage());
			}
		}

		return eventMap;
	}
	
	public String getEvents(boolean future , Long userID) {//true --> future false --> last
		StringBuilder sb = new StringBuilder();
		
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
		long utcLong = utc.toEpochSecond();
		
		
		DBCollection dbcoll = db.getCollection(this.collEvent);
		
		List<BasicDBObject> objList = new ArrayList<BasicDBObject>();
		
		BasicDBObject mainQuery = new BasicDBObject();
		
		BasicDBObject whereQueryFirst = new BasicDBObject();
		
		if(future) {
			whereQueryFirst.put("nextTimeInLong", new BasicDBObject("$gt", utcLong));
		} else {
			whereQueryFirst.put("nextTimeInLong", new BasicDBObject("$lt", utcLong));
		}
		
		BasicDBObject whereQuerySecond = new BasicDBObject();
		whereQuerySecond.put("idCreateUser", userID);
		
		objList.add(whereQueryFirst);
		objList.add(whereQuerySecond);
		
		mainQuery.put("$and", objList);
		
		int count=1;
		DBCursor cursor = dbcoll.find(mainQuery);
	//	cursor.sort(orderBy)
		
		try {
		while (cursor.hasNext()) {
			BasicDBObject bdbo = (BasicDBObject) cursor.next();
			
			System.err.println(bdbo.toString());
			
			try {
				
				ObjectMapper om = new ObjectMapper();
				om.registerModule(new JavaTimeModule());
				om.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,false);
				
				Event event = om.readValue(bdbo.toString(), Event.class);
			
				sb.append(System.lineSeparator()).append("Номер события --> " + count++).append(System.lineSeparator());
				sb.append(event.getInfo());
			} catch (IOException e) {
				System.err.println("Event in BD is corrupt " + e.getMessage());
			}
		} } catch (MongoException e) {
			System.err.println(e.getMessage() + "\n" + e.getCause());
		}
		
		if(sb.length() == 0) {//not future event
			sb.append(System.lineSeparator()).append(Command.EVENT_NOT.getText());
		}

		return sb.toString();
	}
	
	public boolean deleteEvents() {
		
		DBCollection dbcoll = db.getCollection(this.collEvent);
		BasicDBObject query = new BasicDBObject();
		
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
		long utcLong = utc.toEpochSecond();
		
		query.put("nextTimeInLong", new BasicDBObject("$lt", utcLong));
		
		try {
		dbcoll.remove(query);
		} catch (MongoException e) {
			System.err.println(e.getMessage() + "\n" + e.getCause());
			return false;
		}
		return true;
	}
	
	public boolean setEvent(Event event) {
		DBCollection dbcoll = db.getCollection(this.collEvent);
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("UUID", event.getUuid());
		
		try {
			ObjectMapper om = new ObjectMapper();
			om.registerModule(new JavaTimeModule());
		    om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		    om.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);

			String eventStr = om.writeValueAsString(event);
			DBObject dbObject = (DBObject) JSON.parse(eventStr);
			dbcoll.update(whereQuery,dbObject,true,false);
			
			return true;
		} catch (JsonProcessingException e) {
			System.err.println("Error insert to BD " + e.getMessage());
		}
		
		return false;
	}
	
	
}
