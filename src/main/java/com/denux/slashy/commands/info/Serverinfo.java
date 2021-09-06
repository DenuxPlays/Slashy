package com.denux.slashy.commands.info;

import com.denux.slashy.services.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Instant;

public class Serverinfo {

    public void onServerinfo(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        var embed = new EmbedBuilder()
                .setTitle("Serverinfo")
                .setTimestamp(Instant.now())
                .setColor(Config.EMBED_GREY)
                .setThumbnail(event.getGuild().getIconUrl())
                .addField("Name", "```" + event.getGuild().getName() + "```", true)
                .addField("Owner", "```" + event.getGuild().getOwner().getUser().getAsTag() + "```", true)
                .addField("ID", "```" + event.getGuild().getId() + "```", false)
                .addField("Region", "```" + event.getGuild().retrieveRegions().complete() + "```", true)
                .build();
    }
}
