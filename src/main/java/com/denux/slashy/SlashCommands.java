package com.denux.slashy;

import com.denux.slashy.commands.info.Botinfo;
import com.denux.slashy.commands.info.Serverinfo;
import com.denux.slashy.commands.info.Userinfo;
import com.denux.slashy.commands.moderation.*;
import com.denux.slashy.services.Config;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.Date;

public class SlashCommands extends ListenerAdapter {

     public void registerSlashCommands(Guild guild) {

        //Adds slash commands to all the guilds
         //TODO global commands
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
        updateAction.addCommands(new CommandData("slowdown", "Sets the slow mode for a channel.")
                .addOption(OptionType.INTEGER, "seconds", "How long the slow mode would be.", true));
        updateAction.addCommands(new CommandData("lockdown", "Locks the channel for normal users.")
                .addOption(OptionType.STRING, "reason", "Reason why the channel is under lockdown.", false));
        updateAction.addCommands(new CommandData("unlockdown", "Unlocks the channel for normal users."));

        //Info
        updateAction.addCommands(new CommandData("botinfo", "Gives you the general information about the bot."));
        updateAction.addCommands(new CommandData("serverinfo", "Gives you the general information about the server."));
        updateAction.addCommands(new CommandData("userinfo", "Gives you a few information about a user.")
                .addOption(OptionType.USER, "member", "Member you want the information from.", true));

        //Config subcommand
        updateAction.addCommands(new CommandData("config", "Configuration commands.")
                .addSubcommands(new SubcommandData("setlogchannel", "Sets the LogChannel ID for your Server.")
                        .addOption(OptionType.CHANNEL, "logchannel", "The Channel you want to be the logChannel.", true)
                        .addOption(OptionType.BOOLEAN, "disabled", "Set this to true if you want to remove the logchannel", false))
                .addSubcommands(new SubcommandData("setmuterole", "Sets the Muterole ID for your server.")
                        .addOption(OptionType.ROLE, "muterole", "The role you want to be the muterole", true)
                        .addOption(OptionType.BOOLEAN, "disabled", "Set this to true if you want to remove the mutRole", false)));


        updateAction.queue();
    }
    @Override
    public void onReady(ReadyEvent event) {

        //Connecting to MongoDB when the bot is ready
        new Database().connectToDatabase();
        Bot.logger.info("Successfully connected to the Database.");

        //Adding commands to the guilds
        for(var guild : event.getJDA().getGuilds()) registerSlashCommands(guild);
        Bot.logger.info("SlashCommands loaded");
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        //Part 2 for async commands
        Bot.asyncPool.submit(() -> {

            //Returns when the command is used in a DM Channel
            if (event.getChannelType() == ChannelType.PRIVATE) return;

        //Try catch block for error handling
        try {
            switch (event.getName()) {

                //Testing
                case "test" -> new Test().onTest(event);

                //Moderation
                case "clear" -> new Clear().onClear(event);
                case "ban" -> new Ban().onBan(event);
                case "kick" -> new Kick().onKick(event);
                case "slowdown" -> new Slowdown().onSlowdown(event);
                case "lockdown" -> new Lockdown().onLockdown(event);
                case "unlockdown" -> new UnLockdown().onUnLockdown(event);

                //Info
                case "botinfo" -> new Botinfo().onBotinfo(event);
                case "serverinfo" -> new Serverinfo().onServerinfo(event);
                case "userinfo" -> new Userinfo().onUserinfo(event);

                //Adding the config subCommands
                case "config" -> new com.denux.slashy.commands.configuration.Config().execute(event);
            }

        //Throwing an exception for the Error handling embed
        } catch (Exception e) {

            //Error handling embed
            var embed = new EmbedBuilder()
                    .setColor(Config.RED)
                    .setAuthor(e.getClass().getSimpleName(), null, Bot.jda.getSelfUser().getEffectiveAvatarUrl())
                    .setDescription("```" + e.getMessage() + "```")
                    .setTimestamp(new Date().toInstant())
                    .build();

            event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
        }
    });
}
}
