package com.denux.slashy.commands.configuration.subcommands;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashSubCommand;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public class SetServerLock extends GuildSlashSubCommand implements SlashCommandHandler {

    public SetServerLock () {
        this.subcommandData = new SubcommandData("setserverlock", "Locks or unlocks the server so that nobody can join.")
                .addOption(OptionType.BOOLEAN, "status", "Set this too true if you want to lock the server.", true);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.getHook().sendMessage("**You don't have the `administrator` permission.**").queue();
            return;
        }
        OptionMapping option = event.getOption("status");
        boolean status = option.getAsBoolean();

        new Database().setDatabaseEntry(event.getGuild(), "serverLock", status);
        event.getHook().sendMessage("**You lock status is now: `" + status + "`**").queue();
    }
}
