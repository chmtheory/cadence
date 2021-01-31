package command;

import control.Server;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class StopCommand implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event, Server server) {

    }

    @Override
    public String getKeyword() {
        return "stop";
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }
}
