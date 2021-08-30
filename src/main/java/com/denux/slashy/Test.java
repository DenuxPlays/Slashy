package com.denux.slashy;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Test {

    public void onTest(SlashCommandEvent event) {

        event.deferReply().setEphemeral(false).queue();

        var logChannel = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
        event.getHook().sendMessage(logChannel).queue();
        }
    }

