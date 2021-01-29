package command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PauseCommand implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event) {

    }

    @Override
    public String getKeyword() {
        return "pause";
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }
}
