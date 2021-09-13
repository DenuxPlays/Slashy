package com.denux.slashy.commands.moderation;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class Lockdown extends GuildSlashCommand implements SlashCommandHandler {

    public Lockdown () {
        this.commandData = new CommandData("lockdown", "Locks the channel for normal users.")
                .addOption(OptionType.STRING, "reason", "Reason why the channel is under lockdown.", false);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply().setEphemeral(false).queue();

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

            event.getHook().sendMessage("**You don't have the `mange messages` permission.**").queue();
            return;
        }

        OptionMapping option = event.getOption("reason");
        String reason = option == null ? "None" : option.getAsString();

        //Gets the Permission from the channel and sets it to "deny"
        EnumSet<Permission> readPerms = EnumSet.of(Permission.MESSAGE_WRITE);
        event.getTextChannel().putPermissionOverride(event.getGuild().getPublicRole()).deny(readPerms).queue();

        event.getHook().sendMessage("**The channel was closed with the reason:** `"+reason+"`").queue();
    }
}
