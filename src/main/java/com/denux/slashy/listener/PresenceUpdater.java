package com.denux.slashy.listener;

import com.denux.slashy.Bot;
import com.denux.slashy.services.Constants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class PresenceUpdater extends ListenerAdapter {

    private static ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();

    private final List<Function<JDA, Activity>> activities;

    private int currentActivityIndex = 0;

    private final long delay;

    private final TimeUnit delayUnit;

    private JDA jda;

    public PresenceUpdater(List<Function<JDA, Activity>> activities, long delay, TimeUnit delayUnit) {
        this.activities = new CopyOnWriteArrayList<>(activities);
        this.delay = delay;
        this.delayUnit = delayUnit;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.jda = event.getJDA();
        threadPool.scheduleWithFixedDelay(() -> {
            if (this.jda.getPresence().getStatus() != OnlineStatus.DO_NOT_DISTURB) {
                this.jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            }
            if (currentActivityIndex >= this.activities.size()) currentActivityIndex = 0;
            if (this.activities.size() > 0) {
                this.jda.getPresence().setActivity(this.activities.get(currentActivityIndex++).apply(this.jda));
            }
        }, 0, this.delay, this.delayUnit);
    }

    public static PresenceUpdater standardActivities() {
        return new PresenceUpdater(List.of(
                jda -> Activity.watching("User count: " + new Bot().userCount()),
                jda -> Activity.watching("javadiscord.net"),
                jda -> Activity.playing("Version: "+ Constants.VERSION)
        ), 35, TimeUnit.SECONDS);
    }
}
