package com.denux.slashy.commands.info;

import com.denux.slashy.services.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Instant;

public class Roadmap {

    public void onRoadmap(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        var embed = new EmbedBuilder()
                .setTitle("Roadmap")
                .setColor(Config.EMBED_GREY)
                .setTimestamp(Instant.now())
                .setDescription("[Trello](https://trello.com/b/cWtef4oQ/slashy)\n" +
                                "[GitHub](https://github.com/DenuxPlays/Slashy)")
                .setFooter(event.getMember().getUser().getAsTag()+Config.FOOTER_MESSAGE, event.getMember().getUser().getAvatarUrl())
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
