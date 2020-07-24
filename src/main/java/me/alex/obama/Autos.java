package me.alex.obama;

import net.dv8tion.jda.api.entities.TextChannel;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.util.*;

public class Autos {

    private static final Map<Long, Autos> autoInstances = new HashMap<>();

    private static List<String> randomLines;
    private static final String FILE = "randLines.txt";
    private final TextChannel channel;

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
                randomLines = readLines(FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Random random = new Random();
        return randomLines.get(random.nextInt(randomLines.size()-1));

    }

    private List<String> readLines(String file) throws IOException {
        List<String> lines = new ArrayList<>();

        ClassLoader classLoader = Main.class.getClassLoader();

        try (Source fileSource = Okio.source(Objects.requireNonNull(classLoader.getResourceAsStream(file)));
             BufferedSource bufferedSource = Okio.buffer(fileSource)) {

            while (true) {
                String line = bufferedSource.readUtf8Line();
                if (line == null) break;

                lines.add(line);
            }
        }

        return lines;
    }

    public static Map<Long, Autos> getAutoInstances() {
        return autoInstances;
    }
}
