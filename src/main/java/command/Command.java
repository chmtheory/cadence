package command;

import control.Server;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface Command {

    void execute(GuildMessageReceivedEvent event, Server server);
    String getKeyword();
    boolean requiresAuthorization();

}
