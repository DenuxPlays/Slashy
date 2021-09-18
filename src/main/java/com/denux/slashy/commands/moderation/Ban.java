package com.denux.slashy.commands.moderation;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.services.Constants;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class Ban extends GuildSlashCommand implements SlashCommandHandler {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(Ban.class);

    public Ban() {
        this.commandData = new CommandData("ban", "Will ban the member permanently.")
                .addOption(OptionType.USER, "member", "This member will be banned.", true)
                .addOption(OptionType.STRING, "time", "Days, Hours, Minutes, Seconds", false)
                .addOption(OptionType.STRING, "reason", "Reason why the member was banned.", false);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply(true).queue();

        //Checks the permissions from the member
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {

            event.getHook().sendMessage("**You don't have the `ban members` permission.**").queue();
            return;
        }

        Member member = event.getOption("member").getAsMember();

        //Checks the permissions from the member you want to ban
        if (member.hasPermission(Permission.BAN_MEMBERS)) {

            event.getHook().sendMessage("**You can't ban an administrator/moderator.**").queue();
            return;
        }

        //Gets the option reason from the option we added in the SlashCommands Class
        OptionMapping option = event.getOption("reason");
        OptionMapping timeOption = event.getOption("time");
        String reason = option == null ? "None" : option.getAsString();
        String time = timeOption == null ? "None" : timeOption.getAsString();

        if (time.equals("None")) {

            var embed = new EmbedBuilder()
                    .setAuthor(member.getUser().getAsTag() + " | Ban", null, member.getUser().getEffectiveAvatarUrl())
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
            } else logger.warn("Cannot send the message to this user.");

            member.ban(1, reason).queue();

            String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
            if (!logChannelID.equals("0")) {
                TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
                logChannel.sendMessageEmbeds(embed).queue();
            } else {
                event.getTextChannel().sendMessageEmbeds(embed).queue();
            }
            event.getHook().sendMessage("Done").queue();

        } else {
            String[] split = time.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            String[] dayValue = new String[]{"days", "day", "d"};
            String[] hoursValue = new String[]{"hours", "hour", "h"};
            String[] minutesValue = new String[]{"minutes", "minute", "m"};
            String[] secondsValue = new String[]{"seconds", "s", "second", "sec"};

            //Days
            if (Arrays.asList(dayValue).contains(split[1])) {
                var unbanTime = Instant.now().plus(Long.parseLong(split[0]), ChronoUnit.DAYS);

                new Database().createTempBanEntry(member, event.getMember(), reason, unbanTime);


                var embed = tempBanEmbed(event.getGuild(), member, event.getMember(), reason, unbanTime);

                if (member.getUser().hasPrivateChannel()) {
                    member.getUser().openPrivateChannel().complete()
                            .sendMessageEmbeds(embed).queue();
                }
                else logger.warn("Cannot send the message to this user.");

                event.getGuild().ban(member.getUser(), 1).queue();

                String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
                if (!logChannelID.equals("0")) {
                    TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
                    logChannel.sendMessageEmbeds(embed).queue();
                } else {
                    event.getTextChannel().sendMessageEmbeds(embed).queue();
                }
                event.getHook().sendMessage("Done").queue();
                }

            //Hours
            else if (Arrays.asList(hoursValue).contains(split[1])) {
                var unbanTime = Instant.now().plus(Long.parseLong(split[0]), ChronoUnit.HOURS);

                new Database().createTempBanEntry(member, event.getMember(), reason, unbanTime);


                var embed = tempBanEmbed(event.getGuild(), member, event.getMember(), reason, unbanTime);

                if (member.getUser().hasPrivateChannel()) {
                    member.getUser().openPrivateChannel().complete()
                            .sendMessageEmbeds(embed).queue();
                }
                else logger.warn("Cannot send the message to this user.");

                event.getGuild().ban(member.getUser(), 1).queue();

                String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
                if (!logChannelID.equals("0")) {
                    TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
                    logChannel.sendMessageEmbeds(embed).queue();
                } else {
                    event.getTextChannel().sendMessageEmbeds(embed).queue();
                }
                event.getHook().sendMessage("Done").queue();
            }

            //Minutes
            else if (Arrays.asList(minutesValue).contains(split[1])) {
                var unbanTime = Instant.now().plus(Long.parseLong(split[0]), ChronoUnit.MINUTES);

                new Database().createTempBanEntry(member, event.getMember(), reason, unbanTime);


                var embed = tempBanEmbed(event.getGuild(), member, event.getMember(), reason, unbanTime);

                if (member.getUser().hasPrivateChannel()) {
                    member.getUser().openPrivateChannel().complete()
                            .sendMessageEmbeds(embed).queue();
                }
                else logger.warn("Cannot send the message to this user.");

                event.getGuild().ban(member.getUser(), 1).queue();

                String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
                if (!logChannelID.equals("0")) {
                    TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
                    logChannel.sendMessageEmbeds(embed).queue();
                } else {
                    event.getTextChannel().sendMessageEmbeds(embed).queue();
                }
                event.getHook().sendMessage("Done").queue();
            }
            else if (Arrays.asList(secondsValue).contains(split[1])) {
                var unbanTime = Instant.now().plus(Long.parseLong(split[0]), ChronoUnit.SECONDS);

                new Database().createTempBanEntry(member, event.getMember(), reason, unbanTime);


                var embed = tempBanEmbed(event.getGuild(), member, event.getMember(), reason, unbanTime);

                if (member.getUser().hasPrivateChannel()) {
                    member.getUser().openPrivateChannel().complete()
                            .sendMessageEmbeds(embed).queue();
                }
                else logger.warn("Cannot send the message to this user.");

                event.getGuild().ban(member.getUser(), 1).queue();

                String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
                if (!logChannelID.equals("0")) {
                    TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
                    logChannel.sendMessageEmbeds(embed).queue();
                } else {
                    event.getTextChannel().sendMessageEmbeds(embed).queue();
                }
                event.getHook().sendMessage("Done").queue();
            }
            else {

                //TODO better
                event.getHook().sendMessage("**Invalid time indicator.**").queue();
            }
        }

    }

    private MessageEmbed tempBanEmbed(Guild guild , Member member, Member moderator, String reason, Instant unbanTime) {

        var formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy H:mm:s", Locale.ENGLISH).withZone(ZoneId.of("UTC")).format(unbanTime);;

        var tempBanEmbed = new EmbedBuilder()
                .setAuthor(member.getUser().getAsTag() + " | Ban", null, member.getUser().getEffectiveAvatarUrl())
                .setColor(Constants.RED)
                .setTimestamp(Instant.now())
                .addField("Name", "```" + member.getUser().getAsTag() + "```", true)
                .addField("Moderator", "```" + moderator.getUser().getAsTag() + "```", true)
                .addField("Discord", "```" + guild.getName() + "```", true)
                .addField("ID", "```" + member.getUser().getId() + "```", false)
                .addField("Reason", "```" + reason + "```", false)
                .addField("Banned until", "```" + formatter + "```", false)
                .setFooter(moderator.getUser().getAsTag() + Constants.FOOTER_MESSAGE, moderator.getUser().getEffectiveAvatarUrl())
                .build();

        return tempBanEmbed;
    }
}
