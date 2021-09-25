package com.denux.slashy.commands.moderation;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.services.Constants;
import com.denux.slashy.services.Database;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

import java.time.Instant;

public class WarnList extends GuildSlashCommand implements SlashCommandHandler {

    public WarnList() {
        this.commandData = new CommandData("warnlist", "Gives you the warns from the member.")
                .addOption(OptionType.USER, "member", "The Member you want the warns from.", true);
    }

    @Override
    public void execute(SlashCommandEvent event) {

        event.deferReply(false).queue();

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

            event.getHook().sendMessage("**You dont have the `manage message` permission.**").queue();
            return;
        }

        User user = event.getOption("member").getAsUser();

        long warnCount = new Database().warnCount(user, event.getGuild());

        if (warnCount == 0) {

            event.getHook().sendMessage("**This user hasn't any warnings.**").queue();
            return;
        }

        MongoDatabase database = Database.mongoClient.getDatabase("other");
        MongoCollection<Document> warns = database.getCollection("warns");

        StringBuilder stringBuilder = new StringBuilder();

        BasicDBObject criteria = new BasicDBObject()
                .append("guildID", event.getGuild().getId())
                .append("memberID", user.getId());
        MongoCursor<Document> it = warns.find(criteria).iterator();

        while (it.hasNext()) {
            JsonObject root = JsonParser.parseString(it.next().toJson()).getAsJsonObject();
            String moderatorID = root.get("moderatorID").getAsString();
            String moderator = event.getGuild().getMemberById(moderatorID).getUser().getAsTag();
            String reason = root.get("reason").getAsString();
            String date = root.get("time").getAsString();
            stringBuilder.append("\n\n")
                    .append("Moderator: ").append(moderator)
                    .append("\nReason: ").append(reason)
                    .append("\nDate: ").append(date);
        }

        var embed = new EmbedBuilder()
                .setAuthor(user.getAsTag() + " | Warns", null, user.getEffectiveAvatarUrl())
                .setDescription("```" + user.getAsTag() + " has been warned " + warnCount + " times so far." +
                        "\n" + stringBuilder + "```")
                .setColor(Constants.YELLOW)
                .setFooter(event.getMember().getUser().getAsTag()+Constants.FOOTER_MESSAGE, event.getMember().getUser().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
