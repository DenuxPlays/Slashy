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

public class SetReportChannel extends GuildSlashSubCommand implements SlashCommandHandler {

    public SetReportChannel () {
        this.subcommandData = new SubcommandData("setreportchannel", "Sets the ReportChannel ID for your Server.")
                .addOption(OptionType.CHANNEL, "reportchannel", "The Channel you want to be the ReportChannel.", true)
                .addOption(OptionType.BOOLEAN, "disabled", "Set this to true if you want to remove the ReportChannel", false);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.getHook().sendMessage("**You don't have the `administrator` permission.**").queue();
            return;
        }
        GuildChannel reportChannel = event.getOption("reportchannel").getAsGuildChannel();

        OptionMapping option = event.getOption("disabled");
        boolean disabled = option == null ? false : option.getAsBoolean();

        if (!disabled) {
            new Database().setDatabaseEntry(event.getGuild(), "reportChannel", reportChannel.getId());
            event.getHook().sendMessage("**The channel: `" + reportChannel.getName() + "` is now your reportChannel.**").queue();
        } else {
            new Database().setDatabaseEntry(event.getGuild(), "reportChannel", "0");
            event.getHook().sendMessage("**You don't have a logChannel anymore.**").queue();
        }
    }
}
