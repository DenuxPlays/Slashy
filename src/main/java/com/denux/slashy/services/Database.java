package com.denux.slashy.services;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.denux.slashy.properties.ConfigString;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;
import org.slf4j.LoggerFactory;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.expr;

public class Database {

    public static MongoClient mongoClient;

    public void connectToDatabase() {

        //Logging
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.ERROR);

        //Connection to Database
        MongoClientURI uri = new MongoClientURI(new ConfigString("mongodb", "0").getValue());
        mongoClient = new MongoClient(uri);
    }

    public JsonElement getConfig(Guild guild, String path) {
        //TODO fix return
        try {
            MongoDatabase database = mongoClient.getDatabase("other");
            MongoCollection<Document> collection = database.getCollection("config");

            String doc = collection.find(eq("guild_id", guild.getId())).first().toJson();
            System.out.println(doc);
            String[] splittedPath = path.split("\\.");

            JsonObject root = JsonParser.parseString(doc).getAsJsonObject();
            for (int i = 0; i < splittedPath.length - 1; i++) root = root.get(splittedPath[i]).getAsJsonObject();

            return root.get(splittedPath[splittedPath.length - 1]);
        } catch (NullPointerException e) {
            JsonElement j = new JsonPrimitive("0");
            return j;
        }


    }
}

