package com.denux.slashy.commands.info;

import com.denux.slashy.Bot;
import com.denux.slashy.services.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Instant;

public class Botinfo {

    public void onBotinfo(SlashCommandEvent event) {

        //Makes the "Slashy is thinking" and when its true its only visible for the User who sent the command
        event.deferReply().setEphemeral(true).queue();

        var embed = new EmbedBuilder()
                .setTitle("Botinfo")
                .setColor(Config.EMBED_GREY)
                .setTimestamp(Instant.now())
                .setThumbnail(Bot.jda.getSelfUser().getAvatarUrl())
                .addField("Name","`" + Bot.jda.getSelfUser().getAsTag() + "`", false)
                .addField("Library", "`JDA (Java)`", false)
                .addField("Numbers of Servers the Bot is online", "`" + Bot.jda.getGuilds().size() + "`", false)
                .addField("Users", "`" + Bot.jda.getUsers().size() + "`", false)
                .addField("Version", "`" + Config.VERSION + "`", true)
                .addField("Bot Owner", "`"+event.getJDA().retrieveUserById(Config.OWNER_ID).complete().getAsTag()+"`", false)
                .setFooter(event.getMember().getUser().getAsTag() + Config.FOOTER_MESSAGE, event.getMember().getUser().getAvatarUrl())
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();

    }
}
