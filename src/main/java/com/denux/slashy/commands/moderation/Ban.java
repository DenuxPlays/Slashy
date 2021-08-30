package com.denux.slashy.commands.moderation;

import com.denux.slashy.Bot;
import com.denux.slashy.services.Config;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Instant;
import java.util.Objects;

public class Ban {

    public void onBan(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        Member member = (Member) event.getOption("member");
        String reason;
        reason = Objects.requireNonNull(event.getOption("reason")).toString();

        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.BAN_MEMBERS)) {

            event.getHook().sendMessage("**You don't have the manage message permission.**").queue();
            return;
        }

        assert member != null;
        if (member.hasPermission(Permission.BAN_MEMBERS)) {

            event.getHook().sendMessage("**You can't ban an administrator/moderator.**").queue();
        }

        var embed = new EmbedBuilder()
                .setTitle(member+" | Ban")
                .setColor(Config.RED)
                .setTimestamp(Instant.now())
                .addField("Name", "```"+member+"```", true)
                .addField("Moderator", "```"+event.getUser().getAsTag()+"```", true)
                .addField("Guild", "```"+ Objects.requireNonNull(event.getGuild()).getName()+"```", true)
                .addField("ID", "```"+event.getUser().getId()+"```", false)
                .addField("Reason", "```"+reason+"```", false)
                .setFooter(event.getUser().getAsTag()+Config.FOOTER_MESSAGE, event.getUser().getAvatarUrl())
                .build();
        try {
            member.getUser().openPrivateChannel().complete()
                    .sendMessageEmbeds(embed).queue();
        }
        catch (Exception exception) {
            Bot.logger.warn(exception.getMessage());
        //member.ban(1, reason).queue();
        String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
        if (!logChannelID.equals("0")) {
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
            assert logChannel != null;
            logChannel.sendMessageEmbeds(embed).queue();
        }
        }
    }
}
