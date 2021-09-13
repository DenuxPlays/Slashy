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

public class SetWarnLimit extends GuildSlashSubCommand implements SlashCommandHandler {

    public SetWarnLimit () {
        this.subcommandData = new SubcommandData("setwarnlimit", "Sets the warn limit for your Server.")
                .addOption(OptionType.INTEGER, "limit", "The max amount of warns a User can have before getting banned.", true)
                .addOption(OptionType.BOOLEAN, "disabled", "Disables the warn limit for your Server.", false);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

            event.getHook().sendMessage("**You don't have the `administrator` permission.**").queue();
            return;
        }

        String limit = event.getOption("limit").getAsString();

        OptionMapping option = event.getOption("disabled");
        boolean disabled = option == null ? false : option.getAsBoolean();

        if (!disabled) {

            new Database().setDatabaseEntry(event.getGuild(), "warnLimit", limit);

            event.getHook().sendMessage("**The warn limit for your Server is now: `"+limit+"`**").queue();
        }
        else {

            new Database().setDatabaseEntry(event.getGuild(), "warnLimit", "0");

            event.getHook().sendMessage("**You don't have a warn limit anymore.**").queue();
        }
    }
}
