package com.denux.slashy.commands.configuration.subcommands;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashSubCommand;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public class SetMuteRole extends GuildSlashSubCommand implements SlashCommandHandler {

    public SetMuteRole () {
        this.subcommandData = new SubcommandData("setmuterole", "Sets the Muterole ID for your server.")
                .addOption(OptionType.ROLE, "muterole", "The role you want to be the muterole", true)
                .addOption(OptionType.BOOLEAN, "disabled", "Set this to true if you want to remove the mutRole", false);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.getHook().sendMessage("**You don't have the `administrator` permission.**").queue();
            return;
        }
        Role muteRole = event.getOption("muterole").getAsRole();

        OptionMapping option = event.getOption("disabled");
        boolean disabled = option == null ? false : option.getAsBoolean();
        if (!disabled) {
            new Database().setDatabaseEntry(event.getGuild(), "muteRole", muteRole.getId());
            event.getHook().sendMessage("**The role: `" + muteRole.getName() + "` is now your muterole.**").queue();
        } else {
            new Database().setDatabaseEntry(event.getGuild(), "muteRole", "0");
            event.getHook().sendMessage("**You don't have a muterole anymore.**").queue();
        }
    }
}
