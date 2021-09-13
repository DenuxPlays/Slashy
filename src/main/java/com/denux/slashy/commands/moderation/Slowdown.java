package com.denux.slashy.commands.moderation;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class Slowdown extends GuildSlashCommand implements SlashCommandHandler {

    public Slowdown () {
        this.commandData = new CommandData("slowdown", "Sets the slow mode for a channel.")
                .addOption(OptionType.INTEGER, "seconds", "How long the slow mode would be.", true);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        int seconds = (int) event.getOption("seconds").getAsLong();

        event.deferReply().setEphemeral(false).queue();

        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {

            event.getHook().sendMessage("**You don't have the `manage channel` permission.**").queue();
            return;
        }

        event.getTextChannel().getManager().setSlowmode(seconds).queue();
        event.getHook().sendMessage("**The slow mode in this channel was set to:** `"+seconds+"s`").queue();
    }
}
