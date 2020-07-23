package me.alex.obama;

import net.dv8tion.jda.api.entities.TextChannel;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Autos {

    private static List<String> randomLines;
    private static final String FILE = "randLines.txt";
    private final TextChannel channel;
    private Timer timer = new Timer();

    public Autos(TextChannel channel) {
        this.channel = channel;
        runTimer();
    }

    private void runTimer() {
        int upperBound = 3 * 60  * 1000;
        int lowerBound = 1 * 60 * 1000;
        int delay = (new Random().nextInt(upperBound - lowerBound) + lowerBound);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (channel.canTalk()) {
                    channel.sendMessage(getRandomLine()).queue();
                    runTimer();
                }
            }
        }, delay);
        System.out.println(delay);
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

}
