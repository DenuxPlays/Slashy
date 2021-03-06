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
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Warn extends GuildSlashCommand implements SlashCommandHandler {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(Warn.class);

    public Warn() {
        this.commandData = new CommandData("warn", "Warns a Member for a specific reason.")
                .addOption(OptionType.USER, "member", "The Member you want to warn", true)
                .addOption(OptionType.STRING, "reason", "The reason why you warned the user.", false);
    }

    @Override
    public void execute(SlashCommandEvent event) {

        event.deferReply(true).queue();

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getHook().sendMessage("**You dont have the `manage message` permission.**").queue();
            return;
        }

        Member member = event.getOption("member").getAsMember();

        OptionMapping option = event.getOption("reason");
        String reason = option == null ? "None" : option.getAsString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy H:mm:s", Locale.ENGLISH).withZone(ZoneId.of("UTC"));

        new Database().createWarnEntry(event.getGuild(), member, event.getMember(), reason);

        var embed = new EmbedBuilder()
                .setAuthor(member.getUser().getAsTag() + " | Warn", null, member.getUser().getEffectiveAvatarUrl())
                .setColor(Constants.RED)
                .setTimestamp(Instant.now())
                .addField("Name", "```"+member.getUser().getAsTag()+"```", true)
                .addField("Moderator", "```"+event.getMember().getUser().getAsTag()+"```", true)
                .addField("User ID", "```"+member.getId()+"```", false)
                .addField("Time", "```"+formatter.format(Instant.now())+" | UTC ```", false)
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

        String warnLimit = new Database().getConfig(event.getGuild(), "warnLimit").getAsString();
        int warnCount = new Database().warnCount(member.getUser(), event.getGuild());

        if (warnLimit.equals("0")) {
            return;
        }
        else if (warnCount >= Integer.parseInt(warnLimit)) {

            if (member.getUser().hasPrivateChannel()) {
                member.getUser().openPrivateChannel().complete()
                        .sendMessage("**You have been banned from the guild: `"+member.getGuild().getName()+"` because you got too many warns.**").queue();
            } else logger.warn("Cannot send the message to this user.");

            if (!logChannelID.equals("0")) {
                TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
                logChannel.sendMessage("**The User: `"+member.getUser().getAsTag()+"` has been banned. (Too many warns)**").queue();
                event.getTextChannel().sendMessage("**The User: `"+member.getUser().getAsTag()+"` has been banned. (Too many warns)**").queue();
            } else {
                event.getTextChannel().sendMessage("**The User: `"+member.getUser().getAsTag()+"` has been banned. (Too many warns)**").queue();
            }

            member.ban(1,"Too many warns.").queue();
        }
        else return;
    }
}
