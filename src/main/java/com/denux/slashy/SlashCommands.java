package com.denux.slashy;

import com.denux.slashy.commands.info.Botinfo;
import com.denux.slashy.commands.info.Serverinfo;
import com.denux.slashy.commands.moderation.Ban;
import com.denux.slashy.commands.moderation.Clear;
import com.denux.slashy.commands.moderation.Kick;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.Date;

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
                .addOption(OptionType.STRING, "reason", "Reason why the member was banned.", false));
        updateAction.addCommands(new CommandData("kick", "Kicks a member from the discord.")
                .addOption(OptionType.USER, "member", "This member will be kicked.", true)
                .addOption(OptionType.STRING, "reason", "Reason why the member was kicked.", false));

        //Info
        updateAction.addCommands(new CommandData("botinfo", "Gives you the general information about the bot."));
        updateAction.addCommands(new CommandData("serverinfo", "Gives you the general information about the server."));

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
                case "test" -> new Test().onTest(event);

                //Moderation
                case "clear" -> new Clear().onClear(event);
                case "ban" -> new Ban().onBan(event);
                case "kick" -> new Kick().onKick(event);

                //Info
                case "botinfo" -> new Botinfo().onBotinfo(event);
                case "serverinfo" -> new Serverinfo().onServerinfo(event);
            }

        } catch (Exception e) {

            var embed = new EmbedBuilder()
                    .setAuthor(e.getClass().getSimpleName(), null, Bot.jda.getSelfUser().getEffectiveAvatarUrl())
                    .setDescription("```" + e.getMessage() + "```")
                    .setTimestamp(new Date().toInstant())
                    .build();

            event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
        }
    });
}
}
