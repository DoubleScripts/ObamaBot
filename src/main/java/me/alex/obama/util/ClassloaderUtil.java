package me.alex.obama.util;

import me.alex.obama.Main;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassloaderUtil {

    public static String readLineAsString(String file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        ClassLoader classLoader = Main.class.getClassLoader();

        try (Source fileSource = Okio.source(Objects.requireNonNull(classLoader.getResourceAsStream(file)));
             BufferedSource bufferedSource = Okio.buffer(fileSource)) {

            while (true) {
                String line = bufferedSource.readUtf8Line();
                if (line == null) break;

                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }

    public static List<String> readLines(String file) throws IOException {
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
