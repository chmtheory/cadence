package control;

import command.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DiscordUtil;

import java.util.HashMap;

public class CadenceListener extends ListenerAdapter {

    private final static Logger log = LoggerFactory.getLogger(CadenceListener.class);
    private final HashMap<Long, Server> serverMap = new HashMap<>();
    private final HashMap<String, Command> commandMap;
    private final String commandPrefix = "!";

    public CadenceListener(HashMap<String, Command> commandMap) {
        this.commandMap = commandMap;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();
        jda.getGuildCache().forEachUnordered(guild -> {
            serverMap.put(guild.getIdLong(), new Server(guild));
        });
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Guild guild = event.getGuild();
        serverMap.put(guild.getIdLong(), new Server(guild));
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        if (event.getJDA().getSelfUser().getIdLong() == event.getMember().getIdLong()) {
            serverMap.get(event.getGuild().getIdLong()).getPlayer().notifyDisconnected();
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot()) {
            return; // Never listen to bots!
        }

        String first = DiscordUtil.firstWord(event.getMessage());

        if (first.startsWith(commandPrefix)) {
            first = first.substring(1);

            Command c;
            if ((c = commandMap.get(first)) != null) {

                c.execute(event, serverMap.get(event.getGuild().getIdLong()));
            }
        }
    }

}
