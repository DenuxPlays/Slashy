package com.denux.slashy.commands.configuration.subcommands;

import com.denux.slashy.commands.configuration.ConfigCommandHandler;
import com.denux.slashy.services.Config;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Instant;

public class List implements ConfigCommandHandler {

    @Override
    public void execute(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

            event.getHook().sendMessage("**You don't have the `administrator` permission.**").queue();
            return;
        }

        //Log Channel
        String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
        String logChannel;
        if (logChannelID.equals("0")) {
            logChannel = "`You don't have a log channel.`";
        }
        else {
            logChannel = "<#"+logChannelID+">";
        }

        //Mute Role
        String muteRoleID = new Database().getConfig(event.getGuild(), "muteRole").getAsString();
        String muteRole;
        if (muteRoleID.equals("0")) {
            muteRole = "`You don't have a muterole.`";
        }
        else {
            muteRole = "<@&"+muteRoleID+">";
        }

        //Starboard Channel
        String starboardChannelID = new Database().getConfig(event.getGuild(), "starboardChannel").getAsString();
        String starboardChannel;
        if (starboardChannelID.equals("0")) {
            starboardChannel = "`You don't have a starboard.`";
        }
        else {
            starboardChannel = "<#"+starboardChannelID+">";
        }

        //Server Lock
        String status = new Database().getConfig(event.getGuild(), "serverLock").getAsString();
        if (status.equals("false")) status = "`Open`";
        else status = "`Closed`";

        //Warn Limit
        String warnLimit = new Database().getConfig(event.getGuild(), "warnLimit").getAsString();
        if (warnLimit.equals("0")) {
            warnLimit = "`You don't have a warn limit.`";
        }
        else {
            warnLimit = "`"+warnLimit+"`";
        }

        //Report Channel
        String reportChannelID = new Database().getConfig(event.getGuild(), "reportChannel").getAsString();
        String reportChannel;
        if (reportChannelID.equals("0")) {

            reportChannel = "`You don't have a report channel.`";
        }
        else {

            reportChannel = "<#"+reportChannelID+">";
        }

        var embed = new EmbedBuilder()
                .setTitle("Config")
                .setColor(Config.EMBED_GREY)
                .setTimestamp(Instant.now())
                .setThumbnail(event.getGuild().getIconUrl())
                .addField("Log Channel", logChannel, true)
                .addField("Mute Role", muteRole, true)
                .addField("Starboard Channel", starboardChannel, false)
                .addField("Server lock Status", status, false)
                .addField("Warn limit", warnLimit, true)
                .addField("Report Channel", reportChannel, false)
                .setFooter(event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl())
                .build();

        event.getHook().sendMessageEmbeds(embed).queue();
    }
}
