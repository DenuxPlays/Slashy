package com.denux.slashy.commands.configuration.subcommands;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashSubCommand;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public class SetLogChannel extends GuildSlashSubCommand implements SlashCommandHandler {

    public SetLogChannel () {
        this.subcommandData = new SubcommandData("setlogchannel", "Sets the LogChannel ID for your Server.")
                .addOption(OptionType.CHANNEL, "logchannel", "The Channel you want to be the logChannel.", true)
                .addOption(OptionType.BOOLEAN, "disabled", "Set this to true if you want to remove the logchannel", false);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

            event.getHook().sendMessage("**You don't have the `administrator` permission.**").queue();
            return;
        }

        GuildChannel logChannel = event.getOption("logchannel").getAsGuildChannel();

        OptionMapping option = event.getOption("disabled");
        boolean disabled = option == null ? false : option.getAsBoolean();

        if (!disabled) {

            new Database().setDatabaseEntry(event.getGuild(), "logChannel", logChannel.getId());

            event.getHook().sendMessage("**The channel: `"+logChannel.getName()+"` is now your logChannel.**").queue();
        }
        else {

            new Database().setDatabaseEntry(event.getGuild(), "logChannel", "0");

            event.getHook().sendMessage("**You don't have a logChannel anymore.**").queue();
        }
    }
}
