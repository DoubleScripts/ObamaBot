package me.alex.obama;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.fernutils.console.ArgumentArrayUtils;
import kotlin.Unit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final File CONFIG_FIlE = new File("./config.json");
    private static ConfigManager<? extends Config<BotConfigData>> config;

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {

        try {
            config = new ConfigManager<>(new GsonConfig<>(new BotConfigData(), CONFIG_FIlE));
            config.load();
        } catch (ConfigLoadException e) {
            e.printStackTrace();
            System.exit(1);
        }

        final String[] preToken = new String[1];

        ArgumentArrayUtils.parseArguments(args).handle("-token", strings -> preToken[0] = strings.poll()).apply();

        String token = preToken[0];

        if (token == null) token = config.getConfigData().getToken();

        if (token.equals("FIXME")) {
            System.err.println("Fix the token in config or provide using -token");
            System.exit(1);
        }

        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        JDA jda = null;
        React react = new React();
        jdaBuilder.addEventListeners(react);
        jdaBuilder.setActivity(Activity.watching("+Fact. ID: " + UUID.randomUUID()));
        try {
            jda = jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        JDA finalJda = jda;

        new HashMap<>(config.getServerList()).forEach((guildId, serverSettings) -> {
            Guild guild = finalJda.getGuildById(guildId);

            if (guild == null) {
                config.removeGuild(guildId);
                config.getConfigData().getGuildSettingsMap().remove(guildId);
                return;
            }

            serverSettings.unmodifiableChannelListsListMap().values().forEach(channelIdList -> channelIdList.forEach(channelId -> {
                TextChannel textChannel = guild.getTextChannelById(channelId);

                if (textChannel == null) {
                    config.removeChannel(guild, channelId, ChannelList.SPAM);
                }
            }));

            for (long channelId : serverSettings.getChannels(ChannelList.SPAM)) {
                TextChannel textChannel = guild.getTextChannelById(channelId);

                assert textChannel != null;
                new Autos(textChannel);
            }
        });

        config.registerEventListener(ChannelList.SPAM, (guild, channelId, added) -> {

            if (added && !Autos.getAutoInstances().containsKey(channelId)) {
                TextChannel textChannel = guild.getTextChannelById(channelId);
                new Autos(Objects.requireNonNull(textChannel));
            }

            return Unit.INSTANCE;
        });
    }

    public static ConfigManager<? extends Config<BotConfigData>> getConfig() {
        return config;
    }


    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
