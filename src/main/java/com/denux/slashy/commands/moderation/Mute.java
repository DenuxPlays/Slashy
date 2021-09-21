package com.denux.slashy.commands.moderation;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.services.Constants;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
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

public class Mute extends GuildSlashCommand implements SlashCommandHandler {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(Mute.class);

    public Mute() {
        this.commandData = new CommandData("mute", "Mutes a member.")
                .addOption(OptionType.USER, "member", "The member you want to mute.", true)
                .addOption(OptionType.STRING, "time", "days, hours, minutes, seconds", false)
                .addOption(OptionType.STRING, "reason", "The reason why the member gets muted.", false);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply(true).queue();

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
        OptionMapping timeOption = event.getOption("time");
        String reason = option == null ? "None" : option.getAsString();
        String time = timeOption == null ? "None" : timeOption.getAsString();

        String muteRoleID = new Database().getConfig(event.getGuild(), "muteRole").getAsString();

        Role muteRole;
        if (!muteRoleID.equals("0")) {
            muteRole = event.getGuild().getRoleById(muteRoleID);
        } else {
            event.getHook().sendMessage("**You don't have a muterole.**").queue();
            return;
        }

        if (time.equals("None")) {

            event.getGuild().addRoleToMember(member, muteRole).complete();

            var embed = new EmbedBuilder()
                    .setAuthor(member.getUser().getAsTag() + " | Mute", null, member.getUser().getEffectiveAvatarUrl())
                    .setColor(Constants.RED)
                    .setTimestamp(Instant.now())
                    .addField("Name", "```" + member.getUser().getAsTag() + "```", true)
                    .addField("Moderator", "```" + event.getMember().getUser().getAsTag() + "```", true)
                    .addField("User ID", "```" + member.getId() + "```", false)
                    .addField("Reason", "```" + reason + "```", false)
                    .setFooter(event.getMember().getUser().getAsTag() + Constants.FOOTER_MESSAGE, event.getMember().getUser().getEffectiveAvatarUrl())
                    .build();

            if (member.getUser().hasPrivateChannel()) {
                member.getUser().openPrivateChannel().complete()
                        .sendMessageEmbeds(embed).queue();
            } else logger.warn("Cannot send the message to this user.");

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
            String[] minutesValue = new String[]{"minutes", "minute", "m", "min"};
            String[] secondsValue = new String[]{"seconds", "s", "second", "sec"};

            //Days
            if (Arrays.asList(dayValue).contains(split[1])) {
                var unmuteTime = Instant.now().plus(Long.parseLong(split[0]), ChronoUnit.DAYS);

                new Database().createTempMuteEntry(member, event.getMember(), reason, unmuteTime);

                var embed = tempMuteEmbed(event.getGuild(), member, event.getMember(), reason, unmuteTime);

                if (member.getUser().hasPrivateChannel()) {
                    member.getUser().openPrivateChannel().complete()
                            .sendMessageEmbeds(embed).queue();
                }
                else logger.warn("Cannot send the message to this user.");

                event.getGuild().addRoleToMember(member, muteRole).complete();

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
                var unmuteTime = Instant.now().plus(Long.parseLong(split[0]), ChronoUnit.HOURS);

                new Database().createTempMuteEntry(member, event.getMember(), reason, unmuteTime);

                var embed = tempMuteEmbed(event.getGuild(), member, event.getMember(), reason, unmuteTime);

                if (member.getUser().hasPrivateChannel()) {
                    member.getUser().openPrivateChannel().complete()
                            .sendMessageEmbeds(embed).queue();
                }
                else logger.warn("Cannot send the message to this user.");

                event.getGuild().addRoleToMember(member, muteRole).complete();

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
                var unmuteTime = Instant.now().plus(Long.parseLong(split[0]), ChronoUnit.MINUTES);

                new Database().createTempMuteEntry(member, event.getMember(), reason, unmuteTime);

                var embed = tempMuteEmbed(event.getGuild(), member, event.getMember(), reason, unmuteTime);

                if (member.getUser().hasPrivateChannel()) {
                    member.getUser().openPrivateChannel().complete()
                            .sendMessageEmbeds(embed).queue();
                }
                else logger.warn("Cannot send the message to this user.");

                event.getGuild().addRoleToMember(member, muteRole).complete();

                String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();

                if (!logChannelID.equals("0")) {
                    TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
                    logChannel.sendMessageEmbeds(embed).queue();
                } else {
                    event.getTextChannel().sendMessageEmbeds(embed).queue();
                }
                event.getHook().sendMessage("Done").queue();
            }

            //Seconds
            else if (Arrays.asList(secondsValue).contains(split[1])) {
                var unmuteTime = Instant.now().plus(Long.parseLong(split[0]), ChronoUnit.SECONDS);

                new Database().createTempMuteEntry(member, event.getMember(), reason, unmuteTime);

                var embed = tempMuteEmbed(event.getGuild(), member, event.getMember(), reason, unmuteTime);

                if (member.getUser().hasPrivateChannel()) {
                    member.getUser().openPrivateChannel().complete()
                            .sendMessageEmbeds(embed).queue();
                }
                else logger.warn("Cannot send the message to this user.");

                event.getGuild().addRoleToMember(member, muteRole).complete();

                String logChannelID = new Database().getConfig(event.getGuild(), "logChannel").getAsString();

                if (!logChannelID.equals("0")) {
                    TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
                    logChannel.sendMessageEmbeds(embed).queue();
                } else {
                    event.getTextChannel().sendMessageEmbeds(embed).queue();
                }
                event.getHook().sendMessage("Done").queue();
            } else {
                event.getHook().sendMessage("**Invalid time indicator.**").queue();
            }
        }
    }

    private MessageEmbed tempMuteEmbed(Guild guild, Member member, Member moderator, String reason, Instant unmuteTime) {

        var formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy H:mm:s", Locale.ENGLISH).withZone(ZoneId.of("UTC")).format(unmuteTime);;

        var tempMuteEmbed = new EmbedBuilder()
                .setAuthor(member.getUser().getAsTag() + " | Mute", null, member.getUser().getEffectiveAvatarUrl())
                .setColor(Constants.RED)
                .setTimestamp(Instant.now())
                .addField("Name", "```" + member.getUser().getAsTag() + "```", true)
                .addField("Moderator", "```" + moderator.getUser().getAsTag() + "```", true)
                .addField("Discord", "```" + guild.getName() + "```", true)
                .addField("ID", "```" + member.getUser().getId() + "```", false)
                .addField("Reason", "```" + reason + "```", false)
                .addField("Muted until", "```" + formatter + " | UTC```", false)
                .setFooter(moderator.getUser().getAsTag() + Constants.FOOTER_MESSAGE, moderator.getUser().getEffectiveAvatarUrl())
                .build();

        return tempMuteEmbed;
    }
}
