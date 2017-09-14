package by.potato.Bot.DB;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

import org.bson.Document;
import org.bson.conversions.Bson;

import by.potato.Bot.Entities.Event;
import by.potato.Bot.Entities.Client;


public class DBHelper {
	
	private Long idMaker;
	private String token;
	private String nameBot;
	private  ZoneOffset defaultZoneOffsetUser;
	
	
	private static final String dbName = "reminderbot";
	private static final String collConnect = "connect";
	private static final String collUser = "user";
	private static final String collEvent = "event";
	private static final String collIdea = "idea";

	private static MongoClient mongoClient;
	private static MongoDatabase dataBase;
	
	private static Map<Long,Long> timeLastSearch = new ConcurrentHashMap<>();
	private static ObjectMapper mapperForRead;
	private static ObjectMapper mapperForWrite;
	
	static {
		mapperForRead = new ObjectMapper();
		mapperForRead.registerModule(new JavaTimeModule());
		mapperForRead.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,false);
		
		mapperForWrite = new ObjectMapper();
		mapperForWrite.registerModule(new JavaTimeModule());
		mapperForWrite.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapperForWrite.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);
		
		mongoClient = new MongoClient( "localhost" , 27017 );
		dataBase = mongoClient.getDatabase(dbName);
	}

	public DBHelper() {

		MongoCollection<Document> dbcoll = dataBase.getCollection(collConnect);
		Document doc = dbcoll.find().first();
		
		this.token = doc.getString("token");
		this.nameBot = doc.getString("name");
		this.idMaker = doc.getLong("idMaker");
		this.defaultZoneOffsetUser = ZoneOffset.of(doc.getString("defaultZoneOffsetUser"));
	}

	public ZoneOffset getDefaultZoneOffsetUser() {
		return defaultZoneOffsetUser;
	}

	public Long getIdMaker() {
		return idMaker;
	}

	public String getToken() {
		return this.token;
	}
	
	public String getNameBot() {
		return this.nameBot;
	}
	
	public Client getClientOrCreate(Long id,String name,String surname) {
		
		MongoCollection<Document> dbcoll = dataBase.getCollection(collUser);
				
		Document doc = dbcoll.find(Filters.eq("id", id)).first();
		
		if(doc != null) {
			try {
				Client client = mapperForRead.readValue(doc.toString(), Client.class);
				return client;
			} catch (IOException e) {
				System.err.format("Client ID = %d FROM BD is corrupt. Error message %s",id, e.getMessage());
			}
		}
		return new Client(id,name,surname,this.defaultZoneOffsetUser);
	}

	public void setClient(Client client) {
		try {
			String userStr = mapperForWrite.writeValueAsString(client);
			Document doc = Document.parse(userStr);
			
			MongoCollection<Document> dbcoll = dataBase.getCollection(collUser);
			dbcoll.updateOne(Filters.eq("id", client.getId()), doc, new UpdateOptions().upsert(true));
			
		} catch (JsonProcessingException | MongoException e ) {
			System.err.format("Client ID = %d TO BD is corrupt. Error message %s",client.getId(), e.getMessage());
		}
	}
	
	public Queue<Event> getEvent(Long needTime) {
				
		Queue<Event> eventMap = new ConcurrentLinkedQueue<>();
		
		MongoCollection<Document> dbcoll = dataBase.getCollection(collEvent);
		
		Block<Document> addToCollect = new Block<Document>() {
		     @Override
		     public void apply(final Document document){
		    	 try {
					eventMap.add(mapperForRead.readValue(document.toString(), Event.class));
				} catch (IOException e) {
					System.err.format("Error parse event from DB. Error message %s", e.getMessage());
				}
		     }
		};
		
		dbcoll.find(Filters.and(Filters.lt("nextTimeInLong",needTime),Filters.eq("countEvent", 0)))
			.forEach(addToCollect);
		
		return eventMap;
	}
	
	public class Inc {
		private AtomicInteger counter = new AtomicInteger(0);
	
		public int getCounter() {
			return this.counter.get();
		}

		public void inc() {
			this.counter.incrementAndGet();
		}
	}
	
	public String getEvents(boolean future , Long clientID) {//true --> future false --> last
	
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
		long utcLong = utc.toEpochSecond();
	
		timeLastSearch.put(clientID, utcLong);
		
		MongoCollection<Document> dbcoll = dataBase.getCollection(collEvent);
		
		Bson filtTime;
		
		if(future) {
			filtTime = Filters.gte("nextTimeInLong",utcLong);
		} else {
			filtTime = Filters.lt("nextTimeInLong",utcLong);
		}
		
		Bson fullFilter = Filters.and(filtTime,Filters.eq("idCreateUser", clientID));

		StringBuilder sb = new StringBuilder();
		Inc inc = new Inc();
		
		Block<Document> addToCollect = new Block<Document>() {
		     @Override
		     public void apply(final Document document){
		    	 Event event;
				try {
					event = mapperForRead.readValue(document.toString(), Event.class);
					inc.inc();
			    	sb.append(System.lineSeparator()).append("Номер события --> " + inc.getCounter()).append(System.lineSeparator());
					sb.append(event.getInfo());
				} catch (IOException e) {
					System.err.format("Event in BD is corrupt message %s " + e.getMessage());
				}
		     }
		};
		
		dbcoll.find(fullFilter).forEach(addToCollect);
		
		return sb.toString();
	}
	
	public void deleteEvent(boolean future,Long clientID, int number) {
		
		long utcLong = timeLastSearch.get(clientID);

		MongoCollection<Document> dbcoll = dataBase.getCollection(collEvent);
		
		Bson filtTime;
		
		if(future) {
			filtTime = Filters.gte("nextTimeInLong",utcLong);
		} else {
			filtTime = Filters.lt("nextTimeInLong",utcLong);
		}
		
		Bson fullFilter = Filters.and(filtTime,Filters.eq("idCreateUser", clientID));
		
		Document doc = dbcoll.find(fullFilter).skip(--number).first();

		dbcoll.deleteOne(doc);
	}
	
	public void deleteEvents() {
		try {	
			MongoCollection<Document> dbcoll = dataBase.getCollection(collEvent);
			
			ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
			long utcLong = utc.toEpochSecond();
		
			dbcoll.deleteMany(Filters.lt("nextTimeInLong", utcLong));
		} catch (MongoException e ) {
			System.err.format( "Error delete finished events message %s", e.getMessage());
		}
	}
	
	public void setEventOrUpdate(Event event) {				
		try {	
			MongoCollection<Document> dbcoll = dataBase.getCollection(collIdea);
		
			String eventStr = mapperForWrite.writeValueAsString(event);
			Document doc = Document.parse(eventStr);
	
			dbcoll.updateOne(Filters.eq("uuid", event.getUuid()), doc, new UpdateOptions().upsert(true));
			
		} catch (JsonProcessingException | MongoException e ) {
			System.err.format("Event UUID = %d TO BD is corrupt. Error message %s",event.getUuid(), e.getMessage());
		}
	}
	
	public void setIdea(String idea, Long userId) {
		
		MongoCollection<Document> dbcoll = dataBase.getCollection(collIdea);
		
		Document doc = new Document();
		doc.append("userID", userId).append("textIdea", idea);
		
		try {
			dbcoll.insertOne(doc);
		} catch (MongoException e) {
			System.err.println("Error insert IDEA to BD " + e.getMessage());
		}

	}
}
