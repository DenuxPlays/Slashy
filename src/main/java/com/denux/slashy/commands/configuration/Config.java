package com.denux.slashy.commands.configuration;

import com.denux.slashy.commands.configuration.subcommands.SetLogChannel;
import com.denux.slashy.commands.configuration.subcommands.SetMuteRole;
import com.denux.slashy.commands.configuration.subcommands.SetReportChannel;
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
    }

    @Override
    public void execute(SlashCommandEvent event) {

        var command = subCommands.get(event.getSubcommandName());
        if (command != null) command.execute(event);
    }
}