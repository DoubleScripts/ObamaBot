package me.alex.obama;

import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.fernutils.console.ArgumentArrayUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;


import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class Main {

    private static final File CONFIG_FIlE = new File("./config.json");
    private static Config<BotConfigData> config;

    public static void main(String[] args){

        try {
            config = new GsonConfig<>(new BotConfigData(), CONFIG_FIlE);
            config.load();
        } catch (ConfigLoadException e) {
            e.printStackTrace();
            System.exit(1);
        }

        final String[] preToken = new String[1];

        ArgumentArrayUtils.parseArguments(args).handle("-token", strings -> preToken[0] = strings.poll());

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
        jdaBuilder.setActivity(Activity.watching("+Fact"));
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


        // Copy to avoid writing and reading errors.
        new HashMap<>(config.getConfigData().getBotSpamChannels()).forEach((guildId, channelIds) -> {
            Guild guild = finalJda.getGuildById(guildId);

            if (guild == null) {
                config.getConfigData().getBotSpamChannels().remove(guildId);
                return;
            }

            for (long channelId : channelIds) {
                if (guild.getGuildChannelById(ChannelType.TEXT, channelId) == null) {
                    config.getConfigData().getBotSpamChannels().get(guildId).remove(channelId);
                    continue;
                }

                TextChannel textChannel = guild.getTextChannelById(channelId);

                new Autos(textChannel);
            }


        });
    }

    public static Config<BotConfigData> getConfig() {
        return config;
    }

    public static boolean isSpamChannel(TextChannel channel) {
        Guild guild = channel.getGuild();

        Map<Long, List<Long>> botSpamChannels = config.getConfigData().getBotSpamChannels();

        return botSpamChannels.containsKey(guild.getIdLong())
                && botSpamChannels.get(guild.getIdLong()).contains(channel.getIdLong());
    }

    public static void addSpamChannel(TextChannel channel) {
        Map<Long, List<Long>> botSpamChannels = config.getConfigData().getBotSpamChannels();

        Guild guild = channel.getGuild();

        if (!botSpamChannels.containsKey(guild.getIdLong()))
            botSpamChannels.put(guild.getIdLong(), new ArrayList<>());

        List<Long> channels = botSpamChannels.get(guild.getIdLong());

        if (channels.contains(channel.getIdLong())) return;

        channels.add(channel.getIdLong());

        new Autos(channel);

        try {
            config.syncSave();
        } catch (ConfigLoadException e) {
            e.printStackTrace();
        }
    }

    public static void removeSpamChannel(TextChannel channel) {
        Map<Long, List<Long>> botSpamChannels = config.getConfigData().getBotSpamChannels();

        if (botSpamChannels.containsKey(channel.getGuild().getIdLong())) {
            List<Long> channels = botSpamChannels.get(channel.getGuild().getIdLong());

            if (!channels.contains(channel.getIdLong())) return;

            channels.remove(channel.getIdLong());

            if (channels.isEmpty())
                botSpamChannels.remove(channel.getGuild().getIdLong());

            try {
                config.syncSave();
            } catch (ConfigLoadException e) {
                e.printStackTrace();
            }
        }
    }
}
