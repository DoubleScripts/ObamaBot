package me.alex.obama;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.fernutils.console.ArgumentArrayUtils;
import kotlin.Unit;
import me.alex.obama.config.ChannelList;
import me.alex.obama.config.old.OldConfigData;
import me.alex.obama.listeners.Autos;
import me.alex.obama.listeners.React;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.FileUtils;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

        // Make config backup
        try {
            FileUtils.copyFile(CONFIG_FIlE, new File("./config.json.backup"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try {
            try {
                config = new ConfigManager<>(new GsonConfig<>(new BotConfigData(), CONFIG_FIlE));
                config.load();


                // TODO: Remove this temporary migration code
                // Attempt migration
                try {
                    BotConfigData botConfigData = new BotConfigData();

                    GsonConfig<OldConfigData> oldConfig = new GsonConfig<>(new OldConfigData(), CONFIG_FIlE);
                    oldConfig.load();

                    // If config values are not equal
                    if (
                            !oldConfig.getConfigData().getToken().equals(config.getConfigData().getToken()) ||
                                    (!oldConfig.getConfigData().getBotSpamChannels().isEmpty() && config.getConfigData().getGuildSettingsMap().isEmpty())
                    ) {

                        System.out.println("Migrating old config values!");

                        botConfigData.setToken(oldConfig.getConfigData().getToken());
                        ConfigManager<? extends Config<BotConfigData>> testConfig = new ConfigManager<>(new GsonConfig<>(botConfigData, CONFIG_FIlE));


                        oldConfig.getConfigData().getBotSpamChannels().forEach((guildId, channelIds) -> channelIds.forEach(channelId ->
                                testConfig.addChannel(guildId, channelId, ChannelList.SPAM))
                        );

                        testConfig.save();
                        config = testConfig;
                        System.out.println("Successfully migrated and saved old config values!");
                    }
                    //
                    //


                } catch (ConfigLoadException e) {
                    e.printStackTrace();
                }

            } catch (Exception ee) {
                ee.printStackTrace();
                config = new ConfigManager<>(new GsonConfig<>(new BotConfigData(), CONFIG_FIlE));
                config.load();

                System.out.println("Trying to make new config");

                Files.delete(CONFIG_FIlE.toPath());

                config = new ConfigManager<>(new GsonConfig<>(new BotConfigData(), CONFIG_FIlE));
                config.load();
            }
        } catch (IOException e) {
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

        JDA finalJda1 = jda;
        config.registerEventListener(ChannelList.SPAM, (guildId, channelId, added) -> {

            if (added && !Autos.getAutoInstances().containsKey(channelId)) {
                Guild guild = finalJda1.getGuildById(guildId);
                TextChannel textChannel = Objects.requireNonNull(guild).getTextChannelById(channelId);
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
