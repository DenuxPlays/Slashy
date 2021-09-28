package com.denux.slashy.commands.moderation;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.services.Constants;
import com.denux.slashy.services.Database;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

import java.time.Instant;

public class ClearWarn extends GuildSlashCommand implements SlashCommandHandler {

    public ClearWarn() {
        this.commandData = new CommandData("clearwarns", "Clears all the warns from the member.")
                .addOption(OptionType.USER, "user", "The User you want to clear the warns");
    }

    @Override
    public void execute(SlashCommandEvent event) {

        event.deferReply(true).queue();

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getHook().sendMessage("**You dont have the `manage message` permission.**").queue();
            return;
        }

        User user = event.getOption("user").getAsUser();

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
            warns.deleteOne(it.next());
        }

        var embed = new EmbedBuilder()
                .setAuthor(user.getAsTag() + " | Cleared Warn(s)", null, user.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Constants.YELLOW)
                .addField("Cleared warns", "```" + warnCount + "```", true)
                .addField("Name", "```" + user.getAsTag() + "```", true)
                .addField("Moderator", "```" + event.getMember().getUser().getAsTag() + "```", true)
                .addField("ID", "```" + user.getId() + "```", false)
                .setFooter(event.getMember().getUser().getAsTag() + Constants.FOOTER_MESSAGE, event.getMember().getUser().getEffectiveAvatarUrl())
                .build();

        if (user.hasPrivateChannel()) {
            user.openPrivateChannel().complete()
                    .sendMessageEmbeds(embed).queue();
        }

        String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
        if (!logChannelID.equals("0")) {
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
            logChannel.sendMessageEmbeds(embed).queue();
        } else {
            event.getTextChannel().sendMessageEmbeds(embed).queue();
        }
        event.getHook().sendMessage("Done").setEphemeral(true).queue();
    }
}
