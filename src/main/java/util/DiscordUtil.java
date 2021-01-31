package util;

import control.Server;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;

public final class DiscordUtil {

    private DiscordUtil() {

    }

    public static void sendMessage(GuildMessageReceivedEvent event, String message) {
        event.getChannel().sendMessage(message).queue();
    }

    public static void sendMessage(TextChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    public static TextChannel getDefaultTextChannel(Server server) {
        return server.getGuild().getDefaultChannel();
    }

    public static String[] parseArgumentsFromMessage(Message message) {
        String[] args = message.getContentStripped().split("\\s+");
        return Arrays.copyOfRange(args, 1, args.length);
    }

    public static String firstWord(Message message) {
        String msg = message.getContentStripped();

        return msg.substring(0, msg.indexOf(' '));
    }

}
