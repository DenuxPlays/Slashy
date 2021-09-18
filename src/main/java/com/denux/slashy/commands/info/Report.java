package com.denux.slashy.commands.info;

import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.commands.moderation.Ban;
import com.denux.slashy.services.Constants;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class Report extends GuildSlashCommand implements SlashCommandHandler {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(Ban.class);

    public Report() {
        this.commandData = new CommandData("report", "Reports a member if the Server has set a report channel.")
                .addOption(OptionType.USER, "member", "The member you want to report.", true)
                .addOption(OptionType.STRING, "reason", "Explain what the member did.", true);
    }

    @Override
    public void execute(SlashCommandEvent event) {

        event.deferReply(true).queue();

        String reportChannelID = new Database().getConfig(event.getGuild(), "reportChannel").getAsString();

        if (!reportChannelID.equals("0")) {

            TextChannel reportChannel = event.getGuild().getTextChannelById(reportChannelID);

            Member member = event.getOption("member").getAsMember();
            String reason = event.getOption("reason").getAsString();

            var embed = new EmbedBuilder()
                    .setAuthor(member.getUser().getAsTag() + " | Report", null, member.getUser().getEffectiveAvatarUrl())
                    .setColor(Constants.YELLOW)
                    .setTimestamp(Instant.now())
                    .addField("Report by", event.getMember().getAsMention(), false)
                    .addField("Reported User", member.getAsMention(), false)
                    .addField("Reason", "```" + reason + "```", true)
                    .setFooter(event.getUser().getAsTag() + Constants.FOOTER_MESSAGE, event.getUser().getEffectiveAvatarUrl())
                    .build();

            reportChannel.sendMessageEmbeds(embed).queue();

            event.getHook().sendMessage("**Your report has been sent to the Report Channel**").queue();
        }
        else {
            event.getHook().sendMessage("This server doesn't support reports.").queue();
        }
    }
}
