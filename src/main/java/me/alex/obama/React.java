package me.alex.obama;

import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;

public class React extends ListenerAdapter {

    private static byte[] imagebytes;
    private static byte[] imagebytes2;


    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if (e.getMessage().getContentRaw().equals("+Fact") && e.getAuthor() != e.getJDA().getSelfUser()){
            MessageHistory prevMessg = e.getChannel().getHistory();
            if (imagebytes == null) {
                try {
                    imagebytes = getFileAsBytes("response1.png");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            e.getChannel().sendMessage( "> " + prevMessg.retrievePast(2).
                    complete()
                    .get(1)
                    .getContentRaw())
                    .addFile(imagebytes, "Funny.png")
                    .queue();
        }
        if (e.getMessage().getContentRaw().equals("+Bad") && e.getAuthor() != e.getJDA().getSelfUser()){
            MessageHistory prevMessg = e.getChannel().getHistory();
            if (imagebytes2 == null) {
                try {
                    imagebytes2 = getFileAsBytes("response2.png");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            e.getChannel().sendMessage( "> " + prevMessg.retrievePast(2).
                    complete()
                    .get(1)
                    .getContentRaw() + "\n **Not bad**")
                    .addFile(imagebytes2, "Funny.png")
                    .queue();
        }
    }

    public static byte[] getFileAsBytes(String fileName) throws IOException {

        //Get file from resources folder
        ClassLoader classLoader = Main.class.getClassLoader();

        return Objects.requireNonNull(classLoader.getResourceAsStream(fileName)).readAllBytes();
    }
}
