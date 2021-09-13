package com.denux.slashy.commands.info;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.services.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class Roadmap extends GuildSlashCommand implements SlashCommandHandler {

    public Roadmap () {
        this.commandData = new CommandData("roadmap", "Gives you a link to the roadmap");
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        var embed = new EmbedBuilder()
                .setTitle("Roadmap")
                .setColor(Constants.EMBED_GRAY)
                .setTimestamp(Instant.now())
                .setDescription("[Trello](https://trello.com/b/cWtef4oQ/slashy)\n" +
                                "[GitHub](https://github.com/DenuxPlays/Slashy)")
                .setFooter(event.getMember().getUser().getAsTag() + Constants.FOOTER_MESSAGE, event.getMember().getUser().getAvatarUrl())
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
