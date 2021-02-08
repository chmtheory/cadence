package command;

import control.Server;
import music.PlayerState;
import music.ServerPlayer;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import util.DiscordUtil;

public class SkipCommand implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event, Server server) {
        ServerPlayer player = server.getPlayer();

        if (player.getState() == PlayerState.PLAYING) {
            player.skip();
        }
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
