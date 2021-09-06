package com.denux.slashy.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Slowdown {

    public void onSlowdown(SlashCommandEvent event) {

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
