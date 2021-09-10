package com.denux.slashy.commands.configuration.subcommands;

import com.denux.slashy.commands.configuration.ConfigCommandHandler;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SetServerLock implements ConfigCommandHandler {

    @Override
    public void execute(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

            event.getHook().sendMessage("**You don't have the `administrator` permission.**").queue();
            return;
        }

        OptionMapping option = event.getOption("status");
        boolean status = option.getAsBoolean();

        new Database().setDatabaseEntry(event.getGuild(), "serverLock", status);

        event.getHook().sendMessage("**You lock status is now: `"+status+"`**").queue();
    }
}
