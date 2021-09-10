package com.denux.slashy.commands.configuration;

import com.denux.slashy.commands.configuration.subcommands.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.HashMap;

public class Config implements ConfigCommandHandler{

    //Key, value -> List
    private HashMap<String, ConfigCommandHandler> subCommands;

    public Config() {

        //register subcommands
        this.subCommands = new HashMap<>();

        //Adding SubCommands Base=Config
        subCommands.put("setmuterole", new SetMuteRole());
        subCommands.put("setlogchannel", new SetLogChannel());
        subCommands.put("setreportchannel", new SetReportChannel());
        subCommands.put("setserverlock", new SetServerLock());
        subCommands.put("setstarboardchannel", new SetStarboardChannel());
        subCommands.put("setwarnlimit", new SetWarnLimit());
    }

    @Override
    public void execute(SlashCommandEvent event) {

        var command = subCommands.get(event.getSubcommandName());
        if (command != null) command.execute(event);
    }
}