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
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.mongodb.client.model.Filters.eq;

public class Database {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(Database.class);

    /**
     * A static Reference to the MongoDB Client.
     */
    public static MongoClient mongoClient;

    /**
     * Establishes a connection to the MongoDB Database.
     */
    public void connectToDatabase() {

        //Logging | Gives an error if something goes wrong
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.ERROR);

        //Connection to Database
        MongoClientURI uri = new MongoClientURI(new ConfigString("mongodb").getValue());
        mongoClient = new MongoClient(uri);
    }

    /**
     * Returns a JsonElement from a given path. Possible Paths include:
     * <ol>
     *     <li>guildID
     *     <li>logChannel
     *     <li>muteRole
     *     <li>serverLock
     *     <li>starboardChannel
     *     <li>warnLimit
     *     <li>reportChannel</li>
     * </ol>
     * @param guild The Guild the Config Document belongs to.
     * @param path The Path the Database entry is on.
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

        } else {
        //Creating a Config if the guild doesn't have one
        createConfig(guild);
        return new JsonPrimitive("0"); }
    }

    /**
     * Simple boolean to check if a Config Document for the given Guild already exists.
     */
    private boolean ifGuildDocExists(Guild guild) {
        MongoDatabase database = mongoClient.getDatabase("other");
        MongoCollection<Document> collection = database.getCollection("config");

        Document doc = collection.find(eq("guildID", guild.getId())).first();
        return doc != null;
    }

    /**
     * Creates a default Config Document for the given Guild.
     * @param guild The Guild the Config Document should be created for.
     */
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
            logger.info("Creating a Config with the guild id: " + guild.getId());
        } else logger.error("Guild already has a config.");
    }

    /**
     * Updates a Database entry on the given path.
     * @param guild The Guild the Config Document belongs to.
     * @param path The Path the Database entry is on.
     * @param newValue The value the Database Entry should be updated to.
     */
    public void setDatabaseEntry(Guild guild, String path, Object newValue) {
        if (ifGuildDocExists(guild)) {
            BasicDBObject setData = new BasicDBObject(path, newValue);
            BasicDBObject update = new BasicDBObject("$set", setData);

            Document query = new Document("guildID", guild.getId());
            mongoClient.getDatabase("other").getCollection("config").updateOne(query, update);
        } else {
           createConfig(guild);
           logger.warn("Creating a Config for the Guild:" + guild.getId() + " | setDatabaseEntry");

           BasicDBObject setData = new BasicDBObject(path, newValue);
           BasicDBObject update = new BasicDBObject("$set", setData);

           Document query = new Document("guildID", guild.getId());
           mongoClient.getDatabase("other").getCollection("config").updateOne(query, update);
        }
    }

    public void createWarnEntry(Guild guild, Member member, Member moderator, String reason) {

        MongoDatabase database = mongoClient.getDatabase("other");
        MongoCollection<Document> collection = database.getCollection("warns");

        var time = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy H:mm:s", Locale.ENGLISH).withZone(ZoneId.of("UTC")).format(Instant.now());

        Document doc = new Document()
                .append("guildID", guild.getId())
                .append("moderatorID", moderator.getId())
                .append("memberID", member.getId())
                .append("reason", reason)
                .append("time", time);

        collection.insertOne(doc);

        logger.info("Creating warn for the User: {} on the Guild: {}", member.getId(), guild.getId());
    }

    public int warnCount(Member member) {
        MongoDatabase database = mongoClient.getDatabase("other");
        MongoCollection<Document> warns = database.getCollection("warns");

        BasicDBObject criteria = new BasicDBObject()
                .append("guildID", member.getGuild().getId())
                .append("memberID", member.getId());

        return (int) warns.countDocuments(criteria);
    }

    public void createTempBanEntry(Member member, Member moderator, String reason, Instant unbanTime) {

        MongoDatabase database = mongoClient.getDatabase("other");
        MongoCollection<Document> collection = database.getCollection("tempBan");

        Document doc = new Document()
                .append("guildID", moderator.getGuild().getId())
                .append("moderatorID", moderator.getId())
                .append("memberID", member.getId())
                .append("reason", reason)
                .append("unbanTime", unbanTime);

        collection.insertOne(doc);

        logger.info("Creating tempBan for the User: {} on the Guild: {}", member.getId(), moderator.getGuild().getId());
    }
}

