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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.denux.slashy.services.Database.mongoClient;

public class TempMuteListener {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(TempBanListener.class);

    private static ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();

    public void TempUnMute() {

        JDA jda = Bot.jda;

        threadPool.scheduleWithFixedDelay(() -> {

            MongoDatabase database = mongoClient.getDatabase("other");
            MongoCollection<Document> tempMute = database.getCollection("tempMute");

            BasicDBObject criteria = new BasicDBObject()
                    .append("unmuteTime", new BasicDBObject("$lte", Instant.now()));

            try {
                var it = tempMute.find(criteria).iterator();
                while (it.hasNext()) {
                    Document doc = tempMute.find(criteria).first();
                    try {
                        String guildID = doc.getString("guildID");
                        String memberID = doc.getString("memberID");
                        String moderatorID = doc.getString("moderatorID");
                        String muteTime = doc.getString("muteTime");
                        String reason = doc.getString("reason");

                        Guild guild = jda.getGuildById(guildID);
                        Member member = guild.getMemberById(memberID);
                        Member moderator = guild.getMemberById(moderatorID);

                        String muteRoleID = new Database().getConfig(guild, "muteRole").getAsString();
                        Role muteRole = guild.getRoleById(muteRoleID);

                        guild.removeRoleFromMember(member, muteRole).complete();

                        tempMute.deleteOne(doc);
                        String logChannelID = new Database().getConfig(guild, "logChannel").getAsString();
                        if (!logChannelID.equals("0")) {

                            TextChannel logChannel = guild.getTextChannelById(logChannelID);

                            var embed = new EmbedBuilder()
                                    .setAuthor(member.getUser().getAsTag() + "| Unmute", null, member.getUser().getEffectiveAvatarUrl())
                                    .setColor(Constants.YELLOW)
                                    .setTimestamp(Instant.now())
                                    .addField("Muted by", "```" + moderator.getUser().getAsTag() + "```", false)
                                    .addField("Muted on", "```" + muteTime + " | UTC ```", true)
                                    .addField("Reason", "```" + reason + "```", false)
                                    .setFooter(jda.getSelfUser().getAsTag() + Constants.FOOTER_MESSAGE, jda.getSelfUser().getEffectiveAvatarUrl())
                                    .build();

                            logChannel.sendMessageEmbeds(embed).queue();
                        }
                    } catch (Exception exception) {
                        logger.error("Error caused. Deleting Document.");
                        logger.error(exception.getClass().getSimpleName());
                        tempMute.deleteOne(doc);
                    }
                }
            } catch (NullPointerException exception) {
                logger.warn(exception.getClass().getSimpleName());
            } catch (Exception exception) {
                Document doc = tempMute.find(criteria).first();
                tempMute.deleteOne(doc);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
}
