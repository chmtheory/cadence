package control;

import command.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import util.DiscordUtil;

import java.util.HashMap;

public class CadenceListener extends ListenerAdapter {

    private final HashMap<String, Command> commandMap;
    private final String commandPrefix = "!";

    public CadenceListener(HashMap<String, Command> commandMap) {
        this.commandMap = commandMap;
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
                c.execute(event);
            }
        }
    }

}
