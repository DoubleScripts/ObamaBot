package me.alex.obama;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;


import javax.security.auth.login.LoginException;

public class Main {

    private static final String DEDICATED_CHANNEL = "obama";

    public static void main(String[] args){
        JDABuilder jdaBuilder = JDABuilder.createDefault(Constants.token);
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
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        jda.getGuilds().forEach(guild -> {
            guild.getChannels().forEach(guildChannel -> {
                if (guildChannel.getType() == ChannelType.TEXT && guildChannel instanceof TextChannel) {
                    TextChannel textChannel = (TextChannel) guildChannel;
                    if (textChannel.canTalk() && textChannel.getName().equals(DEDICATED_CHANNEL)) {
                        new Autos(textChannel);
                    }
                }
            });
        });
    }
}
