package com.denux.slashy.listener;

import com.denux.slashy.Bot;
import com.denux.slashy.services.Constants;
import com.denux.slashy.services.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
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
                String moderatorID = doc.getString("moderatorID");
                String banTime = doc.getString("banTime");
                String reason = doc.getString("reason");

                Guild guild = jda.getGuildById(guildID);
                User user = jda.retrieveUserById(memberID).complete();
                User moderator = jda.retrieveUserById(moderatorID).complete();

                try {
                    guild.unban(user).queue();
                } catch (Exception exception) {
                    logger.warn("User is already unbanned.");
                } finally {
                    tempBan.deleteOne(doc);
                }

                String logChannelID = new Database().getConfig(guild, "logChannel").getAsString();
                if (!logChannelID.equals("0")) {

                    TextChannel logChannel = guild.getTextChannelById(logChannelID);

                    var embed = new EmbedBuilder()
                            .setAuthor(user.getAsTag() + "| Unban", null, user.getEffectiveAvatarUrl())
                            .setColor(Constants.YELLOW)
                            .setTimestamp(Instant.now())
                            .addField("Banned by", "```" + moderator.getAsTag() + "```", false)
                            .addField("Banned on", "```" + banTime + " | UTC ```", true)
                            .addField("Reason", "```" + reason + "```", false)
                            .setFooter(jda.getSelfUser().getAsTag() + Constants.FOOTER_MESSAGE, jda.getSelfUser().getEffectiveAvatarUrl())
                            .build();

                    logChannel.sendMessageEmbeds(embed).queue();
                }

            } catch (Exception exception) {
                logger.error(exception.getClass().getSimpleName());
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
}