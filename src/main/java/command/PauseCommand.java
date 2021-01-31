package command;

import control.Server;
import music.PlayerState;
import music.ServerPlayer;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import util.DiscordUtil;

public class PauseCommand implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event, Server server) {
        ServerPlayer player = server.getPlayer();
        if (player.getState() == PlayerState.DISCONNECTED) {
            DiscordUtil.sendMessage(event, "Player is not connected!");
        } else if (player.getState() == PlayerState.STOPPED) {
            DiscordUtil.sendMessage(event, "Player is stopped!");
        } else if (player.getState() == PlayerState.PAUSED) {
            DiscordUtil.sendMessage(event, "Player is already paused!");
        } else if (player.getState() == PlayerState.PLAYING) {
            player.pause();
            DiscordUtil.sendMessage(event, "Player paused!");
        }
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
