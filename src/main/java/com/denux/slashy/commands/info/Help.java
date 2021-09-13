package com.denux.slashy.commands.info;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.services.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.Instant;

public class Help extends GuildSlashCommand implements SlashCommandHandler {

    public Help() { this.commandData = new CommandData("help", "Gives you the most important links."); }

    @Override
    public void execute(SlashCommandEvent event) {

        event.deferReply(true).queue();

        var embed = new EmbedBuilder()
                .setTitle("Help")
                .setColor(Constants.EMBED_GRAY)
                .setTimestamp(Instant.now())
                .setDescription("Discord: https://discord.gg/Mwn5DSTGsy\n" +
                                "Docs: https://denux.gitbook.io/slashy/\n" +
                                "GitHub: https://github.com/DenuxPlays/Slashy")
                .setFooter(event.getMember().getUser().getAsTag()+Constants.FOOTER_MESSAGE, event.getMember().getUser().getEffectiveAvatarUrl())
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
