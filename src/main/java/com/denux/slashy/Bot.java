package com.denux.slashy;
import com.denux.slashy.properties.ConfigString;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {

    public static ScheduledExecutorService asyncPool;

    public static JDA jda;

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(Bot.class);

    public static void main(String[] args) throws Exception {

        //Part 1 for async commands
        asyncPool = Executors.newScheduledThreadPool(4);

        //Creating the bot instance
        jda = JDABuilder.createDefault(new ConfigString("token").getValue())
                .addEventListeners(new SlashCommands())
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .enableCache(CacheFlag.ACTIVITY)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
    }
}
