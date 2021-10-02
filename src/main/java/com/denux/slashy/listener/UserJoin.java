package com.denux.slashy.listener;

import com.denux.slashy.commands.moderation.Ban;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

public class UserJoin extends ListenerAdapter {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(Ban.class);

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        boolean serverLock = new Database().getConfig(event.getGuild(), "serverLock").getAsBoolean();

        if (serverLock) {

            if (event.getMember().getUser().hasPrivateChannel()) {
                event.getMember().getUser().openPrivateChannel().complete()
                        .sendMessage("This Server is locked.").queue();
            } else logger.warn("Cannot send the message to this user.");
            event.getMember().kick("Server is locked.").complete();
        }
    }
}
