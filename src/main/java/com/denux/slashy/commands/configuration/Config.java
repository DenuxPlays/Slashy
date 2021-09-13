package com.denux.slashy.commands.configuration;

import com.denux.slashy.commands.configuration.subcommands.*;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Config extends GuildSlashCommand {

    public Config() {
        this.commandData = new CommandData("config", "Configuration commands.");
        this.subCommandClasses = new Class[]{
                List.class, SetLogChannel.class, SetMuteRole.class, SetReportChannel.class,
                SetServerLock.class, SetStarboardChannel.class, SetWarnLimit.class};
    }
}