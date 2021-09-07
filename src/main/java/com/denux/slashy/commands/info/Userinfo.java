package com.denux.slashy.commands.info;

import com.denux.slashy.services.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Userinfo {

    public void onUserinfo(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        //Gets the member from the option we created in the SlashCommands Class
        Member member = event.getOption("member").getAsMember();

        //Formats the time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy H:mm:s", Locale.ENGLISH);

        var embed = new EmbedBuilder()
                .setTitle("Userinfo for "+member.getUser().getAsTag())
                .setColor(Config.EMBED_GREY)
                .setTimestamp(Instant.now())
                .addField("Name", "```"+member.getUser().getAsTag()+"```", false)
                .addField("ID", "```"+member.getId()+"```", false)
                .addField("Joined on", "```"+member.getTimeJoined().format(formatter)+"```", false)
                .addField("Account created at", "```"+member.getUser().getTimeCreated().format(formatter)+"```", false)
                .setFooter(event.getMember().getUser().getAsTag()+Config.FOOTER_MESSAGE, event.getMember().getUser().getAvatarUrl())
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
