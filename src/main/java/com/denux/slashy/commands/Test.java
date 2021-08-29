package com.denux.slashy.commands;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Test {

    public void onTest(SlashCommandEvent event) {

        String logChannelId = new Database().getLogChannel(event.getGuild().getId());
        event.getHook().sendMessage(logChannelId).queue();
    }
}