package com.denux.slashy.commands.moderation;

import com.denux.slashy.Bot;
import com.denux.slashy.SlashCommands;
import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.services.Constants;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;

public class Ban extends GuildSlashCommand implements SlashCommandHandler {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(Ban.class);

    public Ban () {
        this.commandData = new CommandData("ban", "Will ban the member permanently.")
                .addOption(OptionType.USER, "member", "This member will be banned.", true)
                .addOption(OptionType.STRING, "reason", "Reason why the member was banned.", false);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

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
        String reason = option == null ? "None" : option.getAsString();

        var embed = new EmbedBuilder()
                .setTitle(member.getUser().getAsTag()+" | Ban")
                .setColor(Constants.RED)
                .setTimestamp(Instant.now())
                .addField("Name", "```"+member.getUser().getAsTag()+"```", true)
                .addField("Moderator", "```"+event.getUser().getAsTag()+"```", true)
                .addField("Discord", "```"+ Objects.requireNonNull(event.getGuild()).getName()+"```", true)
                .addField("ID", "```"+event.getUser().getId()+"```", false)
                .addField("Reason", "```"+reason+"```", false)
                .setFooter(event.getUser().getAsTag()+Constants.FOOTER_MESSAGE, event.getUser().getAvatarUrl())
                .build();
        if (member.getUser().hasPrivateChannel()) {
            member.getUser().openPrivateChannel().complete()
                    .sendMessageEmbeds(embed).queue();
        }
        else logger.warn("Cannot send the message to this user.");

        member.ban(1, reason).queue();

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
