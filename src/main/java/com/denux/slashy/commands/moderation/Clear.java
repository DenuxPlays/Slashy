package com.denux.slashy.commands.moderation;
import com.denux.slashy.services.Config;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.sql.SQLOutput;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Clear {

    public void onClear(SlashCommandEvent event) {

        event.deferReply().setEphemeral(true).queue();

        int amount = (int) Objects.requireNonNull(event.getOption("amount")).getAsLong();

        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MESSAGE_MANAGE)) {

            event.getHook().sendMessage("**You don't have the `manage message` permission.**").queue();
            return;
        }

        if (amount >= 101) {
            event.getHook().sendMessage("**You can't delete more than 100 messages.**").queue();
            }
        else {
            MessageHistory history = new MessageHistory(event.getChannel());
            List<Message> messages = history.retrievePast(amount).complete();
            for (int i = messages.size()-1; i > 0; i--) {
                if (messages.get(i).isPinned()) messages.remove(i);
            }
            event.getTextChannel().deleteMessages(messages).complete();
            event.getHook().sendMessage("**" + messages.size() + " messages were deleted.**").queue();

            var embed = new EmbedBuilder()
                    .setTitle("**"+messages.size()+" messages were deleted.**")
                    .setColor(Config.EMBED_GREY)
                    .setTimestamp(Instant.now())
                    .addField("Moderator:", event.getUser().getAsTag(), true)
                    .addField("Channel:", event.getTextChannel().getAsMention(), true)
                    .setFooter(event.getUser().getAsTag()+Config.FOOTER_MESSAGE, event.getUser().getAvatarUrl())
                    .build();

            String logChannelID = new Database().getConfig(Objects.requireNonNull(event.getGuild()), "logChannel").getAsString();
            if (!logChannelID.equals("0")) {
                TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);
                logChannel.sendMessageEmbeds(embed).queue();
            } else {
                event.getTextChannel().sendMessageEmbeds(embed).queue();
            }
            }
        }
    }
