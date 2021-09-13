package com.denux.slashy.commands.moderation;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class UnLockdown extends GuildSlashCommand implements SlashCommandHandler {

    public UnLockdown () {
        this.commandData = new CommandData("unlockdown", "Unlocks the channel for normal users.");
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

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
