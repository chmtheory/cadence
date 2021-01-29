package control;

import command.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CadenceListener extends ListenerAdapter {

    private final HashMap<String, Command> commandMap;

    public CadenceListener(HashMap<String, Command> commandMap) {
        this.commandMap = commandMap;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);
    }
}
