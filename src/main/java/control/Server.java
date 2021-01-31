package control;

import music.ServerPlayer;
import music.TrackInfo;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private final Guild guild;
    private final ServerPlayer player;
    private final ConcurrentHashMap<String, TrackInfo> tracks = new ConcurrentHashMap<>();

    public Server(Guild guild) {
        this.guild = guild;
        this.player = new ServerPlayer(this);
    }

    public List<TrackInfo> getPlaylist() {
        ArrayList<TrackInfo> playlist = new ArrayList<>();
        playlist.addAll(tracks.values());
        return playlist;
    }

    public void addTrack(String id, String name) {
        tracks.put(id, new TrackInfo(id, name));
    }

    public void removeTrack(String id) {
        tracks.remove(id);
    }

    public boolean hasTrack(String id) {
        return tracks.containsKey(id);
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public Guild getGuild() {
        return guild;
    }

}
