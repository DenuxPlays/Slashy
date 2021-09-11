package com.denux.slashy.commands.moderation;

import com.denux.slashy.Bot;
import com.denux.slashy.services.Config;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.Instant;

public class Mute {

    public void onMute(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

            event.getHook().sendMessage("**You dont have the `manage message` permission.**").queue();
            return;
        }

        Member member = event.getOption("member").getAsMember();

        if (member.hasPermission(Permission.MESSAGE_MANAGE)) {

            event.getHook().sendMessage("**You can't mute a moderator.**").queue();
            return;
        }

        OptionMapping option = event.getOption("reason");
        String reason = option == null ? "None" : option.getAsString();

        String muteRoleID = new Database().getConfig(event.getGuild(), "muteRole").getAsString();

        Role muteRole;
        if (!muteRoleID.equals("0")) {
            muteRole = event.getGuild().getRoleById(muteRoleID);
        } else {
            event.getHook().sendMessage("**You don't have a muterole.**").queue();
            return;
        }

        event.getGuild().addRoleToMember(member, muteRole).complete();

        var embed = new EmbedBuilder()
                .setTitle(member.getUser().getAsTag()+" | Mute")
                .setColor(Config.RED)
                .setTimestamp(Instant.now())
                .addField("Name", "```"+member.getUser().getAsTag()+"```", true)
                .addField("Moderator", "```"+event.getMember().getUser().getAsTag()+"```", true)
                .addField("User ID", "```"+member.getId()+"```", false)
                .addField("Reason", "```"+reason+"```", false)
                .setFooter(event.getMember().getUser().getAsTag()+Config.FOOTER_MESSAGE, event.getMember().getUser().getAvatarUrl())
                .build();

        if (member.getUser().hasPrivateChannel()) {
            member.getUser().openPrivateChannel().complete()
                    .sendMessageEmbeds(embed).queue();
        }
        else Bot.logger.warn("Cannot send the message to this user.");

        String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();

        if (!logChannelID.equals("0")) {
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
            logChannel.sendMessageEmbeds(embed).queue();
        } else {
            event.getTextChannel().sendMessageEmbeds(embed).queue();
        }
        event.getHook().sendMessage("Done").queue();
    }
}
