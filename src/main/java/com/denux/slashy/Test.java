package com.denux.slashy;
import com.denux.slashy.commands.SlashCommandHandler;
import com.denux.slashy.commands.dao.GuildSlashCommand;
import com.denux.slashy.services.Database;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class Test extends GuildSlashCommand implements SlashCommandHandler {

    public Test () {
        this.commandData = new CommandData("test", "Testing Things.");
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {

        event.deferReply().setEphemeral(false).queue();

        var logChannel = new Database().getConfig(event.getGuild(), "logChannel").getAsString();
        event.getHook().sendMessage(logChannel).queue();
        }
    }

