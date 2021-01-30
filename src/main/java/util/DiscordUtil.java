package util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.function.Consumer;

public final class DiscordUtil {

    private DiscordUtil() {

    }

    public static void sendMessage(GuildMessageReceivedEvent event, String message) {
        event.getChannel().sendMessage(message).queue();
    }

    public static void sendMessage(TextChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    public static TextChannel getDefaultTextChannel(Guild guild) {
        return guild.getDefaultChannel();
    }

}
