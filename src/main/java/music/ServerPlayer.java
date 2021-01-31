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
import util.YoutubeUtil;

import java.util.List;

public class ServerPlayer extends AudioEventAdapter implements AudioLoadResultHandler {
    // TODO: Save currently playing track information to retrieve later.

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

    public ServerPlayer(Server server) {
        player = pManager.createPlayer();
        player.addListener(this);

        linkedServer = server;
        linkedServer.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
    }

    public void connect(VoiceChannel channel) {
        if (state == PlayerState.DISCONNECTED) {
            linkedServer.getGuild().getAudioManager().openAudioConnection(channel);
            state = PlayerState.STOPPED;
        }
    }

    public void disconnect() {
        // TODO: Disconnect automatically after a certain period of inactivity.
        if (state != PlayerState.DISCONNECTED) {
            linkedServer.getGuild().getAudioManager().closeAudioConnection();
            state = PlayerState.DISCONNECTED;
        }
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
            state = PlayerState.STOPPED;
        }
    }

    public PlayerState getState() {
        return state;
    }

    private void loadNext() {
        player.stopTrack();

        if (playlist.size() == 0) {
            playlist = null;
            state = PlayerState.STOPPED;
        } else {
            pManager.loadItem(YoutubeUtil.createYoutubeLinkFromId(playlist.remove(0).trackId), this);
        }
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        state = PlayerState.PAUSED;
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        state = PlayerState.PLAYING;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        state = PlayerState.PLAYING;
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
