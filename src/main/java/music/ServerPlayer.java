package music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import control.Server;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DiscordUtil;
import util.YoutubeUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ServerPlayer extends AudioEventAdapter implements AudioLoadResultHandler {
    // TODO: Detect if nobody is in the same voice channel for an extended period of time.

    static {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        pManager = playerManager;
    }

    private static AudioPlayerManager pManager;
    private final static Logger log = LoggerFactory.getLogger(ServerPlayer.class);

    private PlayerState state = PlayerState.DISCONNECTED;
    
    private final Server linkedServer;
    private final AudioPlayer player;

    private List<TrackInfo> playlist;
    private int position;

    private Timer afkTimer;

    public ServerPlayer(Server server) {
        player = pManager.createPlayer();
        player.addListener(this);

        linkedServer = server;
        linkedServer.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
    }

    public void connect(VoiceChannel channel) {
        if (state == PlayerState.DISCONNECTED) {
            linkedServer.getGuild().getAudioManager().openAudioConnection(channel);
            setState(PlayerState.STOPPED);
        }
    }

    public void disconnect() {
        // TODO: Disconnect automatically after a certain period of inactivity.
        if (state != PlayerState.DISCONNECTED) {
            linkedServer.getGuild().getAudioManager().closeAudioConnection();
            setState(PlayerState.DISCONNECTED);
        }
    }

    public void notifyDisconnected() {
        this.playlist = null;
        this.position = 0;
        setState(PlayerState.DISCONNECTED);
    }

    public void play() {
        if (state != PlayerState.DISCONNECTED) {
            this.playlist = linkedServer.getPlaylist();
            loadNext();
        }
    }

    public void pause() {
        if (state == PlayerState.PLAYING) {
            player.setPaused(true);
        }
    }

    public void resume() {
        if (state == PlayerState.PAUSED) {
            player.setPaused(false);
        }
    }

    public void skip() {
        loadNext();
    }

    public void stop() {
        if (state == PlayerState.PAUSED || state == PlayerState.PLAYING) {
            player.stopTrack();
            setState(PlayerState.STOPPED);
        }
    }

    public PlayerState getState() {
        return state;
    }

    private void setState(PlayerState state) {
        this.state = state;
        if (state == PlayerState.STOPPED) {
            afkTimer = new Timer();
            afkTimer.schedule(new TimerTask() {
                public void run() {
                    disconnect();
                    afkTimer = null;
                }
            }, 30000);
        } else {
            afkTimer.cancel();
            afkTimer = null;
        }
    }

    private void loadNext() {
        player.stopTrack();

        if (state != PlayerState.STOPPED) {
            position++;

        }

        if (position >= playlist.size()) {
            playlist = null;
            position = 0;
            setState(PlayerState.STOPPED);
        } else {
            pManager.loadItem(YoutubeUtil.createYoutubeLinkFromId(playlist.get(position).trackId), this);
        }

    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        setState(PlayerState.PAUSED);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        setState(PlayerState.PLAYING);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        state = PlayerState.PLAYING;

        System.out.println("test");

        DiscordUtil.sendMessageToDefaultTextChannel(linkedServer, "Now playing " + playlist.get(position).getName() + "!");
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            loadNext();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        log.error("Exception occurred while playing track in guild #" + linkedServer.getGuild().getIdLong(), exception);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        loadNext();
    }


    @Override
    public void trackLoaded(AudioTrack track) {
        player.playTrack(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        player.playTrack(audioPlaylist.getTracks().get(0));
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException e) {
        // TODO: Convey exception to user.
    }
}
