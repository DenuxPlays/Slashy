package com.denux.slashy.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

public interface SlashCommandHandler {

    void execute(SlashCommandEvent event);
}
