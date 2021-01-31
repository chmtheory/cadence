package command;

import control.Server;
import music.PlayerState;
import music.ServerPlayer;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import util.DiscordUtil;

public class PlayCommand implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event, Server server) {
        ServerPlayer player = server.getPlayer();

        if (player.getState() == PlayerState.PLAYING) {
            DiscordUtil.sendMessage(event, "Already playing!");
        } else if (player.getState() == PlayerState.DISCONNECTED) {
            player.connect(DiscordUtil.getDefaultVoiceChannel(server));
            player.play();
            DiscordUtil.sendMessage(event, "Now playing!");
        } else if (player.getState() == PlayerState.PAUSED) {
            player.resume();
            DiscordUtil.sendMessage(event, "Resuming playback!");
        } else if (player.getState() == PlayerState.STOPPED) {
            player.play();
            DiscordUtil.sendMessage(event, "Now playing!");
        }
    }

    @Override
    public String getKeyword() {
        return "play";
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }
}
