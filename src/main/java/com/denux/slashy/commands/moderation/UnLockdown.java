package com.denux.slashy.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.EnumSet;

public class UnLockdown {

    public void onUnLockdown(SlashCommandEvent event) {

        event.deferReply().setEphemeral(false).queue();

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

            event.getHook().sendMessage("**You don't have the `mange messages` permission.**").queue();
            return;
        }

        EnumSet<Permission> readPerms = EnumSet.of(Permission.MESSAGE_WRITE);
        event.getTextChannel().putPermissionOverride(event.getGuild().getPublicRole()).clear(readPerms).queue();

        event.getHook().sendMessage("**The channel is now open again.**").queue();
    }
}
