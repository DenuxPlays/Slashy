package com.denux.slashy.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.EnumSet;

public class Lockdown {

    public void onLockdown(SlashCommandEvent event) {

        event.deferReply().setEphemeral(false).queue();

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

            event.getHook().sendMessage("**You don't have the `mange messages` permission.**").queue();
            return;
        }

        OptionMapping option = event.getOption("reason");
        String reason = option == null ? "None" : option.getAsString();

        EnumSet<Permission> readPerms = EnumSet.of(Permission.MESSAGE_WRITE);
        event.getTextChannel().putPermissionOverride(event.getGuild().getPublicRole()).deny(readPerms).queue();

        event.getHook().sendMessage("**The channel was closed with the reason:** `"+reason+"`").queue();
    }
}
