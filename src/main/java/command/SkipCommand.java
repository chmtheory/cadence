package command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SkipCommand implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event) {

    }

    @Override
    public String getKeyword() {
        return "skip";
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }
}
