package music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServerPlayer extends AudioEventAdapter implements AudioLoadResultHandler {

    private final static AudioPlayerManager pManager = new DefaultAudioPlayerManager();
    private final static Logger log = LoggerFactory.getLogger(ServerPlayer.class);

    private PlayerState state = PlayerState.DISCONNECTED;
    private Guild linkedGuild;
    private AudioPlayer player;

    private List<String> playlist;

    private ServerPlayer() {

    }

    public static ServerPlayer createServerPlayer(Guild guild) {
        ServerPlayer player = new ServerPlayer();

        player.linkedGuild = guild;

        player.player = pManager.createPlayer();
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player.player));

        return player;
    }

    public void connect(VoiceChannel channel) {
        if (state == PlayerState.DISCONNECTED) {
            linkedGuild.getAudioManager().openAudioConnection(channel);
            state = PlayerState.STOPPED;
        }
    }

    public void disconnect() {
        if (state != PlayerState.DISCONNECTED) {
            linkedGuild.getAudioManager().closeAudioConnection();
            state = PlayerState.DISCONNECTED;
        }
    }

    public void play(List<String> playlist) {
        if (state != PlayerState.DISCONNECTED) {
            this.playlist = playlist;

            if (playlist.size() == 0) {
                this.playlist = null;
                return;
            }

            pManager.loadItem(playlist.remove(0), this);
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

    private void loadNext() {
        if (playlist.size() == 0) {
            playlist = null;
            state = PlayerState.STOPPED;
        } else {
            pManager.loadItem(playlist.remove(0), this);
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
        } else if (endReason == AudioTrackEndReason.CLEANUP) {
            state = PlayerState.STOPPED;
            playlist = null;
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        log.error("Exception occurred while playing track in guild #" + linkedGuild.getIdLong(), exception);
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
