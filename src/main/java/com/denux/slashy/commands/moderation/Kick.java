package com.denux.slashy.commands.moderation;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.services.Constants;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;

public class Kick extends GuildSlashCommand implements SlashCommandHandler {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(Kick.class);

    public Kick () {
        this.commandData = new CommandData("kick", "Kicks a member from the discord.")
                .addOption(OptionType.USER, "member", "This member will be kicked.", true)
                .addOption(OptionType.STRING, "reason", "Reason why the member was kicked.", false);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply(true).queue();
        Member member = event.getOption("member").getAsMember();

        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.getHook().sendMessage("**You don't have the `kick member` permission.**").queue();
            return;
        }

        if (member.hasPermission(Permission.KICK_MEMBERS)) {
            event.getHook().sendMessage("**You can't ban an administrator/moderator.**").queue();
            return;
        }

        OptionMapping option = event.getOption("reason");
        String reason = option == null ? "None" : option.getAsString();

        var embed = new EmbedBuilder()
                .setAuthor(member.getUser().getAsTag() + " | Mute", null, member.getUser().getEffectiveAvatarUrl())
                .setColor(Constants.RED)
                .setTimestamp(Instant.now())
                .addField("Name", "```" + member.getUser().getAsTag() + "```", true)
                .addField("Moderator", "```" + event.getUser().getAsTag() + "```", true)
                .addField("Discord", "```" + Objects.requireNonNull(event.getGuild()).getName() + "```", true)
                .addField("ID", "```" + event.getUser().getId() + "```", false)
                .addField("Reason", "```" + reason + "```", false)
                .setFooter(event.getUser().getAsTag() + Constants.FOOTER_MESSAGE, event.getUser().getEffectiveAvatarUrl())
                .build();

            if (member.getUser().hasPrivateChannel()) {
                member.getUser().openPrivateChannel().complete()
                        .sendMessageEmbeds(embed).queue();
            }
            else logger.warn("Cannot send messages to this user");

            member.kick(reason).queue();
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

