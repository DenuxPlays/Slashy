package com.denux.slashy.listener;

import com.denux.slashy.Bot;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.denux.slashy.services.Database.mongoClient;

public class TempBanListener {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(TempBanListener.class);

    private static ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();

    public void TempUnBan() {

        JDA jda = Bot.jda;

        threadPool.scheduleWithFixedDelay(() -> {

            MongoDatabase database = mongoClient.getDatabase("other");
            MongoCollection<Document> tempBan = database.getCollection("tempBan");

            BasicDBObject criteria = new BasicDBObject()
                    .append("unbanTime", new BasicDBObject("$lte", Instant.now()));

            try {
                Document doc = tempBan.find(criteria).first();

                String guildID = doc.getString("guildID");
                String memberID = doc.getString("memberID");

                Guild guild = jda.getGuildById(guildID);
                User user = jda.retrieveUserById(memberID).complete();

                try {
                    guild.unban(user).queue();
                } catch (Exception exception) {
                    logger.warn("User is already unbanned.");
                } finally {
                    tempBan.deleteOne(doc);
                }

            } catch (Exception exception) {
                logger.error(exception.getClass().getSimpleName());
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
}