package me.alex.obama.listeners;

import me.alex.obama.Main;
import me.alex.obama.config.ChannelList;
import me.alex.obama.util.ClassloaderUtil;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.util.*;

public class Autos {

    private static final Map<Long, Autos> autoInstances = new HashMap<>();

    private static List<String> randomLines;
    private static final String FILE = "randLines.txt";
    private final TextChannel channel;

    private LinkedList<String> queueLines = new LinkedList<>();

    private int increaseTime = 0;

    private final Timer timer = new Timer();

    public Autos(TextChannel channel) {
        this.channel = channel;
        autoInstances.put(channel.getIdLong(), this);
        runTimer();
    }

    private void runTimer() {
        int upperBound = (9 * 60 * 1000) + (increaseTime);
        int lowerBound = (15 * 1000) + (increaseTime);
        int delay = (new Random().nextInt(upperBound - lowerBound) + lowerBound);
        System.out.println("Time for " + channel.getId() + "(" + channel.getName() + ") Delay: " + delay + " increased with " + increaseTime);
        increaseTime += new Random().nextInt(1000 - 2) + 2;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!Main.getConfig().isChannelInRegistry(channel, ChannelList.SPAM)) return;

                if (channel.canTalk()) {
                    channel.sendMessage(getRandomLine()).queue();
                    runTimer();
                } else {
                    // Cleanup
                    autoInstances.remove(channel.getIdLong());
                }

            }
        }, delay);
    }

    private String getRandomLine() {
        if (randomLines == null) {
            try {
                randomLines = ClassloaderUtil.readLines(FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (queueLines.isEmpty()) {
            queueLines = new LinkedList<>(randomLines);
            Collections.shuffle(queueLines);
        }

        return queueLines.remove();

    }



    public static Map<Long, Autos> getAutoInstances() {
        return autoInstances;
    }
}
