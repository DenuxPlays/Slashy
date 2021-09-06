package com.denux.slashy.commands.moderation;

import com.denux.slashy.Bot;
import com.denux.slashy.services.Config;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.Instant;
import java.util.Objects;

public class Ban {

    public void onBan(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        Member member = event.getOption("member").getAsMember();

        OptionMapping option = event.getOption("reason");
        String reason = option == null ? "None" : option.getAsString();

        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {

            event.getHook().sendMessage("**You don't have the ban members permission.**").queue();
            return;
        }

        if (member.hasPermission(Permission.BAN_MEMBERS)) {

            event.getHook().sendMessage("**You can't ban an administrator/moderator.**").queue();
        }

        var embed = new EmbedBuilder()
                .setTitle(member.getUser().getAsTag()+" | Ban")
                .setColor(Config.RED)
                .setTimestamp(Instant.now())
                .addField("Name", "```"+member.getUser().getAsTag()+"```", true)
                .addField("Moderator", "```"+event.getUser().getAsTag()+"```", true)
                .addField("Discord", "```"+ Objects.requireNonNull(event.getGuild()).getName()+"```", true)
                .addField("ID", "```"+event.getUser().getId()+"```", false)
                .addField("Reason", "```"+reason+"```", false)
                .setFooter(event.getUser().getAsTag()+Config.FOOTER_MESSAGE, event.getUser().getAvatarUrl())
                .build();
        try {
            member.getUser().openPrivateChannel().complete()
                    .sendMessageEmbeds(embed).queue();
        }
        catch (Exception exception) {
            Bot.logger.warn(exception.getClass().getSimpleName());
        }
        finally {
            member.ban(1, reason).queue();
            String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
            if (!logChannelID.equals("0")) {
                TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
                logChannel.sendMessageEmbeds(embed).queue();
            } else {
                event.getTextChannel().sendMessageEmbeds(embed).queue();
                event.getHook().sendMessage("Done").queue();
            }
        }
    }
}
