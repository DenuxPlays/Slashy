package com.denux.slashy.commands.info;

import com.denux.slashy.services.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Serverinfo {

    public void onServerinfo(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        //Formats the time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy H:mm:s", Locale.ENGLISH);

        var embed = new EmbedBuilder()
                .setTitle("Serverinfo")
                .setTimestamp(Instant.now())
                .setColor(Config.EMBED_GREY)
                .setThumbnail(event.getGuild().getIconUrl())
                .addField("Name", "```" + event.getGuild().getName() + "```", true)
                .addField("Owner", "```" + event.getGuild().getOwner().getUser().getAsTag() + "```", true)
                .addField("ID", "```" + event.getGuild().getId() + "```", false)
                .addField("Roles", "```"+event.getGuild().getRoles().size()+" Roles```", true)
                .addField("Channel count", "```"+event.getGuild().getTextChannels().size()+" Text channels\n" +
                        +event.getGuild().getVoiceChannels().size()+" Voice channels```", false)
                .addField("Member count", "```"+event.getGuild().getMembers().size()+" Members```", false)
                .addField("Server created on", "```"+event.getGuild().getTimeCreated().format(formatter)+" | UTC ```", false)
                .setFooter(event.getMember().getUser().getAsTag()+Config.FOOTER_MESSAGE, event.getMember().getUser().getAvatarUrl())
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
