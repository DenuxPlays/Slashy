package com.denux.slashy.services;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.denux.slashy.properties.ConfigString;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import static com.mongodb.client.model.Filters.eq;

public class Database {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(Database.class);
    public static MongoClient mongoClient;

    //Creates a connection to the MongoDB Database
    public void connectToDatabase() {

        //Logging | Gives an error if something goes wrong
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.ERROR);

        //Connection to Database
        MongoClientURI uri = new MongoClientURI(new ConfigString("mongodb").getValue());
        mongoClient = new MongoClient(uri);
    }

    /*
    Available paths:
    guildID
    logChannel
    muteRole
    serverLock
    starboardChannel
    warnLimit
    reportChannel
     */
    public JsonElement getConfig(Guild guild, String path) {

        if (ifGuildDocExists(guild)) {

            //Connection to the cluster
            MongoDatabase database = mongoClient.getDatabase("other");
            MongoCollection<Document> collection = database.getCollection("config");

            //Getting the doc by searching after the guild id
            String doc = collection.find(eq("guildID", guild.getId())).first().toJson();
            String[] split = path.split("\\.");

            JsonObject root = JsonParser.parseString(doc).getAsJsonObject();
            for (int i = 0; i < split.length - 1; i++) root = root.get(split[i]).getAsJsonObject();

            return root.get(split[split.length - 1]);

        }
    else {
        //Creating a Config if the guild doesn't have one
        createConfig(guild);

        return new JsonPrimitive("0");
        }
    }

    //checks if the guild has a config or not
    private boolean ifGuildDocExists(Guild guild) {

        MongoDatabase database = mongoClient.getDatabase("other");
        MongoCollection<Document> collection = database.getCollection("config");

        Document doc = collection.find(eq("guildID", guild.getId())).first();

        if (doc == null) return false;

        else return true;

    }

    //Creating the Config for a guild who doesn't have one
    private void createConfig(Guild guild) {

        //Connection to the cluster
        MongoDatabase database = mongoClient.getDatabase("other");
        MongoCollection<Document> collection = database.getCollection("config");

        //Creating the Database structure
        if (!ifGuildDocExists(guild)) {
            Document doc = new Document()
                    .append("guildID", guild.getId())
                    .append("logChannel", "0")
                    .append("muteRole", "0")
                    .append("serverLock", false)
                    .append("starboardChannel", "0")
                    .append("warnLimit", "0")
                    .append("reportChannel", "0");

            collection.insertOne(doc);

            //Returning a feedback via the Logger Plugin
            logger.info("Creating a Config with the guild id: "+guild.getId());
        }
        else logger.error("Guild already has a config.");
    }

    //Sets |edits a Database Entry
    public void setDatabaseEntry(Guild guild, String path, Object newValue) {

        if (ifGuildDocExists(guild)) {
            BasicDBObject setData = new BasicDBObject(path, newValue);
            BasicDBObject update = new BasicDBObject("$set", setData);

            Document query = new Document("guildID", guild.getId());

            mongoClient.getDatabase("other").getCollection("config").updateOne(query, update);
        }
       else {

           createConfig(guild);
           logger.warn("Creating a Config for the Guild:"+guild.getId()+" | setDatabaseEntry");

           BasicDBObject setData = new BasicDBObject(path, newValue);
           BasicDBObject update = new BasicDBObject("$set", setData);

           Document query = new Document("guildID", guild.getId());

           mongoClient.getDatabase("other").getCollection("config").updateOne(query, update);
        }
    }
}

