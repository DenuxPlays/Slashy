package com.denux.slashy;
import com.denux.slashy.commands.moderation.Clear;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashCommands extends ListenerAdapter {

    void registerSlashCommands(Guild guild) {

        CommandListUpdateAction updateAction = guild.updateCommands();

        //Testing
        //updateAction.addCommands(new CommandData("test", "Testing Things."));

        //Moderation
        updateAction.addCommands(new CommandData("clear", "A command to clear messages in a channel.")
                .addOption(OptionType.INTEGER, "amount", "The amount you want to clear.",true));

        updateAction.queue();
    }
    @Override
    public void onReady(ReadyEvent event) {
        new Database().connectToDatabase();
        for(var guild : event.getJDA().getGuilds()) registerSlashCommands(guild);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        try {

            switch (event.getName()) {

                //Testing

                //Moderation
                case "clear":
                    new Clear().onClear(event); break;
            }
        } catch (Exception exception) {
            event.getHook().sendMessage(exception.getMessage()).queue();
        }

    }
}
