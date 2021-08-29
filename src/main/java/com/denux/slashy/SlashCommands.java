package com.denux.slashy;
import com.denux.slashy.commands.Test;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashCommands extends ListenerAdapter {

    void registerSlashCommands(Guild guild) {

        CommandListUpdateAction updateAction = guild.updateCommands();

        updateAction.addCommands(new CommandData("test", "Testing Things."));

        updateAction.queue();
    }
    @Override
    public void onReady(ReadyEvent event) {
        new Database().connectToDatabase();
        for(var guild : event.getJDA().getGuilds()) registerSlashCommands(guild);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        switch (event.getName()) {
            case "test" : new Test().onTest(event); break;
        }

    }
}
