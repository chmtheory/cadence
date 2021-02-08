package command;

import control.Server;
import music.PlayerState;
import music.ServerPlayer;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import util.DiscordUtil;

public class StopCommand implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event, Server server) {
        ServerPlayer player = server.getPlayer();

        if (player.getState() == PlayerState.PLAYING) {
            player.stop();
        } else if (player.getState() == PlayerState.PAUSED) {
            player.stop();
        }
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
