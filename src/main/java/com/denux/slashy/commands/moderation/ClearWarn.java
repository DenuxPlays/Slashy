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
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;

import java.time.Instant;

public class ClearWarn extends GuildSlashCommand implements SlashCommandHandler {

    public ClearWarn() {
        this.commandData = new CommandData("clearwarns", "Clears all the warns from the member.")
                .addOption(OptionType.USER, "member", "The member you want to clear the warns");
    }

    @Override
    public void execute(SlashCommandEvent event) {

        event.deferReply(true).queue();

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

            event.getHook().sendMessage("**You dont have the `manage message` permission.**").queue();
            return;
        }

        Member member = event.getOption("member").getAsMember();

        long warnCount = new Database().warnCount(member);

        if (warnCount == 0) {

            event.getHook().sendMessage("**This user hasn't any warnings.**").queue();
            return;
        }

        MongoDatabase database = Database.mongoClient.getDatabase("other");
        MongoCollection<Document> warns = database.getCollection("warns");

        StringBuilder stringBuilder = new StringBuilder();

        BasicDBObject criteria = new BasicDBObject()
                .append("guildID", member.getGuild().getId())
                .append("memberID", member.getId());
        MongoCursor<Document> it = warns.find(criteria).iterator();

        while (it.hasNext()) {
            warns.deleteOne(it.next());
        }

        var embed = new EmbedBuilder()
                .setAuthor(member.getUser().getAsTag() + " | Cleared Warn(s)", null, member.getUser().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(Constants.YELLOW)
                .addField("Cleared warns", "```" + warnCount + "```", true)
                .addField("Name", "```" + member.getUser().getAsTag() + "```", true)
                .addField("Moderator", "```" + event.getMember().getUser().getAsTag() + "```", true)
                .addField("ID", "```" + member.getId() + "```", false)
                .setFooter(event.getMember().getUser().getAsTag() + Constants.FOOTER_MESSAGE, event.getMember().getUser().getEffectiveAvatarUrl())
                .build();

        if (member.getUser().hasPrivateChannel()) {
            member.getUser().openPrivateChannel().complete()
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
