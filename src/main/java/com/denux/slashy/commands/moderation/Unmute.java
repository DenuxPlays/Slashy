package com.denux.slashy.commands.moderation;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.services.Constants;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.entities.Member;

import java.time.Instant;

public class Unmute extends GuildSlashCommand implements SlashCommandHandler {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(Unmute.class);

    public Unmute()  {
        this.commandData = new CommandData("unmute", "Umutes a muted member,")
                .addOption(OptionType.USER, "member", "The member you want to unmute.", true);

    }

    @Override
    public void execute(SlashCommandEvent event) {

        event.deferReply(true).queue();

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

            event.getHook().sendMessage("**You dont have the `manage message` permission.**").queue();
            return;
        }

        Member member = event.getOption("member").getAsMember();

        String muteRoleID = new Database().getConfig(event.getGuild(), "muteRole").getAsString();

        Role mutRole;
        if (!muteRoleID.equals("0")) {
            mutRole = event.getGuild().getRoleById(muteRoleID);
        } else {
            event.getHook().sendMessage("**You don't have a muterole.**").queue();
            return;
        }

        event.getGuild().removeRoleFromMember(member, mutRole).complete();

        var embed = new EmbedBuilder()
                .setAuthor(member.getUser().getAsTag() + " | Mute", null, member.getUser().getEffectiveAvatarUrl())
                .setColor(Constants.RED)
                .setTimestamp(Instant.now())
                .addField("Name", "```"+member.getUser().getAsTag()+"```", true)
                .addField("Moderator", "```"+event.getMember().getUser().getAsTag()+"```", true)
                .addField("User ID", "```"+member.getId()+"```", false)
                .setFooter(event.getMember().getUser().getAsTag()+ Constants.FOOTER_MESSAGE, event.getMember().getUser().getEffectiveAvatarUrl())
                .build();

        if (member.getUser().hasPrivateChannel()) {
            member.getUser().openPrivateChannel().complete()
                    .sendMessageEmbeds(embed).queue();
        }
        else logger.warn("Cannot send the message to this user.");

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
