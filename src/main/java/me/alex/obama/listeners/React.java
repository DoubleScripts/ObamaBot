package me.alex.obama.listeners;

import com.github.fernthedev.config.common.exceptions.ConfigLoadException;
import me.alex.obama.Main;
import me.alex.obama.config.ChannelList;
import me.alex.obama.util.Randomizer;
import me.alex.obama.util.SwearCheck;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;
import java.sql.Time;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class React extends ListenerAdapter {

    private static byte[] imageBytes;
    private static byte[] imageBytes2;

    private static final String PREFIX = "+";
    private static final String NEED_HELP_CONSTANT = "Need help with that? Yes or no?";
    private final Map<String, BotCommand> commands = new HashMap<>();

    public React() {
        // TODO: Add command documentation
        registerCommand(new BotCommand("spam", e ->
                toggleConfig(e, ChannelList.SPAM, "Will spam: ", "You don't have permission (Manage Channels) to make this a place for me to make proper and valuable statements to promote morale and sanity")));

        registerCommand(new BotCommand("annoying", e ->
                toggleConfig(e, ChannelList.ANNOYING, "Will be an annoying but likeable bot: ", "You don't have permission (Manage Channels) to make this a place for me to make proper and valuable statements to promote morale and sanity")));

        registerCommand(new BotCommand("swear", e -> {
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
        }));

        registerCommand(new BotCommand("fact", e -> {
            if (e.getAuthor() != e.getJDA().getSelfUser()) {
                MessageHistory prevMessg = e.getChannel().getHistory();
                if (imageBytes == null) {
                    try {
                        imageBytes = getFileAsBytes("response1.png");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                e.getChannel().sendMessage( "> " + prevMessg.retrievePast(2).
                        complete()
                        .get(1)
                        .getContentRaw())
                        .addFile(imageBytes, "Funny.png")
                        .queue();
            }
        }));

        registerCommand(new BotCommand("bad", e -> {
            if (e.getAuthor() != e.getJDA().getSelfUser()) {
                MessageHistory prevMessg = e.getChannel().getHistory();
                if (imageBytes2 == null) {
                    try {
                        imageBytes2 = getFileAsBytes("response2.png");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                e.getChannel().sendMessage( "> " + prevMessg.retrievePast(2).
                        complete()
                        .get(1)
                        .getContentRaw() + "\n **Not bad**")
                        .addFile(imageBytes2, "Funny.png")
                        .queue();
            }
        }));

        registerCommand(new BotCommand("back", e ->
                e.getChannel().sendMessage("Guess who's back, back again\n"
                        + e.getAuthor().getName() + "'s back, tell a friend").queue()));

        registerCommand(new BotCommand("obama", e -> {
            EmbedBuilder builder = new EmbedBuilder();

            for (BotCommand command : commands.values()) {
                builder.addField(command.getName(), command.getDescription(), true);
            }


            builder.setFooter("Obama is made with love and evil at https://github.com/antaxiom/Obamabot");
            builder.setTitle("Obama legislation docs");
            builder.setColor(new Color(50, 150, 230));

            e.getChannel().sendMessage(builder.build()).queue();
        }));

        registerCommand(new BotCommand("no", e -> responseToNeedHelp(e, false)), false);
        registerCommand(new BotCommand("nope", e -> responseToNeedHelp(e, false)), false);
        registerCommand(new BotCommand("nono", e -> responseToNeedHelp(e, false)), false);

        registerCommand(new BotCommand("sure", e -> responseToNeedHelp(e, true)), false);
        registerCommand(new BotCommand("yes", e -> responseToNeedHelp(e, true)), false);
        registerCommand(new BotCommand("definitely", e -> responseToNeedHelp(e, true)), false);
    }

    public void responseToNeedHelp(MessageReceivedEvent e, boolean yes) {
        if (e.getChannel().getHistory().retrievePast(2).complete().get(1).getContentRaw().equals(NEED_HELP_CONSTANT)) {
            if (yes) {
                e.getChannel().sendMessage("I will send Dr. Phil to you.").queue();

                int time = Randomizer.randomNumber(1, 3);



                ChronoUnit timeUnit = ChronoUnit.FOREVER;

                while (timeUnit == ChronoUnit.FOREVER || timeUnit == ChronoUnit.HALF_DAYS)
                    timeUnit = ChronoUnit.values()[Randomizer.randomNumber(0, ChronoUnit.values().length - 1)];

                int timePhil = timeUnit.isTimeBased() ? Randomizer.randomNumber(1, 59) : Randomizer.randomNumber(1, 999);

                e.getChannel().sendMessage("Dr. Phil will arrive after " + timePhil + " " + timeUnit.toString()).queueAfter(time, TimeUnit.SECONDS);
            } else {
                e.getChannel().sendMessage("Fine. But you need help. When you do, I won't give you anything").queue();
            }
        }
    }

    /**
     * Registers the command
     * automatically adds the prefix
     *
     * @param botCommand The command to add
     */
    public void registerCommand(BotCommand botCommand) {
        // Set to lowercase to avoid case conflict
        registerCommand(botCommand, true);
    }

    /**
     * Registers the command
     * automatically adds the prefix
     *
     * @param botCommand The command to add
     */
    public void registerCommand(BotCommand botCommand, boolean prefix) {
        // Set to lowercase to avoid case conflict
        if (!prefix)
            commands.put(botCommand.getName().toLowerCase(), botCommand);
        else
            commands.put(PREFIX + botCommand.getName().toLowerCase(), botCommand);
    }

    /**
     * Convenience method for config commands
     * @param e event
     * @param channelList what channel type to modify
     * @param msg the toggle message
     * @param errorMsg error message
     */
    private void toggleConfig(MessageReceivedEvent e, ChannelList channelList, String msg, String errorMsg) {
        if (e.getChannel() instanceof TextChannel && e.getMember() != null && e.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            try {
                Main.getConfig().syncLoad();

                boolean isSpam = Main.getConfig().isChannelInRegistry(e.getTextChannel(), channelList);

                if (isSpam) Main.getConfig().removeChannel(e.getTextChannel(), channelList);
                else Main.getConfig().addChannel(e.getTextChannel(), channelList);

                e.getChannel().sendMessage(msg + !isSpam).queue();
            } catch (ConfigLoadException configLoadException) {
                configLoadException.printStackTrace();
            }
        } else
            e.getChannel().sendMessage(errorMsg).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if (e.getMessage().isMentioned(e.getJDA().getSelfUser(), Message.MentionType.USER)) {
            e.getChannel().sendMessage("Yes? Need any of my expertise in politics or in the arts of stupidity? I am the man for the job! Just send me the numbers on the back of your credit card, the security code and expiration month and year and I will do nothing for you!").queue();
            e.getChannel().sendMessage("Note to self: This is a bot and you should not send credit card details but rather free VBucks to me.").queue();

            e.getAuthor().openPrivateChannel()
                    .flatMap(privateChannel -> privateChannel.sendMessage("Send me the numbers on the back of your credit card and I will give you the nuclear codes. America depends on it!!! (Don't actually send me them)"))
                    .complete();
        }

        String rawMessage = e.getMessage().getContentRaw();
        BotCommand command = commands.get(rawMessage.toLowerCase());
        if (command != null) {
            try {
                command.getEvent().invoke(e);
            } catch (Exception ex) {
                e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("There was an error running \"" + rawMessage + "\". The error: " + ex.getLocalizedMessage()).queue());
            }
        }

        // Add any non commands like contains here
        if (e.getAuthor() != e.getJDA().getSelfUser()) {
            List<String> words = SwearCheck.containsSwear(e.getMessage().getContentRaw());
            try {
                Main.getConfig().syncLoad();


                boolean isSwear = !Main.getConfig().isChannelInRegistry(e.getTextChannel(), ChannelList.SWEAR);

                if (!words.isEmpty() && isSwear) {
                    e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Hey! Swear words are not allowed in my America! If we want to make America great again, we must not use such horrifying language!\n> " + words.toString()).queue();
                }

                boolean isSpam = Main.getConfig().isChannelInRegistry(e.getTextChannel(), ChannelList.ANNOYING);

                if (e.getMessage().getContentRaw().toLowerCase().contains("will") && e.getAuthor() != e.getJDA().getSelfUser() && !e.getAuthor().isBot() && isSpam){
                    e.getChannel().sendMessage("No I don't think you will").queue();
                }


                if (e.getMessage().getContentRaw().endsWith("?") && isSpam) {
                    e.getChannel().sendMessage(NEED_HELP_CONSTANT).queue();
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
