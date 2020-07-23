package me.alex.obama;

import com.github.fernthedev.fernutils.console.ArgumentArrayUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;


import javax.security.auth.login.LoginException;
import java.util.Queue;
import java.util.function.Consumer;

public class Main {

    private static final String DEDICATED_CHANNEL = "obama";

    public static void main(String[] args){

        final String[] preToken = new String[1];

        ArgumentArrayUtils.parseArguments(args).handle("-token", strings -> preToken[0] = strings.poll());

        String token = preToken[0];

        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        JDA jda = null;
        React react = new React();
        jdaBuilder.addEventListeners(react);
        jdaBuilder.setActivity(Activity.watching("+Fact"));
        try {
            jda = jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        try {
            assert jda != null;
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        jda.getGuilds().forEach(guild -> guild.getChannels().forEach(guildChannel -> {
            if (guildChannel.getType() == ChannelType.TEXT && guildChannel instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) guildChannel;
                if (textChannel.canTalk() && textChannel.getName().equals(DEDICATED_CHANNEL)) {
                    new Autos(textChannel);
                }
            }
        }));
    }
}
