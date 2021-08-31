package com.denux.slashy;
import com.denux.slashy.commands.moderation.Ban;
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
        updateAction.addCommands(new CommandData("test", "Testing Things."));

        //Moderation
        updateAction.addCommands(new CommandData("clear", "A command to clear messages in a channel.")
                .addOption(OptionType.INTEGER, "amount", "The amount you want to clear.",true));
        updateAction.addCommands(new CommandData("ban", "Will ban the member permanently.")
                .addOption(OptionType.USER, "member", "This member will be banned.", true)
                .addOption(OptionType.STRING, "reason", "Reason why the user was banned.", false));

        //Adding commands to the guilds
        updateAction.queue();
    }
    @Override
    public void onReady(ReadyEvent event) {
        new Database().connectToDatabase();
        for(var guild : event.getJDA().getGuilds()) registerSlashCommands(guild);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        Bot.asyncPool.submit(() -> {

        try {

            switch (event.getName()) {

                //Testing
                case "test" : new Test().onTest(event); break;

                //Moderation
                case "clear": new Clear().onClear(event); break;
                case "ban" : new Ban().onBan(event); break;
            }
        } catch (NullPointerException exception) {
            throw new NullPointerException("null");
        }
        catch (Exception e) {
            e.printStackTrace();
            event.getHook().sendMessage(e.getClass().getSimpleName()).queue();
        }
    });
}
}
