package me.alex.obama;

import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class React extends ListenerAdapter {

    private static byte[] imagebytes;
    private static byte[] imagebytes2;


    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if (e.getMessage().isMentioned(e.getJDA().getSelfUser(), Message.MentionType.USER)) {
            e.getChannel().sendMessage("Yes? Need any of my expertise in politics or in the arts of stupidity? I am the man for the job! Just send me the numbers on the back of your credit card, the security code and expiration month and year and I will do nothing for you!").queue();
            e.getChannel().sendMessage("Note to self: This is a bot and you should not send credit card details but rather free VBucks to me.").queue();

            e.getAuthor().openPrivateChannel()
                    .flatMap(privateChannel -> privateChannel.sendMessage("Send me the numbers on the back of your credit card and I will give you the nuclear codes. America depends on it!!! (Don't actually send me them)"))
                    .complete();
        }
        if (e.getMessage().getContentRaw().equalsIgnoreCase("+Spam")) {
            if (e.getChannel() instanceof TextChannel && e.getMember() != null && e.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                try {
                    Main.getConfig().syncLoad();

                    boolean isSpam = Main.getConfig().isChannelInRegistry(e.getTextChannel(), ChannelList.SPAM);

                    if (isSpam) Main.getConfig().removeChannel(e.getTextChannel(), ChannelList.SPAM);
                    else Main.getConfig().addChannel(e.getTextChannel(), ChannelList.SPAM);

                    e.getChannel().sendMessage("Will spam fern: " + !isSpam).queue();
                } catch (ConfigLoadException configLoadException) {
                    configLoadException.printStackTrace();
                }
            } else
                e.getChannel().sendMessage("You don't have permission (Manage Channels) to make this a place for me to make proper and valuable statements to promote morale and sanity").queue();
        }

        if (e.getMessage().getContentRaw().equalsIgnoreCase("+annoying")) {
            if (e.getChannel() instanceof TextChannel && e.getMember() != null && e.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                try {
                    Main.getConfig().syncLoad();

                    boolean isSpam = Main.getConfig().isChannelInRegistry(e.getTextChannel(), ChannelList.ANNOYING);

                    if (isSpam) Main.getConfig().removeChannel(e.getTextChannel(), ChannelList.ANNOYING);
                    else Main.getConfig().addChannel(e.getTextChannel(), ChannelList.ANNOYING);

                    e.getChannel().sendMessage("Will be an annoying but likeable bot: " + !isSpam).queue();
                } catch (ConfigLoadException configLoadException) {
                    configLoadException.printStackTrace();
                }
            } else
                e.getChannel().sendMessage("You don't have permission (Manage Channels) to make this a place for me to make proper and valuable statements to promote morale and sanity").queue();
        }


        if (e.getMessage().getContentRaw().equalsIgnoreCase("+swear")) {
            if (e.getChannel() instanceof TextChannel && e.getMember() != null && e.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                try {
                    Main.getConfig().syncLoad();

                    boolean isSpam = Main.getConfig().isChannelInRegistry(e.getTextChannel(), ChannelList.SWEAR);

                    if (isSpam) Main.getConfig().removeChannel(e.getTextChannel(), ChannelList.SWEAR);
                    else Main.getConfig().addChannel(e.getTextChannel(), ChannelList.SWEAR);

                    e.getChannel().sendMessage("Tolerance to naughty words: " + isSpam).queue();
                } catch (ConfigLoadException configLoadException) {
                    configLoadException.printStackTrace();
                }
            } else
                e.getChannel().sendMessage("You don't have permission (Manage Channels) to make this a place for me to make proper and valuable statements to promote morale and sanity").queue();
        }

        if (e.getMessage().getContentRaw().equalsIgnoreCase("+fact") && e.getAuthor() != e.getJDA().getSelfUser()){
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
        if (e.getMessage().getContentRaw().equalsIgnoreCase("+bad") && e.getAuthor() != e.getJDA().getSelfUser()){
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

        if (e.getMessage().getContentRaw().equalsIgnoreCase("+Back") && e.getAuthor() != e.getJDA().getSelfUser()){
            MessageHistory prevMessg = e.getChannel().getHistory();
            e.getChannel().sendMessage("Guess who's back, back again\n" +
                      e.getAuthor().getName() + "'s back, tell a friend").queue();
        }

        if (e.getAuthor() != e.getJDA().getSelfUser()) {
            List<String> words = SwearCheck.containsSwear(e.getMessage().getContentRaw());
            try {
                Main.getConfig().syncLoad();


                boolean isSwear = !Main.getConfig().isChannelInRegistry(e.getTextChannel(), ChannelList.SWEAR);

                if (!words.isEmpty() && isSwear) {
                    e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Hey! Swear words are not allowed in my America! If we want to make America great again, we must not use such horrifying language!\n> " + words.toString()).queue();
                }

                boolean isSpam = Main.getConfig().isChannelInRegistry(e.getTextChannel(), ChannelList.ANNOYING);

                if (e.getMessage().getContentRaw().toLowerCase().contains("will") && e.getAuthor() != e.getJDA().getSelfUser() && isSpam){
                    MessageHistory prevMessg = e.getChannel().getHistory();
                    e.getChannel().sendMessage("No I don't think you will").queue();
                }

                if (e.getMessage().getContentRaw().endsWith("?") && isSpam) {
                    e.getChannel().sendMessage("Need help with that? Yes or no?").queue();
                }
            } catch (ConfigLoadException configLoadException) {
                configLoadException.printStackTrace();
            }
        }
    }

    public static byte[] getFileAsBytes(String fileName) throws IOException {

        //Get file from resources folder
        ClassLoader classLoader = Main.class.getClassLoader();

        return Objects.requireNonNull(classLoader.getResourceAsStream(fileName)).readAllBytes();
    }
}
